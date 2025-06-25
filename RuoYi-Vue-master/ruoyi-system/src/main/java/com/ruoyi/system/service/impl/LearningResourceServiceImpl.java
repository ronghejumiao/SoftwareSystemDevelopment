package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.LearningResourceMapper;
import com.ruoyi.system.domain.LearningResource;
import com.ruoyi.system.service.ILearningResourceService;

/**
 * 学习资源Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-22
 */
@Service
public class LearningResourceServiceImpl implements ILearningResourceService 
{
    @Autowired
    private LearningResourceMapper learningResourceMapper;

    /**
     * 查询学习资源
     * 
     * @param resourceId 学习资源主键
     * @return 学习资源
     */
    @Override
    public LearningResource selectLearningResourceByResourceId(Long resourceId)
    {
        return learningResourceMapper.selectLearningResourceByResourceId(resourceId);
    }

    /**
     * 查询学习资源列表
     * 
     * @param learningResource 学习资源
     * @return 学习资源
     */
    @Override
    public List<LearningResource> selectLearningResourceList(LearningResource learningResource)
    {
        return learningResourceMapper.selectLearningResourceList(learningResource);
    }

    /**
     * 新增学习资源
     * 
     * @param learningResource 学习资源
     * @return 结果
     */
    @Override
    public int insertLearningResource(LearningResource learningResource)
    {
        // 如果前端未显式传递上传者ID，则默认使用当前登录人
        if (learningResource.getUploaderId() == null || learningResource.getUploaderId() <= 0)
        {
            try
            {
                Long currentUserId = com.ruoyi.common.utils.SecurityUtils.getUserId();
                learningResource.setUploaderId(currentUserId);
            }
            catch (Exception e)
            {
                // 如果获取不到登陆人（如匿名环境），避免空值插入导致数据库异常
                learningResource.setUploaderId(0L);
            }
        }
        return learningResourceMapper.insertLearningResource(learningResource);
    }

    /**
     * 修改学习资源
     * 
     * @param learningResource 学习资源
     * @return 结果
     */
    @Override
    public int updateLearningResource(LearningResource learningResource)
    {
        return learningResourceMapper.updateLearningResource(learningResource);
    }

    /**
     * 批量删除学习资源
     * 
     * @param resourceIds 需要删除的学习资源主键
     * @return 结果
     */
    @Override
    public int deleteLearningResourceByResourceIds(Long[] resourceIds)
    {
        return learningResourceMapper.deleteLearningResourceByResourceIds(resourceIds);
    }

    /**
     * 删除学习资源信息
     * 
     * @param resourceId 学习资源主键
     * @return 结果
     */
    @Override
    public int deleteLearningResourceByResourceId(Long resourceId)
    {
        return learningResourceMapper.deleteLearningResourceByResourceId(resourceId);
    }
}
