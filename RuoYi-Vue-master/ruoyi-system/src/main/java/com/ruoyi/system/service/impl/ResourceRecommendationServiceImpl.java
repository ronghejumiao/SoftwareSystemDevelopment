package com.ruoyi.system.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.system.domain.LearningResource;
import com.ruoyi.system.domain.LearningResourceAnalysis;
import com.ruoyi.system.domain.StudentSkill;
import com.ruoyi.system.domain.VideoAnalysisResult;
import com.ruoyi.system.domain.VideoResource;
import com.ruoyi.system.domain.StudentResourceRecommendation;
import com.ruoyi.system.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResourceRecommendationServiceImpl implements IResourceRecommendationService {
    @Autowired private IStudentSkillService studentSkillService;
    @Autowired private ICourseSkillRequirementService courseSkillRequirementService;
    @Autowired private ILearningResourceService learningResourceService;
    @Autowired private ILearningResourceAnalysisService learningResourceAnalysisService;
    @Autowired private IVideoResourceService videoResourceService;
    @Autowired private IVideoAnalysisService videoAnalysisService;
    @Autowired private IAIModelService aiModelService;
    @Autowired private IStudentResourceRecommendationService studentResourceRecommendationService;

    private static final String MODEL_ID = "doubao-1.5-thinking-pro";

    @Override
    public List<Map<String, Object>> recommendResourcesForStudent(Long studentId, Long courseId) {
        return recommendResourcesForStudent(studentId, courseId, false);
    }

    @Override
    public List<Map<String, Object>> recommendResourcesForStudent(Long studentId, Long courseId, boolean forceRefresh) {
        // 0. 优先查student_resource_recommendation表，除非forceRefresh
        if (!forceRefresh) {
            StudentResourceRecommendation rec = studentResourceRecommendationService.getByStudentAndCourse(studentId, courseId);
            if (rec != null && rec.getRecommendJson() != null && !rec.getRecommendJson().isEmpty()) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    return objectMapper.readValue(rec.getRecommendJson(), new TypeReference<List<Map<String, Object>>>(){});
                } catch (Exception e) {
                    // 解析失败则重新生成
                }
            }
        }
        // 1. 查询学生能力
        List<StudentSkill> skills = studentSkillService.selectStudentSkillByStudentAndCourse(studentId, courseId);
        List<Map<String, Object>> skillList = skills.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("requirementId", s.getRequirementId());
            map.put("skillName", s.getSkillName());
            map.put("score", s.getSkillScore());
            map.put("description", s.getDescription());
            return map;
        }).collect(Collectors.toList());

        // 2. 查询资料资源及AI摘要
        LearningResource resourceQuery = new LearningResource();
        resourceQuery.setCourseId(courseId);
        List<LearningResource> resources = learningResourceService.selectLearningResourceList(resourceQuery);
        List<Map<String, Object>> resourceList = new ArrayList<>();
        for (LearningResource res : resources) {
            LearningResourceAnalysis analysis = learningResourceAnalysisService.getAnalysisByResourceId(res.getResourceId());
            Map<String, Object> map = new HashMap<>();
            map.put("id", res.getResourceId());
            map.put("resourceType", "ppt");
            map.put("contentSummary", analysis != null ? StringUtils.left(analysis.getContentSummary(), 256) : "");
            resourceList.add(map);
        }

        // 3. 查询视频资源及AI摘要
        List<VideoResource> videos = videoResourceService.selectVideoResourceListByCourseId(courseId);
        for (VideoResource video : videos) {
            VideoAnalysisResult va = videoAnalysisService.getAnalysisResultByVideoId(video.getVideoId());
            Map<String, Object> map = new HashMap<>();
            map.put("id", video.getVideoId());
            map.put("resourceType", "video");
            List<Map<String, Object>> segments = new ArrayList<>();
            if (va != null && va.getSegments() != null) {
                for (VideoAnalysisResult.VideoSegment seg : va.getSegments()) {
                    Map<String, Object> segMap = new HashMap<>();
                    segMap.put("segmentId", seg.getSegmentId());
                    segMap.put("contentSummary", StringUtils.left(seg.getContentSummary(), 128));
                    segments.add(segMap);
                }
            }
            map.put("segments", segments);
            resourceList.add(map);
        }

        // 4. 组装AI输入
        Map<String, Object> recommendInput = new HashMap<>();
        recommendInput.put("studentId", studentId);
        recommendInput.put("courseId", courseId);
        recommendInput.put("skills", skillList);
        recommendInput.put("resources", resourceList);

        // 5. 调用AI推荐
        List<Map<String, Object>> aiResult = aiModelService.recommendResourcesForStudent(recommendInput, MODEL_ID);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(aiResult);
            studentResourceRecommendationService.saveOrUpdate(studentId, courseId, json);
        } catch (Exception e) {
            // ignore
        }
        return aiResult;
    }
} 