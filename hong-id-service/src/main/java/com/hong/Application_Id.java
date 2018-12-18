package com.hong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync //开启异步任务调用
public class Application_Id
{
    public static void main( String[] args )
    {
        SpringApplication.run(Application_Id.class, args);
    }
}
