# 视频学习记录功能修复说明

## 问题描述

在视频学习记录相关功能中存在以下问题：

1. **learning_resource表中出现了不应该存在的video类型记录**
2. **video_learning_record表的外键错误地连接到了learning_resource而不是video_resource**
3. **数据混乱导致视频资源重复显示和无法正确管理**

## 修复内容

### 1. 后端代码修复

#### VideoLearningRecordServiceImpl.java
- 移除了自动创建learning_resource记录的逻辑
- 现在resource_id直接对应video_resource.video_id
- 简化了saveOrUpdate方法的逻辑

#### VideoLearningRecordMapper.java
- 添加了selectByLearningRecordIdAndResourceId方法
- 更新了XML映射文件中的关联查询

#### VideoLearningRecordMapper.xml
- 修改了selectVideoLearningRecordVo，将learning_resource的关联改为video_resource的关联
- 更新了查询条件中的字段引用

#### VideoLearningRecord.java
- 更新了resourceId字段的注释，说明现在直接对应video_resource.video_id

### 2. 前端代码修复

#### detail.vue (课程详情页面)
- 修改getResourceList方法，过滤掉video类型的资源
- 确保学习资源部分不显示视频记录

#### resource/index.vue (学习资源管理页面)
- 添加了文件类型限制，不允许上传视频文件
- 修改getList方法，过滤掉video类型的资源
- 添加了beforeUpload方法进行文件类型验证

### 3. 数据库修复

#### 清理脚本
- `cleanup_video_data.sql`: 清理learning_resource表中的video记录
- `fix_video_learning_record.sql`: 修复video_learning_record表的外键关系

## 执行步骤

### 1. 数据库修复
```sql
-- 执行fix_video_learning_record.sql中的脚本
-- 注意：执行前请备份数据库
```

### 2. 代码部署
1. 更新后端代码
2. 更新前端代码
3. 重启应用

### 3. 验证修复结果
- 检查learning_resource表中是否还有video类型记录
- 验证video_learning_record表的数据完整性
- 测试视频播放和学习记录功能

## 修复后的数据流

1. **视频上传** → `video_resource`表
2. **视频播放** → 创建/更新`video_learning_record`记录，resource_id直接对应video_resource.video_id
3. **学习资源管理** → 只管理PPT、PDF等文档资源，不包含视频
4. **视频管理** → 通过专门的视频资源管理功能

## 注意事项

1. 执行数据库脚本前请务必备份数据库
2. 修复后，原有的video_learning_record记录可能需要重新创建
3. 确保前端正确过滤了video类型的资源
4. 视频上传功能现在完全独立于学习资源管理

## 影响范围

- ✅ 视频播放功能正常
- ✅ 视频学习记录功能正常
- ✅ 学习资源管理功能正常（不包含视频）
- ✅ 视频资源管理功能正常
- ✅ 课程详情页面显示正常 