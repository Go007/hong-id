CREATE TABLE `t_system_counter` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `system_name` varchar(50) DEFAULT NULL COMMENT '系统名称',
  `biz_name` varchar(50) DEFAULT NULL COMMENT '业务编码名称',
  `prefix` varchar(10) DEFAULT NULL COMMENT '前缀,两位作为标识：如02、23、等',
  `is_date` int(11) unsigned zerofill DEFAULT NULL COMMENT '包含日期 0不包含 1包含',
  `date_format` varchar(20) DEFAULT NULL COMMENT '日期格式',
  `min` bigint(20) DEFAULT NULL COMMENT '起始数',
  `current` bigint(20) DEFAULT NULL COMMENT '当前数',
  `max` bigint(20) DEFAULT NULL COMMENT '最大数',
  `is_loop` int(11) DEFAULT NULL COMMENT '是否循环',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `last_modify` bigint(20) DEFAULT NULL COMMENT '更新时间',
  `step_size` int(11) DEFAULT NULL COMMENT '步长',
  `length` int(11) DEFAULT NULL COMMENT '生成序列长度',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_t_system_counter_system_name_biz_name` (`biz_name`,`system_name`),
  KEY `idx_t_system_counter_last_modify` (`last_modify`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COMMENT='单号生成器'
