package com.hong.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * <br>Spring Boot启动预加载数据</br>
 */
@Component
public class StartUpRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(StartUpRunner.class);

    @Override
    public void run(String... strings) throws Exception {
        logger.info("ID服务启动,预加载数据start...");

    }
}
