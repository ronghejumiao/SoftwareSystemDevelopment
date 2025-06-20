package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.QuestionBankMapper;
import com.ruoyi.system.domain.QuestionBank;
import com.ruoyi.system.service.IQuestionBankService;

/**
 * 题库，存储课程的题库信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
@Service
public class QuestionBankServiceImpl implements IQuestionBankService 
{
    @Autowired
    private QuestionBankMapper questionBankMapper;

    /**
     * 查询题库，存储课程的题库信息
     * 
     * @param bankId 题库，存储课程的题库信息主键
     * @return 题库，存储课程的题库信息
     */
    @Override
    public QuestionBank selectQuestionBankByBankId(Long bankId)
    {
        return questionBankMapper.selectQuestionBankByBankId(bankId);
    }

    /**
     * 查询题库，存储课程的题库信息列表
     * 
     * @param questionBank 题库，存储课程的题库信息
     * @return 题库，存储课程的题库信息
     */
    @Override
    public List<QuestionBank> selectQuestionBankList(QuestionBank questionBank)
    {
        return questionBankMapper.selectQuestionBankList(questionBank);
    }

    /**
     * 新增题库，存储课程的题库信息
     * 
     * @param questionBank 题库，存储课程的题库信息
     * @return 结果
     */
    @Override
    public int insertQuestionBank(QuestionBank questionBank)
    {
        questionBank.setCreateTime(DateUtils.getNowDate());
        return questionBankMapper.insertQuestionBank(questionBank);
    }

    /**
     * 修改题库，存储课程的题库信息
     * 
     * @param questionBank 题库，存储课程的题库信息
     * @return 结果
     */
    @Override
    public int updateQuestionBank(QuestionBank questionBank)
    {
        questionBank.setUpdateTime(DateUtils.getNowDate());
        return questionBankMapper.updateQuestionBank(questionBank);
    }

    /**
     * 批量删除题库，存储课程的题库信息
     * 
     * @param bankIds 需要删除的题库，存储课程的题库信息主键
     * @return 结果
     */
    @Override
    public int deleteQuestionBankByBankIds(Long[] bankIds)
    {
        return questionBankMapper.deleteQuestionBankByBankIds(bankIds);
    }

    /**
     * 删除题库，存储课程的题库信息信息
     * 
     * @param bankId 题库，存储课程的题库信息主键
     * @return 结果
     */
    @Override
    public int deleteQuestionBankByBankId(Long bankId)
    {
        return questionBankMapper.deleteQuestionBankByBankId(bankId);
    }
}
