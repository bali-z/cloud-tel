-- 会议表
drop table if exists sys_meeting_data;
CREATE TABLE `sys_meeting_data` (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                    `meeting_id` varchar(20) DEFAULT NULL COMMENT '会议唯一id',
                                    `title` varchar(1024) DEFAULT NULL COMMENT '会议标题',
                                    `user_id` bigint(20) DEFAULT NULL COMMENT '会议发起用户',
                                    `password` varchar(100) DEFAULT NULL COMMENT '会议密码，密文',
                                    `start_time` datetime DEFAULT NULL COMMENT '会议开始时间',
                                    `end_time` datetime DEFAULT NULL COMMENT '会议结束时间',
                                    `is_restricted` char(1) DEFAULT NULL COMMENT '非参会人员是否限制参会',
                                    `attendees` text COMMENT '参会人员',
                                    `attendees_name` longtext COMMENT '参会人员姓名',
                                    `create_time` datetime DEFAULT NULL COMMENT '会议创建时间',
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

-- 消息表
drop table if exists sys_meeting_message;
CREATE TABLE `sys_meeting_message` (
                                       `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                       `send_uid` bigint(20) DEFAULT NULL COMMENT '发送消息用户id',
                                       `recv_uid` bigint(20) DEFAULT NULL COMMENT '接受消息用户id',
                                       `meeting_id` varchar(20) DEFAULT NULL COMMENT '会议meeting_id',
                                       `meeting_pid` bigint(20) DEFAULT NULL COMMENT '会议的主键id',
                                       `send_time` datetime DEFAULT NULL COMMENT '消息发送时间',
                                       `content` text COMMENT '消息内容',
                                       `status` int(11) DEFAULT NULL COMMENT '消息状态',
                                       PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


INSERT INTO `ry-cloud`.`sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2006, '即时通讯', 0, 0, 'rtc', NULL, NULL, 1, 0, 'M', '0', '0', NULL, 'email', 'admin', '2025-01-18 23:47:16', '', NULL, '');
INSERT INTO `ry-cloud`.`sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2005, '会议管理导出', 2000, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'rtc:meeting:export', '#', 'admin', '2025-01-18 22:57:34', '', NULL, '');
INSERT INTO `ry-cloud`.`sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2004, '会议管理删除', 2000, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'rtc:meeting:remove', '#', 'admin', '2025-01-18 22:57:34', '', NULL, '');
INSERT INTO `ry-cloud`.`sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2003, '会议管理修改', 2000, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'rtc:meeting:edit', '#', 'admin', '2025-01-18 22:57:34', '', NULL, '');
INSERT INTO `ry-cloud`.`sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2002, '会议管理新增', 2000, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'rtc:meeting:add', '#', 'admin', '2025-01-18 22:57:34', '', NULL, '');
INSERT INTO `ry-cloud`.`sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2001, '会议管理查询', 2000, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'rtc:meeting:query', '#', 'admin', '2025-01-18 22:57:34', '', NULL, '');
INSERT INTO `ry-cloud`.`sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2000, '会议管理', 2006, 1, 'meeting', 'rtc/meeting/index', NULL, 1, 0, 'C', '0', '0', 'rtc:meeting:list', '#', 'admin', '2025-01-18 22:57:34', 'admin', '2025-01-18 23:47:51', '会议管理菜单');
