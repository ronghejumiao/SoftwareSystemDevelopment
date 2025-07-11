package com.ruoyi.system.service;

import com.ruoyi.system.domain.LearningResourceAnalysis;
import java.util.List;

public interface ILearningResourceAnalysisService {
    void analyzePendingResources();
    LearningResourceAnalysis analyzeResource(Long resourceId);
    LearningResourceAnalysis getAnalysisByResourceId(Long resourceId);
    void initAnalysisForCourse(Long courseId);
} 