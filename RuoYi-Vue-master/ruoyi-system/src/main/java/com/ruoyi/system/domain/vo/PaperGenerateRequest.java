package com.ruoyi.system.domain.vo;

import java.math.BigDecimal;
import java.util.List;
import com.ruoyi.system.domain.Question;

/**
 * 试卷生成请求对象
 * 
 * @author ruoyi
 * @date 2025-01-01
 */
public class PaperGenerateRequest
{
    /** 课程ID */
    private Long courseId;

    /** 试卷名称 */
    private String paperName;

    /** 试卷描述 */
    private String paperDesc;

    /** 总分 */
    private BigDecimal totalScore;

    /** 试卷题目内容 */
    private List<Question> content;

    /** 组卷模式：manual-手动组卷，auto-随机组卷 */
    private String mode;

    /** 随机组卷配置 */
    private AutoConfig config;

    public static class AutoConfig {
        /** 总分 */
        private BigDecimal totalScore;
        
        /** 总题目数量 */
        private Integer totalQuestions;
        
        /** 选择题分值 */
        private BigDecimal choiceScore;
        
        /** 填空题分值 */
        private BigDecimal fillScore;
        
        /** 简答题分值 */
        private BigDecimal essayScore;
        
        /** 简单题比例 */
        private Integer easyRatio;
        
        /** 中等题比例 */
        private Integer mediumRatio;
        
        /** 困难题比例 */
        private Integer hardRatio;
        
        /** 选择题比例 */
        private Integer choiceRatio;
        
        /** 填空题比例 */
        private Integer fillRatio;
        
        /** 简答题比例 */
        private Integer essayRatio;
        
        /** 排序规则 */
        private String sortRule;

        // Getters and Setters
        public BigDecimal getTotalScore() {
            return totalScore;
        }

        public void setTotalScore(BigDecimal totalScore) {
            this.totalScore = totalScore;
        }

        public Integer getTotalQuestions() {
            return totalQuestions;
        }

        public void setTotalQuestions(Integer totalQuestions) {
            this.totalQuestions = totalQuestions;
        }

        public BigDecimal getChoiceScore() {
            return choiceScore;
        }

        public void setChoiceScore(BigDecimal choiceScore) {
            this.choiceScore = choiceScore;
        }

        public BigDecimal getFillScore() {
            return fillScore;
        }

        public void setFillScore(BigDecimal fillScore) {
            this.fillScore = fillScore;
        }

        public BigDecimal getEssayScore() {
            return essayScore;
        }

        public void setEssayScore(BigDecimal essayScore) {
            this.essayScore = essayScore;
        }

        public Integer getEasyRatio() {
            return easyRatio;
        }

        public void setEasyRatio(Integer easyRatio) {
            this.easyRatio = easyRatio;
        }

        public Integer getMediumRatio() {
            return mediumRatio;
        }

        public void setMediumRatio(Integer mediumRatio) {
            this.mediumRatio = mediumRatio;
        }

        public Integer getHardRatio() {
            return hardRatio;
        }

        public void setHardRatio(Integer hardRatio) {
            this.hardRatio = hardRatio;
        }

        public Integer getChoiceRatio() {
            return choiceRatio;
        }

        public void setChoiceRatio(Integer choiceRatio) {
            this.choiceRatio = choiceRatio;
        }

        public Integer getFillRatio() {
            return fillRatio;
        }

        public void setFillRatio(Integer fillRatio) {
            this.fillRatio = fillRatio;
        }

        public Integer getEssayRatio() {
            return essayRatio;
        }

        public void setEssayRatio(Integer essayRatio) {
            this.essayRatio = essayRatio;
        }

        public String getSortRule() {
            return sortRule;
        }

        public void setSortRule(String sortRule) {
            this.sortRule = sortRule;
        }
    }

    // Getters and Setters
    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    public String getPaperDesc() {
        return paperDesc;
    }

    public void setPaperDesc(String paperDesc) {
        this.paperDesc = paperDesc;
    }

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }

    public List<Question> getContent() {
        return content;
    }

    public void setContent(List<Question> content) {
        this.content = content;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public AutoConfig getConfig() {
        return config;
    }

    public void setConfig(AutoConfig config) {
        this.config = config;
    }
} 