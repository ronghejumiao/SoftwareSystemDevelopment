package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.VideoResource;

/**
 * 视频学习资源Service接口
 * 
 * @author ruoyi
 * @date 2025-06-23
 */
public interface IVideoResourceService 
{
    /**
     * 查询视频学习资源
     * 
     * @param videoId 视频学习资源主键
     * @return 视频学习资源
     */
    public VideoResource selectVideoResourceByVideoId(Long videoId);

    /**
     * 查询视频学习资源列表
     * 
     * @param videoResource 视频学习资源
     * @return 视频学习资源集合
     */
    public List<VideoResource> selectVideoResourceList(VideoResource videoResource);

    /**
     * 新增视频学习资源
     * 
     * @param videoResource 视频学习资源
     * @return 结果
     */
    public int insertVideoResource(VideoResource videoResource);

    /**
     * 修改视频学习资源
     * 
     * @param videoResource 视频学习资源
     * @return 结果
     */
    public int updateVideoResource(VideoResource videoResource);

    /**
     * 批量删除视频学习资源
     * 
     * @param videoIds 需要删除的视频学习资源主键集合
     * @return 结果
     */
    public int deleteVideoResourceByVideoIds(Long[] videoIds);

    /**
     * 删除视频学习资源信息
     * 
     * @param videoId 视频学习资源主键
     * @return 结果
     */
    public int deleteVideoResourceByVideoId(Long videoId);

    /**
     * 根据课程ID查询视频资源
     */
    java.util.List<com.ruoyi.system.domain.VideoResource> selectVideoResourceListByCourseId(Long courseId);

    /**
     * 根据ID查询视频资源
     */
    com.ruoyi.system.domain.VideoResource selectVideoResourceById(Long videoId);

    /**
     * 查询待分析的视频资源
     */
    java.util.List<com.ruoyi.system.domain.VideoResource> selectPendingAnalysisVideos();
}
