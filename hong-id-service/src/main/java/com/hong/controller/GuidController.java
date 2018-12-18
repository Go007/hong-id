package com.hong.controller;

import com.hong.common.bean.Result;
import com.hong.common.utils.SystemClock;
import com.hong.service.GuidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

@RestController
@RequestMapping("guid")
public class GuidController {

    @Autowired
    private GuidService guidService;

    @GetMapping("/getGuid")
    public Result getGuid() {
        Result result = guidService.getSingleId("pay");
        return result;
    }

    /**
     * 模拟并发请求对接口进行压测
     * @return
     */
    @GetMapping("/testGuidUnderConcurrency")
    public Result testGuidUnderConcurrency() {
        Result result = new Result();
        final CountDownLatch cdl0 = new CountDownLatch(100);
        final CountDownLatch cdl1 = new CountDownLatch(100);
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        Set<Object> idSet = new ConcurrentSkipListSet<>();
        long start = SystemClock.now();
        for (int i=0;i<100;i++){
            executorService.submit(() -> {
                try {
                    cdl0.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Object id = guidService.getSingleId("pay").getData();
                if (!idSet.contains(id)){
                    idSet.add(id);
                }else {
                    System.out.println("产生重复id了,此时idSet大小:" + idSet.size());
                    result.setMessage("idSet大小:" + idSet.size());
                    return;
                }
                cdl1.countDown();
            });
            cdl0.countDown();
        }
        try {
            cdl1.await();
            System.out.println("耗费时间:" + (SystemClock.now() - start) + "ms");
            executorService.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        result.setData(idSet);
        return result;
    }

    @GetMapping("/testGuidUnderConcurrency2")
    public Result testGuidUnderConcurrency2() {
        Result result = new Result();
        int clientTotal = 100;
        // 同时并发执行的线程数
        int threadTotal = 10;
        ExecutorService executorService = Executors.newCachedThreadPool();
        // 信号量,此处用于控制并发的线程数
        final Semaphore semaphore = new Semaphore(threadTotal);
        final CountDownLatch cdl = new CountDownLatch(clientTotal);
        for (int i=0;i<clientTotal;i++){
            executorService.execute(() -> {
                try {
                    /**
                     * 获取执行许可,当总计未释放的许可数不超过threadTotal时,
                     * 允许通行,否则现行阻塞等待,直到获取到许可
                     */
                    semaphore.acquire();

                    Object id = guidService.getSingleId("pay").getData();
                    System.out.println(id);

                    // 释放许可
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                cdl.countDown();
            });
        }

        try {
            cdl.await();
            executorService.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}
