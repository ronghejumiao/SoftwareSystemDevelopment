-- 清理和修复视频学习记录相关数据
-- 执行前请备份数据库

-- 1. 删除learning_resource表中的video类型记录
DELETE FROM learning_resource WHERE resource_type = 'video';

-- 2. 修复video_learning_record表中的resource_id
-- 由于现在resource_id直接对应video_resource.video_id，需要确保数据一致性
-- 先查看当前video_learning_record表中的数据
SELECT 
    vlr.record_id,
    vlr.learning_record_id,
    vlr.resource_id,
    vr.video_id,
    vr.video_name,
    vr.course_id
FROM video_learning_record vlr
LEFT JOIN video_resource vr ON vr.video_id = vlr.resource_id
WHERE vlr.resource_id IS NOT NULL;

-- 3. 删除video_learning_record表中resource_id不存在于video_resource表中的记录
DELETE vlr FROM video_learning_record vlr
LEFT JOIN video_resource vr ON vr.video_id = vlr.resource_id
WHERE vlr.resource_id IS NOT NULL AND vr.video_id IS NULL;

-- 4. 验证清理结果
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