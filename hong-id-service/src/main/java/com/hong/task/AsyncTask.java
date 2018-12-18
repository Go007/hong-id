package com.hong.task;

import com.hong.common.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

/**
 * <br>基于Spring的异步任务调用执行类</br>
 * 使用了@Async的方法，会被当成是一个子线程，会在主线程执行完了之后执行
 */
@Component
public class AsyncTask {

    private static final Logger logger = LoggerFactory.getLogger(AsyncTask.class);

    /**
     * 在Sequence将要溢出时,异步更新t_system_counter的min和max
     *
     * @param paramMap
     */
    @Async
    public void updateSequence(Map<String,Object> paramMap) {
        logger.info("start AsyncTask.updateSequence at time:" + DateUtils.date2Str(LocalDate.now()));

    }

}
