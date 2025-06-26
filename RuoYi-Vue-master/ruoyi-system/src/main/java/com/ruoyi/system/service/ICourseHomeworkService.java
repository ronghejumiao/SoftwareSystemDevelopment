package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.CourseHomework;

/**
 * 课程作业Service接口
 * 
 * @author ruoyi
 * @date 2025-06-25
 */
public interface ICourseHomeworkService 
{
    /**
     * 查询课程作业
     * 
     * @param homeworkId 课程作业主键
     * @return 课程作业
     */
    public CourseHomework selectCourseHomeworkByHomeworkId(Long homeworkId);

    /**
     * 查询课程作业列表
     * 
     * @param courseHomework 课程作业
     * @return 课程作业集合
     */
    public List<CourseHomework> selectCourseHomeworkList(CourseHomework courseHomework);

    /**
     * 新增课程作业
     * 
     * @param courseHomework 课程作业
     * @return 结果
     */
    public int insertCourseHomework(CourseHomework courseHomework);

    /**
     * 修改课程作业
     * 
     * @param courseHomework 课程作业
     * @return 结果
     */
    public int updateCourseHomework(CourseHomework courseHomework);

    /**
     * 批量删除课程作业
     * 
     * @param homeworkIds 需要删除的课程作业主键集合
     * @return 结果
     */
    public int deleteCourseHomeworkByHomeworkIds(Long[] homeworkIds);

    /**
     * 删除课程作业信息
     * 
     * @param homeworkId 课程作业主键
     * @return 结果
     */
    public int deleteCourseHomeworkByHomeworkId(Long homeworkId);

    /**
     * 根据课程ID查询课程作业列表
     * @param courseId 课程ID
     * @return 课程作业集合
     */
    public List<CourseHomework> selectCourseHomeworkListByCourseId(Long courseId);

    /**
     * 根据作业ID列表批量查询作业详情
     * @param homeworkIds 作业ID列表
     * @return 作业详情集合
     */
    public List<CourseHomework> selectCourseHomeworkListByIds(List<Long> homeworkIds);
}
