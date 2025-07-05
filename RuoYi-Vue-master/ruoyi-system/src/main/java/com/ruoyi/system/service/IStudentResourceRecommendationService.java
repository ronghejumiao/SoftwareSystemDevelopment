package com.ruoyi.system.service;

import com.ruoyi.system.domain.StudentResourceRecommendation;

public interface IStudentResourceRecommendationService {
    StudentResourceRecommendation getByStudentAndCourse(Long studentId, Long courseId);
    void saveOrUpdate(Long studentId, Long courseId, String recommendJson);
} 