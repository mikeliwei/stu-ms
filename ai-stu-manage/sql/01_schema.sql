-- ============================================================
-- 学生信息管理系统 - 数据库结构脚本
-- 数据库 : ai_stu_manage
-- 适用   : MySQL 8.0+
-- 执行   : mysql -u root -p --default-character-set=utf8mb4 < 01_schema.sql
-- 说明   : 会先删除同名数据库，请勿在生产库执行
-- ============================================================

DROP DATABASE IF EXISTS `ai_stu_manage`;
CREATE DATABASE `ai_stu_manage`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;
USE `ai_stu_manage`;

-- 删除顺序：先子表后父表
DROP TABLE IF EXISTS `student`;
DROP TABLE IF EXISTS `class_info`;
DROP TABLE IF EXISTS `sys_user`;

-- ------------------------------------------------------------
-- 1. 用户表（用户登录模块）
-- ------------------------------------------------------------
CREATE TABLE `sys_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`    VARCHAR(50)  NOT NULL                COMMENT '登录账号',
    `password`    VARCHAR(100) NOT NULL                COMMENT '登录密码（明文存储，按需求不加密）',
    `real_name`   VARCHAR(50)      NULL                COMMENT '真实姓名',
    `role`        VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN 管理员 / USER 普通用户',
    `status`      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：1 启用，0 禁用',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_user_username` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='系统用户表';

-- ------------------------------------------------------------
-- 2. 班级信息表（班级信息管理模块）
-- ------------------------------------------------------------
CREATE TABLE `class_info` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `class_code`   VARCHAR(30)  NOT NULL                COMMENT '班级编号',
    `class_name`   VARCHAR(50)  NOT NULL                COMMENT '班级名称',
    `grade`        VARCHAR(20)      NULL                COMMENT '所属年级',
    `head_teacher` VARCHAR(50)      NULL                COMMENT '班主任',
    `remark`       VARCHAR(255)     NULL                COMMENT '备注',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_class_info_code` (`class_code`),
    KEY `idx_class_info_name` (`class_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='班级信息表';

-- ------------------------------------------------------------
-- 3. 学生信息表（学生信息管理模块）
-- ------------------------------------------------------------
CREATE TABLE `student` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `student_no`  VARCHAR(30)  NOT NULL                COMMENT '学号',
    `name`        VARCHAR(50)  NOT NULL                COMMENT '姓名',
    `gender`      VARCHAR(10)      NULL                COMMENT '性别：男 / 女',
    `age`         INT              NULL                COMMENT '年龄',
    `birth_date`  DATE             NULL                COMMENT '出生日期',
    `phone`       VARCHAR(20)      NULL                COMMENT '联系电话',
    `email`       VARCHAR(100)     NULL                COMMENT '电子邮箱',
    `class_id`    BIGINT           NULL                COMMENT '所属班级（外键 -> class_info.id）',
    `address`     VARCHAR(255)     NULL                COMMENT '家庭住址',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_no` (`student_no`),
    KEY `idx_student_class_id` (`class_id`),
    KEY `idx_student_name` (`name`),
    CONSTRAINT `fk_student_class`
        FOREIGN KEY (`class_id`) REFERENCES `class_info` (`id`)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='学生信息表';
