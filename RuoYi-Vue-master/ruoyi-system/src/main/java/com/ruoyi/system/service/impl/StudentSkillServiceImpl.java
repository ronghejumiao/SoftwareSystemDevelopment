package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Date;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.StudentSkillMapper;
import com.ruoyi.system.mapper.CourseSkillRequirementMapper;
import com.ruoyi.system.domain.StudentSkill;
import com.ruoyi.system.domain.CourseSkillRequirement;
import com.ruoyi.system.service.IStudentSkillService;

/**
 * 学生能力，基于课程能力要求构建Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
@Service
public class StudentSkillServiceImpl implements IStudentSkillService 
{
    @Autowired
    private StudentSkillMapper studentSkillMapper;

    @Autowired
    private CourseSkillRequirementMapper courseSkillRequirementMapper;

    /**
     * 查询学生能力，基于课程能力要求构建
     * 
     * @param id 学生能力，基于课程能力要求构建主键
     * @return 学生能力，基于课程能力要求构建
     */
    @Override
    public StudentSkill selectStudentSkillById(Long id)
    {
        return studentSkillMapper.selectStudentSkillById(id);
    }

    /**
     * 查询学生能力，基于课程能力要求构建列表
     * 
     * @param studentSkill 学生能力，基于课程能力要求构建
     * @return 学生能力，基于课程能力要求构建
     */
    @Override
    public List<StudentSkill> selectStudentSkillList(StudentSkill studentSkill)
    {
        return studentSkillMapper.selectStudentSkillList(studentSkill);
    }

    /**
     * 新增学生能力，基于课程能力要求构建
     * 
     * @param studentSkill 学生能力，基于课程能力要求构建
     * @return 结果
     */
    @Override
    public int insertStudentSkill(StudentSkill studentSkill)
    {
        return studentSkillMapper.insertStudentSkill(studentSkill);
    }

    /**
     * 修改学生能力，基于课程能力要求构建
     * 
     * @param studentSkill 学生能力，基于课程能力要求构建
     * @return 结果
     */
    @Override
    public int updateStudentSkill(StudentSkill studentSkill)
    {
        studentSkill.setUpdateTime(DateUtils.getNowDate());
        return studentSkillMapper.updateStudentSkill(studentSkill);
    }

    /**
     * 批量删除学生能力，基于课程能力要求构建
     * 
     * @param ids 需要删除的学生能力，基于课程能力要求构建主键
     * @return 结果
     */
    @Override
    public int deleteStudentSkillByIds(Long[] ids)
    {
        return studentSkillMapper.deleteStudentSkillByIds(ids);
    }

    /**
     * 删除学生能力，基于课程能力要求构建信息
     * 
     * @param id 学生能力，基于课程能力要求构建主键
     * @return 结果
     */
    @Override
    public int deleteStudentSkillById(Long id)
    {
        return studentSkillMapper.deleteStudentSkillById(id);
    }

    /**
     * 根据学生ID和课程ID查询学生能力
     * 
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 学生能力列表
     */
    @Override
    public List<StudentSkill> selectStudentSkillByStudentAndCourse(Long studentId, Long courseId)
    {
        return studentSkillMapper.selectStudentSkillByStudentAndCourse(studentId, courseId);
    }

    /**
     * 初始化学生课程能力（为缺失的能力要求创建记录）
     * 
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 结果
     */
    @Override
    public int initStudentCourseSkills(Long studentId, Long courseId)
    {
        // 查询课程的所有能力要求
        CourseSkillRequirement requirement = new CourseSkillRequirement();
        requirement.setCourseId(courseId);
        List<CourseSkillRequirement> requirements = courseSkillRequirementMapper.selectCourseSkillRequirementList(requirement);
        
        if (requirements.isEmpty()) {
            return 0; // 没有能力要求，无需初始化
        }
        
        // 查询学生已有的能力记录
        List<StudentSkill> existingSkills = studentSkillMapper.selectStudentSkillByStudentAndCourse(studentId, courseId);
        
        // 找出缺失的能力要求
        int createdCount = 0;
        for (CourseSkillRequirement req : requirements) {
            boolean exists = existingSkills.stream()
                .anyMatch(skill -> skill.getRequirementId().equals(req.getRequirementId()));
            
            if (!exists) {
                // 创建缺失的能力记录
                StudentSkill newSkill = new StudentSkill();
                newSkill.setStudentId(studentId);
                newSkill.setRequirementId(req.getRequirementId());
                newSkill.setSkillScore(java.math.BigDecimal.ZERO); // 初始分数为0
                newSkill.setUpdateTime(DateUtils.getNowDate());
                newSkill.setUpdateReason("系统自动初始化");
                
                studentSkillMapper.insertStudentSkill(newSkill);
                createdCount++;
            }
        }
        
        return createdCount;
    }

    /**
     * 更新学生能力分数
     * 
     * @param studentId 学生ID
     * @param requirementId 能力要求ID
     * @param skillScore 能力分数
     * @param updateReason 更新原因
     * @return 结果
     */
    @Override
    public int updateStudentSkillScore(Long studentId, Long requirementId, Double skillScore, String updateReason) {
        return studentSkillMapper.updateStudentSkillScore(studentId, requirementId, skillScore, updateReason);
    }

    /**
     * 根据学生ID查询全部能力
     * @param studentId 学生ID
     * @return 学生能力列表
     */
    @Override
    public List<StudentSkill> selectStudentSkillByStudent(Long studentId) {
        StudentSkill query = new StudentSkill();
        query.setStudentId(studentId);
        return studentSkillMapper.selectStudentSkillList(query);
    }
}
