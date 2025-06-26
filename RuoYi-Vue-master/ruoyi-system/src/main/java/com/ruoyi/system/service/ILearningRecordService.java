package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.LearningRecord;

/**
 * 学习记录，记录学生的课程学习进度和关联信息Service接口
 * 
 * @author ruoyi
 * @date 2025-06-22
 */
public interface ILearningRecordService 
{
    /**
     * 查询学习记录，记录学生的课程学习进度和关联信息
     * 
     * @param recordId 学习记录，记录学生的课程学习进度和关联信息主键
     * @return 学习记录，记录学生的课程学习进度和关联信息
     */
    public LearningRecord selectLearningRecordByRecordId(Long recordId);

    /**
     * 查询学习记录，记录学生的课程学习进度和关联信息列表
     * 
     * @param learningRecord 学习记录，记录学生的课程学习进度和关联信息
     * @return 学习记录，记录学生的课程学习进度和关联信息集合
     */
    public List<LearningRecord> selectLearningRecordList(LearningRecord learningRecord);

    /**
     * 新增学习记录，记录学生的课程学习进度和关联信息
     * 
     * @param learningRecord 学习记录，记录学生的课程学习进度和关联信息
     * @return 结果
     */
    public int insertLearningRecord(LearningRecord learningRecord);

    /**
     * 修改学习记录，记录学生的课程学习进度和关联信息
     * 
     * @param learningRecord 学习记录，记录学生的课程学习进度和关联信息
     * @return 结果
     */
    public int updateLearningRecord(LearningRecord learningRecord);

    /**
     * 批量删除学习记录，记录学生的课程学习进度和关联信息
     * 
     * @param recordIds 需要删除的学习记录，记录学生的课程学习进度和关联信息主键集合
     * @return 结果
     */
    public int deleteLearningRecordByRecordIds(Long[] recordIds);

    /**
     * 删除学习记录，记录学生的课程学习进度和关联信息信息
     * 
     * @param recordId 学习记录，记录学生的课程学习进度和关联信息主键
     * @return 结果
     */
    public int deleteLearningRecordByRecordId(Long recordId);

    /**
     * 根据用户ID和课程ID查询学习记录
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 学习记录
     */
    public com.ruoyi.system.domain.LearningRecord selectByUserIdAndCourseId(Long userId, Long courseId);
}
