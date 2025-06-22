package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.CoursePaperLibrary;

/**
 * 课程试卷库，一个课程对应一个试卷库Service接口
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public interface ICoursePaperLibraryService 
{
    /**
     * 查询课程试卷库，一个课程对应一个试卷库
     * 
     * @param libraryId 课程试卷库，一个课程对应一个试卷库主键
     * @return 课程试卷库，一个课程对应一个试卷库
     */
    public CoursePaperLibrary selectCoursePaperLibraryByLibraryId(Long libraryId);

    /**
     * 查询课程试卷库，一个课程对应一个试卷库列表
     * 
     * @param coursePaperLibrary 课程试卷库，一个课程对应一个试卷库
     * @return 课程试卷库，一个课程对应一个试卷库集合
     */
    public List<CoursePaperLibrary> selectCoursePaperLibraryList(CoursePaperLibrary coursePaperLibrary);

    /**
     * 新增课程试卷库，一个课程对应一个试卷库
     * 
     * @param coursePaperLibrary 课程试卷库，一个课程对应一个试卷库
     * @return 结果
     */
    public int insertCoursePaperLibrary(CoursePaperLibrary coursePaperLibrary);

    /**
     * 修改课程试卷库，一个课程对应一个试卷库
     * 
     * @param coursePaperLibrary 课程试卷库，一个课程对应一个试卷库
     * @return 结果
     */
    public int updateCoursePaperLibrary(CoursePaperLibrary coursePaperLibrary);

    /**
     * 批量删除课程试卷库，一个课程对应一个试卷库
     * 
     * @param libraryIds 需要删除的课程试卷库，一个课程对应一个试卷库主键集合
     * @return 结果
     */
    public int deleteCoursePaperLibraryByLibraryIds(Long[] libraryIds);

    /**
     * 删除课程试卷库，一个课程对应一个试卷库信息
     * 
     * @param libraryId 课程试卷库，一个课程对应一个试卷库主键
     * @return 结果
     */
    public int deleteCoursePaperLibraryByLibraryId(Long libraryId);

    /**
     * 根据课程ID查询试卷库
     * 
     * @param courseId 课程ID
     * @return 试卷库信息
     */
    public CoursePaperLibrary selectCoursePaperLibraryByCourseId(Long courseId);
}
