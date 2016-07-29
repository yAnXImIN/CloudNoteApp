/*
Navicat MySQL Data Transfer

Source Server         : test
Source Server Version : 50621
Source Host           : localhost:3306
Source Database       : cloudnote

Target Server Type    : MYSQL
Target Server Version : 50621
File Encoding         : 65001

Date: 2015-12-01 15:52:25
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `log`
-- ----------------------------
DROP TABLE IF EXISTS `log`;
CREATE TABLE `log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `userid` int(10) NOT NULL,
  `method` varchar(255) DEFAULT NULL,
  `ip` varchar(20) DEFAULT NULL,
  `platform` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userLog` (`userid`),
  CONSTRAINT `userLog` FOREIGN KEY (`userid`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of log
-- ----------------------------

-- ----------------------------
-- Table structure for `note`
-- ----------------------------
DROP TABLE IF EXISTS `note`;
CREATE TABLE `note` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `user` int(10) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `content` varchar(10000) DEFAULT NULL,
  `deleted` int(1) DEFAULT NULL,
  `confirmed` int(1) DEFAULT NULL,
  `updated` int(1) DEFAULT NULL,
  `time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `userNote` (`user`),
  CONSTRAINT `userNote` FOREIGN KEY (`user`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of note
-- ----------------------------

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '用户密码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
