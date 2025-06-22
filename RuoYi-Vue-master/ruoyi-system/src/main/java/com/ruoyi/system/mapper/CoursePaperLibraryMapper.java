package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.CoursePaperLibrary;

/**
 * 课程试卷库，一个课程对应一个试卷库Mapper接口
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public interface CoursePaperLibraryMapper 
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
     * 根据课程ID查询试卷库
     * 
     * @param courseId 课程ID
     * @return 课程试卷库
     */
    public CoursePaperLibrary selectCoursePaperLibraryByCourseId(Long courseId);

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
     * 删除课程试卷库，一个课程对应一个试卷库
     * 
     * @param libraryId 课程试卷库，一个课程对应一个试卷库主键
     * @return 结果
     */
    public int deleteCoursePaperLibraryByLibraryId(Long libraryId);

    /**
     * 批量删除课程试卷库，一个课程对应一个试卷库
     * 
     * @param libraryIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCoursePaperLibraryByLibraryIds(Long[] libraryIds);
}
