package com.hong;

import com.hong.common.bean.Result;
import com.hong.service.GuidService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class AppTest {
    @Autowired
    private GuidService guidService;

    @Test
    public void testGuid() {
        Result pay = guidService.getSingleId("pay");
        System.out.println(pay.getData());
    }

    @Test
    public void testGuidUnderConcurrency() {
        final CountDownLatch cdl0 = new CountDownLatch(100);
        final CountDownLatch cdl1 = new CountDownLatch(100);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Set<Object> idSet = new HashSet<>();
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
                    return;
                }

                cdl1.countDown();
            });
            cdl0.countDown();
        }
        try {
            cdl1.await();
            executorService.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}