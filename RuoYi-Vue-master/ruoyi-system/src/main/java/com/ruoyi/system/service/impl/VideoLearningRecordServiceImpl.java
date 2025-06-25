package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.VideoLearningRecordMapper;
import com.ruoyi.system.domain.VideoLearningRecord;
import com.ruoyi.system.service.IVideoLearningRecordService;
import com.ruoyi.system.domain.LearningResource;
import com.ruoyi.system.service.ILearningResourceService;
import com.ruoyi.system.mapper.VideoResourceMapper;
import com.ruoyi.system.domain.VideoResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 视频学习记录，记录学生观看视频的行为数据Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-24
 */
@Service
public class VideoLearningRecordServiceImpl implements IVideoLearningRecordService 
{
    private static final Logger log = LoggerFactory.getLogger(VideoLearningRecordServiceImpl.class);

    @Autowired
    private VideoLearningRecordMapper videoLearningRecordMapper;

    @Autowired
    private ILearningResourceService learningResourceService;

    @Autowired
    private VideoResourceMapper videoResourceMapper;

    /**
     * 查询视频学习记录，记录学生观看视频的行为数据
     * 
     * @param recordId 视频学习记录，记录学生观看视频的行为数据主键
     * @return 视频学习记录，记录学生观看视频的行为数据
     */
    @Override
    public VideoLearningRecord selectVideoLearningRecordByRecordId(Long recordId)
    {
        return videoLearningRecordMapper.selectVideoLearningRecordByRecordId(recordId);
    }

    /**
     * 查询视频学习记录，记录学生观看视频的行为数据列表
     * 
     * @param videoLearningRecord 视频学习记录，记录学生观看视频的行为数据
     * @return 视频学习记录，记录学生观看视频的行为数据
     */
    @Override
    public List<VideoLearningRecord> selectVideoLearningRecordList(VideoLearningRecord videoLearningRecord)
    {
        return videoLearningRecordMapper.selectVideoLearningRecordList(videoLearningRecord);
    }

    /**
     * 新增视频学习记录，记录学生观看视频的行为数据
     * 
     * @param videoLearningRecord 视频学习记录，记录学生观看视频的行为数据
     * @return 结果
     */
    @Override
    public int insertVideoLearningRecord(VideoLearningRecord videoLearningRecord)
    {
        log.debug("[insertVideoLearningRecord] 入参: {}", videoLearningRecord);
        videoLearningRecord.setCreateTime(DateUtils.getNowDate());
        videoLearningRecord.setLastWatchTime(videoLearningRecord.getCreateTime());
        int rows = videoLearningRecordMapper.insertVideoLearningRecord(videoLearningRecord);
        log.debug("[insertVideoLearningRecord] 影响行数: {} 生成recordId:{}", rows, videoLearningRecord.getRecordId());
        return rows;
    }

    /**
     * 修改视频学习记录，记录学生观看视频的行为数据
     * 
     * @param videoLearningRecord 视频学习记录，记录学生观看视频的行为数据
     * @return 结果
     */
    @Override
    public int updateVideoLearningRecord(VideoLearningRecord videoLearningRecord)
    {
        log.debug("[updateVideoLearningRecord] 入参: {}", videoLearningRecord);
        videoLearningRecord.setUpdateTime(DateUtils.getNowDate());
        int rows = videoLearningRecordMapper.updateVideoLearningRecord(videoLearningRecord);
        log.debug("[updateVideoLearningRecord] 影响行数: {}", rows);
        return rows;
    }

    /**
     * 批量删除视频学习记录，记录学生观看视频的行为数据
     * 
     * @param recordIds 需要删除的视频学习记录，记录学生观看视频的行为数据主键
     * @return 结果
     */
    @Override
    public int deleteVideoLearningRecordByRecordIds(Long[] recordIds)
    {
        return videoLearningRecordMapper.deleteVideoLearningRecordByRecordIds(recordIds);
    }

    /**
     * 删除视频学习记录，记录学生观看视频的行为数据信息
     * 
     * @param recordId 视频学习记录，记录学生观看视频的行为数据主键
     * @return 结果
     */
    @Override
    public int deleteVideoLearningRecordByRecordId(Long recordId)
    {
        return videoLearningRecordMapper.deleteVideoLearningRecordByRecordId(recordId);
    }

    @Override
public int saveOrUpdate(VideoLearningRecord videoLearningRecord) {
    log.debug("[saveOrUpdate] 入参: {}", videoLearningRecord);
    // 1. 兼容前端传 videoId 或 learning_resource.resource_id
    log.debug("[saveOrUpdate] Step1: 处理资源ID,当前resourceId={}", videoLearningRecord.getResourceId());
    if (videoLearningRecord.getResourceId() != null) {
        // 先查 learning_resource
        LearningResource lr = learningResourceService.selectLearningResourceByResourceId(videoLearningRecord.getResourceId());
        if (lr == null) {
            // 说明传的是 video_resource.video_id，需要查 video_resource
            VideoResource vr = videoResourceMapper.selectVideoResourceByVideoId(videoLearningRecord.getResourceId());
            if (vr != null) {
                log.debug("[saveOrUpdate] video_resource 找到记录 videoId={}, courseId={}", vr.getVideoId(), vr.getCourseId());
                // 检查 learning_resource 是否已存在（防止重复）
                LearningResource existLr = learningResourceService.selectLearningResourceByResourceId(vr.getVideoId());
                if (existLr != null) {
                    log.debug("[saveOrUpdate] learning_resource 已存在 resourceId={}", existLr.getResourceId());
                    videoLearningRecord.setResourceId(existLr.getResourceId());
                } else {
                    // 新建 learning_resource
                    log.debug("[saveOrUpdate] 创建新的 learning_resource 记录");
                    LearningResource newLr = new LearningResource();
                    newLr.setCourseId(vr.getCourseId());
                    newLr.setResourceName(vr.getVideoName());
                    newLr.setResourcePath(vr.getVideoPath());
                    newLr.setFileSize(vr.getFileSize());
                    newLr.setUploaderId(vr.getSuploaderId());
                    newLr.setResourceType("video");
                    newLr.setUploadTime(DateUtils.getNowDate());
                    learningResourceService.insertLearningResource(newLr);
                    log.debug("[saveOrUpdate] 新 learning_resource 保存成功 resourceId={}", newLr.getResourceId());
                    // 用新 learning_resource 的 resourceId
                    videoLearningRecord.setResourceId(newLr.getResourceId());
                }
            } else {
                log.warn("[saveOrUpdate] 未在 video_resource 表中找到 videoId={} 的记录", videoLearningRecord.getResourceId());
            }
        }
        // 如果 lr 不为 null，说明本来就是 learning_resource.resource_id，无需处理
    }

    // 2. 查重（同 learningRecordId + resourceId 只保留一条，更新）
    log.debug("[saveOrUpdate] Step2: 检查是否已存在记录 learningRecordId={}, resourceId={}"
            , videoLearningRecord.getLearningRecordId(), videoLearningRecord.getResourceId());
    if (videoLearningRecord.getLearningRecordId() != null && videoLearningRecord.getResourceId() != null) {
        VideoLearningRecord exist = videoLearningRecordMapper.selectByLearningAndResourceId(
                videoLearningRecord.getLearningRecordId(), videoLearningRecord.getResourceId());
        if (exist != null) {
            log.debug("[saveOrUpdate] 已存在记录 recordId={}, 执行更新", exist.getRecordId());
            // 更新必要字段
            exist.setWatchedDuration(videoLearningRecord.getWatchedDuration());
            exist.setTotalDuration(videoLearningRecord.getTotalDuration());
            exist.setCompletionRate(videoLearningRecord.getCompletionRate());
            exist.setSkipSegments(videoLearningRecord.getSkipSegments());
            exist.setRepeatSegments(videoLearningRecord.getRepeatSegments());
            exist.setLastWatchTime(videoLearningRecord.getLastWatchTime());
            return updateVideoLearningRecord(exist);
        }
    }

    // 3. 新增
    log.debug("[saveOrUpdate] Step3: 不存在记录, 执行新增");
    videoLearningRecord.setCreateTime(DateUtils.getNowDate());
    return insertVideoLearningRecord(videoLearningRecord);
}
}
