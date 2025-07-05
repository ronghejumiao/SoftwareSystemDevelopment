package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;

/**
 * 学生能力评估服务接口
 *
 * @author ruoyi
 * @date 2025-01-20
 */
public interface IStudentSkillAssessmentService {
    /**
     * 评估单个学生的能力
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 能力要求ID -> 分数的映射
     */
    Map<Long, Double> assessStudentSkill(Long studentId, Long courseId);

    /**
     * 批量评估课程中所有学生的能力
     * @param courseId 课程ID
     * @return 学生评估结果列表
     */
    List<Map<String, Object>> batchAssessAllStudentsSkill(Long courseId);

    /**
     * 更新学生能力分数
     * @param studentId 学生ID
     * @param requirementId 能力要求ID
     * @param score 分数
     * @param reason 更新原因
     * @return 是否成功
     */
    boolean updateStudentSkillScore(Long studentId, Long requirementId, double score, String reason);

    /**
     * 获取学生能力评估报告
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 详细的能力评估报告
     */
    Map<String, Object> getStudentSkillReport(Long studentId, Long courseId);

    /**
     * 获取课程所有学生的能力评估报告
     * @param courseId 课程ID
     * @return 所有学生的能力评估报告列表
     */
    List<Map<String, Object>> getCourseSkillReport(Long courseId);

    /**
     * 人工调整能力分数
     * @param studentId 学生ID
     * @param requirementId 能力要求ID
     * @param score 调整后的分数
     * @param reason 调整原因
     * @return 是否成功
     */
    boolean manualAdjustSkillScore(Long studentId, Long requirementId, double score, String reason);

    /**
     * 数据变更时触发能力评估
     * @param studentId 学生ID
     * @param courseId 课程ID
     */
    void triggerAssessmentOnDataChange(Long studentId, Long courseId);
} 