package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 学习资源对象 learning_resource
 * 
 * @author ruoyi
 * @date 2025-06-22
 */
public class LearningResource extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 资源ID，主键，自增 */
    private Long resourceId;

    /** 课程ID，关联course表 */
    @Excel(name = "课程ID，关联course表")
    private Long courseId;

    /** 资源名称 */
    @Excel(name = "资源名称")
    private String resourceName;

    /** 资源类型（PPT、PDF、视频等） */
    @Excel(name = "资源类型", readConverterExp = "P=PT、PDF、视频等")
    private String resourceType;

    /** 存储路径 */
    @Excel(name = "存储路径")
    private String resourcePath;

    /** 文件大小（字节） */
    @Excel(name = "文件大小", readConverterExp = "字=节")
    private Long fileSize;

    /** 上传者ID，关联sys_user表 */
    @Excel(name = "上传者ID，关联sys_user表")
    private Long uploaderId;

    /** 上传时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "上传时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date uploadTime;

    public void setResourceId(Long resourceId) 
    {
        this.resourceId = resourceId;
    }

    public Long getResourceId() 
    {
        return resourceId;
    }

    public void setCourseId(Long courseId) 
    {
        this.courseId = courseId;
    }

    public Long getCourseId() 
    {
        return courseId;
    }

    public void setResourceName(String resourceName) 
    {
        this.resourceName = resourceName;
    }

    public String getResourceName() 
    {
        return resourceName;
    }

    public void setResourceType(String resourceType) 
    {
        this.resourceType = resourceType;
    }

    public String getResourceType() 
    {
        return resourceType;
    }

    public void setResourcePath(String resourcePath) 
    {
        this.resourcePath = resourcePath;
    }

    public String getResourcePath() 
    {
        return resourcePath;
    }

    public void setFileSize(Long fileSize) 
    {
        this.fileSize = fileSize;
    }

    public Long getFileSize() 
    {
        return fileSize;
    }

    public void setUploaderId(Long uploaderId) 
    {
        this.uploaderId = uploaderId;
    }

    public Long getUploaderId() 
    {
        return uploaderId;
    }

    public void setUploadTime(Date uploadTime) 
    {
        this.uploadTime = uploadTime;
    }

    public Date getUploadTime() 
    {
        return uploadTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("resourceId", getResourceId())
            .append("courseId", getCourseId())
            .append("resourceName", getResourceName())
            .append("resourceType", getResourceType())
            .append("resourcePath", getResourcePath())
            .append("fileSize", getFileSize())
            .append("uploaderId", getUploaderId())
            .append("uploadTime", getUploadTime())
            .toString();
    }
}
