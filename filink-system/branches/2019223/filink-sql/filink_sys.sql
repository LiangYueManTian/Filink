/*
Navicat MySQL Data Transfer

Source Server         : 18
Source Server Version : 50544
Source Host           : 10.5.43.18:3306
Source Database       : filink_sys

Target Server Type    : MYSQL
Target Server Version : 50544
File Encoding         : 65001

Date: 2019-02-21 14:42:00
*/

SET FOREIGN_KEY_CHECKS=0;

drop DATABASE filink_sys;
CREATE DATABASE filink_sys;
-- ----------------------------
-- Table structure for device_protocol
-- ----------------------------
DROP TABLE IF EXISTS `device_protocol`;
CREATE TABLE `device_protocol` (
  `protocol_id` varchar(50) NOT NULL COMMENT '设施协议ID（UUID）',
  `protocol_name` varchar(50) NOT NULL COMMENT '设施协议文件名称',
  `file_name` varchar(50) NOT NULL COMMENT '设施协议文件名称 ',
  `file_length` varchar(50) NOT NULL COMMENT '设施协议文件长度',
  `file_download_url` varchar(150) NOT NULL COMMENT '文件下载路径',
  `hardware_version` varchar(50) NOT NULL COMMENT '硬件版本',
  `software_version` varchar(50) NOT NULL COMMENT '软件版本',
  `is_deleted` varchar(2) DEFAULT '0' COMMENT '是否删除（0未删除 1已删除）',
  `create_user` varchar(20) DEFAULT NULL COMMENT '创建人',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(20) DEFAULT NULL COMMENT '更新人',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`protocol_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of device_protocol
-- ----------------------------
INSERT INTO `device_protocol` VALUES ('05bbfdea2a804ef2992c125a8b19b9da', 'test111', 'newLock.xml', '28517', 'http://10.5.24.142:80/group1/M00/00/01/CgUYjlxuCt2AamVUAABvZRcU9tE41.BACK', '1.6.6', '1.6.1', '1', '403600', '2019-02-19 09:38:48', null, '2019-02-21 10:01:11');
INSERT INTO `device_protocol` VALUES ('16e857d6f511443eb74c806903d0bae9', 'sadfswad', 'newLock - ?? (4).xml', '28515', 'http://10.5.24.142:80/group1/M00/00/01/CgUYjlxuCSmANJ7EAABvYxV2STU41.BACK', '1.7.3', '1.7', '1', null, '2019-02-21 09:49:56', null, '2019-02-21 09:53:54');
INSERT INTO `device_protocol` VALUES ('42710bf076b64c768971718e2fdb8ab8', 'qqq', 'xml.xml', '115', 'http://10.5.24.142:80/group1/M00/00/01/CgUYjlxSzPOAa2AIAAAAc8GSJDo32.BACK', '1231112', '1234rrr', '1', 'admin', '2019-01-31 18:04:58', 'admin', '2019-01-31 18:18:00');
INSERT INTO `device_protocol` VALUES ('47682063ba98453b86705258b455ff7c', 'trtt', 'xml.xml', '119', 'http://10.5.24.142:80/group1/M00/00/01/CgUYjlxSzRCANIaRAAAAd7LdXMM74.BACK', '1231112', '1234rreerrr', '1', 'admin', '2019-01-31 18:06:10', 'admin', '2019-01-31 18:18:28');
INSERT INTO `device_protocol` VALUES ('49e10fe6e8094d588837202a2aab3f58', '设施3', 'newLock.xml', '28518', 'http://10.5.24.142:80/group1/M00/00/01/CgUYjlxtUHWAMIlIAABvZr3rlz4523.xml', '1.6.6', '1.6.42', '0', null, '2019-02-19 16:46:15', '1', '2019-02-20 20:45:52');
INSERT INTO `device_protocol` VALUES ('4a86617c38154486ab709804ddafdf9e', '121212', 'newLock.xml', '28517', 'http://10.5.24.142:80/group1/M00/00/01/CgUYjlxqXu2AZW1CAABvZTGiZjg18.BACK', '1.6.6', '1.6.4', '1', null, '2019-02-15 17:58:09', '403600', '2019-02-18 15:10:56');
INSERT INTO `device_protocol` VALUES ('4b02dfbbe60c4297830b1ba03a525680', '2222', 'xml.xml', '116', 'http://10.5.24.142:80/group1/M00/00/01/CgUYjlxSzYKAKZJnAAAAdPQgedg08.BACK', '1231112', '1234rrrr', '1', 'admin', '2019-01-31 18:04:13', 'admin', '2019-01-31 18:20:22');
INSERT INTO `device_protocol` VALUES ('4c80d372bf7241089ac47781f3cc3e39', 'sssss', 'newLock.xml', '28515', 'http://10.5.24.142:80/group1/M00/00/01/CgUYjlxqXy6AHrAmAABvY97aA6g87.BACK', '1.6.6', '1.6', '1', null, '2019-02-15 18:04:04', '403600', '2019-02-18 15:12:01');
INSERT INTO `device_protocol` VALUES ('5bd3b1f7246b11e9af3528b2bd321cab', '设施1', 'protocol - 副本.xml', '115', 'http://10.5.24.142:80/group1/M00/00/01/CgUYjlxSzYKAYqLcAAAAcyLnjvg37.BACK', '1.7.3', '1.6.4', '1', 'admin', '2019-01-30 16:45:13', 'admin', '2019-01-31 18:20:22');
INSERT INTO `device_protocol` VALUES ('5f11b436a5544b619e4d189ffd122272', 'sdasadsad', 'newLock.xml', '28519', 'http://10.5.24.142:80/group1/M00/00/01/CgUYjlxtUE-AcL0FAABvZxv6EgU33.BACK', '1.6.6', '1.6.4.5', '1', '403600', '2019-02-19 16:31:34', '1', '2019-02-20 20:45:15');
INSERT INTO `device_protocol` VALUES ('acc3d00a48dd41f7b1ca898dc01db39e', 'aaaaa', 'newLock - ?? (3).xml', '28516', 'http://10.5.24.142:80/group1/M00/00/01/CgUYjlxuDDaARtKsAABvZGSmXPo25.BACK', '1.7.2', '1.72', '1', null, '2019-02-21 09:51:26', null, '2019-02-21 10:06:55');
INSERT INTO `device_protocol` VALUES ('b713048eab7c497f83d0e4753df46f24', '1212', 'xml.xml', '118', 'http://10.5.24.142:80/group1/M00/00/01/CgUYjlxrZmqAXFOBAAAAdlUJRpU88.BACK', '1231112', '1234rreerr', '1', 'admin', '2019-01-31 18:08:27', '403600', '2019-02-19 09:55:07');
INSERT INTO `device_protocol` VALUES ('bc6d93855bc04852ac50477adf2ad7e8', '设施34333', 'newLock - 副本.xml', '28513', 'http://10.5.24.142:80/group1/M00/00/01/CgUYjlxuBICAL5ZiAABvYazatk8758.xml', '1.7', '1.7', '0', null, '2019-02-21 09:34:01', '1', '2019-02-21 13:42:09');
INSERT INTO `device_protocol` VALUES ('cb2cbba0867d49e4b89db26c4f8cfdc6', 'aaaaa', 'newLock - ?? (3).xml', '28516', 'http://10.5.24.142:80/group1/M00/00/02/CgUYjlxuFT-AQCIvAABvZGSmXPo79.BACK', '1.7.2', '1.72', '1', null, '2019-02-21 10:17:07', '1', '2019-02-21 10:45:29');
INSERT INTO `device_protocol` VALUES ('d8b924382d1d4153bf6d7af4fdba35c0', '设施34444', 'newLock - 副本 (2).xml', '28515', 'http://10.5.24.142:80/group1/M00/00/02/CgUYjlxuPp-AU76BAABvY7CNILw28.BACK', '1.7.1', '1.7', '1', null, '2019-02-21 10:14:20', '1', '2019-02-21 13:42:01');
INSERT INTO `device_protocol` VALUES ('d8e9bb8f6f8242c3b2a25fb5954c2105', 'ssssss', 'protocol.xml', '115', 'http://10.5.24.142:80/group1/M00/00/01/CgUYjlxtT8KAAAS3AAAAc2KJ2u478.BACK', '1.7.7', '1.6.4', '1', 'admin', '2019-02-01 15:11:44', '1', '2019-02-20 20:42:53');
INSERT INTO `device_protocol` VALUES ('db59cc4cc4774245b9de37e5a3c46178', 'filink', 'newLock.xml', '28933', 'http://10.5.24.142:80/group1/M00/00/01/CgUYjlxtQeaAe6cnAABxBbbSsJA037.xml', 'stm32L4-v001', 'RP9003.002F.bin', '0', '1', '2019-02-20 19:43:46', null, null);
INSERT INTO `device_protocol` VALUES ('e75eefc3bbb74c45bfb55a08afaa6453', '11111', 'xml.xml', '116', 'http://10.5.24.142:80/group1/M00/00/01/CgUYjlxtTvaAfO1oAAAAdCgr4Cs23.BACK', '1231111', '1234rrrr', '1', 'admin', '2019-01-31 18:03:26', '1', '2019-02-20 20:39:29');
INSERT INTO `device_protocol` VALUES ('f7a8ba3bea884f06962344b74b244a7d', '设施11', 'newLock.xml', '28728', 'http://10.5.24.142:80/group1/M00/00/01/CgUYjlxrxGmARimNAABwOEY9vtA482.xml', '1.6.6', '1.6', '0', null, '2019-02-19 09:23:33', '403600', '2019-02-19 16:36:08');

-- ----------------------------
-- Table structure for function_danger_level_config
-- ----------------------------
DROP TABLE IF EXISTS `function_danger_level_config`;
CREATE TABLE `function_danger_level_config` (
  `function_code` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '功能编码',
  `function_name` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '功能名称',
  `danger_level` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '危险级别',
  PRIMARY KEY (`function_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC COMMENT='功能危险级别配置表';

-- ----------------------------
-- Records of function_danger_level_config
-- ----------------------------
INSERT INTO `function_danger_level_config` VALUES ('1301101', '新增区域', 'prompt');
INSERT INTO `function_danger_level_config` VALUES ('1301102', '修改区域', 'general');
INSERT INTO `function_danger_level_config` VALUES ('1301103', '删除区域', 'danger');
INSERT INTO `function_danger_level_config` VALUES ('1301104', '关联设施', 'general');
INSERT INTO `function_danger_level_config` VALUES ('1301201', '新增设施', 'general');
INSERT INTO `function_danger_level_config` VALUES ('1301202', '修改设施', 'danger');
INSERT INTO `function_danger_level_config` VALUES ('1301203', '删除设施', 'danger');
INSERT INTO `function_danger_level_config` VALUES ('1501101', '新增用户', 'prompt');
INSERT INTO `function_danger_level_config` VALUES ('1501102', '修改用户', 'general');
INSERT INTO `function_danger_level_config` VALUES ('1501103', '删除用户', 'danger');
INSERT INTO `function_danger_level_config` VALUES ('1501104', '修改密码', 'general');
INSERT INTO `function_danger_level_config` VALUES ('1501105', '重置密码', 'general');
INSERT INTO `function_danger_level_config` VALUES ('1501106', '强制下线', 'general');
INSERT INTO `function_danger_level_config` VALUES ('1502101', '新增角色', 'prompt');
INSERT INTO `function_danger_level_config` VALUES ('1502102', '修改角色', 'general');
INSERT INTO `function_danger_level_config` VALUES ('1502103', '删除角色', 'danger');
INSERT INTO `function_danger_level_config` VALUES ('1503101', '新增部门', 'prompt');
INSERT INTO `function_danger_level_config` VALUES ('1503102', '修改部门', 'general');
INSERT INTO `function_danger_level_config` VALUES ('1503103', '删除部门', 'danger');
INSERT INTO `function_danger_level_config` VALUES ('2101101', '新增菜单模板', 'prompt');
INSERT INTO `function_danger_level_config` VALUES ('2101102', '修改菜单模板', 'general');
INSERT INTO `function_danger_level_config` VALUES ('2101103', '删除菜单模板', 'danger');
INSERT INTO `function_danger_level_config` VALUES ('2102101', '新增设施协议', 'prompt');
INSERT INTO `function_danger_level_config` VALUES ('2102102', '修改设施协议', 'general');
INSERT INTO `function_danger_level_config` VALUES ('2102103', '删除设施协议', 'danger');

-- ----------------------------
-- Table structure for ip_range
-- ----------------------------
DROP TABLE IF EXISTS `ip_range`;
CREATE TABLE `ip_range` (
  `range_id` varchar(50) NOT NULL DEFAULT '' COMMENT '主键ID',
  `range_code` varchar(50) NOT NULL COMMENT '编号',
  `ip_type` varchar(5) DEFAULT NULL COMMENT 'IP类型',
  `start_ip` varchar(50) DEFAULT NULL COMMENT '起始IP',
  `end_ip` varchar(50) DEFAULT NULL COMMENT '终止IP',
  `mask` varchar(20) DEFAULT NULL COMMENT '掩码',
  `status` varchar(2) DEFAULT '1' COMMENT '启用状态,1是启用，0是禁用',
  `is_deleted` varchar(2) DEFAULT '0' COMMENT '是否删除，0没有，1已删除',
  PRIMARY KEY (`range_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of ip_range
-- ----------------------------

-- ----------------------------
-- Table structure for license_info
-- ----------------------------
DROP TABLE IF EXISTS `license_info`;
CREATE TABLE `license_info` (
  `license_id` varchar(50) NOT NULL,
  `path` varchar(255) DEFAULT NULL,
  `is_default` varchar(2) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  `update_user` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`license_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of license_info
-- ----------------------------
INSERT INTO `license_info` VALUES ('1c78e80e-a1d7-403f-b3e0-abf0fcaef710', 'http://10.5.24.142:80/group1/M00/00/01/CgUYjlxr2U2Ac6ERAAAA_mQLgtE699.xml', '1', '2019-02-19 17:08:01', null, '2019-02-19 18:16:58', null);
INSERT INTO `license_info` VALUES ('32b6c30c-f0e8-4e3d-b7f9-13494b54d4ee', 'http://10.5.24.142:80/group1/M00/00/01/CgUYjlxrzSOAG5liAAAA_mmxuXs065.xml', '0', '2019-02-19 17:15:20', null, '2019-02-19 17:25:04', null);

-- ----------------------------
-- Table structure for menu_info
-- ----------------------------
DROP TABLE IF EXISTS `menu_info`;
CREATE TABLE `menu_info` (
  `menu_id` varchar(50) NOT NULL COMMENT '菜单明细id(UUID)',
  `menu_name` varchar(255) NOT NULL COMMENT '菜单名称',
  `menu_href` varchar(200) NOT NULL COMMENT '菜单指向（菜单请求路径）',
  `parent_menu_id` varchar(50) DEFAULT NULL COMMENT '父级菜单编码',
  `menu_level` int(11) NOT NULL COMMENT '菜单级别',
  `menu_sort` int(11) NOT NULL COMMENT '菜单排序',
  `image_url` varchar(200) DEFAULT NULL COMMENT '图片路径',
  `create_user` varchar(50) NOT NULL COMMENT '创建人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(50) DEFAULT '' COMMENT '更新人',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `is_show` varchar(1) DEFAULT '0',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of menu_info
-- ----------------------------
INSERT INTO `menu_info` VALUES ('', '', '', null, '0', '0', null, '', '2019-02-21 11:35:45', '', null, '1');
INSERT INTO `menu_info` VALUES ('01', '首页', '/business/index', null, '1', '0', 'icon-navigation-index', '', '2019-01-31 15:19:42', '', '2019-02-21 11:35:36', '1');
INSERT INTO `menu_info` VALUES ('02', '设施管理', 'xx', null, '1', '2', 'icon-navigation-facility', '朱琦琦', '2019-01-22 10:25:53', '', '2019-02-21 11:35:36', '1');
INSERT INTO `menu_info` VALUES ('02-1', '设施列表', '/business/facility/facility-list', '02', '2', '1', 'xx', '朱琦琦', '2019-01-22 10:28:12', '', '2019-02-21 11:35:36', '1');
INSERT INTO `menu_info` VALUES ('02-2', '设施日志', '/business/facility/facility-log', '02', '2', '5', 'xx', '', '2019-01-31 15:18:28', '', '2019-02-21 11:35:36', '1');
INSERT INTO `menu_info` VALUES ('02-6', '区域管理', '/business/facility/area-list', '02', '2', '6', 'xx', '朱琦琦', '2019-01-22 10:31:42', '', '2019-02-21 11:35:36', '1');
INSERT INTO `menu_info` VALUES ('04', '用户管理', 'xx', null, '1', '4', 'icon-navigation-user', '朱琦琦', '2019-01-31 14:38:14', '', '2019-02-21 11:35:36', '1');
INSERT INTO `menu_info` VALUES ('05', '告警管理 ', 'xx', null, '1', '5', ' icon-navigation-alarm', '朱琦琦', '2019-01-22 10:38:52', '', '2019-02-21 11:35:36', '1');
INSERT INTO `menu_info` VALUES ('06', '系统设置', 'xx', null, '1', '7', 'icon-navigation-system', '', '2019-01-31 14:42:51', '', '2019-02-21 11:35:36', '1');
INSERT INTO `menu_info` VALUES ('4-1', '用户管理', '/business/user/user-list', '04', '2', '1', 'xx', '朱琦琦', '2019-01-31 14:39:23', '', '2019-02-21 11:35:36', '1');
INSERT INTO `menu_info` VALUES ('4-2', '单位列表', '/business/user/unit-list', '04', '2', '2', 'xx', '', '2019-01-31 14:39:59', '', '2019-02-21 11:35:36', '1');
INSERT INTO `menu_info` VALUES ('4-3', '角色管理', '/business/user/role-list', '04', '2', '3', null, '', '2019-01-31 14:40:52', '', '2019-02-21 11:35:36', '1');
INSERT INTO `menu_info` VALUES ('4-4', '在线用户列表', '/business/user/online-list', '04', '2', '4', null, '', '2019-01-31 14:41:37', '', '2019-02-21 11:35:36', '1');
INSERT INTO `menu_info` VALUES ('5-1', '当前告警', '/business/alarm/current-alarm', '05', '2', '1', 'xx', '朱琦琦', '2019-01-22 10:39:51', '', '2019-02-21 11:35:36', '1');
INSERT INTO `menu_info` VALUES ('5-2', '历史告警', '/business/alarm/history-alarm', '05', '2', '2', 'xx', '朱琦琦', '2019-01-22 10:40:30', '', '2019-02-21 11:35:37', '1');
INSERT INTO `menu_info` VALUES ('5-3', '告警设置', 'xx', '05', '2', '3', 'xx', '朱琦琦', '2019-01-22 10:43:00', '', '2019-02-21 11:35:37', '1');
INSERT INTO `menu_info` VALUES ('5-3-1', '当前告警设置', '/business/alarm/current-alarm-set', '5-3', '3', '1', 'xx', '朱琦琦', '2019-01-31 14:35:28', '', '2019-02-21 11:35:37', '1');
INSERT INTO `menu_info` VALUES ('5-3-2', '历史告警设置', '/business/alarm/history-alarm-set', '5-3', '3', '2', 'xx', '朱琦琦', '2019-01-31 14:36:47', '', '2019-02-21 11:35:37', '1');
INSERT INTO `menu_info` VALUES ('6-1', '菜单管理', '/business/system/menu', '06', '2', '1', null, '', '2019-01-31 14:43:32', '', '2019-02-21 11:35:37', '1');
INSERT INTO `menu_info` VALUES ('6-2', '日志管理', 'xx', '06', '2', '3', 'xx', '', '2019-01-31 14:44:09', '', '2019-02-21 11:35:37', '1');
INSERT INTO `menu_info` VALUES ('6-2-1', '操作日志', '/business/system/log', '6-2', '3', '1', 'xx', '', '2019-01-31 14:44:49', '', '2019-02-21 11:35:37', '1');
INSERT INTO `menu_info` VALUES ('6-2-2', '系统日志', '/business/system/log/system', '6-2', '3', '2', 'xx', '', '2019-01-31 14:45:27', '', '2019-02-21 11:35:37', '1');
INSERT INTO `menu_info` VALUES ('6-2-3', '安全日志', '/business/system/log/security', '6-2', '3', '3', 'xx', '', '2019-01-31 14:45:55', '', '2019-02-21 11:35:37', '1');
INSERT INTO `menu_info` VALUES ('6-3', '协议管理', 'xx', '06', '2', '3', 'xx', '', '2019-01-31 14:46:41', '', '2019-02-21 11:35:37', '1');
INSERT INTO `menu_info` VALUES ('6-3-1', '设施协议', '/business/system/agreement/facility', '6-3', '3', '1', 'xx', '', '2019-01-31 14:47:17', '', '2019-02-21 11:35:37', '1');

-- ----------------------------
-- Table structure for menu_relation
-- ----------------------------
DROP TABLE IF EXISTS `menu_relation`;
CREATE TABLE `menu_relation` (
  `menu_relation_id` varchar(50) NOT NULL COMMENT '编号',
  `menu_template_id` varchar(50) NOT NULL COMMENT '菜单模板编号',
  `menu_id` varchar(50) NOT NULL COMMENT '菜单详情id',
  `menu_sort` int(11) NOT NULL COMMENT '菜单排序',
  `is_show` varchar(2) NOT NULL DEFAULT '1',
  `is_deleted` int(2) NOT NULL,
  PRIMARY KEY (`menu_relation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of menu_relation
-- ----------------------------
INSERT INTO `menu_relation` VALUES ('01294723af214b8096c70e88fde1e2dd', 'dc2c223e797a4c8da32f9cfb54ede54a', '4-4', '4', '0', '0');
INSERT INTO `menu_relation` VALUES ('090ed7668d074f51ad1104976e005575', 'ea2c976770fa4f22a5901b1cd5e13d6d', '5-2', '2', '1', '0');
INSERT INTO `menu_relation` VALUES ('09550d6fee844b9eb7f1f34a1e9a65c4', 'dc2c223e797a4c8da32f9cfb54ede54a', '6-1', '1', '0', '0');
INSERT INTO `menu_relation` VALUES ('0e0c18b9b80f4ceaba07f6eeed6dcdfe', 'ea2c976770fa4f22a5901b1cd5e13d6d', '02', '2', '1', '0');
INSERT INTO `menu_relation` VALUES ('0e4ecd215cba4e568148bf85121738ae', '08d9c12431e14153a290b5a64e2b7618', '6-2', '3', '1', '0');
INSERT INTO `menu_relation` VALUES ('11b5f207b8a947dda9fdcd71d63f08ad', 'dc2c223e797a4c8da32f9cfb54ede54a', '01', '0', '0', '0');
INSERT INTO `menu_relation` VALUES ('1674b24fbb6343d9b89e69f061f78d11', '08d9c12431e14153a290b5a64e2b7618', '02-2', '5', '1', '0');
INSERT INTO `menu_relation` VALUES ('171f94dd6c264cb4bd5aafc39a8576de', 'dc2c223e797a4c8da32f9cfb54ede54a', '6-2', '3', '0', '0');
INSERT INTO `menu_relation` VALUES ('20147dd6fa1f4c3999b69cdaab713853', 'c5b88e39a658492cac53521004792651', '6-2', '3', '1', '0');
INSERT INTO `menu_relation` VALUES ('2402d6974c1d4c1d83ee3db77c388f16', '08d9c12431e14153a290b5a64e2b7618', '6-1', '1', '1', '0');
INSERT INTO `menu_relation` VALUES ('26834d96cd26409da8b6c218172ae13a', '08d9c12431e14153a290b5a64e2b7618', '06', '7', '1', '0');
INSERT INTO `menu_relation` VALUES ('269aad467c9c4cd5a62a13c81c8d62fc', 'ea2c976770fa4f22a5901b1cd5e13d6d', '5-1', '1', '1', '0');
INSERT INTO `menu_relation` VALUES ('2c4a98560a354823a622bf448acdb93e', 'ea2c976770fa4f22a5901b1cd5e13d6d', '5-3-2', '2', '1', '0');
INSERT INTO `menu_relation` VALUES ('2c52edcf49ca464b89cf415d6a5a9cb9', 'dc2c223e797a4c8da32f9cfb54ede54a', '05', '5', '0', '0');
INSERT INTO `menu_relation` VALUES ('2cbefd5978af4af698fdeada165b3fe6', '08d9c12431e14153a290b5a64e2b7618', '6-3', '3', '1', '0');
INSERT INTO `menu_relation` VALUES ('2cd116b4cc3a4f369925aa2bb7c2128b', 'dc2c223e797a4c8da32f9cfb54ede54a', '6-3-1', '1', '0', '0');
INSERT INTO `menu_relation` VALUES ('2e9992294a8e4034988eab96403dc84c', 'ea2c976770fa4f22a5901b1cd5e13d6d', '02-1', '1', '1', '0');
INSERT INTO `menu_relation` VALUES ('2f8032e5add443ccac2b902b709c436e', '08d9c12431e14153a290b5a64e2b7618', '02-1', '1', '1', '0');
INSERT INTO `menu_relation` VALUES ('3063ad110b87421685218d2f779e6da8', 'dc2c223e797a4c8da32f9cfb54ede54a', '4-3', '3', '0', '0');
INSERT INTO `menu_relation` VALUES ('325335bd214a4443aaa7a402fd98ef59', 'dc2c223e797a4c8da32f9cfb54ede54a', '06', '7', '0', '0');
INSERT INTO `menu_relation` VALUES ('35717b9340eb4b0390e44f36ade4d6bd', '08d9c12431e14153a290b5a64e2b7618', '5-3-1', '1', '1', '0');
INSERT INTO `menu_relation` VALUES ('37d4c9e1c2ab4c30996ede9add41408e', 'ea2c976770fa4f22a5901b1cd5e13d6d', '4-1', '1', '1', '0');
INSERT INTO `menu_relation` VALUES ('43509d05dcbd42b3b2b1aa63070d1fcc', 'c5b88e39a658492cac53521004792651', '6-3', '3', '1', '0');
INSERT INTO `menu_relation` VALUES ('46b0f01158684add83bb1e97c1cc5844', 'ea2c976770fa4f22a5901b1cd5e13d6d', '4-3', '3', '1', '0');
INSERT INTO `menu_relation` VALUES ('47de98e376fc4689bc303af58afa5b8b', 'c5b88e39a658492cac53521004792651', '5-3-2', '2', '1', '0');
INSERT INTO `menu_relation` VALUES ('4a60dce8462c450ebdf4202619698087', 'c5b88e39a658492cac53521004792651', '6-1', '1', '1', '0');
INSERT INTO `menu_relation` VALUES ('4be50880d0864372ba9be638406239c0', '08d9c12431e14153a290b5a64e2b7618', '04', '4', '1', '0');
INSERT INTO `menu_relation` VALUES ('51197e97cad54639826e276a1862c48c', '08d9c12431e14153a290b5a64e2b7618', '4-1', '1', '1', '0');
INSERT INTO `menu_relation` VALUES ('569ac2d064ec4c298fff1510aeec9871', 'dc2c223e797a4c8da32f9cfb54ede54a', '4-2', '2', '0', '0');
INSERT INTO `menu_relation` VALUES ('56d20779dd8c4a8dbd9659e41693eb6e', '08d9c12431e14153a290b5a64e2b7618', '6-2-2', '2', '1', '0');
INSERT INTO `menu_relation` VALUES ('5d42e54c68754c9f885cb4eed7de57c5', 'dc2c223e797a4c8da32f9cfb54ede54a', '02-1', '1', '0', '0');
INSERT INTO `menu_relation` VALUES ('60855503a13e48668ff0b37abf72b938', 'c5b88e39a658492cac53521004792651', '6-2-1', '1', '1', '0');
INSERT INTO `menu_relation` VALUES ('641964b1b7c243f79e7968ab84a42a63', 'c5b88e39a658492cac53521004792651', '05', '5', '1', '0');
INSERT INTO `menu_relation` VALUES ('6edc87229251498db8160a5d44beb9af', 'ea2c976770fa4f22a5901b1cd5e13d6d', '02-6', '6', '1', '0');
INSERT INTO `menu_relation` VALUES ('71719a19809a48a8a49b98cf8b9d6fb0', 'dc2c223e797a4c8da32f9cfb54ede54a', '02-2', '5', '0', '0');
INSERT INTO `menu_relation` VALUES ('71fea7e3d4e04f9a8dce583ecf53f4b4', 'dc2c223e797a4c8da32f9cfb54ede54a', '04', '4', '0', '0');
INSERT INTO `menu_relation` VALUES ('75fe5b0b8e754c058ac207516e641a3f', 'ea2c976770fa4f22a5901b1cd5e13d6d', '6-2-2', '2', '1', '0');
INSERT INTO `menu_relation` VALUES ('79832c5674e943758e1b4d7fb0a69040', '08d9c12431e14153a290b5a64e2b7618', '5-1', '1', '1', '0');
INSERT INTO `menu_relation` VALUES ('7ae9962bcbea4354846a7c94ec7f7704', 'ea2c976770fa4f22a5901b1cd5e13d6d', '6-3', '3', '1', '0');
INSERT INTO `menu_relation` VALUES ('7c5b5809b92a403a92c6b492216a2cc8', '08d9c12431e14153a290b5a64e2b7618', '02-6', '6', '1', '0');
INSERT INTO `menu_relation` VALUES ('806f3d9e6be4422f974508cb227900bc', 'ea2c976770fa4f22a5901b1cd5e13d6d', '6-2-3', '3', '1', '0');
INSERT INTO `menu_relation` VALUES ('80976c35a2574e4aa0afc809c75a48aa', 'ea2c976770fa4f22a5901b1cd5e13d6d', '4-4', '4', '1', '0');
INSERT INTO `menu_relation` VALUES ('81b1b20c8ab3403bbfa8b12659b9d3aa', '08d9c12431e14153a290b5a64e2b7618', '4-4', '4', '1', '0');
INSERT INTO `menu_relation` VALUES ('87338fca794f4c69a16f3ef2072520f8', 'dc2c223e797a4c8da32f9cfb54ede54a', '6-3', '3', '0', '0');
INSERT INTO `menu_relation` VALUES ('88f70a7a4e9e43ab926d9d85af21f8c4', 'dc2c223e797a4c8da32f9cfb54ede54a', '02-6', '6', '0', '0');
INSERT INTO `menu_relation` VALUES ('8b70fbb63fdd4b8fa54f211363485a71', 'dc2c223e797a4c8da32f9cfb54ede54a', '5-3', '3', '0', '0');
INSERT INTO `menu_relation` VALUES ('8b959be715144d3fbd01a16db8387f2b', 'dc2c223e797a4c8da32f9cfb54ede54a', '5-1', '1', '0', '0');
INSERT INTO `menu_relation` VALUES ('90eaf3acb61740ac98698937a8bc9586', 'ea2c976770fa4f22a5901b1cd5e13d6d', '04', '4', '1', '0');
INSERT INTO `menu_relation` VALUES ('91380a27b7784b1b960be688ffaec59f', '08d9c12431e14153a290b5a64e2b7618', '6-2-3', '3', '1', '0');
INSERT INTO `menu_relation` VALUES ('925a3724d62b4c3aad0955dcc98607f6', 'ea2c976770fa4f22a5901b1cd5e13d6d', '6-2-1', '1', '1', '0');
INSERT INTO `menu_relation` VALUES ('95cd0ebab78344c394ee285ad89b210b', 'c5b88e39a658492cac53521004792651', '6-2-2', '2', '1', '0');
INSERT INTO `menu_relation` VALUES ('9879006acbe146dea2386aa9026ab0c2', 'dc2c223e797a4c8da32f9cfb54ede54a', '6-2-1', '1', '0', '0');
INSERT INTO `menu_relation` VALUES ('990949aa43f7444d98c14166f456dba5', 'ea2c976770fa4f22a5901b1cd5e13d6d', '5-3-1', '1', '1', '0');
INSERT INTO `menu_relation` VALUES ('994be72291274bba888c3fe45ed2162f', '08d9c12431e14153a290b5a64e2b7618', '4-3', '3', '1', '0');
INSERT INTO `menu_relation` VALUES ('9dd8bdafcb8843f3b50149fea6d027dc', 'dc2c223e797a4c8da32f9cfb54ede54a', '5-2', '2', '0', '0');
INSERT INTO `menu_relation` VALUES ('a4610811ffd44180b0ffbbc208a4710e', 'c5b88e39a658492cac53521004792651', '02', '2', '1', '0');
INSERT INTO `menu_relation` VALUES ('a6724b1b7ef4449fa1e9ba38a8e6ec99', 'c5b88e39a658492cac53521004792651', '02-1', '1', '1', '0');
INSERT INTO `menu_relation` VALUES ('a8a4f03cfa36490681c39fc521c7e990', '08d9c12431e14153a290b5a64e2b7618', '02', '2', '1', '0');
INSERT INTO `menu_relation` VALUES ('a9492339a9cf4c4c9bdbf931453c10a0', 'dc2c223e797a4c8da32f9cfb54ede54a', '5-3-1', '1', '0', '0');
INSERT INTO `menu_relation` VALUES ('aff32e3c7bf940a6b56d42ad1eaaa198', 'ea2c976770fa4f22a5901b1cd5e13d6d', '5-3', '3', '1', '0');
INSERT INTO `menu_relation` VALUES ('b0be1ff2f7d647519a25fb9634ace771', 'ea2c976770fa4f22a5901b1cd5e13d6d', '4-2', '2', '1', '0');
INSERT INTO `menu_relation` VALUES ('b557dcd6ae0d49f796036727572fd9fb', 'dc2c223e797a4c8da32f9cfb54ede54a', '02', '2', '0', '0');
INSERT INTO `menu_relation` VALUES ('b7c463798afd4c23b6a6ab2649d3e07e', '08d9c12431e14153a290b5a64e2b7618', '5-2', '2', '1', '0');
INSERT INTO `menu_relation` VALUES ('b866ec3ab9e344eb8abeee572f2b0c51', 'c5b88e39a658492cac53521004792651', '6-2-3', '3', '1', '0');
INSERT INTO `menu_relation` VALUES ('ba299c272b894aa4a2bd5c70bcaceec8', 'ea2c976770fa4f22a5901b1cd5e13d6d', '6-2', '3', '1', '0');
INSERT INTO `menu_relation` VALUES ('bae7b2e42efe4476a46518145fa3382e', '08d9c12431e14153a290b5a64e2b7618', '6-3-1', '1', '1', '0');
INSERT INTO `menu_relation` VALUES ('bc5ae004aed84126bbef96faa7f4a3ac', 'c5b88e39a658492cac53521004792651', '6-3-1', '1', '1', '0');
INSERT INTO `menu_relation` VALUES ('c04af7b81be64205a92c2c560c7a1aec', 'c5b88e39a658492cac53521004792651', '4-1', '1', '1', '0');
INSERT INTO `menu_relation` VALUES ('c1558dba585f430e912398444f082945', 'c5b88e39a658492cac53521004792651', '04', '4', '1', '0');
INSERT INTO `menu_relation` VALUES ('c490fce090064f8580e0579795fcb9ab', 'c5b88e39a658492cac53521004792651', '5-3', '3', '1', '0');
INSERT INTO `menu_relation` VALUES ('c4f2b8a01236429485aa71478852bc52', '08d9c12431e14153a290b5a64e2b7618', '01', '0', '1', '0');
INSERT INTO `menu_relation` VALUES ('c60dcaacfaeb4b8aa8b620a2927df665', 'dc2c223e797a4c8da32f9cfb54ede54a', '4-1', '1', '0', '0');
INSERT INTO `menu_relation` VALUES ('c66d03d2e4094c8a992c77e3839d698a', 'ea2c976770fa4f22a5901b1cd5e13d6d', '6-3-1', '1', '1', '0');
INSERT INTO `menu_relation` VALUES ('c7449e404d8f41bc9c4d53b0f75b595c', 'c5b88e39a658492cac53521004792651', '4-3', '3', '1', '0');
INSERT INTO `menu_relation` VALUES ('ca1cdd2626c54ac8ba229a12ddf78d56', '08d9c12431e14153a290b5a64e2b7618', '5-3-2', '2', '1', '0');
INSERT INTO `menu_relation` VALUES ('ca30aa291c1445e88792abf8ae4ec9f0', 'ea2c976770fa4f22a5901b1cd5e13d6d', '6-1', '1', '1', '0');
INSERT INTO `menu_relation` VALUES ('caa12e0d9c8f466f81228d774c3e9ec3', 'ea2c976770fa4f22a5901b1cd5e13d6d', '05', '5', '1', '0');
INSERT INTO `menu_relation` VALUES ('cbdb6f17474748ef98016b2b985c4226', 'c5b88e39a658492cac53521004792651', '4-2', '2', '1', '0');
INSERT INTO `menu_relation` VALUES ('cc7a474c49384fa28bec51d1199cd245', 'c5b88e39a658492cac53521004792651', '02-6', '6', '1', '0');
INSERT INTO `menu_relation` VALUES ('cfcb6304d33448359703a016142b73fc', '08d9c12431e14153a290b5a64e2b7618', '4-2', '2', '1', '0');
INSERT INTO `menu_relation` VALUES ('d048c8a7247a40c2830a636aede7e944', 'dc2c223e797a4c8da32f9cfb54ede54a', '6-2-3', '3', '0', '0');
INSERT INTO `menu_relation` VALUES ('d06e7de3054c40af938709e83b9f4e05', '08d9c12431e14153a290b5a64e2b7618', '6-2-1', '1', '1', '0');
INSERT INTO `menu_relation` VALUES ('d7a5624f9b694ffd8ec8032d833cee70', 'c5b88e39a658492cac53521004792651', '5-3-1', '1', '1', '0');
INSERT INTO `menu_relation` VALUES ('db53c42b4d0f4d94ad455e612f08cd65', 'c5b88e39a658492cac53521004792651', '4-4', '4', '1', '0');
INSERT INTO `menu_relation` VALUES ('e0a90dae45d14b0c851a548bfb2282e5', 'ea2c976770fa4f22a5901b1cd5e13d6d', '06', '7', '1', '0');
INSERT INTO `menu_relation` VALUES ('e0d1d7d56fc7441fb50410e394b9b60b', '08d9c12431e14153a290b5a64e2b7618', '05', '5', '1', '0');
INSERT INTO `menu_relation` VALUES ('e0fdce1d77a64090bba0fea18d9443d0', 'dc2c223e797a4c8da32f9cfb54ede54a', '6-2-2', '2', '0', '0');
INSERT INTO `menu_relation` VALUES ('e91275c899524cc5ba0b2619f9ec8744', 'c5b88e39a658492cac53521004792651', '06', '7', '1', '0');
INSERT INTO `menu_relation` VALUES ('f99f1b4babf14178960271e22a32d44b', '08d9c12431e14153a290b5a64e2b7618', '5-3', '3', '1', '0');
INSERT INTO `menu_relation` VALUES ('fb855f6bd0e24e60b82b65d13cdc6a33', 'c5b88e39a658492cac53521004792651', '5-1', '1', '1', '0');
INSERT INTO `menu_relation` VALUES ('fc4b6fdece4548889bad25524839ece8', 'dc2c223e797a4c8da32f9cfb54ede54a', '5-3-2', '2', '0', '0');
INSERT INTO `menu_relation` VALUES ('fdbb759b5c9844bf9268087d19611707', 'c5b88e39a658492cac53521004792651', '5-2', '2', '1', '0');

-- ----------------------------
-- Table structure for menu_template
-- ----------------------------
DROP TABLE IF EXISTS `menu_template`;
CREATE TABLE `menu_template` (
  `menu_template_id` varchar(50) NOT NULL COMMENT '菜单明细id(UUID)',
  `template_name` varchar(255) NOT NULL COMMENT '模板名称 ',
  `template_status` varchar(50) NOT NULL DEFAULT '0' COMMENT '模板的状态（状态为启用/禁用）',
  `remark` text COMMENT '备注',
  `version` int(11) NOT NULL DEFAULT '1' COMMENT '版本',
  `is_deleted` varchar(2) NOT NULL DEFAULT '0' COMMENT '是否删除  0 未删除 1 删除',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(50) DEFAULT NULL COMMENT '更新人',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`menu_template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of menu_template
-- ----------------------------
INSERT INTO `menu_template` VALUES ('01b243e526744ce08dcf69043642b346', 'test1111', '0', 'test111', '2', '1', null, '2019-01-29 16:09:43', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('0633284d75444f4cb63c43e15037a5d3', 'adsada', '0', null, '1', '1', null, '2019-01-29 17:39:49', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('06a1d43feefa43e5b10950483cc4b864', 'test信息', '0', 'remark', '1', '1', null, '2019-01-29 16:54:09', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('08d9c12431e14153a290b5a64e2b7618', '不要改', '1', '不要改', '1', '0', '403600', '2019-02-19 14:00:59', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('11f780f356864d6a8b84fd157c77b897', '日志测试', '0', '日志测试', '2', '1', '403600', '2019-02-18 16:16:07', '403600', '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('1894594318e6482db9d5b3df9b208f69', 'template menu1', '0', '测试', '2', '1', null, '2019-01-29 11:06:55', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('28ae89104819411e9f510eccc8834a29', '111111', '0', '1111', '2', '1', null, '2019-02-14 11:57:05', '403600', '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('3294265fb8414920bbea0be3f450c01e', 'kkkkkkkkk', '0', null, '1', '1', null, '2019-01-29 17:33:51', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('332eb7da3b4046fc8b96ed2a014213f3', 'dsadsadsadsa90', '0', null, '1', '1', null, '2019-02-18 17:29:46', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('381467f13c734cb5abf3abdc1278f16d', 'dsadsadsadsa5', '0', null, '1', '1', null, '2019-02-18 16:51:53', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('3ad69b54762a4547810c6e1649847e08', '测试模板1111', '0', '11111111', '2', '1', '1', '2019-02-21 11:30:38', '1', '2019-02-21 11:31:38');
INSERT INTO `menu_template` VALUES ('439b54c08ed449628c9f95d7a82f6c2c', '11111', '0', '1111111', '1', '1', null, '2019-01-31 14:04:07', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('458e10c39d9f4c9c971d9b7d608bd475', '测试菜单', '0', '修改测试', '14', '1', null, '2019-01-23 11:40:26', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('4bd8c5d2de19484c9b1718831926cf19', '12', '0', '111', '1', '1', '403600', '2019-02-19 10:20:31', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('5049837fd1ff4f2f8ea7f69e71f5d482', 'test11', '0', '12', '9', '1', null, '2019-01-23 14:09:34', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('58d613de265241f8a01f4b600c3a2794', 'test1111111', '0', '111111111', '1', '1', null, '2019-01-31 15:19:01', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('5af07e52c5ae4de7ae2cd42e1d5407cb', '朱琦琦', '0', null, '1', '1', null, '2019-02-19 10:35:58', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('5c53397f18584546ae1d36645b6b3f85', '新增菜单1', '0', '新增', '2', '1', null, '2019-01-31 15:20:36', null, '2019-02-21 11:31:38');
INSERT INTO `menu_template` VALUES ('6ed4a5ff9a2541caa3751f0b28192b17', '111111111', '0', '111111', '1', '1', null, '2019-02-01 15:12:44', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('71304235f1284d1a91ac14bb68194f58', 'dsadsad', '0', null, '1', '1', null, '2019-02-18 16:41:57', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('7338fc0ae2124d25820e0f873b88505c', 'dsadsadsadsa1', '0', null, '1', '1', null, '2019-02-18 16:45:18', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('9fb32a0c88d14642ae8b4f3646e0b69a', 'test002', '0', '新增模板', '4', '1', null, '2019-01-23 19:34:24', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('a11293b5439d41e8a3427f277071ee76', 'new template', '0', 'new template', '1', '1', null, '2019-01-31 15:10:03', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('a2faab26a1a64196a34fbbf1408de5d5', 'test', '0', '新增loading按钮测试', '1', '1', '403600', '2019-02-18 14:09:05', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('bc10b9a7986948319c6c11f95d53ca00', 'da', '0', null, '1', '1', null, '2019-01-29 17:33:07', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('bfc5bff5ba704796bd520279d343e5b8', 'dsadsa', '0', '212', '5', '1', null, '2019-01-29 17:35:24', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('c5b88e39a658492cac53521004792651', '正常模板111', '0', '正常模板', '1', '0', null, '2019-01-31 15:11:42', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('cbd6a04416b74f3784e546ebb8611e9f', 'test', '0', '111', '1', '1', null, '2019-01-26 14:43:18', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('ccb37fa7917d4df8b6a964b39ef673bf', '11', '0', null, '1', '1', '403600', '2019-02-18 16:06:10', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('ccb9e89baf624b2b821320b5e6beafce', '朱琦琦', '0', null, '1', '1', null, '2019-01-29 16:24:51', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('cf5b4a5d2cde40849daf716a27858877', 'uuuuuuuuuu', '0', null, '1', '1', null, '2019-01-29 17:34:23', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('d177727842a04788a1656ace262e87b2', 'dsadsadsadsa3', '0', null, '1', '0', null, '2019-02-18 16:48:07', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('d20bc7857ace4f7bb7546fc2217ec674', 'dsadsadsadsa2', '0', null, '1', '0', null, '2019-02-18 16:46:13', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('dc2c223e797a4c8da32f9cfb54ede54a', '23432324324', '0', null, '1', '0', null, '2019-02-19 10:34:46', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('dfc9b73172f14f5c86917e5bdda49583', 'test ', '0', '测试', '1', '1', null, '2019-01-24 14:57:56', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('e32273730b3149d68429bc151bca1e84', '魏鹤东', '0', null, '1', '1', null, '2019-01-29 16:52:20', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('e4154ce2fe714b45b66aeac1b839b5dd', '1', '0', '11111', '1', '1', '403600', '2019-02-18 15:49:18', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('e4fb1fa2f83e4f28826eff8d35326ab7', 'zhuqiqi', '0', null, '1', '1', null, '2019-02-19 09:35:45', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('e6732f245fd24034af1818848f92b99b', '111111111111', '0', '111111111111', '1', '1', null, '2019-01-31 15:14:27', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('e73cbff75392414f8e52234129d1400b', 'test1111', '0', null, '2', '1', null, '2019-01-29 17:51:38', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('ea2c976770fa4f22a5901b1cd5e13d6d', '正常模板', '0', '正常模板', '1', '0', null, '2019-01-31 14:48:33', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('ee7752f9402d475895a6dfeb1e626d61', 'zzzz', '0', null, '1', '1', null, '2019-01-29 17:30:07', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('f261c432a1a74a51a683db3155936cdb', 'templte', '0', 'templte', '1', '1', null, '2019-01-31 15:05:38', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('f984e2ba39744ddf8f718b313fd9fccf', 'dsadsadsadsa', '0', null, '1', '1', null, '2019-02-18 16:43:16', null, '2019-02-21 11:31:21');
INSERT INTO `menu_template` VALUES ('fad00947819940a0b992853c0ca64de9', 'aaa', '0', 'aaa', '1', '1', null, '2019-01-29 17:27:33', null, '2019-02-21 11:31:21');

-- ----------------------------
-- Table structure for protocol
-- ----------------------------
DROP TABLE IF EXISTS `protocol`;
CREATE TABLE `protocol` (
  `procotol_id` varchar(50) NOT NULL COMMENT '协议注解(uuid)',
  `procotol_name` varchar(50) DEFAULT NULL COMMENT '协议名称/类型',
  `procotol_value` varchar(255) DEFAULT NULL COMMENT '协议修改Json',
  `procotol_default_value` varchar(255) DEFAULT NULL COMMENT '协议默认Json',
  PRIMARY KEY (`procotol_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of protocol
-- ----------------------------
INSERT INTO `protocol` VALUES ('b1840da7-34e4-11e9-92ed-519d1fc57e29', 'http-server', '{\"ip\":\"10.5.33.11\",\"port\":\"11\",\"maxWait\":\"60s\",\"enabled\":\"0\",\"maxActive\":\"10\"}', '{\"ip\":\"10.5.33.1\",\"port\":\"13\",\"maxWait\":\"60s\",\"enabled\":\"0\",\"maxActive\":\"10\"}');
INSERT INTO `protocol` VALUES ('b1840f92-34e4-11e9-92ed-519d1fc57e29', 'http-client', '{\"ip\":\"10.5.33.21\",\"port\":\"21\",\"apikey\":\"api-info0.1\"}', '{\"ip\":\"10.5.33.21\",\"port\":\"23\",\"apikey\":\"api-info0.1\"}');
INSERT INTO `protocol` VALUES ('b1841041-34e4-11e9-92ed-519d1fc57e29', 'https-server', '{\"ip\":\"10.5.33.12\",\"port\":\"12\",\"maxWait\":\"60s\",\"enabled\":\"0\",\"maxActive\":\"10\",\"certificate\":{\"name\":\"name0.1\",\"url\":\"uri321\"}}', '{\"ip\":\"10.5.33.1\",\"port\":\"13\",\"maxWait\":\"60s\",\"enabled\":\"0\",\"maxActive\":\"10\",\"certificate\":{\"name\":\"name0.1\",\"url\":\"uri321\"}}');
INSERT INTO `protocol` VALUES ('b18410c2-34e4-11e9-92ed-519d1fc57e29', 'https-client', '{\"ip\":\"10.5.33.22\",\"port\":\"22\",\"apikey\":\"api-info0.1\",\"certificate\":{\"name\":\"012\",\"url\":\"uri\"}}', '{\"ip\":\"10.5.33.21\",\"port\":\"23\",\"apikey\":\"api-info0.1\"},\"certificate\":{\"name\":\"012\",\"url\":\"uri\"}}');
INSERT INTO `protocol` VALUES ('b1841146-34e4-11e9-92ed-519d1fc57e29', 'webservice-server', '{\"ip\":\"10.5.33.13\",\"port\":\"13\"},\"maxWait\":\"60s\"}', '{\"ip\":\"10.5.33.13\",\"port\":\"13\"},\"maxWait\":\"60s\"}');
INSERT INTO `protocol` VALUES ('b18411b2-34e4-11e9-92ed-519d1fc57e29', 'webservice-client', '{\"ip\":\"10.5.33.23\",\"port\":\"23\"},\"maxWait\":\"60s\"}', '{\"ip\":\"10.5.33.23\",\"port\":\"23\"},\"maxWait\":\"60s\"}');

-- ----------------------------
-- Table structure for security_strategy
-- ----------------------------
DROP TABLE IF EXISTS `security_strategy`;
CREATE TABLE `security_strategy` (
  `security_id` varchar(32) NOT NULL DEFAULT '' COMMENT '策略ID',
  `present` varchar(255) NOT NULL COMMENT '当前值',
  `tolerant` varchar(255) NOT NULL COMMENT '默认值',
  `strategy_type` varchar(2) NOT NULL COMMENT '策略类型（0账号，1密码）',
  PRIMARY KEY (`security_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of security_strategy
-- ----------------------------

-- ----------------------------
-- Table structure for test
-- ----------------------------
DROP TABLE IF EXISTS `test`;
CREATE TABLE `test` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `desc` varchar(255) DEFAULT NULL,
  `test1` varchar(255) DEFAULT NULL,
  `test2` varchar(255) DEFAULT NULL,
  `test3` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of test
-- ----------------------------
INSERT INTO `test` VALUES ('0088371639f04eab88959e405b9fab71', 'testName', 'this is a test data,the number is :90', '111', 'teset', 'teset');
INSERT INTO `test` VALUES ('0131b34ab8484b9292fe5c878e5cf1f5', 'testName', 'this is a test data,the number is :55', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('015ffbd8e3514641ac49cc40db936f1a', 'testName', 'this is a test data,the number is :57', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('01c59f624c4e418e9942177150cd28b4', 'testName', 'this is a test data,the number is :43', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('02213a03aaed4893b01f45f99f72a462', 'testName', 'this is a test data,the number is :0', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('02553d767e91490b9cdfea0fd9c99615', 'testName', 'this is a test data,the number is :48', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('02b79027656942aea382cb24ec4e3b9a', 'testName', 'this is a test data,the number is :69', '111', 'teset', 'teset');
INSERT INTO `test` VALUES ('049eb45f534e43e3a126e4de95b2a340', 'testName', 'this is a test data,the number is :18', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('04a1d5434cf2448fa1f9b2111a5dea82', 'testName', 'this is a test data,the number is :55', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('05e5844aadc44fcdaed8232ac771ddb8', 'testName', 'this is a test data,the number is :54', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('06065d875ec64cb8a396815a634adb66', 'testName', 'this is a test data,the number is :66', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('061d06a90ae6476ba54d1cedc69521fe', 'testName', 'this is a test data,the number is :85', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('06635cda75b845ebadc483ad8ef43f68', 'testName', 'this is a test data,the number is :6', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('069d264456554994be426bbc971879f7', 'testName', 'this is a test data,the number is :13', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('07475f05f45e481cb4c4d25f27519af2', 'testName', 'this is a test data,the number is :70', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('08f643a1ab3a4e3489dfb8312e7215aa', 'testName', 'this is a test data,the number is :75', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('09ceee85d20c4abeaa2136325cd6c729', 'testName', 'this is a test data,the number is :76', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('0a609bd820194c9296275ed3ab0059ca', 'testName', 'this is a test data,the number is :51', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('0b97ae29565c46a0952c15080ba8ad13', 'testName', 'this is a test data,the number is :44', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('0bd58c84623c48a49396dcec2b2e64b3', 'testName', 'this is a test data,the number is :98', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('0df3cbaf449d4d8bb34235bb67ee37d1', 'testName', 'this is a test data,the number is :80', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('0e4a10a1791d4461bf0370e52ac04550', 'testName', 'this is a test data,the number is :30', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('0fe75326c5be45f4ac57a5f8e766c809', 'testName', 'this is a test data,the number is :4', '111', 'teset', 'teset');
INSERT INTO `test` VALUES ('0fecf39cf8464856b1e5a11066eb2fcb', 'testName', 'this is a test data,the number is :56', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('1020a9c4f0884cde90e96e22cb283340', 'testName', 'this is a test data,the number is :25', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('10dc7578803c46d5a05d946fa9d8cf90', 'testName', 'this is a test data,the number is :48', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('10ecf68c245f4ca6a5c5f0f8f6226933', 'testName', 'this is a test data,the number is :13', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('11ba702eb550431db1b629d6b3742ecc', 'testName', 'this is a test data,the number is :9', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('12bf39a926844dce890a07b1b301a431', 'testName', 'this is a test data,the number is :46', '111', 'teset', 'teset');
INSERT INTO `test` VALUES ('144a4ddcea954157bd8accd7469cf6f0', 'testName', 'this is a test data,the number is :62', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('150e8be3715e46ea8eba965abda47191', 'testName', 'this is a test data,the number is :35', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('161bdf3d384a48b1925b5d7179d78a94', 'testName', 'this is a test data,the number is :80', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('179395b099d548528230607e1f4307b7', 'testName', 'this is a test data,the number is :16', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('17a6636cf1f9439d8adb2650f22cbdf7', 'testName', 'this is a test data,the number is :22', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('1834a3222c9d4340bd1077bdcd3fa931', 'testName', 'this is a test data,the number is :43', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('19bf64221e2b4c8dbe14af1be546fd75', 'testName', 'this is a test data,the number is :28', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('1adf8728c4e5420798e4b9e9c04cefd8', 'testName', 'this is a test data,the number is :51', '111', 'teset', 'teset');
INSERT INTO `test` VALUES ('1b064224c1dd45e0a9085edaf13d9566', 'testName', 'this is a test data,the number is :69', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('1b89f1c3efe7427491414b66341d35da', 'testName', 'this is a test data,the number is :55', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('1bd5cbc07d78492eaea06d34ec601891', 'testName', 'this is a test data,the number is :30', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('1bd945d291be4ddf856a84a81cb6d8f6', 'testName', 'this is a test data,the number is :9', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('1c0b61b02baa4f8e9ef6b9c42d5ef166', 'testName', 'this is a test data,the number is :9', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('1c2a1d4be7ef47e08a538c6dd92db326', 'testName', 'this is a test data,the number is :23', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('1c58452f35d3453baed85a32aa221038', 'testName', 'this is a test data,the number is :45', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('1f0e2350d1f14fdbbb42f3464c66742d', 'testName', 'this is a test data,the number is :38', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('1f5b5b0a2cda4c42b8aebd25f768dfa2', 'testName', 'this is a test data,the number is :42', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('1fac186f97fb476bab0f87bf7d53ba61', 'testName', 'this is a test data,the number is :37', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('203a67cf585140d39dee48a6d603794e', 'testName', 'this is a test data,the number is :44', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('203af0ef505c4156b0b2d74d9306c4b8', 'testName', 'this is a test data,the number is :6', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('2099ad6924bd47989b4c181495ec1e73', 'testName', 'this is a test data,the number is :84', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('21aa1dfc3e934976afcc06d77f3d5e45', 'testName', 'this is a test data,the number is :15', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('21e141612f604e65beb6f789798bcab8', 'testName', 'this is a test data,the number is :57', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('22a9d95a10494c488e22d81fe6aad40c', 'testName', 'this is a test data,the number is :82', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('22f567503f7f41da86a66f9894248b3b', 'testName', 'this is a test data,the number is :94', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('2314b2022bd3492fb6bcb3b4f56db569', 'testName', 'this is a test data,the number is :35', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('238f5960c78543ddb6961b7eccc8a4b8', 'testName', 'this is a test data,the number is :18', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('2436d58308a8449d97704fa5d92b7b32', 'testName', 'this is a test data,the number is :39', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('2458c3985a2a47fa8249d6f9a1df4bea', 'testName', 'this is a test data,the number is :57', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('2497b51a024f411384147ae2663747f6', 'testName', 'this is a test data,the number is :95', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('24bd49614f1b470684579fb3adc3ceee', 'testName', 'this is a test data,the number is :60', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('24bf60dc219540c39fb959bf74cc9ee0', 'testName', 'this is a test data,the number is :46', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('24ea9df3e5024c7f970c37d21551d47f', 'testName', 'this is a test data,the number is :47', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('25b2ab6ec13f420784763e6feec1bdf4', 'testName', 'this is a test data,the number is :41', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('25d921bc345d4175b536ce386d22a5d5', 'testName', 'this is a test data,the number is :47', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('2612958632524b13af9d1890b4f8e244', 'testName', 'this is a test data,the number is :95', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('26cb02e39fea4fa5bc95d9d3924364bb', 'testName', 'this is a test data,the number is :99', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('2762ce9c516e4ab1afe860ec8af59129', 'testName', 'this is a test data,the number is :96', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('29c3db85705144758a09a995ab3173b7', 'testName', 'this is a test data,the number is :20', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('2a6d395ce9784f4dacfc236c90fd4006', 'testName', 'this is a test data,the number is :23', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('2ba0208d28b14e22a8d54fa5b91cdd78', 'testName', 'this is a test data,the number is :15', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('2bcbf4386c194ad6b5671f819ad4c5d1', 'testName', 'this is a test data,the number is :46', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('2c59e4bd5e4f43a8a25d8e4c2bda9e4f', 'testName', 'this is a test data,the number is :37', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('2e82690bc69640b68f5a58f8f6a1bb44', 'testName', 'this is a test data,the number is :47', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('2f52d23d47334b75b97f6d7ab59cfc2c', 'testName', 'this is a test data,the number is :45', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('3005ab878a774471933ebb31dc211bb5', 'testName', 'this is a test data,the number is :53', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('31e2e2da64694c94a8ca1445cfa75a9e', 'testName', 'this is a test data,the number is :1', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('32254b9d098843e29a601a0f1ea8208e', 'testName', 'this is a test data,the number is :7', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('326a52e74cb247dcace42bce81bbee2c', 'testName', 'this is a test data,the number is :37', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('332f695d04304ffb8b0da70fb61809a4', 'testName', 'this is a test data,the number is :75', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('33dd1fc9b68d427ab281ec54a7788a4f', 'testName', 'this is a test data,the number is :58', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('36a113f1834f43339fd5dcb9ce9dea6d', 'testName', 'this is a test data,the number is :87', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('370e8c2601814e439897cfd25d846e8c', 'testName', 'this is a test data,the number is :12', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('3795b436cd904fc4b213bb4ce7265ed6', 'testName', 'this is a test data,the number is :34', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('38ea5327b4c8441bb0970f31b23a4318', 'testName', 'this is a test data,the number is :98', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('3b4ef59b5fde4755ab8d7e154608b8e1', 'testName', 'this is a test data,the number is :73', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('3b82e96702d241efad33825e8beb4fbb', 'testName', 'this is a test data,the number is :72', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('3b8763aeac6a4ed7b622a467bba757af', 'testName', 'this is a test data,the number is :82', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('3bd9387dfb584a15b8fbe7d461b24e83', 'testName', 'this is a test data,the number is :76', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('3de0185d02f54731a84d9346c24da177', 'testName', 'this is a test data,the number is :2', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('3e47889b3d4c475d92c3b027fa2f5ed0', 'testName', 'this is a test data,the number is :73', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('3ed5c00037164aa19bc4f5f7d9d39b83', 'testName', 'this is a test data,the number is :85', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('3f28f19fbaaf4559af6903e048a98088', 'testName', 'this is a test data,the number is :17', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('3f96e44d372540ad8337225928689764', 'testName', 'this is a test data,the number is :53', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('4016205d95214087a8397ab151aaa7c0', 'testName', 'this is a test data,the number is :61', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('40769f84271443089af78f4a85e68ef4', 'testName', 'this is a test data,the number is :70', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('40a54d247736426bb5558a05ba8fd15b', 'testName', 'this is a test data,the number is :65', '222', 'teset', 'teset');
INSERT INTO `test` VALUES ('40e0b4eaf8b7409a9a543a5d49524014', 'testName', 'this is a test data,the number is :60', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('41168b32439042ce969e7b0fd202876b', 'testName', 'this is a test data,the number is :93', '222', 'teset', 'teset');
INSERT INTO `test` VALUES ('4285ab68da9641fc9e500f2ae384c661', 'testName', 'this is a test data,the number is :42', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('43b0d979ebf24e6b8e699f2cb5616b64', 'testName', 'this is a test data,the number is :79', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('43c9561d3bbb46acb6245f2477596076', 'testName', 'this is a test data,the number is :16', '222', 'teset', 'teset');
INSERT INTO `test` VALUES ('44d8530f7a5249d4a3945f0f477a5226', 'testName', 'this is a test data,the number is :79', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('458e28d80cd84fe0b44de74c54a23cac', 'testName', 'this is a test data,the number is :3', '222', 'teset', 'teset');
INSERT INTO `test` VALUES ('4681940af89d421489641c1136babdfc', 'testName', 'this is a test data,the number is :48', '222', 'teset', 'teset');
INSERT INTO `test` VALUES ('469d1dbea9ac44cc93d05d2d321e707f', 'testName', 'this is a test data,the number is :24', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('4703683df39f48caa381ab1c5217ffb7', 'testName', 'this is a test data,the number is :89', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('4748074db9684349811901951356edd8', 'testName', 'this is a test data,the number is :61', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('478afe5ca13d41b98048d25ec4124fe7', 'testName', 'this is a test data,the number is :25', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('47ba3a6473a14810a27558cb1d64b09e', 'testName', 'this is a test data,the number is :59', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('48136f6910b54733a857ee08404263eb', 'testName', 'this is a test data,the number is :15', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('48ca3bc3e4c643bcae470a91fde81e32', 'testName', 'this is a test data,the number is :66', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('496863b343d449a1b76e1238ec97560a', 'testName', 'this is a test data,the number is :43', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('496f23e2aaf44b7e8dd73ac46e650b16', 'testName', 'this is a test data,the number is :74', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('4a1e155290d04029858f0aa49676a128', 'testName', 'this is a test data,the number is :97', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('4a34822c951741d891e447ad01c5adf2', 'testName', 'this is a test data,the number is :90', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('4a8e77d502464d678e916161bbc851b0', 'testName', 'this is a test data,the number is :68', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('4bf0f3d42b564c3a9a404d18131fa1e6', 'testName', 'this is a test data,the number is :33', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('4cd9eaa3276a4c3ebf784186e94beb68', 'testName', 'this is a test data,the number is :6', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('4d9c36cf9b11424ca181e142df820736', 'testName', 'this is a test data,the number is :36', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('4de6b5800fa6486ba4c1eadfa236d575', 'testName', 'this is a test data,the number is :97', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('4ed645abfaac477a859b7d74b6d0732f', 'testName', 'this is a test data,the number is :67', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('4f91efe85dde4ddc934852c6c054b154', 'testName', 'this is a test data,the number is :17', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('506efcae287f4588be8917ad103d99fe', 'testName', 'this is a test data,the number is :85', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('519df0bba56b49b299e0dc0b004872f4', 'testName', 'this is a test data,the number is :10', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('51e95c9d1db542728fed78028e4d1fe8', 'testName', 'this is a test data,the number is :82', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('51ef773e09da4ae1a36129db11e4da8e', 'testName', 'this is a test data,the number is :83', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('52487b696ca7426596646dc5628a6828', 'testName', 'this is a test data,the number is :51', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('52a93890aebc4a23a00558bdc4ba9fc4', 'testName', 'this is a test data,the number is :92', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('537258e9be6d4be9a7698a89d65b0a56', 'testName', 'this is a test data,the number is :56', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('53ecadba387946f7859a997acfa369ac', 'testName', 'this is a test data,the number is :31', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('54def53a8ef94396a98eebf303ad8d6f', 'testName', 'this is a test data,the number is :17', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('55369583c4c34d0e8933455acbcb0c8a', 'testName', 'this is a test data,the number is :29', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('56716806623844d79cce15e92b4ca343', 'testName', 'this is a test data,the number is :12', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('56f6817786734100a684e9161dfe85aa', 'testName', 'this is a test data,the number is :39', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('574a0b81daf04f44b4a9f22e259adaaf', 'testName', 'this is a test data,the number is :64', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('58a0518a98ba48bc87507498f60ef90d', 'testName', 'this is a test data,the number is :6', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('59055a3bb16f4793bfb621402b6aa81b', 'testName', 'this is a test data,the number is :88', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('5970b09363104c388b8b717adc37c5dd', 'testName', 'this is a test data,the number is :82', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('597259c306f9492b858f976c1d8d19fb', 'testName', 'this is a test data,the number is :65', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('599b61cbaf364162b15874e120bf863d', 'testName', 'this is a test data,the number is :90', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('5ab0a025ccc246d19eebc9eddff3258f', 'testName', 'this is a test data,the number is :27', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('5ab955cff1c04a939f67aab57127630b', 'testName', 'this is a test data,the number is :65', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('5e09ad7ba5954b58a08c82ece55b0235', 'testName', 'this is a test data,the number is :24', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('5e5d930c1f61440ebabe65e788a5dd5e', 'testName', 'this is a test data,the number is :5', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('5e6450d5ac6a400e92a503116dc8ad11', 'testName', 'this is a test data,the number is :22', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('5fd4a0b6812f4e9cad3fefd87540c9f6', 'testName', 'this is a test data,the number is :85', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('603d64989056420f8f4cc19092c8b38f', 'testName', 'this is a test data,the number is :41', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('6113171330fa40b3af43198c839d7833', 'testName', 'this is a test data,the number is :12', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('615e0beaeb024ce1b5b2a583062af558', 'testName', 'this is a test data,the number is :78', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('619eccae73db40dca7ef2ee85a12e0e3', 'testName', 'this is a test data,the number is :35', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('62077bf888e14c6ea02c4a5cbced0040', 'testName', 'this is a test data,the number is :61', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('6240b9474def4c059df8ff24140118d8', 'testName', 'this is a test data,the number is :58', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('62fd46556299446b93232f2f90c879a4', 'testName', 'this is a test data,the number is :88', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('6345f25da90245b7911e5997ebf659c3', 'testName', 'this is a test data,the number is :11', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('638817e882984071a417addb71a14558', 'testName', 'this is a test data,the number is :2', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('6408bf944bc343a6949ea2433878420d', 'testName', 'this is a test data,the number is :52', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('653e65867a034ce29a9fee7c6e4b5787', 'testName', 'this is a test data,the number is :59', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('6571a36f22034bc4b4335e6a13292d0d', 'testName', 'this is a test data,the number is :50', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('65dcb1645f3e4bf59d756d3ee2589426', 'testName', 'this is a test data,the number is :87', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('67fcf7fd3c1d4fe48e1d671f62a3d437', 'testName', 'this is a test data,the number is :74', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('6841aa9214414fdaa8d7173d9c9e8649', 'testName', 'this is a test data,the number is :94', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('68b8e3cd837142ab801911a5f6538ccb', 'testName', 'this is a test data,the number is :8', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('690c2a9170164e1ca88c7be3f38f46c3', 'testName', 'this is a test data,the number is :75', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('6b2570add3604ce5a3605142947a20c7', 'testName', 'this is a test data,the number is :16', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('6be52c765d5b42448345112a23484696', 'testName', 'this is a test data,the number is :49', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('6c58d010724d4628a2fb61442e3fe52c', 'testName', 'this is a test data,the number is :1', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('6ca05717866a462fbb1d17547df4a240', 'testName', 'this is a test data,the number is :46', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('6d4542b746ed45cca703253787be6352', 'testName', 'this is a test data,the number is :19', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('6e2244aff47a42a7a9cbd4b484e3fe18', 'testName', 'this is a test data,the number is :93', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('6e2c32a8543d44ecac5f37240d4f6d41', 'testName', 'this is a test data,the number is :49', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('6e49f24967b84805883068518839cad4', 'testName', 'this is a test data,the number is :80', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('6f234fcea38240d7af48bd18df4a48e2', 'testName', 'this is a test data,the number is :36', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('70235c9ef57a4072ab00a81711ea292f', 'testName', 'this is a test data,the number is :12', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('708765d91dae4f02b51b52740caaebdf', 'testName', 'this is a test data,the number is :83', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('71b2f8bc4fca4a18aef15199be7fbc09', 'testName', 'this is a test data,the number is :95', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('723735908f93450d9ccb5b4be95fdeff', 'testName', 'this is a test data,the number is :84', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('724ec96c76464ed0a72617ffc00c516f', 'testName', 'this is a test data,the number is :19', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('731861063ea342a0a1848d30575fc819', 'testName', 'this is a test data,the number is :28', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('73331496371449269a81f28f715264d8', 'testName', 'this is a test data,the number is :72', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('7343f6bd778841dc89be34b431578e61', 'testName', 'this is a test data,the number is :4', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('73457d4649b1405580d951926208ad1c', 'testName', 'this is a test data,the number is :28', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('73e050731a0e40409f30ea87c4d808f9', 'testName', 'this is a test data,the number is :73', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('74016025fb744d65b54b3687a4cf680e', 'testName', 'this is a test data,the number is :34', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('740e841867ba4504b08d532eb726adcf', 'testName', 'this is a test data,the number is :89', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('741f97f37084441894cc6cbbed9400ed', 'testName', 'this is a test data,the number is :34', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('74b48de82eaf49aaa04681693efcf623', 'testName', 'this is a test data,the number is :45', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('75828f3cac9445f98a04421d12678597', 'testName', 'this is a test data,the number is :8', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('763b64634617473488b1f03c40bc30a3', 'testName', 'this is a test data,the number is :91', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('76a04d4beef24c85b85c2ed46784658b', 'testName', 'this is a test data,the number is :35', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('77076a401ecc4792b5c89e7eb8e05d42', 'testName', 'this is a test data,the number is :26', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('77de3a01cc894626981b679f1a99c664', 'testName', 'this is a test data,the number is :48', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('782dc239a4944806bbffc350b86a8957', 'testName', 'this is a test data,the number is :7', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('793911ca072248a69b55f569703fab14', 'testName', 'this is a test data,the number is :5', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('79f1a030b2704bb68211f96a7ce32558', 'testName', 'this is a test data,the number is :68', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('7a1e968bfc9f4a0cb1098f4ad5f6bc21', 'testName', 'this is a test data,the number is :57', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('7b0ef9a2698740a8adf0231c8bed6480', 'testName', 'this is a test data,the number is :62', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('7b54cb4845c241af8e2dd0f0af515ea0', 'testName', 'this is a test data,the number is :18', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('7c3be61084314c99a61cf6d3a078d03a', 'testName', 'this is a test data,the number is :33', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('7d434a5c4d3d4fd48328444e5f9bd3ca', 'testName', 'this is a test data,the number is :14', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('7da18319b60f49b09106a373e51842d8', 'testName', 'this is a test data,the number is :90', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('7df5aecf08ed4406956c21355d56408d', 'testName', 'this is a test data,the number is :14', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('7e69e78804424b6fa87c2c865c97d492', 'testName', 'this is a test data,the number is :80', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('7ff730d0484a489588f1622ca3d6d5a8', 'testName', 'this is a test data,the number is :45', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('800aa214e7ac4077b5c5282fb317a983', 'testName', 'this is a test data,the number is :96', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('802e80db54b34d249e44b66675023af6', 'testName', 'this is a test data,the number is :69', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('80627fe858f246299fa159c2428f8e04', 'testName', 'this is a test data,the number is :0', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('8077011a661e459393209c5fd7005763', 'testName', 'this is a test data,the number is :84', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('814c78d31806489d9cf57bf0ce0677ad', 'testName', 'this is a test data,the number is :62', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('814eb942b85c4dd6a0d044d20f40b412', 'testName', 'this is a test data,the number is :8', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('81b4805a1e284e9c8324e8f0f570ce4c', 'testName', 'this is a test data,the number is :95', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('82010b4429294e5ba5b76fe93402da72', 'testName', 'this is a test data,the number is :93', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('822c56bc5d4246d1880f3587e992ec3f', 'testName', 'this is a test data,the number is :94', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('822f6d9d5782499f8beee3c5c3772505', 'testName', 'this is a test data,the number is :1', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('829ea3afc53745a89ad6284b03fae3d0', 'testName', 'this is a test data,the number is :3', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('82b7ab6d3131471e940008916dca344e', 'testName', 'this is a test data,the number is :94', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('84c668010f1c4db786218f90f90a0c66', 'testName', 'this is a test data,the number is :68', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('84d1840ae91e4762804dcbf480b7ec89', 'testName', 'this is a test data,the number is :87', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('8518ef00456c4275b6bb969ed8882ea4', 'testName', 'this is a test data,the number is :83', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('86802a931cb84e1cb994a5bf1597c2a1', 'testName', 'this is a test data,the number is :97', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('87862e97e6404dcca87bd4bb75cce222', 'testName', 'this is a test data,the number is :66', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('8814f15374aa4c8ea0f6dc0d23bdbee0', 'testName', 'this is a test data,the number is :63', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('88cc4175b7ac459e819b20f1a309032b', 'testName', 'this is a test data,the number is :3', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('88d6bcd750ab4411a488e022429aec2e', 'testName', 'this is a test data,the number is :81', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('893fce6a1a1a4c06a00ce6a164253443', 'testName', 'this is a test data,the number is :60', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('8987cabba4d541f8b284a8a985f6b5c9', 'testName', 'this is a test data,the number is :30', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('89f1aa8901ed4451a696fc73c0ebeef3', 'testName', 'this is a test data,the number is :44', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('8aaeed27db7743dd8b9a115517256367', 'testName', 'this is a test data,the number is :66', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('8cef8775d30d4554a6e70e0c66a00df6', 'testName', 'this is a test data,the number is :32', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('8d2c1c6ee0274916be95efe57bec9af0', 'testName', 'this is a test data,the number is :78', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('8d85d98f316b43e7b472ffec1a75e47d', 'testName', 'this is a test data,the number is :50', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('8de65c895fdf4683b9f0afb2b719a415', 'testName', 'this is a test data,the number is :92', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('8e353361a1a74a48b0a03ca9a84f7a39', 'testName', 'this is a test data,the number is :67', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('8f3dcbfa9ee74c7e86f89735dd51afc3', 'testName', 'this is a test data,the number is :20', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('8f617f13dbdc4a3da621c65521c53bea', 'testName', 'this is a test data,the number is :10', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('90458126622a4a4fa61d90d2965bbd46', 'testName', 'this is a test data,the number is :98', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('9073f6ef7bca4fa0ae25d14f01aac219', 'testName', 'this is a test data,the number is :79', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('91aa86d8bb76440ebec0516b232cb74d', 'testName', 'this is a test data,the number is :0', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('92c7193f0d6240ffbd3ff1459fd84da2', 'testName', 'this is a test data,the number is :67', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('93a0dc608b3b45c580be5d5194624798', 'testName', 'this is a test data,the number is :71', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('93e495634cbe4d168c8793721ae3915f', 'testName', 'this is a test data,the number is :86', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('94300e7bd0dc4255827604d06aafd8fe', 'testName', 'this is a test data,the number is :1', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('948af18bf3834c4590174dfe1ea8fecb', 'testName', 'this is a test data,the number is :21', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('94d5cb6ec15040e7bfe056ce5c99b43b', 'testName', 'this is a test data,the number is :64', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('95ee46d51f904fbebd343c8d7a5baadb', 'testName', 'this is a test data,the number is :28', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('964b4601ae294d3e9d960cebdefa0427', 'testName', 'this is a test data,the number is :84', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('96d9db01821548ee9bdf4277eccdcb5e', 'testName', 'this is a test data,the number is :86', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('97bd550a3f9c4bcfa19d9f47ac69bd8f', 'testName', 'this is a test data,the number is :49', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('97d20108e2494a218c7de75fd0094952', 'testName', 'this is a test data,the number is :27', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('9b83e401a5e04088878b8c5800003874', 'testName', 'this is a test data,the number is :63', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('9d2a186f61774176912c10a9e951f03f', 'testName', 'this is a test data,the number is :16', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('9d5c78aef0a44c70b2ec5ab1166e4cab', 'testName', 'this is a test data,the number is :18', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('9db1b65d2bc54691a95d698b94e7911d', 'testName', 'this is a test data,the number is :21', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('9dc6ade9b2c64d12a7e34110189c7e4d', 'testName', 'this is a test data,the number is :86', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('9e6435c16b6d4cdca573ac54ad49ded1', 'testName', 'this is a test data,the number is :7', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('9e6ce0ab598f4d618263642e62d7d918', 'testName', 'this is a test data,the number is :71', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('9fab093a83c64c2dae435fc8bc51ae82', 'testName', 'this is a test data,the number is :29', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('a00df25646cd4f20bcee3f3643c2c99a', 'testName', 'this is a test data,the number is :5', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('a2f8cc055cb04461b65e48405cb8c75a', 'testName', 'this is a test data,the number is :7', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('a35cfa029b3648708c2e28bd7729f0bb', 'testName', 'this is a test data,the number is :91', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('a38c142c16994df5b29b11ba1af9ffcb', 'testName', 'this is a test data,the number is :21', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('a3bbd683aee24e15a1f16d9caf6d6f88', 'testName', 'this is a test data,the number is :74', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('a4317e8e5ade49aaa19e40088dc21afd', 'testName', 'this is a test data,the number is :56', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('a4fb8aa4306a43fda8cd523c6702ef71', 'testName', 'this is a test data,the number is :31', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('a53cba85e21140df8508e15d418dc529', 'testName', 'this is a test data,the number is :14', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('a55b09eeebd74832af567b625b8bb3b2', 'testName', 'this is a test data,the number is :26', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('a6288dc69e9b490aadb71c5945364101', 'testName', 'this is a test data,the number is :89', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('a6a21b7b18fa4336add5917f712dc760', 'testName', 'this is a test data,the number is :36', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('a6b1c57bfc204c60bc6766b98afaf288', 'testName', 'this is a test data,the number is :70', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('a7148adcae114a66af5346b405f669de', 'testName', 'this is a test data,the number is :63', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('a785bf6904854f028ce06beaacd564d5', 'testName', 'this is a test data,the number is :67', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('a85cdd120c0843b581fa1c1de525e7b1', 'testName', 'this is a test data,the number is :93', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('a889164550e74bb38d6e265d63d2d5b7', 'testName', 'this is a test data,the number is :55', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('a8a6a622e9ad4ef5b86b881dab1a1a31', 'testName', 'this is a test data,the number is :20', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('a99b74d532814c9296fe38229cc75337', 'testName', 'this is a test data,the number is :91', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('ac04dd7c25cb43a7b08d9e6d018ba670', 'testName', 'this is a test data,the number is :70', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('ac6438575ff24da09f8e4d3939830cc1', 'testName', 'this is a test data,the number is :11', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('acc33320d0a943e7aa9f3e0b3774c89e', 'testName', 'this is a test data,the number is :26', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('ad5d4f528645444cb75bb8eefeb68779', 'testName', 'this is a test data,the number is :42', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('adbe816bb7e9405fa8faf3081249de1c', 'testName', 'this is a test data,the number is :37', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('ae8b5f10799f4448ae2273b81ea58ac2', 'testName', 'this is a test data,the number is :75', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('af2bbce60b504eb4a822a5abe83c8e7a', 'testName', 'this is a test data,the number is :89', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('af4077dcca1444198b23ded23ec04045', 'testName', 'this is a test data,the number is :4', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('b0e79c33f03446ab97de5a234512e162', 'testName', 'this is a test data,the number is :38', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('b19fc0086b324ec48848ac4cc49a87bd', 'testName', 'this is a test data,the number is :23', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('b1bcfdec62394277926b2a1728ae14a9', 'testName', 'this is a test data,the number is :3', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('b1e7c6ac1da4485da71ab0d33e33456c', 'testName', 'this is a test data,the number is :60', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('b288752afecc42a9aa916bdcb450eaea', 'testName', 'this is a test data,the number is :25', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('b2a96e346ac04003a17445cd9b83896e', 'testName', 'this is a test data,the number is :30', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('b3537e5f7d574855876f130063f55c4a', 'testName', 'this is a test data,the number is :77', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('b3634cef040e47be9291f15395821a56', 'testName', 'this is a test data,the number is :76', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('b460b85cd7c543a98eca9b8e11109445', 'testName', 'this is a test data,the number is :54', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('b5207563ec9e4dc283a1ab259ff5604b', 'testName', 'this is a test data,the number is :49', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('b58d3502e17545a5b32ee1ffb4126d39', 'testName', 'this is a test data,the number is :64', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('b5eb2d8741e6488da73f04d0c4e046e3', 'testName', 'this is a test data,the number is :4', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('b627669e87704240996db5b8b8974046', 'testName', 'this is a test data,the number is :71', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('b67857326ceb4a11bc27ddbfd02b6f22', 'testName', 'this is a test data,the number is :62', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('b72874ef8289459ebcca26209dd48152', 'testName', 'this is a test data,the number is :32', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('b74ec621c2484633975294a01f0db941', 'testName', 'this is a test data,the number is :19', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('b754ef0259aa4161821896d2cb2458db', 'testName', 'this is a test data,the number is :87', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('b773873795f148748f94176fb886bd7f', 'testName', 'this is a test data,the number is :33', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('b8af8c56896a4f8e969a6ae00d188636', 'testName', 'this is a test data,the number is :2', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('b8e56eb205f24c82b6151923a3f36171', 'testName', 'this is a test data,the number is :50', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('b945457e354a4a1e9c19121b6f4a8448', 'testName', 'this is a test data,the number is :34', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('ba1fa3b470f442c39929a4f13d12a801', 'testName', 'this is a test data,the number is :52', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('ba5084d061304a5eb8fff18e07788ffc', 'testName', 'this is a test data,the number is :97', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('babe962cd25d40c3af6d2e549243d759', 'testName', 'this is a test data,the number is :13', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('bbe5a7a879e84dc5989d990e34134efd', 'testName', 'this is a test data,the number is :33', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('bc0ce348afc94980a22aaa452e5d118d', 'testName', 'this is a test data,the number is :73', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('bd1d31d861e34abaa4d1d783c20714a4', 'testName', 'this is a test data,the number is :38', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('bd24b27def0d44488dc9f1bc9e9d2891', 'testName', 'this is a test data,the number is :99', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('bdb5b02d6d6c41c0a71068ba1c6f341c', 'testName', 'this is a test data,the number is :98', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('bea3ec138c4b41f291f61516ab384cbf', 'testName', 'this is a test data,the number is :40', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('bf9821fe688a430793f3a41268a60340', 'testName', 'this is a test data,the number is :78', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('c00bf74b53ed4baf953acf134dd5691e', 'testName', 'this is a test data,the number is :29', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('c061c292075045f59e259245e3bb3a8b', 'testName', 'this is a test data,the number is :79', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('c0959a5319ef431ca0aae0c70d8f8736', 'testName', 'this is a test data,the number is :41', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('c0c79d3c763b424eb29345792eb1e2d7', 'testName', 'this is a test data,the number is :5', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('c14be651381a443d93f21ac2f206b34b', 'testName', 'this is a test data,the number is :69', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('c1e6da9968364405bad861300b2622ce', 'testName', 'this is a test data,the number is :92', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('c3ed1fe1e5f9451b9bb7c19693c448c4', 'testName', 'this is a test data,the number is :11', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('c3ff79a9da9b4acbb36d23172b23769f', 'testName', 'this is a test data,the number is :52', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('c4628342d3ba4368bf7f11344ee6826d', 'testName', 'this is a test data,the number is :40', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('c4893797457d40b6bdf54b54bbcad362', 'testName', 'this is a test data,the number is :40', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('c509762a7a6d4647888dfc2798b14412', 'testName', 'this is a test data,the number is :64', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('c548bf8999fe40059b8c294179ffcc4d', 'testName', 'this is a test data,the number is :19', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('c5aeb79e0d5a43c39d125e784d2ab9ec', 'testName', 'this is a test data,the number is :81', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('c5db4d06a4564fe7a94f5e9fa8a850b4', 'testName', 'this is a test data,the number is :99', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('c64db3bbdc884b93a04d86951ab82c8e', 'testName', 'this is a test data,the number is :92', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('c6d1f823925f4fb88d5334df9a776fdd', 'testName', 'this is a test data,the number is :99', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('c957e3a87f9b449dbd07b6a8b0292dac', 'testName', 'this is a test data,the number is :63', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('ca02cb2775ca416e8bdee7f8a1867f49', 'testName', 'this is a test data,the number is :26', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('ca2e8897b0a4482ea00b4f36074eea39', 'testName', 'this is a test data,the number is :91', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('cabb27e5a9c14a7499730c050af91589', 'testName', 'this is a test data,the number is :86', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('cc1b99bee9f1487c93d7cf3e1e783cf6', 'testName', 'this is a test data,the number is :22', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('cc77c4cda66e4110a79d9883442adf5f', 'testName', 'this is a test data,the number is :39', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('cc77ca6887634f7093dfb2ef0cb7ba76', 'testName', 'this is a test data,the number is :21', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('ccdaa300036b42f0bb1992b7ff205937', 'testName', 'this is a test data,the number is :29', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('ccf79944760240849def91bba267946a', 'testName', 'this is a test data,the number is :51', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('ceaab83edb22437f9260b80acf090159', 'testName', 'this is a test data,the number is :53', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('cf19a47932844a5d85a5e3e11db06128', 'testName', 'this is a test data,the number is :96', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('cf74715eda7a499592ae9b6af9dd2eb0', 'testName', 'this is a test data,the number is :25', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('d0228004dc21495e830ff315602c8792', 'testName', 'this is a test data,the number is :43', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('d0ddb2d7141c487385decae3f4dce0e8', 'testName', 'this is a test data,the number is :8', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('d2c523af053a4b87abc330ae01afd510', 'testName', 'this is a test data,the number is :50', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('d315f23a75fd4c80bd483d71070910c4', 'testName', 'this is a test data,the number is :65', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('d3ccb445dcf24b62b207b296066a0422', 'testName', 'this is a test data,the number is :54', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('d4209955c13f4fb99a3bc4a0371539ac', 'testName', 'this is a test data,the number is :71', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('d5a0d5a78ae44c16b4d51e5978d4cb3f', 'testName', 'this is a test data,the number is :41', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('d6dfc8c53dac4bba86ac2d1de1b129c8', 'testName', 'this is a test data,the number is :42', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('d8b0519ab0154f15a4219ab9dcb64d54', 'testName', 'this is a test data,the number is :81', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('d9806d1facc84a69b0cce81456e2dd8b', 'testName', 'this is a test data,the number is :88', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('da45bca6d580491883c3ffcf3bced9a8', 'testName', 'this is a test data,the number is :52', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('da8a6e06ded048e4a14fa676c7492327', 'testName', 'this is a test data,the number is :32', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('dabc7a2424a143398d44e8edfe30ec16', 'testName', 'this is a test data,the number is :76', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('dbce6cc50f1243418ebbc1f5096aafcf', 'testName', 'this is a test data,the number is :39', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('dbec85e7311a41058b107570e5aefd46', 'testName', 'this is a test data,the number is :11', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('dbeebae36bb547fda6a130df20eeee13', 'testName', 'this is a test data,the number is :81', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('dd5f966e64da4bd890ae406971ab4e75', 'testName', 'this is a test data,the number is :77', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('ddb2ece2d5d84388959a1921c546d56e', 'testName', 'this is a test data,the number is :17', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('dde68ae2ac354e88a0fcb936344124a4', 'testName', 'this is a test data,the number is :20', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('e0aa8ebe661641679872527d3eb8a9b3', 'testName', 'this is a test data,the number is :77', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('e17244af5dfc45b4a68c5b616d056e02', 'testName', 'this is a test data,the number is :24', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('e28e0d3d74c24281bfbb963cd6d70ae6', 'testName', 'this is a test data,the number is :22', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('e49928d9fdf64ce9af959f02022d712f', 'testName', 'this is a test data,the number is :83', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('e5897a485ee54f71914df043762be85b', 'testName', 'this is a test data,the number is :14', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('e7e46cfabfe945d8979e19eb35957e23', 'testName', 'this is a test data,the number is :72', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('e8057f4133f84210a0ec991534b36dda', 'testName', 'this is a test data,the number is :31', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('e96f2a0a92dd4b4b94b3cc7cdd9f567d', 'testName', 'this is a test data,the number is :56', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('e9a6c50b0e6f4a8a9bbb131085c969e0', 'testName', 'this is a test data,the number is :13', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('e9f122c9869541348f73ee4e67162ab4', 'testName', 'this is a test data,the number is :53', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('ed5bd14d680e412b9341802537a1420d', 'testName', 'this is a test data,the number is :32', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('ef4e2faa4bbf477f9f2bf72913e1ad3d', 'testName', 'this is a test data,the number is :47', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('f0eb8bddfafc4b5fb52d9a8141c5431f', 'testName', 'this is a test data,the number is :44', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('f19b06e8526f4347a4dc37df1eb49d32', 'testName', 'this is a test data,the number is :59', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('f23b47e840274ce0914e1e321a2c6f8f', 'testName', 'this is a test data,the number is :36', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('f2bfe6cc211b4522bd69d4fde34ffd69', 'testName', 'this is a test data,the number is :24', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('f3a446e0e50c4b9f82e95177e8aedc17', 'testName', 'this is a test data,the number is :72', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('f42d26e8747c4099bac1022050a53799', 'testName', 'this is a test data,the number is :74', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('f5820c03b56e46028a6611942e921d7f', 'testName', 'this is a test data,the number is :10', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('f5f739b6d3fc4a8a84358ca5c9678913', 'testName', 'this is a test data,the number is :2', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('f64ad6a9eb3e41feaae65f42db9e9284', 'testName', 'this is a test data,the number is :23', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('f6b4869767064769a3b94a48211e423a', 'testName', 'this is a test data,the number is :10', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('f6e253d37eaf4fbfa78cea299e4bce4f', 'testName', 'this is a test data,the number is :54', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('f7824f621e38492eb0ac9e6749a18730', 'testName', 'this is a test data,the number is :31', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('f882a10720aa4f21884733e0ae7b3fc1', 'testName', 'this is a test data,the number is :59', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('f887819c9c2343a48ddfb85d8c8c27f9', 'testName', 'this is a test data,the number is :68', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('f9d837eb41d94349a1d678af78f643cf', 'testName', 'this is a test data,the number is :96', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('f9e36d7641484e09b431af6b81274c70', 'testName', 'this is a test data,the number is :38', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('fa3d809499c64a3ebfe29c00cf6b0558', 'testName', 'this is a test data,the number is :9', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('fae1520db89749d0ac8be5fa593c2cd9', 'testName', 'this is a test data,the number is :88', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('faf7c018c78144e7a276682da44cbac8', 'testName', 'this is a test data,the number is :40', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('fb0cebb01bb24a51a5ed352d583bc72b', 'testName', 'this is a test data,the number is :58', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('fbcecd33962b474ba04d07ef61f02dc8', 'testName', 'this is a test data,the number is :78', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('fbee59f324ad421b955a729c2c4e4bd4', 'testName', 'this is a test data,the number is :77', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('fc4109b5259240138627f18c15bfc1e6', 'testName', 'this is a test data,the number is :27', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('fd2f583eb1c946298ce7b1e2f9951479', 'testName', 'this is a test data,the number is :15', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('fdfcd03d7a474840bbe6634d047d9941', 'testName', 'this is a test data,the number is :61', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('fe94df5ac06a481b993bab6c25e24595', 'testName', 'this is a test data,the number is :58', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('fe96782dff294e4388bc0479ebc0c111', 'testName', 'this is a test data,the number is :0', 'teset', 'teset', 'teset');
INSERT INTO `test` VALUES ('fffc54936b2346dc8f5e59046a1eded9', 'testName', 'this is a test data,the number is :27', 'teset', 'teset', 'teset');
DROP TRIGGER IF EXISTS `tri_device_protocol_update_time`;
DELIMITER ;;
CREATE TRIGGER `tri_device_protocol_update_time` BEFORE UPDATE ON `device_protocol` FOR EACH ROW BEGIN
	SET NEW.update_time = now();
END
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `tri_menu_info_update_time`;
DELIMITER ;;
CREATE TRIGGER `tri_menu_info_update_time` BEFORE UPDATE ON `menu_info` FOR EACH ROW BEGIN
	SET NEW.update_time = now();
 
end
;;
DELIMITER ;
DROP TRIGGER IF EXISTS `tri_menu_template_update_time`;
DELIMITER ;;
CREATE TRIGGER `tri_menu_template_update_time` BEFORE UPDATE ON `menu_template` FOR EACH ROW BEGIN
	SET NEW.update_time = now();
 
end
;;
DELIMITER ;
