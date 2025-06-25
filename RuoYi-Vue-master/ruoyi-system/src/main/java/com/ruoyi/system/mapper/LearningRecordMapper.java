package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.LearningRecord;

/**
 * 学习记录，记录学生的课程学习进度和关联信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-06-22
 */
public interface LearningRecordMapper 
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
     * 删除学习记录，记录学生的课程学习进度和关联信息
     * 
     * @param recordId 学习记录，记录学生的课程学习进度和关联信息主键
     * @return 结果
     */
    public int deleteLearningRecordByRecordId(Long recordId);

    /**
     * 批量删除学习记录，记录学生的课程学习进度和关联信息
     * 
     * @param recordIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteLearningRecordByRecordIds(Long[] recordIds);

    /**
     * 根据用户ID和课程ID查询对应的学习记录（若存在返回一条即可）
     *
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 学习记录
     */
    public LearningRecord selectLearningRecordByUserIdAndCourseId(@org.apache.ibatis.annotations.Param("userId") Long userId,
                                                                  @org.apache.ibatis.annotations.Param("courseId") Long courseId);
}
