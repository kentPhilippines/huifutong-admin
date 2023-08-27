ALTER TABLE cp_alipay.alipay_withdraw ADD watingTime int(5) DEFAULT 240 NULL COMMENT '等待推送时间';
ALTER TABLE cp_alipay.alipay_withdraw ADD moreMacth int(5) DEFAULT 0 NULL COMMENT '0 不挂起，1挂起 就是停止操作';
ALTER TABLE cp_alipay.alipay_withdraw ADD dealDescribe text NULL COMMENT '备注';
