/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 50731
 Source Host           : localhost:3306
 Source Schema         : reminder

 Target Server Type    : MySQL
 Target Server Version : 50731
 File Encoding         : 65001

 Date: 20/10/2022 14:28:30
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin_info
-- ----------------------------
DROP TABLE IF EXISTS `admin_info`;
CREATE TABLE `admin_info`  (
  `member_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '飞书人员ID',
  `is_delete` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0',
  PRIMARY KEY (`member_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '管理员通知名单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for fs_group_info
-- ----------------------------
DROP TABLE IF EXISTS `fs_group_info`;
CREATE TABLE `fs_group_info`  (
  `chat_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '群组 ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '群名称',
  `owner_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '群主 ID',
  `owner_id_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '群主 ID 类型\r\n\r\n',
  `send_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '发送群消息类型：0，三部日常提醒',
  `reminder_none` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '提醒文案',
  `is_delete` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`chat_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '飞书群组详情' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for fs_user_group
-- ----------------------------
DROP TABLE IF EXISTS `fs_user_group`;
CREATE TABLE `fs_user_group`  (
  `chat_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `member_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '成员的用户ID，ID值与查询参数中的 member_id_type 对应。',
  PRIMARY KEY (`chat_id`, `member_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户、群主关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for fs_user_member_info
-- ----------------------------
DROP TABLE IF EXISTS `fs_user_member_info`;
CREATE TABLE `fs_user_member_info`  (
  `member_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '飞书用户ID',
  `name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '飞书用户名称',
  `tenant_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '为租户在飞书上的唯一标识，用来换取对应的tenant_access_token，也可以用作租户在应用里面的唯一标识',
  `member_id_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '飞书用户ID类型',
  `user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'redmine 用户名',
  `r_user_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'redmine api key',
  `is_delete` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0',
  PRIMARY KEY (`member_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'redmine、飞书用户表关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for none_status
-- ----------------------------
DROP TABLE IF EXISTS `none_status`;
CREATE TABLE `none_status`  (
  `none_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '排除的状态',
  `expired_days` tinyint(5) NOT NULL COMMENT '过期天数：\r\n0当天；\r\n1 过期1天；\r\n2 过期2天。',
  PRIMARY KEY (`none_status`, `expired_days`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'redmine排除筛选状态' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for overdue_tasks_history
-- ----------------------------
DROP TABLE IF EXISTS `overdue_tasks_history`;
CREATE TABLE `overdue_tasks_history`  (
  `redmine_id` char(7) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'redmineID',
  `project_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目名称',
  `subject_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务主题',
  `assignee_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '指派人姓名',
  `type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '类型：\r\n0：正常提醒数据\r\n1：过期提醒数据',
  `last_update_time` timestamp(0) NULL DEFAULT NULL COMMENT 'redmine 最后更新时间',
  `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`redmine_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'redmine 过期任务提醒、记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for project_info
-- ----------------------------
DROP TABLE IF EXISTS `project_info`;
CREATE TABLE `project_info`  (
  `p_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'redmine项目ID',
  `p_key` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'redmine项目key',
  `p_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'redmine项目名称',
  PRIMARY KEY (`p_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'redmine 项目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for redmine_user_info
-- ----------------------------
DROP TABLE IF EXISTS `redmine_user_info`;
CREATE TABLE `redmine_user_info`  (
  `assignee_id` int(11) NOT NULL COMMENT 'redmine人员ID',
  `assignee_name` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'redmine人员姓名',
  `redmine_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT 'redmine类型',
  `is_delete` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标识',
  PRIMARY KEY (`assignee_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'redmine人员列表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
