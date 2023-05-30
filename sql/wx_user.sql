/*
 Navicat MySQL Data Transfer

 Source Server         : mysql57
 Source Server Type    : MySQL
 Source Server Version : 50732
 Source Host           : localhost:3306
 Source Schema         : ry-vue-1

 Target Server Type    : MySQL
 Target Server Version : 50732
 File Encoding         : 65001

 Date: 30/05/2023 16:05:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for wx_user
-- ----------------------------
DROP TABLE IF EXISTS `wx_user`;
CREATE TABLE `wx_user`
(
    `uid`         bigint(20)                                                    NOT NULL AUTO_INCREMENT COMMENT '用户id',
    `openid`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '微信openid',
    `phone`       char(11) CHARACTER SET utf8 COLLATE utf8_general_ci           NULL     DEFAULT NULL COMMENT '手机号',
    `nickname`    varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '昵称',
    `avatar`      varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci       NULL     DEFAULT NULL COMMENT '头像',
    `password`    varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci       NULL     DEFAULT NULL COMMENT '密码',
    `gender`      bit(1)                                                        NULL     DEFAULT b'0' COMMENT '性别',
    `birthday`    date                                                          NULL     DEFAULT NULL COMMENT '生日',
    `disabled`    char(1) CHARACTER SET utf8 COLLATE utf8_general_ci            NOT NULL DEFAULT '1' COMMENT '状态',
    `last_ip`     varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci       NULL     DEFAULT '' COMMENT '最近登录ip',
    `city`        varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci       NULL     DEFAULT '' COMMENT '城市',
    `create_time` datetime(0)                                                   NULL     DEFAULT NULL COMMENT '注册时间',
    `update_time` datetime(0)                                                   NULL     DEFAULT NULL COMMENT '最近登录时间',
    `deleted`     tinyint(1)                                                    NULL     DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`uid`) USING BTREE,
    UNIQUE INDEX `username` (`openid`) USING BTREE,
    UNIQUE INDEX `phone` (`phone`) USING BTREE,
    INDEX `status` (`disabled`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '微信用户'
  ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
