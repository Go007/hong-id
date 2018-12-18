package com.hong.service;

import com.hong.bean.Sequence;
import com.hong.common.utils.ObjectUtils;
import com.hong.entity.Counter;
import com.hong.mapper.CounterMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <br>Spring Boot启动预加载数据</br>
 */
@Component
public class StartUpRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(StartUpRunner.class);

    @Autowired
    private CounterMapper counterMapper;

    public static final Map<String, Sequence> SEQUENCE_HOLDER = new HashMap<>();

    @Override
    public void run(String... strings) throws Exception {
        logger.info("ID服务启动,预加载数据start...");
        List<Counter> counterList = counterMapper.getAll();
        Sequence sequence = null;
        for (Counter counter:counterList){
            boolean flag = ObjectUtils.areNotEmptyOrNull(counter.getLength(), counter.getMin(), counter.getMax(), counter.getStepSize());
            if (!flag){
                continue;
            }
            boolean isDate = (counter.getIsDate() == null ? true:counter.getIsDate() == 1);
            sequence = new Sequence(counter.getBizName(), counter.getPrefix(), counter.getLength(), counter.getMin(), counter.getMax(), counter.getStepSize(), isDate, counter.getDateFormat());
            SEQUENCE_HOLDER.put(counter.getBizName(),sequence);
        }
    }
}
