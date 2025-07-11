package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.LearningResourceAnalysis;
import com.ruoyi.system.domain.LearningResource;
import com.ruoyi.system.mapper.LearningResourceAnalysisMapper;
import com.ruoyi.system.mapper.LearningResourceMapper;
import com.ruoyi.system.service.ILearningResourceAnalysisService;
import com.ruoyi.system.service.IAIModelService;
import com.ruoyi.system.service.IDocumentTextExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class LearningResourceAnalysisServiceImpl implements ILearningResourceAnalysisService {
    private static final Logger log = LoggerFactory.getLogger(LearningResourceAnalysisServiceImpl.class);

    @Autowired
    private LearningResourceAnalysisMapper analysisMapper;
    @Autowired
    private LearningResourceMapper resourceMapper;
    @Autowired
    private IAIModelService aiModelService;
    @Autowired
    private IDocumentTextExtractor documentTextExtractor;

    @Value("${ark.model.id:doubao-1-5-pro-256k-250115}")
    private String model;

    @Override
    @Transactional
    public void analyzePendingResources() {
        List<LearningResourceAnalysis> pending = analysisMapper.selectPendingResources();
        for (LearningResourceAnalysis analysis : pending) {
            try {
                analyzeResource(analysis.getResourceId());
            } catch (Exception e) {
                log.error("学习资源分析失败: resourceId={}, error={}", analysis.getResourceId(), e.getMessage(), e);
                analysisMapper.updateLearningResourceAnalysisStatus(analysis.getResourceId(), 3, e.getMessage());
            }
        }
    }

    @Override
    @Transactional
    public LearningResourceAnalysis analyzeResource(Long resourceId) {
        LearningResource resource = resourceMapper.selectLearningResourceByResourceId(resourceId);
        if (resource == null) throw new RuntimeException("学习资源不存在: " + resourceId);

        // 先查是否有分析记录
        List<LearningResourceAnalysis> existList = analysisMapper.selectLearningResourceAnalysisByResourceId(resourceId);
        LearningResourceAnalysis analysis;
        if (existList != null && !existList.isEmpty()) {
            analysis = existList.get(0);
            analysis.setStatus(1);
            analysis.setErrorMessage(null);
            analysisMapper.updateLearningResourceAnalysis(analysis);
        } else {
            analysis = new LearningResourceAnalysis();
            analysis.setResourceId(resourceId);
            analysis.setStatus(1);
            analysis.setCreateTime(new Date());
            analysis.setUpdateTime(new Date());
            analysisMapper.insertLearningResourceAnalysis(analysis);
        }

        String prompt = buildResourceAnalysisPrompt(resource);
        String aiResult = null;
        try {
            log.info("[DEBUG] 调用AI分析方法 callAIModelForText, prompt={}", prompt);
            aiResult = aiModelService.callAIModelForText(prompt);
        } catch (Exception e) {
            analysis.setStatus(3);
            analysis.setErrorMessage(e.getMessage());
            analysisMapper.updateLearningResourceAnalysis(analysis);
            throw new RuntimeException("AI分析失败: " + e.getMessage(), e);
        }
        // 解析AI返回
        String summary = parseContentSummary(aiResult);
        analysis.setStatus(2);
        analysis.setContentSummary(summary);
        analysis.setModelUsed(model);
        analysis.setAnalysisTime(new Date());
        analysis.setErrorMessage(null);
        analysis.setUpdateTime(new Date());
        analysisMapper.updateLearningResourceAnalysis(analysis);
        return analysis;
    }

    @Override
    public LearningResourceAnalysis getAnalysisByResourceId(Long resourceId) {
        List<LearningResourceAnalysis> list = analysisMapper.selectLearningResourceAnalysisByResourceId(resourceId);
        if (list == null || list.isEmpty()) {
            LearningResourceAnalysis analysis = new LearningResourceAnalysis();
            analysis.setResourceId(resourceId);
            analysis.setStatus(0); // 未分析
            analysis.setCreateTime(new Date());
            analysis.setUpdateTime(new Date());
            analysisMapper.insertLearningResourceAnalysis(analysis);
            return analysis;
        }
        // 如果有多条，只返回第一条
        return list.get(0);
    }

    @Override
    @Transactional
    public void initAnalysisForCourse(Long courseId) {
        // 查询该课程下所有资源
        List<LearningResource> resources = resourceMapper.selectLearningResourceListByCourseId(courseId);
        for (LearningResource resource : resources) {
            List<LearningResourceAnalysis> existList = analysisMapper.selectLearningResourceAnalysisByResourceId(resource.getResourceId());
            if (existList == null || existList.isEmpty()) {
                LearningResourceAnalysis analysis = new LearningResourceAnalysis();
                analysis.setResourceId(resource.getResourceId());
                analysis.setStatus(0); // 未分析
                analysis.setCreateTime(new Date());
                analysis.setUpdateTime(new Date());
                analysisMapper.insertLearningResourceAnalysis(analysis);
            }
        }
    }

    private String buildResourceAnalysisPrompt(LearningResource resource) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请你作为专业的内容分析AI，阅读下方学习资源的文件内容，提炼出最核心的知识点和主题，输出极简明的内容概要（100字以内，禁止分点、禁止解释、禁止废话、禁止引用原文）。\n");
        prompt.append("【资源名称】").append(resource.getResourceName()).append("\n");
        prompt.append("【资源类型】").append(resource.getResourceType()).append("\n");
        // 自动提取文件内容
        String fileText = documentTextExtractor.extractTextFromFile(resource.getResourcePath());
        if (fileText != null && !fileText.isEmpty()) {
            // 只取前2000字，防止prompt过长
            String preview = fileText.length() > 2000 ? fileText.substring(0, 2000) + "..." : fileText;
            prompt.append("【文件内容】\n").append(preview).append("\n");
        } else {
            prompt.append("【文件内容】无法提取或内容为空\n");
        }
        prompt.append("【输出要求】\n只允许输出如下JSON格式：\n");
        prompt.append("{\n  \"content_summary\": \"内容概要\"\n}\n");
        return prompt.toString();
    }

    private String parseContentSummary(String aiResult) {
        // 简单提取content_summary字段
        if (aiResult == null) return "";
        int idx = aiResult.indexOf("content_summary");
        if (idx >= 0) {
            int start = aiResult.indexOf(":", idx);
            int quote1 = aiResult.indexOf('"', start);
            int quote2 = aiResult.indexOf('"', quote1 + 1);
            if (quote1 >= 0 && quote2 > quote1) {
                return aiResult.substring(quote1 + 1, quote2);
            }
        }
        return aiResult.length() > 100 ? aiResult.substring(0, 100) : aiResult;
    }
} 