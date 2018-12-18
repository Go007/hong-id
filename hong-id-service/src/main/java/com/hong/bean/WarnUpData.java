package com.hong.bean;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class WarnUpData {
    /**
     * 保存序列（一个数据序列对应一个sequence）
     */
    public static final Map<String, Sequence> SEQUENCE_HOLDER = new HashMap<>();
}
