package com.hong.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @ClassName: Sequence
 * @Description: 定义序列
 * @author jhonRawls@gmail.com
 * @date Apr 22, 2017 9:00:13 PM
 */
public class Sequence implements Serializable {

	private static final long serialVersionUID = 3369286233086572759L;

	/**
	 * 系统名称
	 */
	public final String systemName;
	/**
	 * 对应的名称，是哪个序列
	 */
	public final String name;
	/**
	 * 前缀
	 */
	public final String pre;
	/**
	 * 生成的每个字符长度大小
	 */
	public final int contentLength;
	/**
	 * 步长
	 */
	public final int size;
	/**
	 * 是否包含时间
	 */
	public final boolean isDate;
	/**
	 * 日期格式
	 */
	public final String dateFormat;
	/**
	 * 最小值
	 */
	public volatile long start;
	/**
	 * 最大值
	 */
	public volatile long end;
	/***
	 * 当前值
	 */
	private AtomicLong current;

	public Sequence(String systemName, String name, String pre, int contentLength, long min, long max, int size,
                    boolean isDate, String dateFormat) {
		this.systemName = systemName;
		this.name = name;
		this.pre = pre;
		this.contentLength = contentLength;
		this.start = min;
		this.size = size;
		this.end = max;
		this.current = new AtomicLong(min);
		this.isDate = isDate;
		this.dateFormat = dateFormat;
	}

	public Sequence(String name, String pre, int contentLength, long min, long max, int size, boolean isDate,
                    String dateFormat) {
		this.systemName = "shop";
		this.name=name;
		this.pre=pre;
		this.contentLength=contentLength;
		this.start = min;
		this.size = size;
		this.end = max;
		this.current=new AtomicLong(min);
		this.isDate=isDate;
		this.dateFormat=dateFormat;
	}

	public long nextValue() {
		long value = current.getAndIncrement();
		if (value > end) {
			value = -1;
		}
		return value;
	}

	public long current() {
		return current.get();
	}

	public boolean isOverFlow() {
		return current.get() > end;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
