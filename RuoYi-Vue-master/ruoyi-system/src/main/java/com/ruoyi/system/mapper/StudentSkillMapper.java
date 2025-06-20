package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.StudentSkill;

/**
 * 学生能力，基于课程能力要求构建Mapper接口
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public interface StudentSkillMapper 
{
    /**
     * 查询学生能力，基于课程能力要求构建
     * 
     * @param id 学生能力，基于课程能力要求构建主键
     * @return 学生能力，基于课程能力要求构建
     */
    public StudentSkill selectStudentSkillById(Long id);

    /**
     * 查询学生能力，基于课程能力要求构建列表
     * 
     * @param studentSkill 学生能力，基于课程能力要求构建
     * @return 学生能力，基于课程能力要求构建集合
     */
    public List<StudentSkill> selectStudentSkillList(StudentSkill studentSkill);

    /**
     * 新增学生能力，基于课程能力要求构建
     * 
     * @param studentSkill 学生能力，基于课程能力要求构建
     * @return 结果
     */
    public int insertStudentSkill(StudentSkill studentSkill);

    /**
     * 修改学生能力，基于课程能力要求构建
     * 
     * @param studentSkill 学生能力，基于课程能力要求构建
     * @return 结果
     */
    public int updateStudentSkill(StudentSkill studentSkill);

    /**
     * 删除学生能力，基于课程能力要求构建
     * 
     * @param id 学生能力，基于课程能力要求构建主键
     * @return 结果
     */
    public int deleteStudentSkillById(Long id);

    /**
     * 批量删除学生能力，基于课程能力要求构建
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStudentSkillByIds(Long[] ids);
}
