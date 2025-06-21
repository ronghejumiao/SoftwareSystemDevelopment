package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.StudentClass;

/**
 * 学生班级关联，实现学生与班级的多对多关系Service接口
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public interface IStudentClassService 
{
    /**
     * 查询学生班级关联，实现学生与班级的多对多关系
     * 
     * @param id 学生班级关联，实现学生与班级的多对多关系主键
     * @return 学生班级关联，实现学生与班级的多对多关系
     */
    public StudentClass selectStudentClassById(Long id);

    /**
     * 查询学生班级关联，实现学生与班级的多对多关系列表
     * 
     * @param studentClass 学生班级关联，实现学生与班级的多对多关系
     * @return 学生班级关联，实现学生与班级的多对多关系集合
     */
    public List<StudentClass> selectStudentClassList(StudentClass studentClass);

    /**
     * 新增学生班级关联，实现学生与班级的多对多关系
     * 
     * @param studentClass 学生班级关联，实现学生与班级的多对多关系
     * @return 结果
     */
    public int insertStudentClass(StudentClass studentClass);

    /**
     * 修改学生班级关联，实现学生与班级的多对多关系
     * 
     * @param studentClass 学生班级关联，实现学生与班级的多对多关系
     * @return 结果
     */
    public int updateStudentClass(StudentClass studentClass);

    /**
     * 批量删除学生班级关联，实现学生与班级的多对多关系
     * 
     * @param ids 需要删除的学生班级关联，实现学生与班级的多对多关系主键集合
     * @return 结果
     */
    public int deleteStudentClassByIds(Long[] ids);

    /**
     * 删除学生班级关联，实现学生与班级的多对多关系信息
     * 
     * @param id 学生班级关联，实现学生与班级的多对多关系主键
     * @return 结果
     */
    public int deleteStudentClassById(Long id);
}
