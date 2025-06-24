package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 视频学习资源对象 video_resource
 * 
 * @author ruoyi
 * @date 2025-06-23
 */
public class VideoResource extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 视频资源 */
    private Long videoId;

    /** 所属课程ID */
    @Excel(name = "所属课程ID")
    private Long courseId;

    /** 视频名称 */
    @Excel(name = "视频名称")
    private String videoName;

    /** 视频类型 */
    @Excel(name = "视频类型")
    private String videoType;

    /** 视频存储路径 */
    @Excel(name = "视频存储路径")
    private String videoPath;

    /** 视频大小 */
    @Excel(name = "视频大小")
    private Long fileSize;

    /** 视频时长 */
    @Excel(name = "视频时长")
    private Long duration;

    /** 上传用户 ID（外键） */
    @Excel(name = "上传用户 ID", readConverterExp = "外=键")
    private Long suploaderId;

    /** 上传时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "上传时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date uploadTime;

    /** 视频封面路径 */
    @Excel(name = "视频封面路径")
    private String thumbnail;

    /** 视频简介 */
    @Excel(name = "视频简介")
    private String description;

    public void setVideoId(Long videoId) 
    {
        this.videoId = videoId;
    }

    public Long getVideoId() 
    {
        return videoId;
    }

    public void setCourseId(Long courseId) 
    {
        this.courseId = courseId;
    }

    public Long getCourseId() 
    {
        return courseId;
    }

    public void setVideoName(String videoName) 
    {
        this.videoName = videoName;
    }

    public String getVideoName() 
    {
        return videoName;
    }

    public void setVideoType(String videoType) 
    {
        this.videoType = videoType;
    }

    public String getVideoType() 
    {
        return videoType;
    }

    public void setVideoPath(String videoPath) 
    {
        this.videoPath = videoPath;
    }

    public String getVideoPath() 
    {
        return videoPath;
    }

    public void setFileSize(Long fileSize) 
    {
        this.fileSize = fileSize;
    }

    public Long getFileSize() 
    {
        return fileSize;
    }

    public void setDuration(Long duration) 
    {
        this.duration = duration;
    }

    public Long getDuration() 
    {
        return duration;
    }

    public void setSuploaderId(Long suploaderId) 
    {
        this.suploaderId = suploaderId;
    }

    public Long getSuploaderId() 
    {
        return suploaderId;
    }

    public void setUploadTime(Date uploadTime) 
    {
        this.uploadTime = uploadTime;
    }

    public Date getUploadTime() 
    {
        return uploadTime;
    }

    public void setThumbnail(String thumbnail) 
    {
        this.thumbnail = thumbnail;
    }

    public String getThumbnail() 
    {
        return thumbnail;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("videoId", getVideoId())
            .append("courseId", getCourseId())
            .append("videoName", getVideoName())
            .append("videoType", getVideoType())
            .append("videoPath", getVideoPath())
            .append("fileSize", getFileSize())
            .append("duration", getDuration())
            .append("suploaderId", getSuploaderId())
            .append("uploadTime", getUploadTime())
            .append("thumbnail", getThumbnail())
            .append("description", getDescription())
            .toString();
    }
}
