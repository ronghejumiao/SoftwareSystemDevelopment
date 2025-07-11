package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.VideoLearningRecord;

/**
 * 视频学习记录，记录学生观看视频的行为数据Service接口
 * 
 * @author ruoyi
 * @date 2025-06-24
 */
public interface IVideoLearningRecordService 
{
    /**
     * 查询视频学习记录，记录学生观看视频的行为数据
     * 
     * @param recordId 视频学习记录，记录学生观看视频的行为数据主键
     * @return 视频学习记录，记录学生观看视频的行为数据
     */
    public VideoLearningRecord selectVideoLearningRecordByRecordId(Long recordId);

    /**
     * 查询视频学习记录，记录学生观看视频的行为数据列表
     * 
     * @param videoLearningRecord 视频学习记录，记录学生观看视频的行为数据
     * @return 视频学习记录，记录学生观看视频的行为数据集合
     */
    public List<VideoLearningRecord> selectVideoLearningRecordList(VideoLearningRecord videoLearningRecord);

    /**
     * 新增视频学习记录，记录学生观看视频的行为数据
     * 
     * @param videoLearningRecord 视频学习记录，记录学生观看视频的行为数据
     * @return 结果
     */
    public int insertVideoLearningRecord(VideoLearningRecord videoLearningRecord);

    /**
     * 修改视频学习记录，记录学生观看视频的行为数据
     * 
     * @param videoLearningRecord 视频学习记录，记录学生观看视频的行为数据
     * @return 结果
     */
    public int updateVideoLearningRecord(VideoLearningRecord videoLearningRecord);

    /**
     * 批量删除视频学习记录，记录学生观看视频的行为数据
     * 
     * @param recordIds 需要删除的视频学习记录，记录学生观看视频的行为数据主键集合
     * @return 结果
     */
    public int deleteVideoLearningRecordByRecordIds(Long[] recordIds);

    /**
     * 删除视频学习记录，记录学生观看视频的行为数据信息
     * 
     * @param recordId 视频学习记录，记录学生观看视频的行为数据主键
     * @return 结果
     */
    public int deleteVideoLearningRecordByRecordId(Long recordId);

    /**
     * 保存观看记录：如果同(learningRecordId, resourceId)记录已存在，则更新；否则新增
     * @param videoLearningRecord 观看记录
     * @return 影响行数
     */
    int saveOrUpdate(VideoLearningRecord videoLearningRecord);

    /**
     * 根据学习记录ID查询视频学习记录列表
     * @param learningRecordId 学习记录ID
     * @return 视频学习记录列表
     */
    public List<VideoLearningRecord> selectVideoLearningRecordByLearningRecord(Long learningRecordId);
}
