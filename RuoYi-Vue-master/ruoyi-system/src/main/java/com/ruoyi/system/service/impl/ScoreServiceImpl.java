package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.ScoreMapper;
import com.ruoyi.system.domain.Score;
import com.ruoyi.system.service.IScoreService;

/**
 * 成绩，记录学生的学习成绩信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
@Service
public class ScoreServiceImpl implements IScoreService 
{
    @Autowired
    private ScoreMapper scoreMapper;

    /**
     * 查询成绩，记录学生的学习成绩信息
     * 
     * @param scoreId 成绩，记录学生的学习成绩信息主键
     * @return 成绩，记录学生的学习成绩信息
     */
    @Override
    public Score selectScoreByScoreId(Long scoreId)
    {
        return scoreMapper.selectScoreByScoreId(scoreId);
    }

    /**
     * 查询成绩，记录学生的学习成绩信息列表
     * 
     * @param score 成绩，记录学生的学习成绩信息
     * @return 成绩，记录学生的学习成绩信息
     */
    @Override
    public List<Score> selectScoreList(Score score)
    {
        return scoreMapper.selectScoreList(score);
    }

    /**
     * 新增成绩，记录学生的学习成绩信息
     * 
     * @param score 成绩，记录学生的学习成绩信息
     * @return 结果
     */
    @Override
    public int insertScore(Score score)
    {
        score.setCreateTime(DateUtils.getNowDate());
        return scoreMapper.insertScore(score);
    }

    /**
     * 修改成绩，记录学生的学习成绩信息
     * 
     * @param score 成绩，记录学生的学习成绩信息
     * @return 结果
     */
    @Override
    public int updateScore(Score score)
    {
        score.setUpdateTime(DateUtils.getNowDate());
        return scoreMapper.updateScore(score);
    }

    /**
     * 批量删除成绩，记录学生的学习成绩信息
     * 
     * @param scoreIds 需要删除的成绩，记录学生的学习成绩信息主键
     * @return 结果
     */
    @Override
    public int deleteScoreByScoreIds(Long[] scoreIds)
    {
        return scoreMapper.deleteScoreByScoreIds(scoreIds);
    }

    /**
     * 删除成绩，记录学生的学习成绩信息信息
     * 
     * @param scoreId 成绩，记录学生的学习成绩信息主键
     * @return 结果
     */
    @Override
    public int deleteScoreByScoreId(Long scoreId)
    {
        return scoreMapper.deleteScoreByScoreId(scoreId);
    }
}
