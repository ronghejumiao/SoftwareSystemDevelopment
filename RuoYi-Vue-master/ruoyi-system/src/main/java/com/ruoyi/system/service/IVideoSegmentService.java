package com.ruoyi.system.service;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 视频分段服务接口
 * 
 * @author ruoyi
 * @date 2025-01-20
 */
public interface IVideoSegmentService 
{
    /**
     * 分段视频文件
     * 
     * @param videoFile 视频文件
     * @param segmentDuration 分段时长（秒）
     * @param maxFileSize 最大文件大小（字节）
     * @return 分段文件列表
     */
    public List<File> segmentVideo(File videoFile, int segmentDuration, long maxFileSize);

    /**
     * 按时间分段视频
     * 
     * @param videoFile 视频文件
     * @param segmentDuration 分段时长（秒）
     * @return 分段文件列表
     */
    public List<File> segmentVideoByTime(File videoFile, int segmentDuration);

    /**
     * 按大小分段视频
     * 
     * @param videoFile 视频文件
     * @param maxFileSize 最大文件大小（字节）
     * @return 分段文件列表
     */
    public List<File> segmentVideoBySize(File videoFile, long maxFileSize);

    /**
     * 获取视频信息
     * 
     * @param videoFile 视频文件
     * @return 视频信息
     */
    public Map<String, Object> getVideoInfo(File videoFile);

    /**
     * 计算分段数量
     * 
     * @param totalDuration 总时长（秒）
     * @param segmentDuration 分段时长（秒）
     * @return 分段数量
     */
    public int calculateSegmentCount(int totalDuration, int segmentDuration);

    /**
     * 生成分段信息
     * 
     * @param totalDuration 总时长（秒）
     * @param segmentDuration 分段时长（秒）
     * @return 分段信息列表
     */
    public List<Map<String, Object>> generateSegmentInfo(int totalDuration, int segmentDuration);

    /**
     * 清理临时分段文件
     * 
     * @param segmentFiles 分段文件列表
     */
    public void cleanupSegmentFiles(List<File> segmentFiles);

    /**
     * 检查视频格式是否支持
     * 
     * @param videoFile 视频文件
     * @return 是否支持
     */
    public boolean isVideoFormatSupported(File videoFile);

    /**
     * 获取支持的视频格式
     * 
     * @return 支持的格式列表
     */
    public List<String> getSupportedVideoFormats();

    /**
     * 获取视频时长（秒）
     * @param videoFile 视频文件
     * @return 总时长（秒）
     */
    int getVideoDuration(File videoFile);
} 