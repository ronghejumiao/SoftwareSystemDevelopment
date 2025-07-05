package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.CourseSkillRequirement;

/**
 * 课程能力要求Service接口
 * 
 * @author ruoyi
 * @date 2025-06-22
 */
public interface ICourseSkillRequirementService 
{
    /**
     * 查询课程能力要求
     * 
     * @param requirementId 课程能力要求主键
     * @return 课程能力要求
     */
    public CourseSkillRequirement selectCourseSkillRequirementByRequirementId(Long requirementId);

    /**
     * 查询课程能力要求列表
     * 
     * @param courseSkillRequirement 课程能力要求
     * @return 课程能力要求集合
     */
    public List<CourseSkillRequirement> selectCourseSkillRequirementList(CourseSkillRequirement courseSkillRequirement);

    /**
     * 新增课程能力要求
     * 
     * @param courseSkillRequirement 课程能力要求
     * @return 结果
     */
    public int insertCourseSkillRequirement(CourseSkillRequirement courseSkillRequirement);

    /**
     * 修改课程能力要求
     * 
     * @param courseSkillRequirement 课程能力要求
     * @return 结果
     */
    public int updateCourseSkillRequirement(CourseSkillRequirement courseSkillRequirement);

    /**
     * 批量删除课程能力要求
     * 
     * @param requirementIds 需要删除的课程能力要求主键集合
     * @return 结果
     */
    public int deleteCourseSkillRequirementByRequirementIds(Long[] requirementIds);

    /**
     * 删除课程能力要求信息
     * 
     * @param requirementId 课程能力要求主键
     * @return 结果
     */
    public int deleteCourseSkillRequirementByRequirementId(Long requirementId);

    /**
     * 根据课程ID查询能力要求
     */
    java.util.List<com.ruoyi.system.domain.CourseSkillRequirement> selectCourseSkillRequirementByCourseId(Long courseId);
}
