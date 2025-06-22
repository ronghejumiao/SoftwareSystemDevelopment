package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.CourseMapper;
import com.ruoyi.system.domain.Course;
import com.ruoyi.system.service.ICourseService;

/**
 * 课程信息，存储课程的基本信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-21
 */
@Service
public class CourseServiceImpl implements ICourseService 
{
    @Autowired
    private CourseMapper courseMapper;

    /**
     * 查询课程信息，存储课程的基本信息
     * 
     * @param courseId 课程信息，存储课程的基本信息主键
     * @return 课程信息，存储课程的基本信息
     */
    @Override
    public Course selectCourseByCourseId(Long courseId)
    {
        return courseMapper.selectCourseByCourseId(courseId);
    }

    /**
     * 查询课程信息，存储课程的基本信息列表
     * 
     * @param course 课程信息，存储课程的基本信息
     * @return 课程信息，存储课程的基本信息
     */
    @Override
    public List<Course> selectCourseList(Course course)
    {
        return courseMapper.selectCourseList(course);
    }

    /**
     * 新增课程信息，存储课程的基本信息
     * 
     * @param course 课程信息，存储课程的基本信息
     * @return 结果
     */
    @Override
    public int insertCourse(Course course)
    {
        course.setCreateTime(DateUtils.getNowDate());
        return courseMapper.insertCourse(course);
    }

    /**
     * 修改课程信息，存储课程的基本信息
     * 
     * @param course 课程信息，存储课程的基本信息
     * @return 结果
     */
    @Override
    public int updateCourse(Course course)
    {
        course.setUpdateTime(DateUtils.getNowDate());
        return courseMapper.updateCourse(course);
    }

    /**
     * 批量删除课程信息，存储课程的基本信息
     * 
     * @param courseIds 需要删除的课程信息，存储课程的基本信息主键
     * @return 结果
     */
    @Override
    public int deleteCourseByCourseIds(Long[] courseIds)
    {
        return courseMapper.deleteCourseByCourseIds(courseIds);
    }

    /**
     * 删除课程信息，存储课程的基本信息信息
     * 
     * @param courseId 课程信息，存储课程的基本信息主键
     * @return 结果
     */
    @Override
    public int deleteCourseByCourseId(Long courseId)
    {
        return courseMapper.deleteCourseByCourseId(courseId);
    }
}
