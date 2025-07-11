package com.ruoyi.system.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.VideoAnalysisResult;
import com.ruoyi.system.domain.VideoResource;
import com.ruoyi.system.domain.CourseSkillRequirement;
import com.ruoyi.system.service.IVideoAnalysisService;
import com.ruoyi.system.service.IVideoResourceService;
import com.ruoyi.system.service.ICourseSkillRequirementService;
import com.ruoyi.system.service.IVolcengineStorageService;
import com.ruoyi.system.service.IAIModelService;
import com.ruoyi.system.service.IVideoSegmentService;
import com.ruoyi.system.mapper.VideoAnalysisResultMapper;
import com.ruoyi.common.config.RuoYiConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 视频分析服务实现类
 * 
 * @author ruoyi
 * @date 2025-01-20
 */
@Service
public class VideoAnalysisServiceImpl implements IVideoAnalysisService 
{
    private static final Logger log = LoggerFactory.getLogger(VideoAnalysisServiceImpl.class);

    @Autowired
    private VideoAnalysisResultMapper videoAnalysisResultMapper;

    @Autowired
    private IVideoResourceService videoResourceService;

    @Autowired
    private ICourseSkillRequirementService courseSkillRequirementService;

    @Autowired
    private IVolcengineStorageService volcengineStorageService;

    @Autowired
    private IAIModelService aiModelService;

    @Autowired
    private IVideoSegmentService videoSegmentService;

    @Value("${video.analysis.segment.duration:300}")
    private int segmentDuration; // 默认5分钟

    @Value("${video.analysis.max.file.size:47185920}")
    private long maxFileSize; // 默认45M

    @Value("${video.analysis.overlap.duration:30}")
    private int overlapDuration; // 默认30秒重叠

    private final Executor analysisExecutor = Executors.newFixedThreadPool(3);
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 查询视频分析结果
     * 
     * @param analysisId 分析结果ID
     * @return 视频分析结果
     */
    @Override
    public VideoAnalysisResult selectVideoAnalysisResultById(Long analysisId)
    {
        return videoAnalysisResultMapper.selectVideoAnalysisResultById(analysisId);
    }

    /**
     * 查询视频分析结果列表
     * 
     * @param videoAnalysisResult 视频分析结果
     * @return 视频分析结果集合
     */
    @Override
    public List<VideoAnalysisResult> selectVideoAnalysisResultList(VideoAnalysisResult videoAnalysisResult)
    {
        return videoAnalysisResultMapper.selectVideoAnalysisResultList(videoAnalysisResult);
    }

    /**
     * 新增视频分析结果
     * 
     * @param videoAnalysisResult 视频分析结果
     * @return 结果
     */
    @Override
    public int insertVideoAnalysisResult(VideoAnalysisResult videoAnalysisResult)
    {
        videoAnalysisResult.setCreateTime(DateUtils.getNowDate());
        return videoAnalysisResultMapper.insertVideoAnalysisResult(videoAnalysisResult);
    }

    /**
     * 修改视频分析结果
     * 
     * @param videoAnalysisResult 视频分析结果
     * @return 结果
     */
    @Override
    public int updateVideoAnalysisResult(VideoAnalysisResult videoAnalysisResult)
    {
        videoAnalysisResult.setUpdateTime(DateUtils.getNowDate());
        return videoAnalysisResultMapper.updateVideoAnalysisResult(videoAnalysisResult);
    }

    /**
     * 批量删除视频分析结果
     * 
     * @param analysisIds 需要删除的分析结果ID
     * @return 结果
     */
    @Override
    public int deleteVideoAnalysisResultByIds(Long[] analysisIds)
    {
        return videoAnalysisResultMapper.deleteVideoAnalysisResultByIds(analysisIds);
    }

    /**
     * 删除视频分析结果信息
     * 
     * @param analysisId 分析结果ID
     * @return 结果
     */
    @Override
    public int deleteVideoAnalysisResultById(Long analysisId)
    {
        return videoAnalysisResultMapper.deleteVideoAnalysisResultById(analysisId);
    }

    /**
     * 分析视频内容
     * 
     * @param videoId 视频ID
     * @return 分析结果
     */
    @Override
    @Async("analysisExecutor")
    @Transactional
    public VideoAnalysisResult analyzeVideoContent(Long videoId) {
        log.info("[分析开始] 视频ID: {}", videoId);
        try {
            VideoResource videoResource = videoResourceService.selectVideoResourceById(videoId);
            if (videoResource == null) throw new RuntimeException("视频不存在: " + videoId);
            updateVideoAnalysisStatus(videoId, 1, null);
            String dbPath = videoResource.getVideoPath();
            String realPath = dbPath;
            if (dbPath != null && dbPath.startsWith("/profile")) {
                realPath = dbPath.replaceFirst("/profile", RuoYiConfig.getProfile().replaceAll("\\\\", "/"));
            }
            File videoFile = new File(realPath);
            if (!videoFile.exists()) throw new RuntimeException("视频文件不存在: " + realPath);
            int totalDuration = videoSegmentService.getVideoDuration(videoFile);
            int segmentCount = (totalDuration + segmentDuration - 1) / segmentDuration;
            log.info("[分段] 总时长:{}s, 分段时长:{}s, 分段数:{}", totalDuration, segmentDuration, segmentCount);
            String ffmpegPath = "C:/SoftwareSystemDevelopment/ffmpeg-7.1.1-essentials_build/bin/ffmpeg.exe";
            String tmpDir = System.getProperty("java.io.tmpdir");
            int maxImagesPerSegment = 8;
            List<VideoAnalysisResult.VideoSegment> segmentResults = new ArrayList<>();
            for (int i = 0; i < segmentCount; i++) {
                int segStart = i * segmentDuration;
                int segEnd = Math.min((i + 1) * segmentDuration, totalDuration);
                int segLen = segEnd - segStart;
                // 均匀采样8帧
                List<File> frames = new ArrayList<>();
                for (int j = 0; j < maxImagesPerSegment; j++) {
                    int frameTime = segStart + (int)((double)j / maxImagesPerSegment * segLen);
                    String outPath = tmpDir + File.separator + String.format("video_%d_seg_%d_frame_%d.jpg", videoId, i + 1, j + 1);
                    String cmd = String.format("\"%s\" -ss %d -i \"%s\" -frames:v 1 -q:v 2 \"%s\" -y", ffmpegPath, frameTime, videoFile.getAbsolutePath(), outPath);
                    Process process = Runtime.getRuntime().exec(cmd);
                    process.waitFor();
                    File frameFile = new File(outPath);
                    if (frameFile.exists() && frameFile.length() > 0) {
                        frames.add(frameFile);
                    }
                }
                // 上传TOS
                List<String> objectKeys = new ArrayList<>();
                for (File frame : frames) {
                    String objectKey = String.format("video-frames/%d/seg_%d/%s", videoId, i + 1, frame.getName());
                    objectKeys.add(objectKey);
                }
                List<String> imageUrls = volcengineStorageService.uploadFiles(frames, objectKeys);
                // AI分析（只取前8张图片）
                List<String> limitedImageUrls = imageUrls.size() > 8 ? imageUrls.subList(0, 8) : imageUrls;
                String prompt = "请分析该视频片段的主要知识点和内容";
                String model = "doubao-1-5-vision-pro-32k-250115";
                String aiResult = aiModelService.analyzeImagesWithDoubao(limitedImageUrls, prompt, model);
                // 解析AI结果为VideoSegment
                VideoAnalysisResult.VideoSegment segment = parseSegmentAIResult(aiResult, i + 1, segStart, segEnd);
                segmentResults.add(segment);
                // 删除TOS图片
                volcengineStorageService.deleteFiles(objectKeys);
                // 清理本地帧
                for (File frame : frames) frame.delete();
            }
            // 聚合结果
            VideoAnalysisResult analysisResult = new VideoAnalysisResult();
            analysisResult.setVideoId(videoId);
            analysisResult.setTotalDuration(totalDuration);
            analysisResult.setSegmentsCount(segmentCount);
            analysisResult.setModelUsed("doubao-1-5-vision-pro-32k-250115");
            analysisResult.setSegments(segmentResults);
            analysisResult.setAnalysisStatus(2);
            analysisResult.setAnalysisTime(DateUtils.getNowDate());
            saveAnalysisResult(analysisResult);
            updateVideoResourceAnalysis(videoResource, analysisResult);
            log.info("[分析完成] 视频ID: {} 分析已完成", videoId);
            return analysisResult;
        } catch (Exception e) {
            log.error("[分析失败] 视频ID: {}, 错误: {}", videoId, e.getMessage(), e);
            updateVideoAnalysisStatus(videoId, 3, e.getMessage());
            throw new RuntimeException("视频分析失败: " + e.getMessage(), e);
        }
    }

    // 解析AI返回内容为VideoSegment对象
    private VideoAnalysisResult.VideoSegment parseSegmentAIResult(String aiResult, int segmentId, int startTime, int endTime) throws Exception {
        VideoAnalysisResult.VideoSegment segment = new VideoAnalysisResult.VideoSegment();
        segment.setSegmentId(segmentId);
        segment.setStartTime(startTime);
        segment.setEndTime(endTime);
        segment.setDuration(endTime - startTime);
        segment.setContentSummary(aiResult); // 可进一步解析
        // 可根据AI返回内容进一步解析填充knowledgePoints等
        return segment;
    }

    /**
     * 批量分析待分析视频
     * 
     * @return 分析结果
     */
    @Override
    public List<VideoAnalysisResult> batchAnalyzePendingVideos()
    {
        List<VideoResource> pendingVideos = getPendingAnalysisVideos();
        List<VideoAnalysisResult> results = new ArrayList<>();

        for (VideoResource video : pendingVideos) {
            try {
                CompletableFuture<VideoAnalysisResult> future = CompletableFuture.supplyAsync(() -> {
                    return analyzeVideoContent(video.getVideoId());
                }, analysisExecutor);

                VideoAnalysisResult result = future.get();
                results.add(result);
            } catch (Exception e) {
                log.error("批量分析视频失败，视频ID: {}, 错误: {}", video.getVideoId(), e.getMessage(), e);
            }
        }

        return results;
    }

    /**
     * 获取待分析视频列表
     * 
     * @return 待分析视频列表
     */
    @Override
    public List<VideoResource> getPendingAnalysisVideos()
    {
        return videoResourceService.selectPendingAnalysisVideos();
    }

    /**
     * 更新视频分析状态
     * 
     * @param videoId 视频ID
     * @param status 分析状态
     * @param errorMessage 错误信息（如果有）
     */
    @Override
    public void updateVideoAnalysisStatus(Long videoId, Integer status, String errorMessage)
    {
        VideoResource videoResource = new VideoResource();
        videoResource.setVideoId(videoId);
        videoResource.setAnalysisStatus(status);
        if (StringUtils.isNotEmpty(errorMessage)) {
            videoResource.setRemark(errorMessage);
        }
        videoResourceService.updateVideoResource(videoResource);
    }

    /**
     * 根据视频ID获取分析结果
     * 
     * @param videoId 视频ID
     * @return 分析结果
     */
    @Override
    public VideoAnalysisResult getAnalysisResultByVideoId(Long videoId)
    {
        return videoAnalysisResultMapper.selectVideoAnalysisResultByVideoId(videoId);
    }

    /**
     * 重新分析视频
     * 
     * @param videoId 视频ID
     * @return 分析结果
     */
    @Override
    public VideoAnalysisResult reanalyzeVideo(Long videoId)
    {
        // 删除旧的分析结果
        VideoAnalysisResult oldResult = getAnalysisResultByVideoId(videoId);
        if (oldResult != null) {
            deleteVideoAnalysisResultById(oldResult.getAnalysisId());
        }

        // 重新分析
        return analyzeVideoContent(videoId);
    }

    /**
     * 处理视频分段
     */
    private List<File> processVideoSegmentation(VideoResource videoResource) throws Exception
    {
        // 获取本地视频文件
        File videoFile = new File(videoResource.getVideoPath());
        if (!videoFile.exists()) {
            throw new RuntimeException("视频文件不存在: " + videoResource.getVideoPath());
        }

        // 检查视频格式
        if (!videoSegmentService.isVideoFormatSupported(videoFile)) {
            throw new RuntimeException("不支持的视频格式: " + videoResource.getVideoType());
        }

        // 获取视频信息
        Map<String, Object> videoInfo = videoSegmentService.getVideoInfo(videoFile);
        int totalDuration = (Integer) videoInfo.get("duration");

        // 计算分段数量
        int segmentCount = videoSegmentService.calculateSegmentCount(totalDuration, segmentDuration);
        log.info("视频总时长: {}秒, 分段时长: {}秒, 预计分段数: {}", totalDuration, segmentDuration, segmentCount);

        // 执行分段
        return videoSegmentService.segmentVideo(videoFile, segmentDuration, maxFileSize);
    }

    /**
     * 上传分段文件
     */
    private List<String> uploadSegments(List<File> segmentFiles, Long videoId)
    {
        List<String> segmentUrls = new ArrayList<>();
        List<String> objectKeys = new ArrayList<>();

        for (int i = 0; i < segmentFiles.size(); i++) {
            String objectKey = String.format("videos/%d/segments/segment_%03d.mp4", videoId, i + 1);
            objectKeys.add(objectKey);
        }

        // 批量上传
        List<String> urls = volcengineStorageService.uploadFiles(segmentFiles, objectKeys);
        segmentUrls.addAll(urls);

        log.info("成功上传 {} 个视频分段", segmentUrls.size());
        return segmentUrls;
    }

    /**
     * 保存分析结果
     */
    private void saveAnalysisResult(VideoAnalysisResult analysisResult)
    {
        try {
            // 将分析结果转换为JSON存储
            String analysisJson = objectMapper.writeValueAsString(analysisResult);

            // 更新video_resource表的video_analysis字段
            VideoResource videoResource = new VideoResource();
            videoResource.setVideoId(analysisResult.getVideoId());
            videoResource.setVideoAnalysis(analysisJson);
            videoResource.setAnalysisStatus(2); // 分析完成
            videoResource.setAnalysisTime(DateUtils.getNowDate());
            videoResourceService.updateVideoResource(videoResource);

            // 同时保存到video_analysis_result表
            analysisResult.setVideoAnalysis(analysisJson);
            insertVideoAnalysisResult(analysisResult);

        } catch (JsonProcessingException e) {
            log.error("保存分析结果失败", e);
            throw new RuntimeException("保存分析结果失败", e);
        }
    }

    /**
     * 更新视频资源分析信息
     */
    private void updateVideoResourceAnalysis(VideoResource videoResource, VideoAnalysisResult analysisResult)
    {
        try {
            String analysisJson = objectMapper.writeValueAsString(analysisResult);
            videoResource.setVideoAnalysis(analysisJson);
            videoResource.setAnalysisStatus(2);
            videoResource.setAnalysisTime(DateUtils.getNowDate());
            videoResourceService.updateVideoResource(videoResource);
        } catch (JsonProcessingException e) {
            log.error("更新视频资源分析信息失败", e);
        }
    }

    /**
     * 转换能力要求格式
     */
    private List<Map<String, Object>> convertSkillRequirements(List<CourseSkillRequirement> skillRequirements)
    {
        List<Map<String, Object>> result = new ArrayList<>();
        for (CourseSkillRequirement requirement : skillRequirements) {
            Map<String, Object> map = new HashMap<>();
            map.put("requirement_id", requirement.getRequirementId());
            map.put("skill_name", requirement.getSkillName());
            map.put("description", requirement.getDescription());
            map.put("required_text", requirement.getRequiredText());
            result.add(map);
        }
        return result;
    }

    @Override
    public int getAnalysisProgress(Long videoId) {
        VideoAnalysisResult result = videoAnalysisResultMapper.selectVideoAnalysisResultByVideoId(videoId);
        if (result == null) return 0;
        if (result.getAnalysisStatus() != null) {
            if (result.getAnalysisStatus() == 2) return 100; // 分析完成
            if (result.getAnalysisStatus() == 3) return -1; // 分析失败
        }
        // 分析中，按已完成分段数/总分段数
        Integer total = result.getSegmentsCount();
        int done = 0;
        if (result.getSegments() != null) {
            done = result.getSegments().size();
        }
        if (total != null && total > 0) {
            int percent = (int) Math.round(done * 100.0 / total);
            // 0<percent<100
            return Math.max(1, Math.min(percent, 99));
        }
        return 1; // 有记录但未分段，视为刚开始
    }
} 