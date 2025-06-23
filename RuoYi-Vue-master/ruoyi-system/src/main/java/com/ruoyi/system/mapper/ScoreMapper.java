package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.Score;

/**
 * 成绩，记录学生的学习成绩信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public interface ScoreMapper 
{
    /**
     * 查询成绩，记录学生的学习成绩信息
     * 
     * @param scoreId 成绩，记录学生的学习成绩信息主键
     * @return 成绩，记录学生的学习成绩信息
     */
    public Score selectScoreByScoreId(Long scoreId);

    /**
     * 查询成绩，记录学生的学习成绩信息列表
     * 
     * @param score 成绩，记录学生的学习成绩信息
     * @return 成绩，记录学生的学习成绩信息集合
     */
    public List<Score> selectScoreList(Score score);

    /**
     * 新增成绩，记录学生的学习成绩信息
     * 
     * @param score 成绩，记录学生的学习成绩信息
     * @return 结果
     */
    public int insertScore(Score score);

    /**
     * 修改成绩，记录学生的学习成绩信息
     * 
     * @param score 成绩，记录学生的学习成绩信息
     * @return 结果
     */
    public int updateScore(Score score);

    /**
     * 删除成绩，记录学生的学习成绩信息
     * 
     * @param scoreId 成绩，记录学生的学习成绩信息主键
     * @return 结果
     */
    public int deleteScoreByScoreId(Long scoreId);

    /**
     * 批量删除成绩，记录学生的学习成绩信息
     * 
     * @param scoreIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteScoreByScoreIds(Long[] scoreIds);

    /**
     * 根据用户ID和课程ID查询成绩
     * 
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 成绩列表
     */
    public List<Score> selectScoreByUserAndCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);

    /**
     * 根据用户ID查询所有成绩
     * 
     * @param userId 用户ID
     * @return 成绩列表
     */
    public List<Score> selectScoreByUserId(@Param("userId") Long userId);
}
