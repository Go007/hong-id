package com.hong.bean;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WarnUpData {
    /**
     * 预先缓存下一个序列（一个数据序列对应一个sequence）
     */
    public static final Map<String, Sequence> NEXT_SEQUENCE_HOLDER = new ConcurrentHashMap<>();
}
