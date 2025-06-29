package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.ScoreMapper;
import com.ruoyi.system.domain.Score;
import com.ruoyi.system.service.IScoreService;

/**
 * 成绩管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-22
 */
@Service
public class ScoreServiceImpl implements IScoreService 
{
    private static final Logger log = LoggerFactory.getLogger(ScoreServiceImpl.class);

    @Autowired
    private ScoreMapper scoreMapper;

    /**
     * 查询成绩管理
     * 
     * @param scoreId 成绩管理主键
     * @return 成绩管理
     */
    @Override
    public Score selectScoreByScoreId(Long scoreId)
    {
        return scoreMapper.selectScoreByScoreId(scoreId);
    }

    /**
     * 查询成绩管理列表
     * 
     * @param score 成绩管理
     * @return 成绩管理
     */
    @Override
    public List<Score> selectScoreList(Score score)
    {
        log.debug("[selectScoreList] 查询条件: {}", score);
        List<Score> list = scoreMapper.selectScoreList(score);
        log.debug("[selectScoreList] 返回记录数:{} 示例:{}", list.size(), list.isEmpty()?null:list.get(0));
        return list;
    }

    /**
     * 新增成绩管理
     * 
     * @param score 成绩管理
     * @return 结果
     */
    @Override
    public int insertScore(Score score)
    {
        log.debug("[insertScore] 原始入参: {}", score);
        java.util.Date now = DateUtils.getNowDate();
        score.setCreateTime(now);
        // 若前端未传提交时间，默认取当前时间
        if (score.getSubmitTime() == null) {
            score.setSubmitTime(now);
        }
        // insert 时保持 updateTime 与 createTime 一致，便于显示
        score.setUpdateTime(now);
        log.debug("[insertScore] 预插入对象: {}", score);
        int rows = scoreMapper.insertScore(score);
        log.debug("[insertScore] 插入影响行数:{}, 生成scoreId:{}", rows, score.getScoreId());
        return rows;
    }

    /**
     * 修改成绩管理
     * 
     * @param score 成绩管理
     * @return 结果
     */
    @Override
    public int updateScore(Score score)
    {
        score.setUpdateTime(DateUtils.getNowDate());
        return scoreMapper.updateScore(score);
    }

    /**
     * 批量删除成绩管理
     * 
     * @param scoreIds 需要删除的成绩管理主键
     * @return 结果
     */
    @Override
    public int deleteScoreByScoreIds(Long[] scoreIds)
    {
        return scoreMapper.deleteScoreByScoreIds(scoreIds);
    }

    /**
     * 删除成绩管理信息
     * 
     * @param scoreId 成绩管理主键
     * @return 结果
     */
    @Override
    public int deleteScoreByScoreId(Long scoreId)
    {
        return scoreMapper.deleteScoreByScoreId(scoreId);
    }

    /**
     * 根据用户ID和课程ID查询成绩
     * 
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 成绩列表
     */
    @Override
    public List<Score> selectScoreByUserAndCourse(Long userId, Long courseId)
    {
        return scoreMapper.selectScoreByUserAndCourse(userId, courseId);
    }

    /**
     * 根据用户ID查询所有成绩
     * 
     * @param userId 用户ID
     * @return 成绩列表
     */
    @Override
    public List<Score> selectScoreByUserId(Long userId)
    {
        log.debug("[selectScoreByUserId] userId={}", userId);
        List<Score> list = scoreMapper.selectScoreByUserId(userId);
        log.debug("[selectScoreByUserId] 记录数:{} 示例:{}", list.size(), list.isEmpty()?null:list.get(0));
        return list;
    }
}
