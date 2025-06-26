# 课程作业功能说明

## 功能概述

在 `quiz.vue` 中集成了作业功能，通过菜单栏可以在"作业"和"试卷"之间切换。

## 主要功能

### 1. 作业管理（教师功能）
- **发布作业**：教师可以发布新作业，包括作业名称、描述、截止时间和附件
- **删除作业**：教师可以删除单个作业或批量删除选中的作业
- **作业列表**：显示当前课程的所有作业

### 2. 作业展示（学生功能）
- **已完成作业**：显示学生已完成的作业，包括得分、提交时间等信息
- **未完成作业**：显示学生未完成的作业，可以点击"去完成"按钮提交作业

### 3. 作业详情
- **查看详情**：可以查看作业的详细信息，包括附件、提交内容、评分等
- **文件下载**：支持下载作业附件和提交的文件

### 4. 作业提交
- **文本内容**：支持输入文本内容
- **文件上传**：支持上传PDF、DOC、DOCX、TXT格式的文件
- **文件限制**：单个文件大小不超过10MB

## 数据库表结构

### course_homework 表
```sql
CREATE TABLE course_homework (
  homework_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '作业ID',
  course_id BIGINT NOT NULL COMMENT '课程ID',
  homework_name VARCHAR(100) NOT NULL COMMENT '作业名称',
  homework_desc TEXT NULL COMMENT '作业描述',
  file_paths TEXT NULL COMMENT '作业附件路径，JSON数组字符串',
  create_by VARCHAR(64) NULL COMMENT '创建人',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  update_by VARCHAR(64) NULL COMMENT '更新人',
  update_time DATETIME NULL COMMENT '更新时间',
  status CHAR DEFAULT '1' NULL COMMENT '状态（1-启用，0-停用）',
  remark VARCHAR(500) NULL COMMENT '备注',
  CONSTRAINT fk_homework_course FOREIGN KEY (course_id) REFERENCES course (course_id)
) COMMENT='课程作业表';
```

### learning_task 表（已修改）
- 修改 `task_type` 和 `submit_method` 只包含"资料阅读"和"作业完成"
- 删除 `paper_id`，添加 `homework_id`
- 添加外键约束关联 `course_homework` 表

## API接口

### 作业管理接口
- `GET /system/homework/list` - 获取作业列表
- `POST /system/homework` - 发布作业
- `DELETE /system/homework/{id}` - 删除作业
- `GET /system/homework/{id}` - 获取作业详情

### 作业状态接口
- `GET /system/homework/user/status` - 获取用户作业完成情况
- `POST /system/homework/submit` - 提交作业
- `POST /system/homework/upload` - 上传作业附件

## 使用说明

### 教师操作
1. 进入课程页面，默认显示作业标签页
2. 点击"发布作业"按钮，填写作业信息并上传附件
3. 可以查看作业列表，删除不需要的作业

### 学生操作
1. 进入课程页面，查看"已完成作业"和"未完成作业"
2. 点击"去完成"按钮提交作业
3. 点击"查看详情"查看作业的详细信息和评分

## 注意事项

1. 文件上传支持PDF、DOC、DOCX、TXT格式，单个文件不超过10MB
2. 作业附件和提交文件都使用JSON格式存储文件路径
3. 教师角色判断基于用户角色中的"teacher"标识
4. 作业完成状态通过 `task_submission` 表判断 