package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.CourseImgMapper;
import com.ruoyi.system.domain.CourseImg;
import com.ruoyi.system.service.ICourseImgService;

/**
 * 课程概念图，存储课程的概念图URL（1对多关系）Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
@Service
public class CourseImgServiceImpl implements ICourseImgService 
{
    @Autowired
    private CourseImgMapper courseImgMapper;

    /**
     * 查询课程概念图，存储课程的概念图URL（1对多关系）
     * 
     * @param mapId 课程概念图，存储课程的概念图URL（1对多关系）主键
     * @return 课程概念图，存储课程的概念图URL（1对多关系）
     */
    @Override
    public CourseImg selectCourseImgByMapId(Long mapId)
    {
        return courseImgMapper.selectCourseImgByMapId(mapId);
    }

    /**
     * 查询课程概念图，存储课程的概念图URL（1对多关系）列表
     * 
     * @param courseImg 课程概念图，存储课程的概念图URL（1对多关系）
     * @return 课程概念图，存储课程的概念图URL（1对多关系）
     */
    @Override
    public List<CourseImg> selectCourseImgList(CourseImg courseImg)
    {
        return courseImgMapper.selectCourseImgList(courseImg);
    }

    /**
     * 新增课程概念图，存储课程的概念图URL（1对多关系）
     * 
     * @param courseImg 课程概念图，存储课程的概念图URL（1对多关系）
     * @return 结果
     */
    @Override
    public int insertCourseImg(CourseImg courseImg)
    {
        courseImg.setCreateTime(DateUtils.getNowDate());
        return courseImgMapper.insertCourseImg(courseImg);
    }

    /**
     * 修改课程概念图，存储课程的概念图URL（1对多关系）
     * 
     * @param courseImg 课程概念图，存储课程的概念图URL（1对多关系）
     * @return 结果
     */
    @Override
    public int updateCourseImg(CourseImg courseImg)
    {
        courseImg.setUpdateTime(DateUtils.getNowDate());
        return courseImgMapper.updateCourseImg(courseImg);
    }

    /**
     * 批量删除课程概念图，存储课程的概念图URL（1对多关系）
     * 
     * @param mapIds 需要删除的课程概念图，存储课程的概念图URL（1对多关系）主键
     * @return 结果
     */
    @Override
    public int deleteCourseImgByMapIds(Long[] mapIds)
    {
        return courseImgMapper.deleteCourseImgByMapIds(mapIds);
    }

    /**
     * 删除课程概念图，存储课程的概念图URL（1对多关系）信息
     * 
     * @param mapId 课程概念图，存储课程的概念图URL（1对多关系）主键
     * @return 结果
     */
    @Override
    public int deleteCourseImgByMapId(Long mapId)
    {
        return courseImgMapper.deleteCourseImgByMapId(mapId);
    }
}
