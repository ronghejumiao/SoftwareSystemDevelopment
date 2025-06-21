package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.StudentSkillMapper;
import com.ruoyi.system.domain.StudentSkill;
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
}
