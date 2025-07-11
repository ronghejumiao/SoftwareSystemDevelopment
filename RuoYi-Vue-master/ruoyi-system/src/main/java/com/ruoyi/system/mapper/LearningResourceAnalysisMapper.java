package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.LearningResourceAnalysis;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LearningResourceAnalysisMapper {
    int insertLearningResourceAnalysis(LearningResourceAnalysis analysis);
    int updateLearningResourceAnalysisStatus(@Param("resourceId") Long resourceId, @Param("status") Integer status, @Param("errorMessage") String errorMessage);
    List<LearningResourceAnalysis> selectPendingResources();
    List<LearningResourceAnalysis> selectLearningResourceAnalysisByResourceId(Long resourceId);
    int updateLearningResourceAnalysis(LearningResourceAnalysis analysis);
} 