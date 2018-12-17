package com.hong.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

public class Counter implements Serializable {

    private static final long serialVersionUID = 696544272440020775L;

    private Long id;

    private String systemName;

    private String bizName;

    private String prefix;

    private Integer isDate;

    private String dateFormat;

    private Long min;

    private Long current;

    private Long max;

    private Integer isLoop;

    private Long createTime;

    private Long lastModify;

    private Integer stepSize;

    private Integer length;

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBizName() {
        return bizName;
    }

    public void setBizName(String bizName) {
        this.bizName = bizName == null ? null : bizName.trim();
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix == null ? null : prefix.trim();
    }

    public Integer getIsDate() {
        return isDate;
    }

    public void setIsDate(Integer isDate) {
        this.isDate = isDate;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat == null ? null : dateFormat.trim();
    }

    public Long getMin() {
        return min;
    }

    public void setMin(Long min) {
        this.min = min;
    }

    public Long getCurrent() {
        return current;
    }

    public void setCurrent(Long current) {
        this.current = current;
    }

    public Long getMax() {
        return max;
    }

    public void setMax(Long max) {
        this.max = max;
    }

    public Integer getIsLoop() {
        return isLoop;
    }

    public void setIsLoop(Integer isLoop) {
        this.isLoop = isLoop;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getLastModify() {
        return lastModify;
    }

    public void setLastModify(Long lastModify) {
        this.lastModify = lastModify;
    }

    public Integer getStepSize() {
        return stepSize;
    }

    public void setStepSize(Integer stepSize) {
        this.stepSize = stepSize;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}