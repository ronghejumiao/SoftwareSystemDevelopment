package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Collections;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.CourseHomeworkMapper;
import com.ruoyi.system.domain.CourseHomework;
import com.ruoyi.system.service.ICourseHomeworkService;

/**
 * 课程作业Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-25
 */
@Service
public class CourseHomeworkServiceImpl implements ICourseHomeworkService 
{
    @Autowired
    private CourseHomeworkMapper courseHomeworkMapper;

    /**
     * 查询课程作业
     * 
     * @param homeworkId 课程作业主键
     * @return 课程作业
     */
    @Override
    public CourseHomework selectCourseHomeworkByHomeworkId(Long homeworkId)
    {
        return courseHomeworkMapper.selectCourseHomeworkByHomeworkId(homeworkId);
    }

    /**
     * 查询课程作业列表
     * 
     * @param courseHomework 课程作业
     * @return 课程作业
     */
    @Override
    public List<CourseHomework> selectCourseHomeworkList(CourseHomework courseHomework)
    {
        return courseHomeworkMapper.selectCourseHomeworkList(courseHomework);
    }

    /**
     * 新增课程作业
     * 
     * @param courseHomework 课程作业
     * @return 结果
     */
    @Override
    public int insertCourseHomework(CourseHomework courseHomework)
    {
        courseHomework.setCreateTime(DateUtils.getNowDate());
        return courseHomeworkMapper.insertCourseHomework(courseHomework);
    }

    /**
     * 修改课程作业
     * 
     * @param courseHomework 课程作业
     * @return 结果
     */
    @Override
    public int updateCourseHomework(CourseHomework courseHomework)
    {
        courseHomework.setUpdateTime(DateUtils.getNowDate());
        return courseHomeworkMapper.updateCourseHomework(courseHomework);
    }

    /**
     * 批量删除课程作业
     * 
     * @param homeworkIds 需要删除的课程作业主键
     * @return 结果
     */
    @Override
    public int deleteCourseHomeworkByHomeworkIds(Long[] homeworkIds)
    {
        return courseHomeworkMapper.deleteCourseHomeworkByHomeworkIds(homeworkIds);
    }

    /**
     * 删除课程作业信息
     * 
     * @param homeworkId 课程作业主键
     * @return 结果
     */
    @Override
    public int deleteCourseHomeworkByHomeworkId(Long homeworkId)
    {
        return courseHomeworkMapper.deleteCourseHomeworkByHomeworkId(homeworkId);
    }

    @Override
    public List<CourseHomework> selectCourseHomeworkListByCourseId(Long courseId) {
        CourseHomework query = new CourseHomework();
        query.setCourseId(courseId);
        query.setStatus("1"); // 只查启用
        return courseHomeworkMapper.selectCourseHomeworkList(query);
    }

    @Override
    public List<CourseHomework> selectCourseHomeworkListByIds(List<Long> homeworkIds) {
        if (homeworkIds == null || homeworkIds.isEmpty()) return Collections.emptyList();
        return courseHomeworkMapper.selectCourseHomeworkListByIds(homeworkIds);
    }
}
