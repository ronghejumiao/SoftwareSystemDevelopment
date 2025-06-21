package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.StudentClassMapper;
import com.ruoyi.system.domain.StudentClass;
import com.ruoyi.system.service.IStudentClassService;

/**
 * 学生班级关联，实现学生与班级的多对多关系Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
@Service
public class StudentClassServiceImpl implements IStudentClassService 
{
    @Autowired
    private StudentClassMapper studentClassMapper;

    /**
     * 查询学生班级关联，实现学生与班级的多对多关系
     * 
     * @param id 学生班级关联，实现学生与班级的多对多关系主键
     * @return 学生班级关联，实现学生与班级的多对多关系
     */
    @Override
    public StudentClass selectStudentClassById(Long id)
    {
        return studentClassMapper.selectStudentClassById(id);
    }

    /**
     * 查询学生班级关联，实现学生与班级的多对多关系列表
     * 
     * @param studentClass 学生班级关联，实现学生与班级的多对多关系
     * @return 学生班级关联，实现学生与班级的多对多关系
     */
    @Override
    public List<StudentClass> selectStudentClassList(StudentClass studentClass)
    {
        return studentClassMapper.selectStudentClassList(studentClass);
    }

    /**
     * 新增学生班级关联，实现学生与班级的多对多关系
     * 
     * @param studentClass 学生班级关联，实现学生与班级的多对多关系
     * @return 结果
     */
    @Override
    public int insertStudentClass(StudentClass studentClass)
    {
        studentClass.setCreateTime(DateUtils.getNowDate());
        return studentClassMapper.insertStudentClass(studentClass);
    }

    /**
     * 修改学生班级关联，实现学生与班级的多对多关系
     * 
     * @param studentClass 学生班级关联，实现学生与班级的多对多关系
     * @return 结果
     */
    @Override
    public int updateStudentClass(StudentClass studentClass)
    {
        studentClass.setUpdateTime(DateUtils.getNowDate());
        return studentClassMapper.updateStudentClass(studentClass);
    }

    /**
     * 批量删除学生班级关联，实现学生与班级的多对多关系
     * 
     * @param ids 需要删除的学生班级关联，实现学生与班级的多对多关系主键
     * @return 结果
     */
    @Override
    public int deleteStudentClassByIds(Long[] ids)
    {
        return studentClassMapper.deleteStudentClassByIds(ids);
    }

    /**
     * 删除学生班级关联，实现学生与班级的多对多关系信息
     * 
     * @param id 学生班级关联，实现学生与班级的多对多关系主键
     * @return 结果
     */
    @Override
    public int deleteStudentClassById(Long id)
    {
        return studentClassMapper.deleteStudentClassById(id);
    }
}
