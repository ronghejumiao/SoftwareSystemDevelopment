-- 课程作业表
CREATE TABLE IF NOT EXISTS `course_homework` (
  `homework_id` bigint NOT NULL AUTO_INCREMENT COMMENT '作业ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `homework_name` varchar(100) NOT NULL COMMENT '作业名称',
  `homework_desc` text COMMENT '作业描述',
  `due_date` datetime COMMENT '截止时间',
  `file_paths` text COMMENT '作业附件路径，json数组字符串',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime COMMENT '更新时间',
  `status` char(1) DEFAULT '1' COMMENT '状态（1-启用，0-停用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`homework_id`),
  KEY `idx_course_id` (`course_id`),
  CONSTRAINT `fk_homework_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='课程作业表';

-- 作业提交表
CREATE TABLE IF NOT EXISTS `homework_submission` (
  `submission_id` bigint NOT NULL AUTO_INCREMENT COMMENT '提交ID',
  `homework_id` bigint NOT NULL COMMENT '作业ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `submission_content` text COMMENT '提交内容',
  `submission_file` text COMMENT '提交文件路径，json数组字符串',
  `submission_time` datetime NOT NULL COMMENT '提交时间',
  `is_graded` char(1) DEFAULT '0' COMMENT '是否评分（0-未评分，1-已评分）',
  `grade_score` decimal(5,2) DEFAULT NULL COMMENT '评分分数',
  `grade_comment` text COMMENT '评分评语',
  `grader_id` bigint DEFAULT NULL COMMENT '评分人ID',
  `grade_time` datetime DEFAULT NULL COMMENT '评分时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime COMMENT '更新时间',
  `status` char(1) DEFAULT '1' COMMENT '状态（1-正常，0-停用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`submission_id`),
  KEY `idx_homework_id` (`homework_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_submission_time` (`submission_time`),
  CONSTRAINT `fk_submission_homework` FOREIGN KEY (`homework_id`) REFERENCES `course_homework` (`homework_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_submission_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_submission_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_submission_grader` FOREIGN KEY (`grader_id`) REFERENCES `sys_user` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='作业提交表';

-- 修改learning_task表
-- 添加homework_id字段
ALTER TABLE `learning_task` ADD COLUMN `homework_id` bigint DEFAULT NULL COMMENT '作业ID，关联course_homework表' AFTER `resource_id`;

-- 添加外键约束
ALTER TABLE `learning_task` ADD CONSTRAINT `fk_learning_task_homework` FOREIGN KEY (`homework_id`) REFERENCES `course_homework` (`homework_id`) ON DELETE SET NULL;

-- 删除paper_id字段（如果存在）
-- ALTER TABLE `learning_task` DROP COLUMN IF EXISTS `paper_id`;

-- 修改task_type和submit_method的约束（需要在应用层面控制）
-- 建议在应用代码中限制task_type和submit_method的值
-- task_type: "资料阅读", "作业完成"
-- submit_method: "资料阅读", "作业完成" 