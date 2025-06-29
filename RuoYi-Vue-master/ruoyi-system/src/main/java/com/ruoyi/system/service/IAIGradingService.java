package com.ruoyi.system.service;

/**
 * AI评分服务接口
 * 
 * @author ruoyi
 */
public interface IAIGradingService {
    
    /**
     * AI评分
     * 
     * @param courseId 课程ID
     * @param homeworkId 作业ID
     * @param submissionId 提交ID
     * @return 评分结果
     */
    AIGradingResult gradeHomework(Long courseId, Long homeworkId, Long submissionId);
    
    /**
     * AI评分结果
     */
    class AIGradingResult {
        private Integer score;
        private String comment;
        private String error;
        
        public AIGradingResult() {}
        
        public AIGradingResult(Integer score, String comment) {
            this.score = score;
            this.comment = comment;
        }
        
        public AIGradingResult(String error) {
            this.error = error;
        }
        
        // Getters and Setters
        public Integer getScore() { return score; }
        public void setScore(Integer score) { this.score = score; }
        
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        
        public boolean isSuccess() { return error == null; }
    }
} 