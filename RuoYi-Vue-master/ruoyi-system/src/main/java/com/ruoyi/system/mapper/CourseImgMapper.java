package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.CourseImg;

/**
 * 课程概念图，存储课程的概念图URL（1对多关系）Mapper接口
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public interface CourseImgMapper 
{
    /**
     * 查询课程概念图，存储课程的概念图URL（1对多关系）
     * 
     * @param mapId 课程概念图，存储课程的概念图URL（1对多关系）主键
     * @return 课程概念图，存储课程的概念图URL（1对多关系）
     */
    public CourseImg selectCourseImgByMapId(Long mapId);

    /**
     * 查询课程概念图，存储课程的概念图URL（1对多关系）列表
     * 
     * @param courseImg 课程概念图，存储课程的概念图URL（1对多关系）
     * @return 课程概念图，存储课程的概念图URL（1对多关系）集合
     */
    public List<CourseImg> selectCourseImgList(CourseImg courseImg);

    /**
     * 新增课程概念图，存储课程的概念图URL（1对多关系）
     * 
     * @param courseImg 课程概念图，存储课程的概念图URL（1对多关系）
     * @return 结果
     */
    public int insertCourseImg(CourseImg courseImg);

    /**
     * 修改课程概念图，存储课程的概念图URL（1对多关系）
     * 
     * @param courseImg 课程概念图，存储课程的概念图URL（1对多关系）
     * @return 结果
     */
    public int updateCourseImg(CourseImg courseImg);

    /**
     * 删除课程概念图，存储课程的概念图URL（1对多关系）
     * 
     * @param mapId 课程概念图，存储课程的概念图URL（1对多关系）主键
     * @return 结果
     */
    public int deleteCourseImgByMapId(Long mapId);

    /**
     * 批量删除课程概念图，存储课程的概念图URL（1对多关系）
     * 
     * @param mapIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCourseImgByMapIds(Long[] mapIds);
}
