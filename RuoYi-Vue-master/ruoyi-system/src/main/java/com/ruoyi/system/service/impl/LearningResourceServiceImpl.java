package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.LearningResourceMapper;
import com.ruoyi.system.domain.LearningResource;
import com.ruoyi.system.service.ILearningResourceService;

/**
 * 学习资源，存储课程的学习资源信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
@Service
public class LearningResourceServiceImpl implements ILearningResourceService 
{
    @Autowired
    private LearningResourceMapper learningResourceMapper;

    /**
     * 查询学习资源，存储课程的学习资源信息
     * 
     * @param resourceId 学习资源，存储课程的学习资源信息主键
     * @return 学习资源，存储课程的学习资源信息
     */
    @Override
    public LearningResource selectLearningResourceByResourceId(Long resourceId)
    {
        return learningResourceMapper.selectLearningResourceByResourceId(resourceId);
    }

    /**
     * 查询学习资源，存储课程的学习资源信息列表
     * 
     * @param learningResource 学习资源，存储课程的学习资源信息
     * @return 学习资源，存储课程的学习资源信息
     */
    @Override
    public List<LearningResource> selectLearningResourceList(LearningResource learningResource)
    {
        return learningResourceMapper.selectLearningResourceList(learningResource);
    }

    /**
     * 新增学习资源，存储课程的学习资源信息
     * 
     * @param learningResource 学习资源，存储课程的学习资源信息
     * @return 结果
     */
    @Override
    public int insertLearningResource(LearningResource learningResource)
    {
        learningResource.setCreateTime(DateUtils.getNowDate());
        return learningResourceMapper.insertLearningResource(learningResource);
    }

    /**
     * 修改学习资源，存储课程的学习资源信息
     * 
     * @param learningResource 学习资源，存储课程的学习资源信息
     * @return 结果
     */
    @Override
    public int updateLearningResource(LearningResource learningResource)
    {
        learningResource.setUpdateTime(DateUtils.getNowDate());
        return learningResourceMapper.updateLearningResource(learningResource);
    }

    /**
     * 批量删除学习资源，存储课程的学习资源信息
     * 
     * @param resourceIds 需要删除的学习资源，存储课程的学习资源信息主键
     * @return 结果
     */
    @Override
    public int deleteLearningResourceByResourceIds(Long[] resourceIds)
    {
        return learningResourceMapper.deleteLearningResourceByResourceIds(resourceIds);
    }

    /**
     * 删除学习资源，存储课程的学习资源信息信息
     * 
     * @param resourceId 学习资源，存储课程的学习资源信息主键
     * @return 结果
     */
    @Override
    public int deleteLearningResourceByResourceId(Long resourceId)
    {
        return learningResourceMapper.deleteLearningResourceByResourceId(resourceId);
    }
}
