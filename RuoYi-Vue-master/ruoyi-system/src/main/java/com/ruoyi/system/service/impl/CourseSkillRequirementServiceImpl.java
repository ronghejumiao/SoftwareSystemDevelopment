package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.CourseSkillRequirementMapper;
import com.ruoyi.system.domain.CourseSkillRequirement;
import com.ruoyi.system.service.ICourseSkillRequirementService;

/**
 * 课程能力要求，一个课程包含多个能力要求Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
@Service
public class CourseSkillRequirementServiceImpl implements ICourseSkillRequirementService 
{
    @Autowired
    private CourseSkillRequirementMapper courseSkillRequirementMapper;

    /**
     * 查询课程能力要求，一个课程包含多个能力要求
     * 
     * @param requirementId 课程能力要求，一个课程包含多个能力要求主键
     * @return 课程能力要求，一个课程包含多个能力要求
     */
    @Override
    public CourseSkillRequirement selectCourseSkillRequirementByRequirementId(Long requirementId)
    {
        return courseSkillRequirementMapper.selectCourseSkillRequirementByRequirementId(requirementId);
    }

    /**
     * 查询课程能力要求，一个课程包含多个能力要求列表
     * 
     * @param courseSkillRequirement 课程能力要求，一个课程包含多个能力要求
     * @return 课程能力要求，一个课程包含多个能力要求
     */
    @Override
    public List<CourseSkillRequirement> selectCourseSkillRequirementList(CourseSkillRequirement courseSkillRequirement)
    {
        return courseSkillRequirementMapper.selectCourseSkillRequirementList(courseSkillRequirement);
    }

    /**
     * 新增课程能力要求，一个课程包含多个能力要求
     * 
     * @param courseSkillRequirement 课程能力要求，一个课程包含多个能力要求
     * @return 结果
     */
    @Override
    public int insertCourseSkillRequirement(CourseSkillRequirement courseSkillRequirement)
    {
        courseSkillRequirement.setCreateTime(DateUtils.getNowDate());
        return courseSkillRequirementMapper.insertCourseSkillRequirement(courseSkillRequirement);
    }

    /**
     * 修改课程能力要求，一个课程包含多个能力要求
     * 
     * @param courseSkillRequirement 课程能力要求，一个课程包含多个能力要求
     * @return 结果
     */
    @Override
    public int updateCourseSkillRequirement(CourseSkillRequirement courseSkillRequirement)
    {
        courseSkillRequirement.setUpdateTime(DateUtils.getNowDate());
        return courseSkillRequirementMapper.updateCourseSkillRequirement(courseSkillRequirement);
    }

    /**
     * 批量删除课程能力要求，一个课程包含多个能力要求
     * 
     * @param requirementIds 需要删除的课程能力要求，一个课程包含多个能力要求主键
     * @return 结果
     */
    @Override
    public int deleteCourseSkillRequirementByRequirementIds(Long[] requirementIds)
    {
        return courseSkillRequirementMapper.deleteCourseSkillRequirementByRequirementIds(requirementIds);
    }

    /**
     * 删除课程能力要求，一个课程包含多个能力要求信息
     * 
     * @param requirementId 课程能力要求，一个课程包含多个能力要求主键
     * @return 结果
     */
    @Override
    public int deleteCourseSkillRequirementByRequirementId(Long requirementId)
    {
        return courseSkillRequirementMapper.deleteCourseSkillRequirementByRequirementId(requirementId);
    }
}
