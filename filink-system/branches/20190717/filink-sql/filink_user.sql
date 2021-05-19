/*
Navicat MySQL Data Transfer

Source Server         : 18
Source Server Version : 50544
Source Host           : 10.5.43.18:3306
Source Database       : filink_user

Target Server Type    : MYSQL
Target Server Version : 50544
File Encoding         : 65001

Date: 2019-02-21 14:42:37
*/

SET FOREIGN_KEY_CHECKS=0;

drop DATABASE filink_user;
CREATE DATABASE filink_user;

-- ----------------------------
-- Table structure for department
-- ----------------------------
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department` (
  `id` varchar(32) NOT NULL COMMENT '部门ID',
  `dept_name` varchar(50) DEFAULT NULL COMMENT '部门名称',
  `dept_chargeuser` varchar(50) DEFAULT NULL COMMENT '责任人',
  `dept_phonenum` varchar(50) DEFAULT NULL COMMENT '联系电话',
  `address` varchar(200) DEFAULT NULL COMMENT '地址',
  `dept_type` varchar(50) DEFAULT NULL COMMENT '类型',
  `dept_fatherId` varchar(32) DEFAULT NULL COMMENT '父级部门ID',
  `dept_level` varchar(2) DEFAULT NULL COMMENT '部门级别',
  `deleted` varchar(2) DEFAULT '0' COMMENT '是否被删除,0没有，1已删除',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(50) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  `childDepartmentList` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='单位部门表';

-- ----------------------------
-- Records of department
-- ----------------------------
INSERT INTO `department` VALUES ('137dc516b2e94a9c952726c59fd2337a', '烽火科技', null, null, null, null, '', '1', '0', null, null, null, null, null, null);
INSERT INTO `department` VALUES ('137dc516b2e94a9c952726c59fd2337b', '二级单位1', '111111', '15000180023', '湖北武汉', '1', '137dc516b2e94a9c952726c59fd2337a', '2', '0', '11111111111111', null, '2019-01-03 15:00:35', '1', '2019-02-13 17:46:12', null);
INSERT INTO `department` VALUES ('137dc516b2e94a9c952726c59fd2337c', '二级单位3', '222222', '15000170023', '武汉光谷', '2', '137dc516b2e94a9c952726c59fd2337a', '2', '0', '22222222222222222', null, '2019-01-03 15:00:38', '1', '2019-02-13 17:47:00', null);
INSERT INTO `department` VALUES ('137dc516b2e94a9c952726c59fd2337d', '二级单位2', '李欢欢', '13000189922', '民族大道112号', null, '137dc516b2e94a9c952726c59fd2337a', '2', '0', '的顶顶顶顶顶顶顶顶顶顶', '1', '2019-02-18 19:13:40', null, null, null);
INSERT INTO `department` VALUES ('26902d6a139441ddad624e113a626f19', '三级单位1', '黎孟德', '16002782780', '民族大道180号', null, '137dc516b2e94a9c952726c59fd2337b', '3', '0', '点点滴滴的的', '1', '2019-02-18 19:18:31', null, null, null);
INSERT INTO `department` VALUES ('43b47b2da9fb41fb867154ebcdf64c68', '三级单位2', '13123213', '13111111111', '12121321321', null, '137dc516b2e94a9c952726c59fd2337b', '3', '0', '1321312312', '1', '2019-02-18 14:21:33', null, null, null);
INSERT INTO `department` VALUES ('5c9d52cdaa0b43c6909a323f665d6a57', '三级单位3', '万强', '19000182822', '花山大道100号', null, '137dc516b2e94a9c952726c59fd2337c', '3', '0', '谁谁谁谁谁谁', '1', '2019-02-18 19:24:23', null, null, null);
INSERT INTO `department` VALUES ('5fcb68ee33c04f1f972cdc4e42a52f23', '三级单位4', 'ewqewq', '13928132213', '12321', null, '137dc516b2e94a9c952726c59fd2337d', '3', '0', '3213213', '1', '2019-02-19 15:03:19', null, null, null);
INSERT INTO `department` VALUES ('6985b0b1e4784c3db8041f9abd3f111e', '三级单位5', '很尴尬和骨灰盒', '16025635122', '4545656666', null, '137dc516b2e94a9c952726c59fd2337d', '3', '1', '32333', '1', '2019-02-19 16:57:26', null, null, null);
INSERT INTO `department` VALUES ('8a5ff3adcbb543569dd540bf31a565af', '四级单位1', 'sssaaa', '18001200220', '上海', null, '26902d6a139441ddad624e113a626f19', '4', '0', '山山水水', '1', '2019-02-14 11:15:39', null, null, null);
INSERT INTO `department` VALUES ('8e486021c980403f926c9523895e5f10', '四级单位2', '333333', '15000180012', '光谷', '20', '43b47b2da9fb41fb867154ebcdf64c68', '4', '1', '33333333333333', '1', '2019-01-28 14:34:56', '1', '2019-02-13 17:47:27', null);
INSERT INTO `department` VALUES ('a52ee83465114a5e8679efe1ca6a0638', '测试单位11', '1111', '13918273212', '23132132321', null, '137dc516b2e94a9c952726c59fd2337a', null, '0', null, '1', '2019-02-21 11:33:35', null, null, null);
INSERT INTO `department` VALUES ('af998916a14d46678ca53394c0e3d9c7', '四级单位3', '444444', '15000180021', '武汉', '19', '5fcb68ee33c04f1f972cdc4e42a52f23', '4', '0', '44444444444444444444444444444444444444', '1', '2019-01-28 14:38:57', '1', '2019-02-13 17:47:48', null);
INSERT INTO `department` VALUES ('c35936d804a54cc38c20565b2ac1319a', '五级单位1', '张明华', '18998282223', '高新二路梅万村5号', null, '8e486021c980403f926c9523895e5f10', '5', '0', '撒啊啊啊', '1', '2019-02-18 19:22:41', null, null, null);
INSERT INTO `department` VALUES ('d86396eb56bd4b5daa2e62422418da8f', '五级单位2', '李立虎', '18007888766', '光谷大道112号', null, '8e486021c980403f926c9523895e5f10', '5', '0', '水水水水', '1', '2019-02-18 19:21:31', null, null, null);
INSERT INTO `department` VALUES ('f4ba02a25e80406686d1d829bd3fae04', '五级单位3', 'wxfwxf', '15000180021', '光谷', null, '8a5ff3adcbb543569dd540bf31a565af', '5', '0', 'ssss', '1', '2019-02-14 11:06:56', null, null, null);
INSERT INTO `department` VALUES ('f8ac581385e54a54b2a02a24b13c69f6', '五级单位4', '2222222', '13026341526', '233423', null, 'af998916a14d46678ca53394c0e3d9c7', '5', '0', '222222', '1', '2019-02-19 16:59:44', null, null, null);

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` varchar(32) NOT NULL COMMENT '角色ID',
  `role_name` varchar(50) DEFAULT NULL COMMENT '角色名称',
  `role_desc` varchar(200) DEFAULT NULL COMMENT '角色描述',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(50) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  `deleted` varchar(2) DEFAULT '0' COMMENT '是否被删除,0没有，1已删除',
  `default_role` tinyint(4) DEFAULT '0' COMMENT '1:默认用户  0：非默认用户（可以删除）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色表';

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES ('0800d5b606064064b7a0e1e9b9d1af24', '教师', '谁谁谁谁谁谁谁谁谁2', '谁谁谁谁谁谁谁谁谁', '1', '2019-02-18 19:32:46', null, null, '0', '0');
INSERT INTO `role` VALUES ('2', '建筑', '222', '2222', '22222', '2019-01-03 10:44:22', '1', '2019-02-14 18:56:32', '1', '0');
INSERT INTO `role` VALUES ('3', '经理', '333', '3333', '33333', '2019-01-03 10:44:29', null, null, '0', '0');
INSERT INTO `role` VALUES ('37d28a8a746f4f478be1beca582c327b', '程序员', '水水水水撒', 'vvvv', '1', '2019-02-18 19:35:23', null, null, '0', '0');
INSERT INTO `role` VALUES ('4', '程序', '444', '水水水水水水水水水水水水是', null, null, '1', '2019-02-14 18:57:15', '0', '0');
INSERT INTO `role` VALUES ('415a5c5c0cd1460da03db8cdf6e1e6f4', '测试角色001', '测试角色001', '测试角色001', '1', '2019-02-21 11:29:01', null, null, '0', '0');
INSERT INTO `role` VALUES ('86dc079f724448a794f21a5925af02a5', '代维人员', '444', 'wahaha', '1', '2019-01-15 14:12:53', '1', '2019-02-14 18:08:12', '0', '1');
INSERT INTO `role` VALUES ('8edad04686bb4a048562dd3928409e8a', '设计', '123123', '12312313', '1', '2019-02-20 15:15:16', null, null, '0', '1');
INSERT INTO `role` VALUES ('9df2f6d28eb347ee93aa2aec535a77a4', '建设', '谁谁谁谁谁谁谁谁谁', '水水水水水水水水', '1', '2019-02-18 19:28:01', null, null, '0', '1');
INSERT INTO `role` VALUES ('dc2d06c05a9242969711c4da6073cab1', '装维', '丰富的双方都', '拍拍', '1', '2019-02-18 19:28:57', null, null, '0', '1');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` varchar(32) NOT NULL COMMENT '用户ID',
  `user_code` varchar(50) DEFAULT NULL COMMENT '用户代码',
  `user_nickname` varchar(50) DEFAULT NULL COMMENT '用户名称',
  `user_name` varchar(50) DEFAULT NULL COMMENT '姓名',
  `user_status` varchar(2) DEFAULT NULL COMMENT '用户状态,1启用，0停用',
  `dept_id` varchar(32) DEFAULT NULL COMMENT '部门ID',
  `role_id` varchar(32) DEFAULT NULL COMMENT '角色ID',
  `address` varchar(200) DEFAULT NULL COMMENT '地址',
  `phonenumber` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `password` varchar(50) DEFAULT NULL COMMENT '密码',
  `count_validity_time` varchar(50) DEFAULT NULL COMMENT '账号有效期',
  `userdesc` varchar(200) DEFAULT NULL COMMENT '描述',
  `last_login_time` timestamp NULL DEFAULT NULL COMMENT '最后一次登录时间',
  `last_login_ip` varchar(50) DEFAULT NULL COMMENT '最后一次登录ip',
  `login_type` varchar(2) DEFAULT NULL COMMENT '登录模式，1是单用户，2是多用户',
  `unlocked_time` timestamp NULL DEFAULT NULL COMMENT '解禁时间',
  `login_sourse` varchar(2) DEFAULT NULL COMMENT '登录源，1是Web网管，0是APP',
  `max_users` int(11) DEFAULT NULL COMMENT '最大用户数，单用户模式默认为1',
  `deleted` varchar(2) DEFAULT '0' COMMENT '是否被删除,0没有，1已删除',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(50) DEFAULT NULL COMMENT '修改人',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  `area` varchar(200) DEFAULT NULL COMMENT '区域',
  `login_time` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_code` (`user_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息表';

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('03b11e8143794a6c94821add73ee4d02', '1234563', 'admin3', '张杰', '0', '5fcb68ee33c04f1f972cdc4e42a52f23', '3', '鄂州', '18002873733', '3@qq.com', 'admin3', '12d', 'www', null, null, null, null, null, null, '1', '1', '2019-02-19 16:12:32', null, null, null, null);
INSERT INTO `user` VALUES ('1', 'admin', 'admin', 'admin', '0', 'd86396eb56bd4b5daa2e62422418da8f', '3', '光谷关山大道', '15000180025', '111qq@123.com', '123456', '20d', 'wwww', null, null, null, null, null, null, '0', null, '2019-02-19 16:02:04', '1', '2019-02-20 19:32:13', null, '1550730752511');
INSERT INTO `user` VALUES ('1591ac9bd5ea4a639ec7be82d9491769', 'test002', '测试用户002', '测试用户002', '1', '137dc516b2e94a9c952726c59fd2337a', '0800d5b606064064b7a0e1e9b9d1af24', '321323123123', '13978876758', '1312@qq.com', '123456', '1112d', null, null, null, null, null, null, null, '1', '1', '2019-02-21 10:40:30', null, null, null, null);
INSERT INTO `user` VALUES ('2c4f76fd69f44efabfdea35bd627189e', '1234562', 'admin2', '李宽', '1', 'd86396eb56bd4b5daa2e62422418da8f', '3', '街道口', '15000788884', '12@qq.com', 'admin2', '30d', 'ssss', null, null, null, null, null, null, '1', '1', '2019-02-19 16:02:04', null, null, null, null);
INSERT INTO `user` VALUES ('9168c995052b4105ab963ffc406995d1', 'test001', '测试用户001', '测试用户001', '1', '137dc516b2e94a9c952726c59fd2337b', '8edad04686bb4a048562dd3928409e8a', null, '13123311291', '2321@qq.com.cn', '123456', '12312d', null, null, null, null, null, null, null, '0', '1', '2019-02-21 10:31:20', null, null, null, null);
INSERT INTO `user` VALUES ('c08d84dd48f44472be854d21dba83aa8', 'test1234', 'test1234', 'test1234', '1', 'f4ba02a25e80406686d1d829bd3fae04', '3', '光谷大道', '14001232733', '3@qq.com', '123456', '30d', 'ddd', null, null, null, null, null, null, '1', '1', '2019-02-21 10:49:16', null, null, null, null);
