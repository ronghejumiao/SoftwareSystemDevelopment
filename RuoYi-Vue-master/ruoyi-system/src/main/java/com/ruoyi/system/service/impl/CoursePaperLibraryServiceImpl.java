package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.CoursePaperLibraryMapper;
import com.ruoyi.system.domain.CoursePaperLibrary;
import com.ruoyi.system.service.ICoursePaperLibraryService;

/**
 * 课程试卷库，一个课程对应一个试卷库Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
@Service
public class CoursePaperLibraryServiceImpl implements ICoursePaperLibraryService 
{
    @Autowired
    private CoursePaperLibraryMapper coursePaperLibraryMapper;

    /**
     * 查询课程试卷库，一个课程对应一个试卷库
     * 
     * @param libraryId 课程试卷库，一个课程对应一个试卷库主键
     * @return 课程试卷库，一个课程对应一个试卷库
     */
    @Override
    public CoursePaperLibrary selectCoursePaperLibraryByLibraryId(Long libraryId)
    {
        return coursePaperLibraryMapper.selectCoursePaperLibraryByLibraryId(libraryId);
    }

    /**
     * 查询课程试卷库，一个课程对应一个试卷库列表
     * 
     * @param coursePaperLibrary 课程试卷库，一个课程对应一个试卷库
     * @return 课程试卷库，一个课程对应一个试卷库
     */
    @Override
    public List<CoursePaperLibrary> selectCoursePaperLibraryList(CoursePaperLibrary coursePaperLibrary)
    {
        return coursePaperLibraryMapper.selectCoursePaperLibraryList(coursePaperLibrary);
    }

    /**
     * 新增课程试卷库，一个课程对应一个试卷库
     * 
     * @param coursePaperLibrary 课程试卷库，一个课程对应一个试卷库
     * @return 结果
     */
    @Override
    public int insertCoursePaperLibrary(CoursePaperLibrary coursePaperLibrary)
    {
        coursePaperLibrary.setCreateTime(DateUtils.getNowDate());
        return coursePaperLibraryMapper.insertCoursePaperLibrary(coursePaperLibrary);
    }

    /**
     * 修改课程试卷库，一个课程对应一个试卷库
     * 
     * @param coursePaperLibrary 课程试卷库，一个课程对应一个试卷库
     * @return 结果
     */
    @Override
    public int updateCoursePaperLibrary(CoursePaperLibrary coursePaperLibrary)
    {
        coursePaperLibrary.setUpdateTime(DateUtils.getNowDate());
        return coursePaperLibraryMapper.updateCoursePaperLibrary(coursePaperLibrary);
    }

    /**
     * 批量删除课程试卷库，一个课程对应一个试卷库
     * 
     * @param libraryIds 需要删除的课程试卷库，一个课程对应一个试卷库主键
     * @return 结果
     */
    @Override
    public int deleteCoursePaperLibraryByLibraryIds(Long[] libraryIds)
    {
        return coursePaperLibraryMapper.deleteCoursePaperLibraryByLibraryIds(libraryIds);
    }

    /**
     * 删除课程试卷库，一个课程对应一个试卷库信息
     * 
     * @param libraryId 课程试卷库，一个课程对应一个试卷库主键
     * @return 结果
     */
    @Override
    public int deleteCoursePaperLibraryByLibraryId(Long libraryId)
    {
        return coursePaperLibraryMapper.deleteCoursePaperLibraryByLibraryId(libraryId);
    }

    /**
     * 根据课程ID查询试卷库
     * 
     * @param courseId 课程ID
     * @return 试卷库信息
     */
    @Override
    public CoursePaperLibrary selectCoursePaperLibraryByCourseId(Long courseId)
    {
        return coursePaperLibraryMapper.selectCoursePaperLibraryByCourseId(courseId);
    }
}
