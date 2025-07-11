package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;

/**
 * 个性化学习资源推荐服务
 */
public interface IResourceRecommendationService {
    /**
     * 个性化推荐学习资源（兼容老接口）
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 推荐资源列表（JSON）
     */
    List<Map<String, Object>> recommendResourcesForStudent(Long studentId, Long courseId);

    /**
     * 个性化推荐学习资源（支持强制刷新）
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @param forceRefresh 是否强制刷新AI推荐
     * @return 推荐资源列表（JSON）
     */
    List<Map<String, Object>> recommendResourcesForStudent(Long studentId, Long courseId, boolean forceRefresh);
} 