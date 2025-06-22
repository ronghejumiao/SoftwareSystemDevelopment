package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.LearningRecord;

/**
 * 学习记录，记录学生的课程学习关联信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-06-21
 */
public interface LearningRecordMapper 
{
    /**
     * 查询学习记录，记录学生的课程学习关联信息
     * 
     * @param recordId 学习记录，记录学生的课程学习关联信息主键
     * @return 学习记录，记录学生的课程学习关联信息
     */
    public LearningRecord selectLearningRecordByRecordId(Long recordId);

    /**
     * 查询学习记录，记录学生的课程学习关联信息列表
     * 
     * @param learningRecord 学习记录，记录学生的课程学习关联信息
     * @return 学习记录，记录学生的课程学习关联信息集合
     */
    public List<LearningRecord> selectLearningRecordList(LearningRecord learningRecord);

    /**
     * 新增学习记录，记录学生的课程学习关联信息
     * 
     * @param learningRecord 学习记录，记录学生的课程学习关联信息
     * @return 结果
     */
    public int insertLearningRecord(LearningRecord learningRecord);

    /**
     * 修改学习记录，记录学生的课程学习关联信息
     * 
     * @param learningRecord 学习记录，记录学生的课程学习关联信息
     * @return 结果
     */
    public int updateLearningRecord(LearningRecord learningRecord);

    /**
     * 删除学习记录，记录学生的课程学习关联信息
     * 
     * @param recordId 学习记录，记录学生的课程学习关联信息主键
     * @return 结果
     */
    public int deleteLearningRecordByRecordId(Long recordId);

    /**
     * 批量删除学习记录，记录学生的课程学习关联信息
     * 
     * @param recordIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteLearningRecordByRecordIds(Long[] recordIds);
}
