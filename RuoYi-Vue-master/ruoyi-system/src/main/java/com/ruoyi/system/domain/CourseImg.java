package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 课程概念图，存储课程的概念图URL（1对多关系）对象 course_img
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public class CourseImg extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 概念图ID，主键，自增 */
    private Long mapId;

    /** 课程ID，关联course表 */
    @Excel(name = "课程ID，关联course表")
    private Long courseId;

    /** 概念图名称 */
    @Excel(name = "概念图名称")
    private String mapName;

    /** 概念图URL */
    @Excel(name = "概念图URL")
    private String mapUrl;

    public void setMapId(Long mapId) 
    {
        this.mapId = mapId;
    }

    public Long getMapId() 
    {
        return mapId;
    }

    public void setCourseId(Long courseId) 
    {
        this.courseId = courseId;
    }

    public Long getCourseId() 
    {
        return courseId;
    }

    public void setMapName(String mapName) 
    {
        this.mapName = mapName;
    }

    public String getMapName() 
    {
        return mapName;
    }

    public void setMapUrl(String mapUrl) 
    {
        this.mapUrl = mapUrl;
    }

    public String getMapUrl() 
    {
        return mapUrl;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("mapId", getMapId())
            .append("courseId", getCourseId())
            .append("mapName", getMapName())
            .append("mapUrl", getMapUrl())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
