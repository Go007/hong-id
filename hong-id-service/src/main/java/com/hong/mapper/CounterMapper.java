package com.hong.mapper;

import com.hong.entity.Counter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CounterMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Counter record);

    int insertSelective(Counter record);

    Counter selectByPrimaryKey(Long id);
    
    Counter selectByName(String bizName);

    int updateByPrimaryKeySelective(Counter record);

    int updateByPrimaryKey(Counter record);

    Counter selectBySystemNameAndBizName(@Param("systemName") String systemName, @Param("bizName") String bizName);
}