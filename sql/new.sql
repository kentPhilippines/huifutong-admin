INSERT INTO sys_menu
(menu_id, menu_name, parent_id, order_num, url, target, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(2302, '卡商历史数据', 2007, 99, '/alipay/fund/bak?userType=2', 'menuItem', 'C', '0', NULL, 'fa fa-bar-chart', 'admin', '2022-06-21 21:23:05', '', NULL, '');
INSERT INTO sys_menu
(menu_id, menu_name, parent_id, order_num, url, target, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES(2303, '商户历史数据', 2006, 99, '/alipay/fund/bak?userType=1', 'menuItem', 'C', '0', NULL, '#', 'admin', '2022-06-21 21:23:33', '', NULL, '');




ALTER TABLE alipay_medium ADD maxAmount decimal(19,6) NULL;
ALTER TABLE alipay_medium MODIFY COLUMN maxAmount decimal(19,6) DEFAULT 0 NULL;

--催单
ALTER TABLE alipay_deal_order ADD urge INT UNSIGNED DEFAULT 0 NULL COMMENT '催单，1是催单，0是正常';
--单卡冷却时间
ALTER TABLE alipay_medium ADD coolDownTime INT UNSIGNED DEFAULT 30 NOT NULL COMMENT '冷却时间30秒内只可接一单';

