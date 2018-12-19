package com.hong.task;

import com.alibaba.fastjson.JSON;
import com.hong.bean.Sequence;
import com.hong.bean.WarnUpData;
import com.hong.common.utils.DateUtils;
import com.hong.common.utils.SystemClock;
import com.hong.entity.Counter;
import com.hong.mapper.CounterMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <br>基于Spring的异步任务调用执行类</br>
 * 使用了@Async的方法，会被当成是一个子线程，会在主线程执行完了之后执行
 */
@Component
public class AsyncTask {

    private static final Logger logger = LoggerFactory.getLogger(AsyncTask.class);

    @Autowired
    private CounterMapper counterMapper;

    /**
     * 在当前Sequence消耗到DEFAULT_LOAD_FACTOR时,
     * 开启异步任务,另起一个更新线程提前缓存下一个Sequence
     */
    @Async
    public void loadNextSequence(Sequence sequence) {
        logger.info("start AsyncTask.loadNextSequence at time [{}],param [{}]", DateUtils.date2Str(LocalDate.now()), JSON.toJSONString(sequence));
        Long start = sequence.end + 1;
        long end = start + sequence.size;
        Sequence nextSequence = new Sequence(sequence.name, sequence.pre, sequence.contentLength, start, end, sequence.size, sequence.isDate, sequence.dateFormat);
        WarnUpData.NEXT_SEQUENCE_HOLDER.put(sequence.systemName+ "#" + sequence.name,nextSequence);
    }

    @Async
    public void updateCounter(String systemName,String bizName){
        logger.info("start AsyncTask.loadNextSequence at time [{}]", DateUtils.date2Str(LocalDate.now()));
        Counter counter = counterMapper.selectBySystemNameAndBizName(systemName, bizName);
        logger.info("查询数据库counter对象:" + counter.toString() + "\r\n");
        AtomicReference<Long> start = new AtomicReference<>(counter.getMax() + 1);
        counter.setMin(start.get());
        int size = counter.getStepSize();
        Long end = start.get() + size;
        counter.setMax(end);
        counter.setLastModify(SystemClock.now());
        try {
            counterMapper.updateByPrimaryKey(counter);
        } catch (Exception e) {
            logger.error("申请序列异常systemName:[{}],bizName:[{}]", systemName,bizName, e);
            // 发送邮件通知
            // sendMail();
        }
    }

}
