package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.LearningRecordMapper;
import com.ruoyi.system.domain.LearningRecord;
import com.ruoyi.system.service.ILearningRecordService;

/**
 * 学习记录，记录学生的课程学习关联信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
@Service
public class LearningRecordServiceImpl implements ILearningRecordService 
{
    @Autowired
    private LearningRecordMapper learningRecordMapper;

    /**
     * 查询学习记录，记录学生的课程学习关联信息
     * 
     * @param recordId 学习记录，记录学生的课程学习关联信息主键
     * @return 学习记录，记录学生的课程学习关联信息
     */
    @Override
    public LearningRecord selectLearningRecordByRecordId(Long recordId)
    {
        return learningRecordMapper.selectLearningRecordByRecordId(recordId);
    }

    /**
     * 查询学习记录，记录学生的课程学习关联信息列表
     * 
     * @param learningRecord 学习记录，记录学生的课程学习关联信息
     * @return 学习记录，记录学生的课程学习关联信息
     */
    @Override
    public List<LearningRecord> selectLearningRecordList(LearningRecord learningRecord)
    {
        return learningRecordMapper.selectLearningRecordList(learningRecord);
    }

    /**
     * 新增学习记录，记录学生的课程学习关联信息
     * 
     * @param learningRecord 学习记录，记录学生的课程学习关联信息
     * @return 结果
     */
    @Override
    public int insertLearningRecord(LearningRecord learningRecord)
    {
        return learningRecordMapper.insertLearningRecord(learningRecord);
    }

    /**
     * 修改学习记录，记录学生的课程学习关联信息
     * 
     * @param learningRecord 学习记录，记录学生的课程学习关联信息
     * @return 结果
     */
    @Override
    public int updateLearningRecord(LearningRecord learningRecord)
    {
        return learningRecordMapper.updateLearningRecord(learningRecord);
    }

    /**
     * 批量删除学习记录，记录学生的课程学习关联信息
     * 
     * @param recordIds 需要删除的学习记录，记录学生的课程学习关联信息主键
     * @return 结果
     */
    @Override
    public int deleteLearningRecordByRecordIds(Long[] recordIds)
    {
        return learningRecordMapper.deleteLearningRecordByRecordIds(recordIds);
    }

    /**
     * 删除学习记录，记录学生的课程学习关联信息信息
     * 
     * @param recordId 学习记录，记录学生的课程学习关联信息主键
     * @return 结果
     */
    @Override
    public int deleteLearningRecordByRecordId(Long recordId)
    {
        return learningRecordMapper.deleteLearningRecordByRecordId(recordId);
    }
}
