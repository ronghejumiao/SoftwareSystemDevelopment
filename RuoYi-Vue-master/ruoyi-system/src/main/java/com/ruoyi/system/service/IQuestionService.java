package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.Question;

/**
 * 题目，存储题库中的题目信息Service接口
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public interface IQuestionService 
{
    /**
     * 查询题目，存储题库中的题目信息
     * 
     * @param questionId 题目，存储题库中的题目信息主键
     * @return 题目，存储题库中的题目信息
     */
    public Question selectQuestionByQuestionId(Long questionId);

    /**
     * 查询题目，存储题库中的题目信息列表
     * 
     * @param question 题目，存储题库中的题目信息
     * @return 题目，存储题库中的题目信息集合
     */
    public List<Question> selectQuestionList(Question question);

    /**
     * 新增题目，存储题库中的题目信息
     * 
     * @param question 题目，存储题库中的题目信息
     * @return 结果
     */
    public int insertQuestion(Question question);

    /**
     * 修改题目，存储题库中的题目信息
     * 
     * @param question 题目，存储题库中的题目信息
     * @return 结果
     */
    public int updateQuestion(Question question);

    /**
     * 批量删除题目，存储题库中的题目信息
     * 
     * @param questionIds 需要删除的题目，存储题库中的题目信息主键集合
     * @return 结果
     */
    public int deleteQuestionByQuestionIds(Long[] questionIds);

    /**
     * 删除题目，存储题库中的题目信息信息
     * 
     * @param questionId 题目，存储题库中的题目信息主键
     * @return 结果
     */
    public int deleteQuestionByQuestionId(Long questionId);

    /**
     * 根据课程ID查询题目列表
     * 
     * @param courseId 课程ID
     * @return 题目列表
     */
    public List<Question> selectQuestionsByCourseId(Long courseId);
}
