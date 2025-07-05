package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
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
import com.ruoyi.system.service.IStudentSkillAssessmentService;
import com.ruoyi.system.service.ILearningRecordService;
import com.ruoyi.system.domain.LearningRecord;
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
    
    @Autowired
    private IStudentSkillAssessmentService studentSkillAssessmentService;
    
    @Autowired
    private ILearningRecordService learningRecordService;

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
        
        // 仅在 isEnd==true 时触发能力评估
        Object isEndObj = null;
        if (videoLearningRecord.getParams() != null) {
            isEndObj = videoLearningRecord.getParams().get("isEnd");
        }
        boolean isEnd = isEndObj != null && ("true".equalsIgnoreCase(isEndObj.toString()) || Boolean.TRUE.equals(isEndObj));
        if (rows > 0 && isEnd) {
            triggerAssessment(videoLearningRecord.getLearningRecordId());
        }
        
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
        
        // 仅在 isEnd==true 时触发能力评估
        Object isEndObj = null;
        if (videoLearningRecord.getParams() != null) {
            isEndObj = videoLearningRecord.getParams().get("isEnd");
        }
        boolean isEnd = isEndObj != null && ("true".equalsIgnoreCase(isEndObj.toString()) || Boolean.TRUE.equals(isEndObj));
        if (rows > 0 && isEnd) {
            triggerAssessment(videoLearningRecord.getLearningRecordId());
        }
        
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
        // 获取要删除的记录信息，用于触发评估
        List<VideoLearningRecord> records = videoLearningRecordMapper.selectVideoLearningRecordByRecordIds(recordIds);
        
        int result = videoLearningRecordMapper.deleteVideoLearningRecordByRecordIds(recordIds);
        
        // 触发能力评估
        if (result > 0 && records != null) {
            for (VideoLearningRecord record : records) {
                triggerAssessment(record.getLearningRecordId());
            }
        }
        
        return result;
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
        // 获取要删除的记录信息，用于触发评估
        VideoLearningRecord record = videoLearningRecordMapper.selectVideoLearningRecordByRecordId(recordId);
        
        int result = videoLearningRecordMapper.deleteVideoLearningRecordByRecordId(recordId);
        
        // 触发能力评估
        if (result > 0 && record != null) {
            triggerAssessment(record.getLearningRecordId());
        }
        
        return result;
    }

    @Override
    public int saveOrUpdate(VideoLearningRecord videoLearningRecord) {
        log.debug("[saveOrUpdate] 入参: {}", videoLearningRecord);
        
        // 直接使用传入的resourceId，不再自动创建learning_resource记录
        // resourceId现在直接对应video_resource.video_id
        log.debug("[saveOrUpdate] 使用resourceId: {}", videoLearningRecord.getResourceId());

        // 2. 查重（同 learningRecordId + resourceId 只保留一条，更新）
        VideoLearningRecord existRecord = videoLearningRecordMapper.selectByLearningRecordIdAndResourceId(
            videoLearningRecord.getLearningRecordId(), 
            videoLearningRecord.getResourceId()
        );
        
        if (existRecord != null) {
            log.debug("[saveOrUpdate] 找到已存在记录 recordId={}, 执行更新", existRecord.getRecordId());
            // 更新已存在的记录，保留原有的观看进度
            videoLearningRecord.setRecordId(existRecord.getRecordId());
            
            // 如果传入的watchedDuration为0，保留原有的观看进度
            if (videoLearningRecord.getWatchedDuration() == null || videoLearningRecord.getWatchedDuration() == 0) {
                videoLearningRecord.setWatchedDuration(existRecord.getWatchedDuration());
                videoLearningRecord.setCompletionRate(existRecord.getCompletionRate());
            }
            
            // 合并跳过片段和重复观看片段
            if (existRecord.getSkipSegments() != null && !existRecord.getSkipSegments().isEmpty()) {
                String existingSkips = existRecord.getSkipSegments();
                String newSkips = videoLearningRecord.getSkipSegments();
                if (newSkips != null && !newSkips.isEmpty()) {
                    // 合并并去重
                    Set<String> allSkips = new HashSet<>();
                    allSkips.addAll(Arrays.asList(existingSkips.split(",")));
                    allSkips.addAll(Arrays.asList(newSkips.split(",")));
                    videoLearningRecord.setSkipSegments(String.join(",", allSkips));
                } else {
                    videoLearningRecord.setSkipSegments(existingSkips);
                }
            }
            
            if (existRecord.getRepeatSegments() != null && !existRecord.getRepeatSegments().isEmpty()) {
                String existingRepeats = existRecord.getRepeatSegments();
                String newRepeats = videoLearningRecord.getRepeatSegments();
                if (newRepeats != null && !newRepeats.isEmpty()) {
                    // 合并并去重
                    Set<String> allRepeats = new HashSet<>();
                    allRepeats.addAll(Arrays.asList(existingRepeats.split(",")));
                    allRepeats.addAll(Arrays.asList(newRepeats.split(",")));
                    videoLearningRecord.setRepeatSegments(String.join(",", allRepeats));
                } else {
                    videoLearningRecord.setRepeatSegments(existingRepeats);
                }
            }
            
            videoLearningRecord.setUpdateTime(DateUtils.getNowDate());
            int result = videoLearningRecordMapper.updateVideoLearningRecord(videoLearningRecord);
            
            // 仅在 isEnd==true 时触发能力评估
            Object isEndObj = null;
            if (videoLearningRecord.getParams() != null) {
                isEndObj = videoLearningRecord.getParams().get("isEnd");
            }
            boolean isEnd = isEndObj != null && ("true".equalsIgnoreCase(isEndObj.toString()) || Boolean.TRUE.equals(isEndObj));
            if (result > 0 && isEnd) {
                triggerAssessment(videoLearningRecord.getLearningRecordId());
            }
            
            return result;
        } else {
            log.debug("[saveOrUpdate] 未找到已存在记录，执行新增");
            // 新增记录
            videoLearningRecord.setCreateTime(DateUtils.getNowDate());
            videoLearningRecord.setUpdateTime(DateUtils.getNowDate());
            int result = videoLearningRecordMapper.insertVideoLearningRecord(videoLearningRecord);
            
            // 仅在 isEnd==true 时触发能力评估
            Object isEndObj = null;
            if (videoLearningRecord.getParams() != null) {
                isEndObj = videoLearningRecord.getParams().get("isEnd");
            }
            boolean isEnd = isEndObj != null && ("true".equalsIgnoreCase(isEndObj.toString()) || Boolean.TRUE.equals(isEndObj));
            if (result > 0 && isEnd) {
                triggerAssessment(videoLearningRecord.getLearningRecordId());
            }
            
            return result;
        }
    }

    @Override
    public List<VideoLearningRecord> selectVideoLearningRecordByLearningRecord(Long learningRecordId) {
        VideoLearningRecord query = new VideoLearningRecord();
        query.setLearningRecordId(learningRecordId);
        return videoLearningRecordMapper.selectVideoLearningRecordList(query);
    }

    /**
     * 触发能力评估
     * @param learningRecordId 学习记录ID
     */
    private void triggerAssessment(Long learningRecordId) {
        try {
            LearningRecord record = learningRecordService.selectLearningRecordByRecordId(learningRecordId);
            if (record != null) {
                studentSkillAssessmentService.triggerAssessmentOnDataChange(record.getUserId(), record.getCourseId());
            }
        } catch (Exception e) {
            // 记录日志但不影响主业务流程
            log.error("触发能力评估失败: learningRecordId=" + learningRecordId + ", error=" + e.getMessage());
        }
    }
}
