package com.ruoyi.system.domain;

import java.util.Date;

public class StudentResourceRecommendation {
    private Long id;
    private Long studentId;
    private Long courseId;
    private String recommendJson;
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public String getRecommendJson() { return recommendJson; }
    public void setRecommendJson(String recommendJson) { this.recommendJson = recommendJson; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
} 