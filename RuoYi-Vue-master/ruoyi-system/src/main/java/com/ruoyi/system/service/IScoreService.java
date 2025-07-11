package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.Score;

/**
 * 成绩管理Service接口
 * 
 * @author ruoyi
 * @date 2025-06-22
 */
public interface IScoreService 
{
    /**
     * 查询成绩管理
     * 
     * @param scoreId 成绩管理主键
     * @return 成绩管理
     */
    public Score selectScoreByScoreId(Long scoreId);

    /**
     * 查询成绩管理列表
     * 
     * @param score 成绩管理
     * @return 成绩管理集合
     */
    public List<Score> selectScoreList(Score score);

    /**
     * 新增成绩管理
     * 
     * @param score 成绩管理
     * @return 结果
     */
    public int insertScore(Score score);

    /**
     * 修改成绩管理
     * 
     * @param score 成绩管理
     * @return 结果
     */
    public int updateScore(Score score);

    /**
     * 批量删除成绩管理
     * 
     * @param scoreIds 需要删除的成绩管理主键集合
     * @return 结果
     */
    public int deleteScoreByScoreIds(Long[] scoreIds);

    /**
     * 删除成绩管理信息
     * 
     * @param scoreId 成绩管理主键
     * @return 结果
     */
    public int deleteScoreByScoreId(Long scoreId);

    /**
     * 根据用户ID和课程ID查询成绩
     * 
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 成绩列表
     */
    public List<Score> selectScoreByUserAndCourse(Long userId, Long courseId);

    /**
     * 根据用户ID查询所有成绩
     * 
     * @param userId 用户ID
     * @return 成绩列表
     */
    public List<Score> selectScoreByUserId(Long userId);

    /**
     * 根据学习记录ID列表查询成绩列表
     * @param recordIds 学习记录ID列表
     * @return 成绩列表
     */
    public List<Score> selectScoreListByLearningRecords(List<Long> recordIds);
}
