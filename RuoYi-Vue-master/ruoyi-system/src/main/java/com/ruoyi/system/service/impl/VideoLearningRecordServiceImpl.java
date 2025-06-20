package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.VideoLearningRecordMapper;
import com.ruoyi.system.domain.VideoLearningRecord;
import com.ruoyi.system.service.IVideoLearningRecordService;

/**
 * 视频学习记录，记录学生观看视频的行为数据Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
@Service
public class VideoLearningRecordServiceImpl implements IVideoLearningRecordService 
{
    @Autowired
    private VideoLearningRecordMapper videoLearningRecordMapper;

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
        videoLearningRecord.setCreateTime(DateUtils.getNowDate());
        return videoLearningRecordMapper.insertVideoLearningRecord(videoLearningRecord);
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
        videoLearningRecord.setUpdateTime(DateUtils.getNowDate());
        return videoLearningRecordMapper.updateVideoLearningRecord(videoLearningRecord);
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
}
