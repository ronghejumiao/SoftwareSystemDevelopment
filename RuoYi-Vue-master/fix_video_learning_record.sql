-- 修复video_learning_record表的外键关系
-- 执行前请备份数据库

-- 1. 查看当前video_learning_record表中的数据
SELECT 
    vlr.record_id,
    vlr.learning_record_id,
    vlr.resource_id,
    lr.resource_name as learning_resource_name,
    lr.resource_type as learning_resource_type,
    vr.video_name as video_resource_name,
    vr.video_type as video_resource_type
FROM video_learning_record vlr
LEFT JOIN learning_resource lr ON lr.resource_id = vlr.resource_id
LEFT JOIN video_resource vr ON vr.video_id = vlr.resource_id
ORDER BY vlr.record_id;

-- 2. 删除learning_resource表中的video类型记录
DELETE FROM learning_resource WHERE resource_type = 'video';

-- 3. 删除video_learning_record表中resource_id不存在于video_resource表中的记录
DELETE vlr FROM video_learning_record vlr
LEFT JOIN video_resource vr ON vr.video_id = vlr.resource_id
WHERE vlr.resource_id IS NOT NULL AND vr.video_id IS NULL;

-- 4. 验证修复结果
-- 检查learning_resource表中是否还有video类型记录
SELECT COUNT(*) as video_records_in_learning_resource 
FROM learning_resource 
WHERE resource_type = 'video';

-- 检查video_learning_record表中的数据完整性
SELECT 
    COUNT(*) as total_video_learning_records,
    COUNT(CASE WHEN vr.video_id IS NOT NULL THEN 1 END) as valid_records,
    COUNT(CASE WHEN vr.video_id IS NULL THEN 1 END) as invalid_records
FROM video_learning_record vlr
LEFT JOIN video_resource vr ON vr.video_id = vlr.resource_id;

-- 5. 显示修复后的数据
SELECT 
    vlr.record_id,
    vlr.learning_record_id,
    vlr.resource_id,
    vr.video_name,
    vr.course_id,
    vlr.completion_rate,
    vlr.last_watch_time
FROM video_learning_record vlr
LEFT JOIN video_resource vr ON vr.video_id = vlr.resource_id
ORDER BY vlr.record_id;

-- 6. 更新表结构注释（可选）
-- ALTER TABLE video_learning_record MODIFY COLUMN resource_id bigint NOT NULL COMMENT '资源ID，关联video_resource表（视频）'; 