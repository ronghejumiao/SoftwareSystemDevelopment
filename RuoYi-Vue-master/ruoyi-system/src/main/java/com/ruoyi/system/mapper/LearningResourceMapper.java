package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.LearningResource;

/**
 * 学习资源，存储课程的学习资源信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public interface LearningResourceMapper 
{
    /**
     * 查询学习资源，存储课程的学习资源信息
     * 
     * @param resourceId 学习资源，存储课程的学习资源信息主键
     * @return 学习资源，存储课程的学习资源信息
     */
    public LearningResource selectLearningResourceByResourceId(Long resourceId);

    /**
     * 查询学习资源，存储课程的学习资源信息列表
     * 
     * @param learningResource 学习资源，存储课程的学习资源信息
     * @return 学习资源，存储课程的学习资源信息集合
     */
    public List<LearningResource> selectLearningResourceList(LearningResource learningResource);

    /**
     * 新增学习资源，存储课程的学习资源信息
     * 
     * @param learningResource 学习资源，存储课程的学习资源信息
     * @return 结果
     */
    public int insertLearningResource(LearningResource learningResource);

    /**
     * 修改学习资源，存储课程的学习资源信息
     * 
     * @param learningResource 学习资源，存储课程的学习资源信息
     * @return 结果
     */
    public int updateLearningResource(LearningResource learningResource);

    /**
     * 删除学习资源，存储课程的学习资源信息
     * 
     * @param resourceId 学习资源，存储课程的学习资源信息主键
     * @return 结果
     */
    public int deleteLearningResourceByResourceId(Long resourceId);

    /**
     * 批量删除学习资源，存储课程的学习资源信息
     * 
     * @param resourceIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteLearningResourceByResourceIds(Long[] resourceIds);
}
