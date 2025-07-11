package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.StudentResourceRecommendation;
import org.apache.ibatis.annotations.Param;

public interface StudentResourceRecommendationMapper {
    StudentResourceRecommendation selectByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
    int insertStudentResourceRecommendation(StudentResourceRecommendation rec);
    int updateStudentResourceRecommendation(StudentResourceRecommendation rec);
} 