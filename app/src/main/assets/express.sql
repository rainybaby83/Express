/*
Navicat MySQL Data Transfer

Source Server         : aliyun
Source Server Version : 50718
Source Host           : rainybaby.mysql.rds.aliyuncs.com:3306
Source Database       : express

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2019-02-16 09:49:58
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for param
-- ----------------------------
DROP TABLE IF EXISTS `param`;
CREATE TABLE `param` (
  `id` int(11) NOT NULL,
  `param_name` varchar(255) NOT NULL,
  `value` char(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of param
-- ----------------------------
INSERT INTO `param` VALUES ('1', 'sync_time', '2019-2-1 00:00:00');

-- ----------------------------
-- Table structure for sms
-- ----------------------------
DROP TABLE IF EXISTS `sms`;
CREATE TABLE `sms` (
  `sms_id` bigint(40) NOT NULL,
  `sms_short_date` varchar(100) DEFAULT NULL,
  `sms_code` varchar(100) DEFAULT NULL,
  `sms_phone` varchar(100) DEFAULT NULL,
  `sms_position` varchar(100) DEFAULT NULL,
  `sms_fetch_date` varchar(100) DEFAULT NULL,
  `sms_fetch_status` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`sms_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sms
-- ----------------------------

