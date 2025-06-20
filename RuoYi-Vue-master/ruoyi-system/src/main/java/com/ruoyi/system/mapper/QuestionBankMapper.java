package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.QuestionBank;

/**
 * 题库，存储课程的题库信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public interface QuestionBankMapper 
{
    /**
     * 查询题库，存储课程的题库信息
     * 
     * @param bankId 题库，存储课程的题库信息主键
     * @return 题库，存储课程的题库信息
     */
    public QuestionBank selectQuestionBankByBankId(Long bankId);

    /**
     * 查询题库，存储课程的题库信息列表
     * 
     * @param questionBank 题库，存储课程的题库信息
     * @return 题库，存储课程的题库信息集合
     */
    public List<QuestionBank> selectQuestionBankList(QuestionBank questionBank);

    /**
     * 新增题库，存储课程的题库信息
     * 
     * @param questionBank 题库，存储课程的题库信息
     * @return 结果
     */
    public int insertQuestionBank(QuestionBank questionBank);

    /**
     * 修改题库，存储课程的题库信息
     * 
     * @param questionBank 题库，存储课程的题库信息
     * @return 结果
     */
    public int updateQuestionBank(QuestionBank questionBank);

    /**
     * 删除题库，存储课程的题库信息
     * 
     * @param bankId 题库，存储课程的题库信息主键
     * @return 结果
     */
    public int deleteQuestionBankByBankId(Long bankId);

    /**
     * 批量删除题库，存储课程的题库信息
     * 
     * @param bankIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteQuestionBankByBankIds(Long[] bankIds);
}
