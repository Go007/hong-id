package com.hong.service;

import com.hong.common.bean.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <br>分布式全局唯一ID生成器接口</br>
 */
public interface GuidService {
    /**
     * 申请单个ID
     * @param name,具体服务名称，name可以考虑枚举变量
     * @return
     */
    @RequestMapping(value = "/id/nextValue", method = RequestMethod.GET)
    Result getSingleId(@RequestParam("name") String name);

    /**
     * 申请一批ID
     * @param name
     * @param size
     * @return
     */
    @RequestMapping(value = "/id/nextRange", method = RequestMethod.GET)
    Result getBatchId(@RequestParam("name") String name, @RequestParam("size") int size);
}
