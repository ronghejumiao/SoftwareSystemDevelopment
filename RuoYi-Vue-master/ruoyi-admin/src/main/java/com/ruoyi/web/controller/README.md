# 作业功能后端实现说明

## 概述
本模块实现了课程作业的完整后端功能，包括作业管理、作业提交、文件上传等功能。

## 数据库表结构

### 1. course_homework（课程作业表）
- `homework_id`: 作业ID（主键）
- `course_id`: 课程ID（外键）
- `homework_name`: 作业名称
- `homework_desc`: 作业描述
- `due_date`: 截止时间
- `file_paths`: 作业附件路径（JSON数组字符串）
- `status`: 状态（1-启用，0-停用）
- 其他标准字段：create_by, create_time, update_by, update_time, remark

### 2. homework_submission（作业提交表）
- `submission_id`: 提交ID（主键）
- `homework_id`: 作业ID（外键）
- `course_id`: 课程ID（外键）
- `user_id`: 用户ID（外键）
- `submission_content`: 提交内容
- `submission_file`: 提交文件路径（JSON数组字符串）
- `submission_time`: 提交时间
- `is_graded`: 是否评分（0-未评分，1-已评分）
- `grade_score`: 评分分数
- `grade_comment`: 评分评语
- `grader_id`: 评分人ID（外键）
- `grade_time`: 评分时间
- 其他标准字段：create_by, create_time, update_by, update_time, status, remark

### 3. learning_task表修改
- 新增 `homework_id` 字段，关联course_homework表
- 删除 `paper_id` 字段
- task_type和submit_method限定为"资料阅读"和"作业完成"

## 核心类说明

### 实体类
1. **CourseHomework**: 课程作业实体类
   - 包含作业的基本信息：名称、描述、截止时间、附件等
   - 支持JSON格式的附件路径存储

2. **HomeworkSubmission**: 作业提交实体类
   - 包含提交内容、文件、评分信息等
   - 支持JSON格式的文件路径存储

### Mapper接口
1. **CourseHomeworkMapper**: 作业数据访问接口
   - 基本的CRUD操作
   - 支持按课程ID查询作业列表

2. **HomeworkSubmissionMapper**: 作业提交数据访问接口
   - 基本的CRUD操作
   - 支持按作业ID和用户ID查询提交记录
   - 支持按课程ID和用户ID查询提交记录列表

### Service层
1. **ICourseHomeworkService**: 作业服务接口
   - 定义作业管理的业务方法

2. **CourseHomeworkServiceImpl**: 作业服务实现类
   - 实现作业的增删改查业务逻辑

3. **IHomeworkSubmissionService**: 作业提交服务接口
   - 定义作业提交的业务方法
   - 包含获取用户作业完成情况的方法

4. **HomeworkSubmissionServiceImpl**: 作业提交服务实现类
   - 实现作业提交的业务逻辑
   - 实现用户作业完成情况的查询逻辑

### Controller层
**CourseHomeworkController**: 作业控制器
- `/system/homework/list`: 查询作业列表
- `/system/homework/{homeworkId}`: 获取作业详情
- `/system/homework`: 新增作业（POST）
- `/system/homework`: 修改作业（PUT）
- `/system/homework/{homeworkIds}`: 删除作业（DELETE）
- `/system/homework/user/status`: 获取用户作业完成情况
- `/system/homework/submit`: 提交作业
- `/system/homework/upload`: 上传作业附件

## 主要功能

### 1. 作业管理
- 教师可以发布、修改、删除作业
- 支持作业附件上传（PDF、DOC、DOCX、TXT格式）
- 支持设置作业截止时间
- 作业状态管理（启用/停用）

### 2. 作业提交
- 学生可以提交作业内容和附件
- 支持文本内容和文件上传
- 自动记录提交时间
- 防止重复提交（同一作业同一用户只能提交一次）

### 3. 作业状态查询
- 查询用户在某课程中的作业完成情况
- 区分已完成和未完成的作业
- 包含作业详情和提交信息

### 4. 文件上传
- 支持多种文档格式
- 文件大小限制（10MB）
- 文件格式验证
- 安全的文件路径生成

## 权限控制
- 作业管理需要 `system:homework:*` 权限
- 作业查询和提交需要相应的课程权限
- 文件上传需要登录状态

## 注意事项
1. 文件上传功能需要配置正确的文件存储路径
2. 数据库表需要先创建，外键约束要正确设置
3. 作业完成状态通过关联learning_record和task_submission表判断
4. 建议在生产环境中添加更多的安全验证

## 后续扩展
1. 作业评分功能
2. 作业统计报表
3. 作业提醒功能
4. 批量作业管理
5. 作业模板功能 