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

id  system_name biz_name prefix  is_date     date_format  min     current max     is_loop  create_time  last_modify   step_size  length
27	 shop   	   pay	    08	   00000000001	yyyyMMdd	  1862155	null	  1863155	null     null	        1545069618786	1000	     20
29	 shop	       order	  02	   00000000001	yyyyMMdd	  21222	  null	  22222	  null     null	        1499674084201	1000	     20

CREATE TABLE `t_fn_repayment_jrn` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `amount` bigint(20) unsigned NOT NULL COMMENT '还款(订单)金额',
  `order_no` varchar(50) DEFAULT '' COMMENT '支付业务订单号',
  `pay_channel` varchar(20) DEFAULT '' COMMENT '支付类型',
  `source` varchar(20) DEFAULT '' COMMENT '来源',
  `repay_time` datetime(3) NOT NULL COMMENT '下单时间，还款时间',
  `pay_id` varchar(50) DEFAULT '' COMMENT '与第三方支付交互id',
  `succ_time` datetime(3) DEFAULT NULL COMMENT '支付成功回调时间',
  `state` varchar(10) DEFAULT '' COMMENT '状态',
  `id_person` varchar(20) DEFAULT '' COMMENT 'fn用户id',
  `product_type` varchar(10) DEFAULT '' COMMENT '合同类型',
  `bank_name` varchar(100) DEFAULT '' COMMENT '还款金额',
  `serial_number` varchar(50) DEFAULT '' COMMENT 'fn订单流水号',
  `submit_status` varchar(10) DEFAULT '' COMMENT 'submit反馈',
  `submit_message` varchar(200) DEFAULT '' COMMENT 'submit反馈',
  `gmt_create` datetime(3) DEFAULT NULL,
  `gmt_modified` datetime(3) DEFAULT NULL,
  `device_id` varchar(50) DEFAULT '' COMMENT '设备id',
  `bank_code` varchar(10) DEFAULT '' COMMENT '蜂鸟银行编码',
  `bank` varchar(50) DEFAULT '' COMMENT '银行名称',
  `bank_no` varchar(50) DEFAULT '' COMMENT '银行卡号',
  `bank_person` varchar(50) DEFAULT '' COMMENT '持卡人姓名',
  `id_credit` varchar(50) DEFAULT '' COMMENT '合同ID',
  `open_id` varchar(50) DEFAULT '' COMMENT '第三方open id',
  PRIMARY KEY (`id`),
  KEY `idx_t_fn_repayment_jrn_order_no` (`order_no`),
  KEY `idx_t_fn_repayment_jrn_pay_id` (`pay_id`),
  KEY `idx_t_fn_repayment_jrn_id_person` (`id_person`),
  KEY `idx_t_fn_repayment_jrn_serial_number` (`serial_number`),
  KEY `idx_t_fn_repayment_jrn_id_credit` (`id_credit`),
  KEY `idx_t_jrn_repay_time` (`repay_time`),
  KEY `idx_t_fn_repayment_jrn_repay_time` (`repay_time`)
) ENGINE=InnoDB AUTO_INCREMENT=3179707 DEFAULT CHARSET=utf8mb4 COMMENT='蜂鸟还款流水'
