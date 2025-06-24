package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.VideoResource;

/**
 * 视频学习资源Mapper接口
 * 
 * @author ruoyi
 * @date 2025-06-23
 */
public interface VideoResourceMapper 
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
     * 删除视频学习资源
     * 
     * @param videoId 视频学习资源主键
     * @return 结果
     */
    public int deleteVideoResourceByVideoId(Long videoId);

    /**
     * 批量删除视频学习资源
     * 
     * @param videoIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteVideoResourceByVideoIds(Long[] videoIds);
}
