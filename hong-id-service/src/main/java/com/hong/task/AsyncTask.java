package com.hong.task;

import org.springframework.stereotype.Component;

/**
 * <br>基于Spring的异步任务调用执行类</br>
 * 使用了@Async的方法，会被当成是一个子线程，会在主线程执行完了之后执行
 */
@Component
public class AsyncTask {
}
