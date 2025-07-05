package com.ruoyi.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.Course;
import com.ruoyi.system.domain.CourseHomework;
import com.ruoyi.system.domain.TaskSubmission;
import com.ruoyi.system.mapper.CourseHomeworkMapper;
import com.ruoyi.system.mapper.CourseMapper;
import com.ruoyi.system.mapper.TaskSubmissionMapper;
import com.ruoyi.system.service.IAIGradingService;
import com.ruoyi.system.service.IDocumentTextExtractor;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * AI评分服务实现类
 * 
 * @author ruoyi
 */
@Service
public class AIGradingServiceImpl implements IAIGradingService {
    
    private static final Logger log = LoggerFactory.getLogger(AIGradingServiceImpl.class);
    
    @Resource
    private CourseMapper courseMapper;
    
    @Resource
    private CourseHomeworkMapper courseHomeworkMapper;
    
    @Resource
    private TaskSubmissionMapper taskSubmissionMapper;
    
    @Resource
    private IDocumentTextExtractor documentTextExtractor;
    
    @Value("${ark.api.key:}")
    private String arkApiKey;
    
    @Value("${ark.model.id:doubao-1-5-pro-256k-250115}")
    private String arkModelId;
    
    @Value("${ark.base.url:https://ark.cn-beijing.volces.com/api/v3}")
    private String arkBaseUrl;
    
    @Override
    public IAIGradingService.AIGradingResult gradeHomework(Long courseId, Long homeworkId, Long submissionId) {
        try {
            // 1. 获取课程信息
            Course course = courseMapper.selectCourseByCourseId(courseId);
            if (course == null) {
                return new IAIGradingService.AIGradingResult("课程不存在");
            }
            
            // 2. 获取提交信息
            TaskSubmission submission = taskSubmissionMapper.selectTaskSubmissionBySubmissionId(submissionId);
            if (submission == null) {
                return new IAIGradingService.AIGradingResult("提交记录不存在");
            }
            
            // 3. 通过taskId获取作业信息
            CourseHomework homework = null;
            if (homeworkId != null) {
                homework = courseHomeworkMapper.selectCourseHomeworkByHomeworkId(homeworkId);
            }
            
            // 如果通过homeworkId没找到，尝试通过taskId查找
            if (homework == null && submission.getTaskId() != null) {
                // 这里需要根据实际情况调整，可能需要通过taskId查找对应的homework
                // 暂时使用默认的作业信息
                homework = new CourseHomework();
                homework.setHomeworkName("作业任务");
                homework.setHomeworkDesc("通过任务ID关联的作业");
                homework.setFilePaths("[]");
            }
            
            if (homework == null) {
                return new IAIGradingService.AIGradingResult("作业不存在");
            }
            
            // 4. 提取作业文件文字
            String homeworkText = extractHomeworkText(homework);
            
            // 5. 提取学生提交文件文字
            String submissionText = extractSubmissionText(submission);
            
            // 6. 构建AI评分请求
            String prompt = buildGradingPrompt(course, homework, submission, homeworkText, submissionText);
            
            // 7. 调用AI模型
            return callAIModel(prompt);
            
        } catch (Exception e) {
            log.error("AI评分失败", e);
            return new IAIGradingService.AIGradingResult("AI评分失败: " + e.getMessage());
        }
    }
    
    /**
     * 提取作业文件文字
     */
    private String extractHomeworkText(CourseHomework homework) {
        String filePaths = homework.getFilePaths();
        if (StringUtils.isEmpty(filePaths)) {
            return "";
        }
        
        try {
            String[] paths = JSON.parseObject(filePaths, String[].class);
            return documentTextExtractor.extractTextFromFiles(paths);
        } catch (Exception e) {
            log.error("提取作业文件文字失败", e);
            return "";
        }
    }
    
    /**
     * 提取学生提交文件文字
     */
    private String extractSubmissionText(TaskSubmission submission) {
        String submissionFile = submission.getSubmissionFile();
        if (StringUtils.isEmpty(submissionFile)) {
            return submission.getSubmissionContent() != null ? submission.getSubmissionContent() : "";
        }
        
        try {
            String[] paths = JSON.parseObject(submissionFile, String[].class);
            String fileText = documentTextExtractor.extractTextFromFiles(paths);
            String contentText = submission.getSubmissionContent() != null ? submission.getSubmissionContent() : "";
            
            if (StringUtils.isNotEmpty(fileText) && StringUtils.isNotEmpty(contentText)) {
                return contentText + "\n\n文件内容：\n" + fileText;
            } else if (StringUtils.isNotEmpty(fileText)) {
                return fileText;
            } else {
                return contentText;
            }
        } catch (Exception e) {
            log.error("提取提交文件文字失败", e);
            return submission.getSubmissionContent() != null ? submission.getSubmissionContent() : "";
        }
    }
    
    /**
     * 构建AI评分提示词
     */
    private String buildGradingPrompt(Course course, CourseHomework homework, TaskSubmission submission, 
                                    String homeworkText, String submissionText) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("对于课程\"").append(course.getCourseName()).append("\"，");
        prompt.append("有以下作业\"").append(homework.getHomeworkName()).append("\"，");
        prompt.append("作业内容如下：");
        prompt.append("[作业名称：").append(homework.getHomeworkName()).append("，");
        prompt.append("作业描述：").append(homework.getHomeworkDesc() != null ? homework.getHomeworkDesc() : "").append("，");
        if (StringUtils.isNotEmpty(homeworkText)) {
            prompt.append("从作业文件中提取的文本：").append(homeworkText);
        }
        prompt.append("]，");
        prompt.append("学号为\"").append(submission.getUserId()).append("\"提交的作业如下：");
        prompt.append("[").append(submissionText).append("]，");
        prompt.append("请你为该学生的回答打分，成绩区间在0-100分，并生成简短的评语指出错误，可能的知识点遗漏等。");
        prompt.append("请以JSON格式返回，格式为：{\"score\": 分数, \"comment\": \"评语\"}");
        
        return prompt.toString();
    }
    
    /**
     * 调用AI模型
     */
    IAIGradingService.AIGradingResult callAIModel(String prompt) {
        if (StringUtils.isEmpty(arkApiKey)) {
            return new IAIGradingService.AIGradingResult("未配置AI模型API密钥");
        }
        
        try {
            // 创建ArkService实例
            ArkService arkService = ArkService.builder()
                    .apiKey(arkApiKey)
                    .baseUrl(arkBaseUrl)
                    .build();
            
            // 构建消息
            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage userMessage = ChatMessage.builder()
                    .role(ChatMessageRole.USER)
                    .content(prompt)
                    .build();
            messages.add(userMessage);
            
            // 创建请求
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(arkModelId)
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
            
            // 解析响应
            return parseAIResponse(response);
            
        } catch (Exception e) {
            log.error("调用AI模型失败", e);
            return new IAIGradingService.AIGradingResult("调用AI模型失败: " + e.getMessage());
        }
    }
    
    /**
     * 解析AI响应
     */
    private IAIGradingService.AIGradingResult parseAIResponse(String response) {
        try {
            // 尝试解析JSON
            JSONObject json = JSON.parseObject(response);
            Integer score = json.getInteger("score");
            String comment = json.getString("comment");
            
            if (score != null && comment != null) {
                return new IAIGradingService.AIGradingResult(score, comment);
            }
        } catch (Exception e) {
            log.warn("解析AI响应JSON失败，尝试提取数字和文本", e);
        }
        
        // 如果JSON解析失败，尝试从文本中提取分数和评语
        try {
            // 提取分数（0-100之间的数字）
            String scorePattern = "(\\d{1,2}|100)";
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(scorePattern);
            java.util.regex.Matcher matcher = pattern.matcher(response);
            
            if (matcher.find()) {
                int score = Integer.parseInt(matcher.group(1));
                // 移除分数部分，剩余作为评语
                String comment = response.replaceAll(scorePattern, "").trim();
                return new IAIGradingService.AIGradingResult(score, comment);
            }
        } catch (Exception e) {
            log.error("提取分数失败", e);
        }
        
        return new IAIGradingService.AIGradingResult("无法解析AI响应");
    }
} 