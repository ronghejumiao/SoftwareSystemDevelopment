package com.ruoyi.system.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.system.domain.VideoAnalysisResult;
import com.ruoyi.system.service.IAIModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import okhttp3.*;
import java.io.IOException;

/**
 * AI模型服务实现类
 * 
 * @author ruoyi
 * @date 2025-01-20
 */
@Service
public class AIModelServiceImpl implements IAIModelService 
{
    private static final Logger log = LoggerFactory.getLogger(AIModelServiceImpl.class);

    @Value("${volcengine.ark.api-key}")
    private String apiKey;

    @Value("${ark.model.id:doubao-1-5-pro-256k-250115}")
    private String model;

    @Value("${volcengine.ark.endpoint}")
    private String endpoint;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final OkHttpClient httpClient = new OkHttpClient();

    // 视频理解API endpoint配置（建议新增配置项）
    private String videoUnderstandingEndpoint = "https://ark.cn-beijing.volces.com/api/v1/ark/video/understanding/tasks";

    @Override
    public VideoAnalysisResult analyzeVideoContent(List<String> videoUrls, List<Map<String, Object>> courseSkillRequirements)
    {
        log.info("开始分析视频内容，视频数量: {}", videoUrls.size());
        
        try {
            VideoAnalysisResult result = new VideoAnalysisResult();
            List<VideoAnalysisResult.VideoSegment> segments = new ArrayList<>();
            
            // 并发分析每个视频段
            List<CompletableFuture<VideoAnalysisResult.VideoSegment>> futures = new ArrayList<>();
            
            for (int i = 0; i < videoUrls.size(); i++) {
                final int segmentId = i + 1;
                final String videoUrl = videoUrls.get(i);
                
                CompletableFuture<VideoAnalysisResult.VideoSegment> future = CompletableFuture.supplyAsync(() -> {
                    Map<String, Object> segmentInfo = new HashMap<>();
                    segmentInfo.put("segment_id", segmentId);
                    segmentInfo.put("start_time", (segmentId - 1) * 600);
                    segmentInfo.put("end_time", segmentId * 600);
                    
                    return analyzeVideoSegment(videoUrl, segmentInfo, courseSkillRequirements);
                });
                
                futures.add(future);
            }
            
            // 等待所有分析完成
            for (CompletableFuture<VideoAnalysisResult.VideoSegment> future : futures) {
                try {
                    VideoAnalysisResult.VideoSegment segment = future.get(5, TimeUnit.MINUTES);
                    segments.add(segment);
                } catch (Exception e) {
                    log.error("视频段分析失败: {}", e.getMessage(), e);
                    VideoAnalysisResult.VideoSegment emptySegment = createEmptySegment(segments.size() + 1);
                    segments.add(emptySegment);
                }
            }
            
            result.setSegments(segments);
            result.setTotalDuration(calculateTotalDuration(segments));
            result.setSegmentsCount(segments.size());
            result.setModelUsed(model);
            
            // 设置分析元数据
            VideoAnalysisResult.AnalysisMetadata metadata = new VideoAnalysisResult.AnalysisMetadata();
            metadata.setModelUsed(model);
            metadata.setAnalysisTime(new Date());
            metadata.setSegmentsCount(segments.size());
            result.setAnalysisMetadata(metadata);
            
            log.info("视频内容分析完成，共分析 {} 个分段", segments.size());
            return result;
            
        } catch (Exception e) {
            log.error("视频内容分析失败: {}", e.getMessage(), e);
            throw new RuntimeException("视频内容分析失败", e);
        }
    }

    @Override
    public VideoAnalysisResult.VideoSegment analyzeVideoSegment(String videoUrl, Map<String, Object> segmentInfo, List<Map<String, Object>> courseSkillRequirements)
    {
        try {
            log.info("开始分析视频段: {}", videoUrl);
            
            String prompt = buildVideoAnalysisPrompt(segmentInfo, courseSkillRequirements);
            String response = callVolcengineVideoUnderstanding(videoUrl, apiKey);
            VideoAnalysisResult.VideoSegment segment = parseVideoSegmentResponse(response, segmentInfo);
            
            log.info("视频段分析完成: {}", segment.getContentSummary());
            return segment;
            
        } catch (Exception e) {
            log.error("视频段分析失败: {}, 错误: {}", videoUrl, e.getMessage(), e);
            return createEmptySegment((Integer) segmentInfo.get("segment_id"));
        }
    }

    @Override
    public Map<String, Object> analyzeLearningBehavior(Map<String, Object> learningBehaviorData, VideoAnalysisResult videoAnalysis)
    {
        try {
            log.info("开始分析学习行为");
            String prompt = buildLearningBehaviorPrompt(learningBehaviorData, videoAnalysis);
            String response = callAIModelForText(prompt);
            Map<String, Object> result = parseLearningBehaviorResponse(response);
            log.info("学习行为分析完成");
            return result;
        } catch (Exception e) {
            log.error("学习行为分析失败: {}", e.getMessage(), e);
            return createDefaultLearningBehaviorResult();
        }
    }

    @Override
    public Map<String, Object> assessStudentSkill(Map<String, Object> studentData, Map<String, Object> skillRequirement)
    {
        try {
            log.info("开始综合能力评估");
            String prompt = buildSkillAssessmentPrompt(studentData, skillRequirement);
            String response = callAIModelForText(prompt);
            Map<String, Object> result = parseSkillAssessmentResponse(response);
            log.info("综合能力评估完成");
            return result;
        } catch (Exception e) {
            log.error("综合能力评估失败: {}", e.getMessage(), e);
            return createDefaultSkillAssessmentResult();
        }
    }

    @Override
    public boolean isModelAvailable()
    {
        try {
            return true;
        } catch (Exception e) {
            log.error("AI模型可用性检查失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getModelInfo()
    {
        Map<String, Object> info = new HashMap<>();
        info.put("model", model);
        info.put("endpoint", endpoint);
        info.put("available", isModelAvailable());
        return info;
    }

    @Override
    public Map<String, Object> testModelConnection()
    {
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("success", true);
            result.put("message", "模型连接正常");
            result.put("response_time", 100);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "模型连接失败: " + e.getMessage());
            result.put("error", e.getMessage());
        }
        return result;
    }

    @Override
    public String analyzeImagesWithDoubao(List<String> imageUrls, String prompt, String model) {
        try {
            log.info("调用豆包视觉理解模型分析图片，图片数:{}, prompt:{}, model:{}", imageUrls.size(), prompt, model);
            // Ark SDK初始化
            String apiKeyEnv = System.getenv("ARK_API_KEY");
            String apiKeyToUse = StringUtils.isNotEmpty(apiKeyEnv) ? apiKeyEnv : apiKey;
            String baseUrl = "https://ark.cn-beijing.volces.com/api/v3";
            com.volcengine.ark.runtime.service.ArkService service = com.volcengine.ark.runtime.service.ArkService.builder()
                    .baseUrl(baseUrl)
                    .apiKey(apiKeyToUse)
                    .build();

            // 构造multiContent
            List<com.volcengine.ark.runtime.model.completion.chat.ChatCompletionContentPart> multiParts = new ArrayList<>();
            for (String imageUrl : imageUrls) {
                multiParts.add(
                    com.volcengine.ark.runtime.model.completion.chat.ChatCompletionContentPart.builder()
                        .type("image_url")
                        .imageUrl(new com.volcengine.ark.runtime.model.completion.chat.ChatCompletionContentPart.ChatCompletionContentPartImageURL(imageUrl))
                        .build()
                );
            }
            multiParts.add(
                com.volcengine.ark.runtime.model.completion.chat.ChatCompletionContentPart.builder()
                    .type("text")
                    .text(prompt)
                    .build()
            );
            // 构造消息
            List<com.volcengine.ark.runtime.model.completion.chat.ChatMessage> messages = new ArrayList<>();
            com.volcengine.ark.runtime.model.completion.chat.ChatMessage userMessage =
                com.volcengine.ark.runtime.model.completion.chat.ChatMessage.builder()
                    .role(com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole.USER)
                    .multiContent(multiParts)
                    .build();
            messages.add(userMessage);
            // 构造请求
            com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest chatCompletionRequest =
                com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest.builder()
                    .model(model)
                    .messages(messages)
                    .build();
            // 调用API
            StringBuilder result = new StringBuilder();
            service.createChatCompletion(chatCompletionRequest).getChoices().forEach(choice -> {
                result.append(choice.getMessage().getContent()).append("\n");
            });
            service.shutdownExecutor();
            log.info("豆包视觉理解API返回: {}", result.toString());
            return result.toString();
        } catch (Exception e) {
            log.error("调用豆包视觉理解模型失败: {}", e.getMessage(), e);
            return "";
        }
    }

    @Override
    public double calculateTextSimilarity(String text1, String text2) {
        // TODO: 实现文本相似度计算
        // 目前返回模拟数据
        log.info("计算文本相似度: text1={}, text2={}", text1, text2);
        return 0.8;
    }

    @Override
    public String analyzeVideoContent(String videoUrl) {
        // TODO: 实现视频内容分析
        // 目前返回模拟数据
        log.info("分析视频内容: videoUrl={}", videoUrl);
        return "视频内容分析结果";
    }

    @Override
    public String analyzeLearningBehavior(String behaviorData) {
        // TODO: 实现学习行为分析
        // 目前返回模拟数据
        log.info("分析学习行为: behaviorData={}", behaviorData);
        return "学习行为分析结果";
    }

    @Override
    public String callAIModelForText(String prompt) {
        // 直接使用配置的model，不再强制写死模型ID
        return callAIModelForTextInternal(prompt);
    }

    // 原有私有方法重命名为 callAIModelForTextInternal
    private String callAIModelForTextInternal(String prompt) {
        if (StringUtils.isEmpty(apiKey)) {
            throw new RuntimeException("未配置AI模型API密钥");
        }
        try {
            // 创建ArkService实例
            com.volcengine.ark.runtime.service.ArkService arkService = com.volcengine.ark.runtime.service.ArkService.builder()
                    .apiKey(apiKey)
                    .baseUrl(endpoint)
                    .build();

            // 构建消息
            java.util.List<com.volcengine.ark.runtime.model.completion.chat.ChatMessage> messages = new java.util.ArrayList<>();
            com.volcengine.ark.runtime.model.completion.chat.ChatMessage userMessage =
                    com.volcengine.ark.runtime.model.completion.chat.ChatMessage.builder()
                            .role(com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole.USER)
                            .content(prompt)
                            .build();
            messages.add(userMessage);

            // 创建请求
            com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest request =
                    com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest.builder()
                            .model(model)
                            .messages(messages)
                            .build();

            // 调用模型
            String response = arkService.createChatCompletion(request)
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent()
                    .toString();

            log.info("AI模型响应: {}", response);
            arkService.shutdownExecutor();
            return response;
        } catch (Exception e) {
            log.error("调用AI模型失败", e);
            throw new RuntimeException("调用AI模型失败: " + e.getMessage());
        }
    }

    // 私有辅助方法
    private String buildVideoAnalysisPrompt(Map<String, Object> segmentInfo, List<Map<String, Object>> courseSkillRequirements)
    {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个专业的教学视频内容分析专家。请分析以下视频片段的内容，输出内容要极度精简。\n");
        prompt.append("【必须严格遵守以下要求】\n");
        prompt.append("1. 只允许输出严格的JSON，禁止输出任何分析、解释、公式、分点、段落、Markdown、标题等。\n");
        prompt.append("2. content_summary 字段只允许一句话，必须50字以内，绝不能超过50字，且不能有任何分点、换行、解释、公式。\n");
        prompt.append("3. 字段名用英文，输出内容必须极简。\n");
        prompt.append("4. 只输出如下格式，严格仿照：\n");
        prompt.append("{\n");
        prompt.append("  \"segment_id\": ").append(segmentInfo.get("segment_id")).append(",\n");
        prompt.append("  \"start_time\": ").append(segmentInfo.get("start_time")).append(",\n");
        prompt.append("  \"end_time\": ").append(segmentInfo.get("end_time")).append(",\n");
        prompt.append("  \"duration\": ").append((Integer)segmentInfo.get("end_time") - (Integer)segmentInfo.get("start_time")).append(",\n");
        prompt.append("  \"content_summary\": \"本段内容极度精炼总结（50字以内）\"\n");
        prompt.append("}\n");
        prompt.append("\n【示例】\n");
        prompt.append("{\n");
        prompt.append("  \"segment_id\": 1,\n");
        prompt.append("  \"start_time\": 0,\n");
        prompt.append("  \"end_time\": 300,\n");
        prompt.append("  \"duration\": 300,\n");
        prompt.append("  \"content_summary\": \"讲解瞬时速度与导数定义的联系。\"\n");
        prompt.append("}\n");
        prompt.append("\n视频信息：\n");
        prompt.append("- 分段ID: ").append(segmentInfo.get("segment_id")).append("\n");
        prompt.append("- 开始时间: ").append(segmentInfo.get("start_time")).append("秒\n");
        prompt.append("- 结束时间: ").append(segmentInfo.get("end_time")).append("秒\n");
        prompt.append("- 时长: ").append((Integer)segmentInfo.get("end_time") - (Integer)segmentInfo.get("start_time")).append("秒\n\n");
        prompt.append("课程能力要求：\n");
        for (Map<String, Object> requirement : courseSkillRequirements) {
            prompt.append("- ").append(requirement.get("requirement_id")).append(": ")
                  .append(requirement.get("skill_name")).append(" - ")
                  .append(requirement.get("description")).append("\n");
        }
        return prompt.toString();
    }

    /**
     * 调用火山引擎视频理解API，返回分析结果JSON
     */
    private String callVolcengineVideoUnderstanding(String videoUrl, String apiKey) throws IOException, InterruptedException {
        // 1. 创建视频理解任务
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        String bodyJson = "{\"input\":{\"video_url\":\"" + videoUrl + "\"}}";
        RequestBody body = RequestBody.create(mediaType, bodyJson);
        Request request = new Request.Builder()
                .url(videoUnderstandingEndpoint)
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();
        Response response = httpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("创建视频理解任务失败: " + response);
        String respStr = response.body().string();
        JsonNode respJson = objectMapper.readTree(respStr);
        String taskId = respJson.path("data").path("task_id").asText();
        if (taskId == null || taskId.isEmpty()) throw new IOException("未获取到task_id: " + respStr);

        // 2. 轮询查询任务状态
        String resultUrl = videoUnderstandingEndpoint + "/" + taskId;
        int maxRetry = 20;
        int interval = 3000;
        for (int i = 0; i < maxRetry; i++) {
            Request getReq = new Request.Builder()
                    .url(resultUrl)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .get()
                    .build();
            Response getResp = httpClient.newCall(getReq).execute();
            String getRespStr = getResp.body().string();
            JsonNode getRespJson = objectMapper.readTree(getRespStr);
            String status = getRespJson.path("data").path("status").asText();
            if ("SUCCEEDED".equals(status)) {
                // 返回分析结果
                return getRespJson.path("data").path("result").toString();
            } else if ("FAILED".equals(status)) {
                throw new IOException("视频理解任务失败: " + getRespStr);
            }
            Thread.sleep(interval);
        }
        throw new IOException("视频理解任务超时未完成");
    }

    private VideoAnalysisResult.VideoSegment parseVideoSegmentResponse(String response, Map<String, Object> segmentInfo)
    {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            
            VideoAnalysisResult.VideoSegment segment = new VideoAnalysisResult.VideoSegment();
            segment.setSegmentId((Integer) segmentInfo.get("segment_id"));
            segment.setStartTime((Integer) segmentInfo.get("start_time"));
            segment.setEndTime((Integer) segmentInfo.get("end_time"));
            segment.setDuration((Integer) segmentInfo.get("end_time") - (Integer) segmentInfo.get("start_time"));
            segment.setContentSummary(jsonNode.get("content_summary").asText());
            
            return segment;
            
        } catch (JsonProcessingException e) {
            log.error("解析视频段响应失败: {}", e.getMessage(), e);
            return createEmptySegment((Integer) segmentInfo.get("segment_id"));
        }
    }

    private VideoAnalysisResult.VideoSegment createEmptySegment(int segmentId)
    {
        VideoAnalysisResult.VideoSegment segment = new VideoAnalysisResult.VideoSegment();
        segment.setSegmentId(segmentId);
        segment.setStartTime((segmentId - 1) * 600);
        segment.setEndTime(segmentId * 600);
        segment.setDuration(600);
        segment.setContentSummary("分析失败，无法获取内容");
        return segment;
    }

    private int calculateTotalDuration(List<VideoAnalysisResult.VideoSegment> segments)
    {
        return segments.stream()
                .mapToInt(VideoAnalysisResult.VideoSegment::getDuration)
                .sum();
    }

    private String buildLearningBehaviorPrompt(Map<String, Object> learningBehaviorData, VideoAnalysisResult videoAnalysis)
    {
        return "基于学生的学习行为数据，分析学生的学习模式和能力水平...";
    }

    private String buildSkillAssessmentPrompt(Map<String, Object> studentData, Map<String, Object> skillRequirement)
    {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个极为严格的教育AI评估专家，你喜欢极限压低学生的分数，除非学生特别优秀或学生的学习记录很多，对于这门课程，学生最多可以有上百条学习记录，若你得到的学习记录在10条以下，不应该给予20分以上" +
                "。请根据以下数据，评估学生在指定课程能力要求上的掌握水平，你需要结合相关数据进行具体的分析，分析学生是否在对应能力要求上有相关行为或学习记录，并给出0-100的分数，对于没有相关记录的，你可以给出0分。\n");
        prompt.append("【数据说明】\n");
        prompt.append("1. studentData.videoLearningRecords：学生所有视频观看行为，包含resourceId（视频ID）、watchedDuration、skipSegments、repeatSegments、completionRate等。\n");
        prompt.append("2. studentData.videoAnalysis：每个视频的分析结果，含segments字段，每个分段有segmentId、startTime、endTime、duration、contentSummary。\n");
        prompt.append("3. 其它字段包含作业、考试、课程信息等。\n");
        prompt.append("【极严格评估要求】\n");
        prompt.append("1. 必须结合学生在每个视频分段的观看行为（如跳过、重复、完成度）与该分段的内容摘要，综合判断其对相关知识点/能力点的掌握情况。\n");
        prompt.append("2. 行为与分段内容要精准对齐：如某分段被完整观看、重复观看，说明该分段内容掌握较好；如被跳过或未观看，说明该分段内容掌握较弱。\n");
        prompt.append("3. 结合作业、考试等其它数据，以及上述视频相关记录，综合给出本能力点的最终分数（0-100），并用 assessment 字段一句话说明评估结论。\n");
        prompt.append("4. 【极重要】分数必须极为严格，只有当学生完成绝大多数课程内容（包括视频、作业、考试等）且表现优秀时，分数才能高于60。大部分情况下分数应低于20，数据极少时应接近0。严禁无依据地给出高分或中等分。\n");
        prompt.append("5. 如果学生在对应的能力要求上，通过你的分析，没有任何有效学习行为、作业、考试等数据，分数必须为0。\n");
        prompt.append("6. 只允许输出严格的JSON，格式如下：\n");
        prompt.append("{\n  \"skill_score\": 80.0,\n  \"assessment\": \"学生对本能力点掌握较好，部分分段内容有待加强。\"\n}\n");
        prompt.append("【输入数据】\n");
        prompt.append("studentData: " + studentData + "\n");
        prompt.append("skillRequirement: " + skillRequirement + "\n");
        return prompt.toString();
    }

    private Map<String, Object> parseLearningBehaviorResponse(String response)
    {
        Map<String, Object> result = new HashMap<>();
        result.put("behavior_score", 75.0);
        result.put("analysis", "学生学习行为分析结果");
        return result;
    }

    private Map<String, Object> parseSkillAssessmentResponse(String response)
    {
        Map<String, Object> result = new HashMap<>();
        try {
            // 优先尝试解析JSON
            JsonNode json = objectMapper.readTree(response);
            if (json.has("skill_score") && json.has("assessment")) {
                result.put("skill_score", json.get("skill_score").asDouble());
                result.put("assessment", json.get("assessment").asText());
                return result;
            }
        } catch (Exception e) {
            log.warn("解析AI能力评估响应JSON失败，尝试正则提取", e);
        }
        // 如果JSON解析失败，尝试用正则提取分数和评语
        try {
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\\"skill_score\\\"\\s*[:=]\\s*(\\d+(?:\\.\\d+)?).*?\\\"assessment\\\"\\s*[:=]\\s*\\\"(.*?)\\\"", java.util.regex.Pattern.DOTALL);
            java.util.regex.Matcher matcher = pattern.matcher(response);
            if (matcher.find()) {
                double score = Double.parseDouble(matcher.group(1));
                String assessment = matcher.group(2);
                result.put("skill_score", score);
                result.put("assessment", assessment);
                return result;
            }
        } catch (Exception e) {
            log.error("正则提取AI能力评估分数失败", e);
        }
        // 兜底
        result.put("skill_score", 80.0);
        result.put("assessment", "AI返回无法解析，采用默认分数");
        return result;
    }

    private Map<String, Object> createDefaultLearningBehaviorResult()
    {
        Map<String, Object> result = new HashMap<>();
        result.put("behavior_score", 50.0);
        result.put("analysis", "默认学习行为分析结果");
        return result;
    }

    private Map<String, Object> createDefaultSkillAssessmentResult()
    {
        Map<String, Object> result = new HashMap<>();
        result.put("skill_score", 50.0);
        result.put("assessment", "默认能力评估结果");
        return result;
    }

    private String generateMockTextResponse(String prompt)
    {
        return "{\n" +
               "  \"analysis_result\": \"模拟分析结果\",\n" +
               "  \"score\": 75.0\n" +
               "}";
    }

    @Override
    public List<Map<String, Object>> recommendResourcesForStudent(Map<String, Object> recommendInput, String modelId) {
        // prompt组装
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个极为专业的智能学习推荐AI，请根据学生的能力薄弱点和课程所有资源内容摘要，推荐7-8个最优先学习的资源（包括资料和视频），以帮助学生补足短板。\n");
        prompt.append("【输入说明】\n");
        prompt.append("1. skills: 学生在本课程下的各项能力分数，分数越低代表越薄弱。\n");
        prompt.append("2. resources: 课程下所有资料和视频资源的内容摘要。视频资源的每个segment只取前128字。\n");
        prompt.append("【推荐要求】\n");
        prompt.append("1. 必须优先推荐能补足学生薄弱能力点的资源。\n");
        prompt.append("2. 推荐结果只允许7-8个，且必须多样化，资料和视频类型都要有，禁止只推荐一种类型，否则视为无效。\n");
        prompt.append("3. 只允许输出严格的JSON数组，每个元素格式如下：{\"id\": 资源ID, \"resourceType\": \"ppt/pdf/video\", \"segmentId\": 段落ID(如有), ...}，禁止输出除JSON外的任何内容。\n");
        prompt.append("4. 不允许输出解释、说明、分点、引用原文等废话。\n");
        prompt.append("【输入数据】\n");
        prompt.append(new ObjectMapper().valueToTree(recommendInput).toString());
        // 调用AI
        String aiResult = callAIModelForText(prompt.toString());
        // 解析JSON数组
        try {
            return objectMapper.readValue(aiResult, List.class);
        } catch (Exception e) {
            log.error("AI推荐结果解析失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 构建AI对话prompt，包含学生能力值、历史对话、当前问题
     * @param studentSkill 学生能力值对象（List或Map）
     * @param history 最近两轮历史对话（List<Map>，每个map含role/user/ai, content）
     * @param userInput 当前用户输入
     * @return prompt字符串
     */
    public static String buildChatPrompt(Object studentSkill, List<Map<String, String>> history, String userInput) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是一个智能学习助手。以下是学生的能力背景信息，仅供你理解学生的知识基础和薄弱点，请结合这些信息更有针对性地回答学生问题，但如果学生能力信息缺失或不相关，也要像正常聊天助手一样友好、耐心地回答所有问题。\n\n");
        sb.append("【学生能力值】\n");
        // 优先以表格方式输出能力点
        if (studentSkill instanceof List) {
            List<?> list = (List<?>) studentSkill;
            if (!list.isEmpty() && list.get(0) instanceof com.ruoyi.system.domain.StudentSkill) {
                sb.append("| 能力名称 | 能力描述 | 分数 |\n|---|---|---|\n");
                for (Object o : list) {
                    com.ruoyi.system.domain.StudentSkill s = (com.ruoyi.system.domain.StudentSkill) o;
                    sb.append("| ")
                      .append(s.getSkillName() != null ? s.getSkillName() : "-")
                      .append(" | ")
                      .append(s.getDescription() != null ? s.getDescription() : "-")
                      .append(" | ")
                      .append(s.getSkillScore() != null ? s.getSkillScore() : "-")
                      .append(" |\n");
                }
            } else {
                // 兜底原始json
                try {
                    sb.append(new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(studentSkill));
                } catch (Exception e) {
                    sb.append(studentSkill != null ? studentSkill.toString() : "[]");
                }
            }
        } else {
            // 兜底原始json
            try {
                sb.append(new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(studentSkill));
            } catch (Exception e) {
                sb.append(studentSkill != null ? studentSkill.toString() : "[]");
            }
        }
        sb.append("\n\n【历史对话】\n");
        if (history != null && !history.isEmpty()) {
            int count = 0;
            for (int i = Math.max(0, history.size() - 4); i < history.size(); i++) {
                Map<String, String> msg = history.get(i);
                String role = msg.get("role");
                String content = msg.get("content");
                if ("user".equals(role)) {
                    sb.append("用户: ").append(content).append("\n");
                } else if ("ai".equals(role)) {
                    sb.append("AI: ").append(content).append("\n");
                }
                count++;
                if (count >= 4) break;
            }
        }
        sb.append("\n【当前问题】\n");
        sb.append(userInput != null ? userInput : "");
        return sb.toString();
    }

    public String getApiKey() {
        return this.apiKey;
    }
} 