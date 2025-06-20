package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.VideoLearningRecord;

/**
 * 视频学习记录，记录学生观看视频的行为数据Mapper接口
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public interface VideoLearningRecordMapper 
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
     * 删除视频学习记录，记录学生观看视频的行为数据
     * 
     * @param recordId 视频学习记录，记录学生观看视频的行为数据主键
     * @return 结果
     */
    public int deleteVideoLearningRecordByRecordId(Long recordId);

    /**
     * 批量删除视频学习记录，记录学生观看视频的行为数据
     * 
     * @param recordIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteVideoLearningRecordByRecordIds(Long[] recordIds);
}
