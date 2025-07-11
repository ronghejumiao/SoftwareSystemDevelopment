package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.StudentResourceRecommendation;
import com.ruoyi.system.mapper.StudentResourceRecommendationMapper;
import com.ruoyi.system.service.IStudentResourceRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentResourceRecommendationServiceImpl implements IStudentResourceRecommendationService {
    @Autowired
    private StudentResourceRecommendationMapper mapper;

    @Override
    public StudentResourceRecommendation getByStudentAndCourse(Long studentId, Long courseId) {
        return mapper.selectByStudentAndCourse(studentId, courseId);
    }

    @Override
    public void saveOrUpdate(Long studentId, Long courseId, String recommendJson) {
        StudentResourceRecommendation exist = mapper.selectByStudentAndCourse(studentId, courseId);
        if (exist == null) {
            StudentResourceRecommendation rec = new StudentResourceRecommendation();
            rec.setStudentId(studentId);
            rec.setCourseId(courseId);
            rec.setRecommendJson(recommendJson);
            mapper.insertStudentResourceRecommendation(rec);
        } else {
            exist.setRecommendJson(recommendJson);
            mapper.updateStudentResourceRecommendation(exist);
        }
    }
} 