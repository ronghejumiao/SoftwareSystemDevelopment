package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.QuestionMapper;
import com.ruoyi.system.domain.Question;
import com.ruoyi.system.service.IQuestionService;

/**
 * 题目，存储题库中的题目信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
@Service
public class QuestionServiceImpl implements IQuestionService 
{
    @Autowired
    private QuestionMapper questionMapper;

    /**
     * 查询题目，存储题库中的题目信息
     * 
     * @param questionId 题目，存储题库中的题目信息主键
     * @return 题目，存储题库中的题目信息
     */
    @Override
    public Question selectQuestionByQuestionId(Long questionId)
    {
        return questionMapper.selectQuestionByQuestionId(questionId);
    }

    /**
     * 查询题目，存储题库中的题目信息列表
     * 
     * @param question 题目，存储题库中的题目信息
     * @return 题目，存储题库中的题目信息
     */
    @Override
    public List<Question> selectQuestionList(Question question)
    {
        return questionMapper.selectQuestionList(question);
    }

    /**
     * 新增题目，存储题库中的题目信息
     * 
     * @param question 题目，存储题库中的题目信息
     * @return 结果
     */
    @Override
    public int insertQuestion(Question question)
    {
        question.setCreateTime(DateUtils.getNowDate());
        return questionMapper.insertQuestion(question);
    }

    /**
     * 修改题目，存储题库中的题目信息
     * 
     * @param question 题目，存储题库中的题目信息
     * @return 结果
     */
    @Override
    public int updateQuestion(Question question)
    {
        question.setUpdateTime(DateUtils.getNowDate());
        return questionMapper.updateQuestion(question);
    }

    /**
     * 批量删除题目，存储题库中的题目信息
     * 
     * @param questionIds 需要删除的题目，存储题库中的题目信息主键
     * @return 结果
     */
    @Override
    public int deleteQuestionByQuestionIds(Long[] questionIds)
    {
        return questionMapper.deleteQuestionByQuestionIds(questionIds);
    }

    /**
     * 删除题目，存储题库中的题目信息信息
     * 
     * @param questionId 题目，存储题库中的题目信息主键
     * @return 结果
     */
    @Override
    public int deleteQuestionByQuestionId(Long questionId)
    {
        return questionMapper.deleteQuestionByQuestionId(questionId);
    }
}
