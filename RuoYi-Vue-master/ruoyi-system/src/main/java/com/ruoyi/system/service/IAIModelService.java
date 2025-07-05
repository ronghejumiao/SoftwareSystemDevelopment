package com.ruoyi.system.service;

import com.ruoyi.system.domain.VideoAnalysisResult;

import java.util.List;
import java.util.Map;

/**
 * AI模型服务接口
 * 
 * @author ruoyi
 * @date 2025-01-20
 */
public interface IAIModelService 
{
    /**
     * 计算文本相似度
     * 
     * @param text1 文本1
     * @param text2 文本2
     * @return 相似度（0-1）
     */
    double calculateTextSimilarity(String text1, String text2);

    /**
     * 分析视频内容
     * 
     * @param videoUrl 视频URL
     * @return 分析结果
     */
    String analyzeVideoContent(String videoUrl);

    /**
     * 分析学习行为
     * 
     * @param behaviorData 行为数据
     * @return 分析结果
     */
    String analyzeLearningBehavior(String behaviorData);

    /**
     * 分析视频内容
     * 
     * @param videoUrls 视频URL列表
     * @param courseSkillRequirements 课程能力要求
     * @return 分析结果
     */
    public VideoAnalysisResult analyzeVideoContent(List<String> videoUrls, List<Map<String, Object>> courseSkillRequirements);

    /**
     * 分析单个视频段
     * 
     * @param videoUrl 视频URL
     * @param segmentInfo 分段信息
     * @param courseSkillRequirements 课程能力要求
     * @return 分段分析结果
     */
    public VideoAnalysisResult.VideoSegment analyzeVideoSegment(String videoUrl, Map<String, Object> segmentInfo, List<Map<String, Object>> courseSkillRequirements);

    /**
     * 分析学习行为
     * 
     * @param learningBehaviorData 学习行为数据
     * @param videoAnalysis 视频分析结果
     * @return 学习行为评分
     */
    public Map<String, Object> analyzeLearningBehavior(Map<String, Object> learningBehaviorData, VideoAnalysisResult videoAnalysis);

    /**
     * 综合能力评估
     * 
     * @param studentData 学生数据
     * @param skillRequirement 能力要求
     * @return 能力评估结果
     */
    public Map<String, Object> assessStudentSkill(Map<String, Object> studentData, Map<String, Object> skillRequirement);

    /**
     * 检查AI模型可用性
     * 
     * @return 是否可用
     */
    public boolean isModelAvailable();

    /**
     * 获取模型信息
     * 
     * @return 模型信息
     */
    public Map<String, Object> getModelInfo();

    /**
     * 测试AI模型连接
     * 
     * @return 测试结果
     */
    public Map<String, Object> testModelConnection();

    /**
     * 调用豆包视觉理解模型分析图片
     * @param imageUrls 图片URL列表
     * @param prompt 分析提示词
     * @param model 模型名称
     * @return AI分析结果
     */
    String analyzeImagesWithDoubao(List<String> imageUrls, String prompt, String model);

    /**
     * 通用文本AI大模型调用
     * @param prompt 提示词
     * @return AI返回内容
     */
    String callAIModelForText(String prompt);

    /**
     * 个性化推荐学习资源（AI大模型）
     * @param recommendInput 推荐输入数据
     * @param modelId 模型ID
     * @return 推荐资源列表（JSON数组）
     */
    List<Map<String, Object>> recommendResourcesForStudent(Map<String, Object> recommendInput, String modelId);
} 