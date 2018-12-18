package com.hong.service;

import com.hong.annotations.LockAction;
import com.hong.annotations.RedisParameterLocked;
import com.hong.bean.Sequence;
import com.hong.common.bean.Result;
import com.hong.common.utils.DateUtils;
import com.hong.common.utils.SystemClock;
import com.hong.entity.Counter;
import com.hong.mapper.CounterMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Global Uniqe ID Generator
 * leaf-segment  瓶颈在于数据库的单点问题
 */
@Service
public class GuidServiceImpl implements GuidService {

    private static final Logger logger = LoggerFactory.getLogger(GuidServiceImpl.class);

    /**
     * 保存序列（一个数据序列对应一个sequence）
     */
    private static final Map<String, Sequence> SEQUENCE_HOLDER = new ConcurrentHashMap<>();

    @Autowired
    private CounterMapper counterMapper;

    /**
     * 预先在数据库中给每个服务分配好id生成的起始值min和max以及步长step
     * 第一次请求生成id时,根据服务名先从数据库中拉取出对应的Segment(下面即为Counter对象),
     * 转换为Sequence对象放到内存缓存中(这里指ConcurrentHashMap),
     * 后面相同服务的请求则直接从Map中取出,但每次请求记得要判断数据是否溢出,如果溢出,
     * 则以数据库中配置好的固定步长更新id数据范围的起始值。
     *
     * 这里有个问题:存在以下两种情况会造成瞬间CPU负载过大,惊群效应
     * 1.第一次涌入大量并发请求的瞬间因为 SEQUENCE_HOLDER 还没有初始化,会造成并发请求阻塞以及重复id
     * 2.某一瞬间并发请求量较大,且刚好 Sequence 中的 current 将要溢出,会造成和1相同的影响
     *
     * 因此这里的关键问题是 SEQUENCE_HOLDER 的处理,如何能够确保请求的id总是从 SEQUENCE_HOLDER 中拿到?
     * 即 SEQUENCE_HOLDER 中数据更新问题.
     *
     * 同时要考虑分布式系统,可以加一个分布式锁
     *
     * TODO 在每次服务重启 或者 Sequence中的current将要溢出时,提前将各个服务对应的 Sequence 更新到 SEQUENCE_HOLDER ?
     * @param name,具体服务名称,可以考虑枚举变量
     * @return
     */
    /*@LockAction(keepMills = 30)
    @Override
    public Result getSingleId(@RedisParameterLocked String name) {*/
    @Override
    public Result getSingleId(String name) {
        Result result = new Result();
        try {
            if (StringUtils.isBlank(name)) {
                throw new IllegalArgumentException("序列名称不能为空");
            }
            // 获取序列
            Sequence sequence = SEQUENCE_HOLDER.get(name);
            // 未加载到SEQUENCE_HOLDER中,或者溢出时需要重新向数据库申请
            if (sequence == null || sequence.isOverFlow()) {
                sequence = applyNextSequence(name);
            }
            result.setData(generateId(sequence));
        } catch (Exception e) {
            //返回uuid
            result.setData(generateSingleId(name));
            logger.error("获取单个Id异常[{}],手动生成Id", name, e);
        }
        return result;
    }

    @Override
    public Result getBatchId(String name, int size) {
        Result result = new Result();
        try {
            if (StringUtils.isBlank(name)) {
                throw new IllegalArgumentException("序列名称不能为空");
            }
            if (size < 1) {
                throw new IllegalArgumentException("size必须大于0");
            }
            List<String> idList = new ArrayList<>(size);

            // 获取序列
            Sequence sequence = SEQUENCE_HOLDER.get(name);
            // 未加载到SEQUENCE_HOLDER中,或者溢出时需要重新向数据库申请
            if (sequence == null || sequence.isOverFlow()) {
                sequence = applyNextSequence(name);
            }
            // 需要一批序列,如果序列不足重新取得
            for (int i = 0; i < size; i++) {
                if (sequence.isOverFlow()) {
                    sequence = applyNextSequence(name);
                }
                idList.add(generateId(sequence));
            }
            result.setData(idList);
        } catch (Exception e) {
            logger.error("获取批量Id异常name[{}],size[{}]", name, size, e);
            result.setData(generateBatchId(name, size));
        }
        logger.info("序列名[{}],生成批量Id为[{}]", name, result.getData());
        return result;
    }

    /**
     * @throws
     * @Title: applyNextSequence
     * @Description: 向数据中申请下一批序列
     * @paramta: @param name
     * @paramta:@return
     * @return:Sequence
     */
    @Transactional(timeout = 10, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public Sequence applyNextSequence(String name) {
        Sequence sequence = null;
        try {
            logger.info("start:" + SystemClock.now());
            //考虑分布式锁,name为key,保证同一时刻,查询数据没有其他线程update
            Counter counter = counterMapper.selectBySystemNameAndBizName("shop", name);
            logger.info("查询数据库count对象:" + counter.toString() + "\r\n");
            if (counter.getMax() == null) {
                counter.setMax(0L);
            }
            // 生成格式如下：前缀（业务系统）+日期8位+生成的序列
            /** 需要保证原子特性
             *  https://stackoverflow.com/questions/3964211/when-to-use-atomicreference-in-java
             *  https://www.cnblogs.com/love-jishu/p/5007882.html
             */
            AtomicReference<Long> start = new AtomicReference<>(counter.getMax() + 1);
            int size = counter.getStepSize();
            Long end = start.get() + size;
            int contentLength = counter.getLength();
            String pre = counter.getPrefix();
            counter.setMin(start.get());
            counter.setMax(end);
            counter.setLastModify(SystemClock.now());
            /**
             * 因为加了内存缓存,这里的更新操作时间周期以数据库中设定的step为周期,防止了频繁更新数据库加锁影响性能
             * 这里有个小问题:因为表设计中有个表示当前最新序列值的字段current,但是current是实时更新的,所以这里没有同步current值
             */
            int effectRow = counterMapper.updateByPrimaryKey(counter);
            logger.debug("update数据库count对象" + counter.toString() + "\r\n");
            logger.debug("更新受影响行数" + effectRow);
            boolean isDate = (counter.getIsDate() == 1);
            String dateformatStr = counter.getDateFormat();
            sequence = new Sequence(name, pre, contentLength, start.get(), end, size, isDate, dateformatStr);
            SEQUENCE_HOLDER.put(name, sequence);
            logger.debug("end:" + SystemClock.now());
        } catch (Exception e) {
            logger.error("申请序列异常name[{}]", name, e);
        }
        return sequence;
    }

    /**
     * @throws
     * @Title: generateId
     * @Description: 按照一定规则生成固定长度Id, 生成的序列的长度>=20,即如果不足20位,在自增序列的前面补0;超过20位,则以实际长度为准
     * @paramta:@param sequence
     * @paramta:@return
     * @return:String
     */
    private String generateId(Sequence sequence) {
        StringBuilder genIdBuilder = new StringBuilder();
        String date = StringUtils.EMPTY;
        if (sequence.isDate) {
            // 对应数据库日期格式
            date = DateUtils.dateTime2Str(LocalDateTime.now(), sequence.dateFormat);
        }
        // 下一个值
        long value = sequence.nextValue();
        // 数据库中设置的长度
        int setLength = sequence.contentLength;
        // 生成元素长度
        int valueLength = String.valueOf(value).length();
        int preLength = 0;
        if (StringUtils.isNotBlank(sequence.pre)) {
            // 前缀长度
            preLength = sequence.pre.length();
        }
        int dateLength = date.length();
        // 剩余位数长度
        int remainLen = setLength - valueLength - preLength - dateLength;

        String valueStr;
        // 剩余补零位数
        if (remainLen > 0) {
            String format = "%0" + remainLen + "d";
            String fill = String.format(format, 0);
            valueStr = fill + value;
        } else {
            valueStr = String.valueOf(value);
        }
        // String genId = String.format("%s%s%s%s", sequence.pre, date, rand, valueStr);//
        // 前缀（prefix）+ 时间(date)位 + 自增序列,不足设定的长度,则补全  long最大（9223372036854775807,2^64 - 1）
        genIdBuilder.append(sequence.pre).append(date).append(valueStr);
        return genIdBuilder.toString();
    }

    private String generateSingleId(String name) {
        return name + System.currentTimeMillis() + StringUtils.remove(String.valueOf(UUID.randomUUID().hashCode()), "-");
    }

    private List<String> generateBatchId(String name, int size) {
        List<String> idList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            idList.add(generateSingleId(name));
        }
        return idList;
    }
}
