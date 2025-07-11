package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.VideoAnalysisResult;
import com.ruoyi.system.domain.VideoResource;
import com.ruoyi.system.domain.CourseSkillRequirement;
import com.ruoyi.system.domain.StudentSkill;
import com.ruoyi.system.domain.LearningRecord;
import com.ruoyi.system.domain.Score;
import com.ruoyi.system.domain.TaskSubmission;
import com.ruoyi.system.domain.VideoLearningRecord;
import com.ruoyi.system.domain.Course;
import com.ruoyi.system.domain.LearningResourceAnalysis;
import com.ruoyi.system.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 学生能力评估服务实现类
 *
 * @author ruoyi
 * @date 2025-01-20
 */
@Service
public class StudentSkillAssessmentServiceImpl implements IStudentSkillAssessmentService {
    private static final Logger log = LoggerFactory.getLogger(StudentSkillAssessmentServiceImpl.class);

    @Autowired private IVideoResourceService videoResourceService;
    @Autowired private IVideoAnalysisService videoAnalysisService;
    @Autowired private ICourseSkillRequirementService courseSkillRequirementService;
    @Autowired private IStudentSkillService studentSkillService;
    @Autowired private ILearningRecordService learningRecordService;
    @Autowired private IScoreService scoreService;
    @Autowired private ITaskSubmissionService taskSubmissionService;
    @Autowired private IVideoLearningRecordService videoLearningRecordService;
    @Autowired private IAIModelService aiModelService;
    @Autowired private ICourseService courseService;
    @Autowired private ILearningResourceAnalysisService learningResourceAnalysisService;

    // 权重可配置
    private static final double VIDEO_BEHAVIOR_WEIGHT = 0.25;
    private static final double VIDEO_CONTENT_WEIGHT = 0.35;
    private static final double HOMEWORK_WEIGHT = 0.25;
    private static final double EXAM_WEIGHT = 0.15;

    @Override
    public Map<Long, Double> assessStudentSkill(Long studentId, Long courseId) {
        log.info("[能力评估] 开始评估 studentId={}, courseId={}", studentId, courseId);
        Map<Long, Double> result = new HashMap<>();
        List<CourseSkillRequirement> requirements = courseSkillRequirementService.selectCourseSkillRequirementByCourseId(courseId);
        if (requirements == null || requirements.isEmpty()) {
            log.warn("[能力评估] 课程无能力要求 studentId={}, courseId={}", studentId, courseId);
            return result;
        }

        // 获取学生所有学习记录
        List<LearningRecord> records = learningRecordService.selectLearningRecordListByUserAndCourse(studentId, courseId);
        if (records == null || records.isEmpty()) {
            log.warn("[能力评估] 学生无学习记录 studentId={}, courseId={}", studentId, courseId);
            return result;
        }

        // 获取所有视频资源及分析
        List<VideoResource> videos = videoResourceService.selectVideoResourceListByCourseId(courseId);
        Map<Long, VideoAnalysisResult> videoAnalysisMap = new HashMap<>();
        for (VideoResource v : videos) {
            VideoAnalysisResult va = videoAnalysisService.getAnalysisResultByVideoId(v.getVideoId());
            if (va != null) videoAnalysisMap.put(v.getVideoId(), va);
        }

        // 获取作业、考试成绩
        List<Score> scores = scoreService.selectScoreListByLearningRecords(records.stream().map(LearningRecord::getRecordId).collect(Collectors.toList()));
        List<TaskSubmission> submissions = taskSubmissionService.selectTaskSubmissionListByLearningRecords(records.stream().map(LearningRecord::getRecordId).collect(Collectors.toList()));

        // 获取课程信息
        Course course = courseService.selectCourseByCourseId(courseId);

        for (CourseSkillRequirement req : requirements) {
            log.info("[能力评估] 评估能力点: {}({})", req.getSkillName(), req.getRequirementId());
            // 组装AI输入数据
            Map<String, Object> studentData = new HashMap<>();
            studentData.put("studentId", studentId);
            studentData.put("courseId", courseId);
            studentData.put("courseName", course != null ? course.getCourseName() : "");
            studentData.put("userName", records.size() > 0 ? records.get(0).getUserName() : "");
            studentData.put("learningRecords", records);
            studentData.put("videoLearningRecords", records.stream().flatMap(r -> videoLearningRecordService.selectVideoLearningRecordByLearningRecord(r.getRecordId()).stream()).collect(Collectors.toList()));
            studentData.put("videoAnalysis", videoAnalysisMap);
            studentData.put("taskSubmissions", submissions);
            studentData.put("scores", scores);
            // 新增：资料分析内容
            List<Map<String, Object>> resourceAnalysisList = new ArrayList<>();
            for (TaskSubmission submission : submissions) {
                // 只处理资料阅读类提交（假设submissionFile不为空且为资料类型，具体可根据业务调整）
                if (submission.getSubmissionFile() != null && submission.getSubmissionFile().length() > 0) {
                    // 假设submissionFile中存储的是resourceId或可解析出resourceId
                    try {
                        Long resourceId = null;
                        // 尝试直接解析为Long
                        try {
                            resourceId = Long.valueOf(submission.getSubmissionFile());
                        } catch (Exception e) {
                            // submissionFile不是纯数字，跳过
                        }
                        if (resourceId != null) {
                            LearningResourceAnalysis analysis = learningResourceAnalysisService.getAnalysisByResourceId(resourceId);
                            if (analysis != null && analysis.getContentSummary() != null && !analysis.getContentSummary().isEmpty()) {
                                String summary = analysis.getContentSummary();
                                if (summary.length() > 50) summary = summary.substring(0, 50);
                                Map<String, Object> resourceInfo = new HashMap<>();
                                resourceInfo.put("resourceId", resourceId);
                                resourceInfo.put("contentSummary", summary);
                                resourceAnalysisList.add(resourceInfo);
                            }
                        }
                    } catch (Exception e) {
                        // 忽略单条异常，继续处理
                    }
                }
            }
            studentData.put("resourceAnalysisList", resourceAnalysisList);

            Map<String, Object> skillRequirement = new HashMap<>();
            skillRequirement.put("requirementId", req.getRequirementId());
            skillRequirement.put("skillName", req.getSkillName());
            skillRequirement.put("description", req.getDescription());
            skillRequirement.put("requiredText", req.getRequiredText());

            log.info("[能力评估] AI输入数据: studentData={}, skillRequirement={}", studentData, skillRequirement);
            Double aiScore = null;
            try {
                Map<String, Object> aiResult = aiModelService.assessStudentSkill(studentData, skillRequirement);
                log.info("[能力评估] AI返回: {}", aiResult);
                if (aiResult != null && aiResult.get("skill_score") != null) {
                    aiScore = Double.valueOf(aiResult.get("skill_score").toString());
                }
            } catch (Exception e) {
                log.error("[能力评估] AI能力评估失败，使用规则分数兜底: studentId={}, requirementId={}, error={}", studentId, req.getRequirementId(), e.getMessage(), e);
            }

            if (aiScore == null) {
                double videoBehaviorScore = calcVideoBehaviorScore(studentId, req, records, videoAnalysisMap);
                double videoContentScore = calcVideoContentScore(studentId, req, records, videoAnalysisMap);
                double homeworkScore = calcHomeworkScore(studentId, req, submissions);
                double examScore = calcExamScore(studentId, req, scores);
                aiScore = VIDEO_BEHAVIOR_WEIGHT * videoBehaviorScore
                        + VIDEO_CONTENT_WEIGHT * videoContentScore
                        + HOMEWORK_WEIGHT * homeworkScore
                        + EXAM_WEIGHT * examScore;
                log.info("[能力评估] 规则分数: videoBehaviorScore={}, videoContentScore={}, homeworkScore={}, examScore={}, total={}", videoBehaviorScore, videoContentScore, homeworkScore, examScore, aiScore);
            }
            aiScore = Math.max(0, Math.min(100, aiScore));
            result.put(req.getRequirementId(), aiScore);
            updateStudentSkillScore(studentId, req.getRequirementId(), aiScore, "AI能力评估");
            log.info("[能力评估] 更新student_skill: studentId={}, requirementId={}, score={}", studentId, req.getRequirementId(), aiScore);
        }
        log.info("[能力评估] 评估完成 studentId={}, courseId={}", studentId, courseId);
        return result;
    }

    @Override
    public List<Map<String, Object>> batchAssessAllStudentsSkill(Long courseId) {
        List<Map<String, Object>> result = new ArrayList<>();
        // 获取所有选课学生
        List<Long> studentIds = learningRecordService.selectAllStudentIdsByCourseId(courseId);
        for (Long studentId : studentIds) {
            Map<Long, Double> scores = assessStudentSkill(studentId, courseId);
            Map<String, Object> map = new HashMap<>();
            map.put("studentId", studentId);
            map.put("scores", scores);
            result.add(map);
        }
        return result;
    }

    @Override
    public boolean updateStudentSkillScore(Long studentId, Long requirementId, double score, String reason) {
        return studentSkillService.updateStudentSkillScore(studentId, requirementId, score, reason) > 0;
    }

    @Override
    public Map<String, Object> getStudentSkillReport(Long studentId, Long courseId) {
        Map<String, Object> report = new HashMap<>();
        
        // 获取能力要求
        List<CourseSkillRequirement> requirements = courseSkillRequirementService.selectCourseSkillRequirementByCourseId(courseId);
        
        // 获取学生能力分数
        List<StudentSkill> studentSkills = studentSkillService.selectStudentSkillByStudentAndCourse(studentId, courseId);
        Map<Long, StudentSkill> skillMap = studentSkills.stream()
                .collect(Collectors.toMap(StudentSkill::getRequirementId, s -> s));
        
        // 构建详细报告
        List<Map<String, Object>> skillDetails = new ArrayList<>();
        for (CourseSkillRequirement req : requirements) {
            Map<String, Object> detail = new HashMap<>();
            detail.put("requirementId", req.getRequirementId());
            detail.put("skillName", req.getSkillName());
            detail.put("description", req.getDescription());
            
            StudentSkill skill = skillMap.get(req.getRequirementId());
            if (skill != null) {
                detail.put("score", skill.getSkillScore());
                detail.put("updateReason", skill.getUpdateReason());
                detail.put("updateTime", skill.getUpdateTime());
            } else {
                detail.put("score", 0.0);
                detail.put("updateReason", "未评估");
                detail.put("updateTime", null);
            }
            
            skillDetails.add(detail);
        }
        
        report.put("studentId", studentId);
        report.put("courseId", courseId);
        report.put("skillDetails", skillDetails);
        report.put("totalSkills", requirements.size());
        report.put("assessedSkills", skillDetails.stream().filter(d -> (Double)d.get("score") > 0).count());
        
        // 计算平均分
        double avgScore = skillDetails.stream()
                .mapToDouble(d -> (Double)d.get("score"))
                .average()
                .orElse(0.0);
        report.put("averageScore", avgScore);
        
        return report;
    }

    @Override
    public List<Map<String, Object>> getCourseSkillReport(Long courseId) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 获取所有选课学生
        List<Long> studentIds = learningRecordService.selectAllStudentIdsByCourseId(courseId);
        
        for (Long studentId : studentIds) {
            Map<String, Object> studentReport = getStudentSkillReport(studentId, courseId);
            result.add(studentReport);
        }
        
        return result;
    }

    @Override
    public boolean manualAdjustSkillScore(Long studentId, Long requirementId, double score, String reason) {
        return updateStudentSkillScore(studentId, requirementId, score, "人工调整: " + reason);
    }

    @Override
    public void triggerAssessmentOnDataChange(Long studentId, Long courseId) {
        try {
            log.info("数据变更触发能力评估: studentId={}, courseId={}", studentId, courseId);
            assessStudentSkill(studentId, courseId);
        } catch (Exception e) {
            log.error("自动能力评估失败: studentId={}, courseId={}", studentId, courseId, e);
        }
    }

    // 视频行为评分算法
    private double calcVideoBehaviorScore(Long studentId, CourseSkillRequirement req, List<LearningRecord> records, Map<Long, VideoAnalysisResult> videoAnalysisMap) {
        double totalScore = 0.0;
        int videoCount = 0;
        
        for (LearningRecord record : records) {
            List<VideoLearningRecord> videoRecords = videoLearningRecordService.selectVideoLearningRecordByLearningRecord(record.getRecordId());
            
            for (VideoLearningRecord videoRecord : videoRecords) {
                double videoScore = 0.0;
                
                // 1. 完成率评分 (40%)
                if (videoRecord.getCompletionRate() != null) {
                    double completionRate = videoRecord.getCompletionRate().doubleValue();
                    videoScore += (completionRate / 100.0) * 40.0;
                }
                
                // 2. 观看时长评分 (30%)
                if (videoRecord.getWatchedDuration() != null && videoRecord.getTotalDuration() != null) {
                    double watchRatio = (double) videoRecord.getWatchedDuration() / videoRecord.getTotalDuration();
                    videoScore += watchRatio * 30.0;
                }
                
                // 3. 重复观看评分 (20%) - 重复观看表示学习认真
                if (videoRecord.getRepeatSegments() != null && !videoRecord.getRepeatSegments().isEmpty()) {
                    String[] repeatParts = videoRecord.getRepeatSegments().split(",");
                    double repeatScore = Math.min(repeatParts.length * 5.0, 20.0); // 最多20分
                    videoScore += repeatScore;
                }
                
                // 4. 跳过行为评分 (10%) - 跳过少表示学习专注
                if (videoRecord.getSkipSegments() != null && !videoRecord.getSkipSegments().isEmpty()) {
                    String[] skipParts = videoRecord.getSkipSegments().split(",");
                    double skipPenalty = Math.min(skipParts.length * 2.0, 10.0); // 最多扣10分
                    videoScore = Math.max(0, videoScore - skipPenalty);
                }
                
                totalScore += videoScore;
                videoCount++;
            }
        }
        
        return videoCount > 0 ? totalScore / videoCount : 0.0;
    }

    // 视频内容理解评分算法
    private double calcVideoContentScore(Long studentId, CourseSkillRequirement req, List<LearningRecord> records, Map<Long, VideoAnalysisResult> videoAnalysisMap) {
        double totalScore = 0.0;
        int videoCount = 0;
        
        for (LearningRecord record : records) {
            List<VideoLearningRecord> videoRecords = videoLearningRecordService.selectVideoLearningRecordByLearningRecord(record.getRecordId());
            
            for (VideoLearningRecord videoRecord : videoRecords) {
                VideoAnalysisResult analysis = videoAnalysisMap.get(videoRecord.getResourceId());
                if (analysis == null) continue;
                
                double videoScore = 0.0;
                
                // 1. 观看质量评分 (30%)
                if (videoRecord.getCompletionRate() != null) {
                    double completionRate = videoRecord.getCompletionRate().doubleValue();
                    // 完成率越高，内容理解越好
                    videoScore += (completionRate / 100.0) * 30.0;
                }
                
                // 2. 重复观看深度评分 (20%)
                if (videoRecord.getRepeatSegments() != null && !videoRecord.getRepeatSegments().isEmpty()) {
                    // 重复观看表示深入理解
                    String[] repeatParts = videoRecord.getRepeatSegments().split(",");
                    double repeatScore = Math.min(repeatParts.length * 4.0, 20.0);
                    videoScore += repeatScore;
                }
                
                totalScore += videoScore;
                videoCount++;
            }
        }
        
        return videoCount > 0 ? totalScore / videoCount : 0.0;
    }

    // 作业评分算法
    private double calcHomeworkScore(Long studentId, CourseSkillRequirement req, List<TaskSubmission> submissions) {
        if (submissions == null || submissions.isEmpty()) return 0.0;
        
        double totalScore = 0.0;
        int submissionCount = 0;
        
        for (TaskSubmission submission : submissions) {
            if (submission.getScore() != null) {
                // 直接使用作业分数
                totalScore += submission.getScore().doubleValue();
                submissionCount++;
            }
        }
        
        return submissionCount > 0 ? totalScore / submissionCount : 0.0;
    }

    // 考试评分算法
    private double calcExamScore(Long studentId, CourseSkillRequirement req, List<Score> scores) {
        if (scores == null || scores.isEmpty()) return 0.0;
        
        double totalScore = 0.0;
        int scoreCount = 0;
        
        for (Score score : scores) {
            if (score.getScore() != null) {
                // 直接使用考试分数
                totalScore += score.getScore().doubleValue();
                scoreCount++;
            }
        }
        
        return scoreCount > 0 ? totalScore / scoreCount : 0.0;
    }
} 