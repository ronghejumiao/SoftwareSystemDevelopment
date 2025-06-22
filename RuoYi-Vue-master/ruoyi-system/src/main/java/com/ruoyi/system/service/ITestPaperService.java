package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TestPaper;
import com.ruoyi.system.domain.vo.PaperGenerateRequest;

/**
 * 试卷，一个试卷库包含多个试卷Service接口
 * 
 * @author ruoyi
 * @date 2025-06-21
 */
public interface ITestPaperService 
{
    /**
     * 查询试卷，一个试卷库包含多个试卷
     * 
     * @param paperId 试卷，一个试卷库包含多个试卷主键
     * @return 试卷，一个试卷库包含多个试卷
     */
    public TestPaper selectTestPaperByPaperId(Long paperId);

    /**
     * 查询试卷，一个试卷库包含多个试卷列表
     * 
     * @param testPaper 试卷，一个试卷库包含多个试卷
     * @return 试卷，一个试卷库包含多个试卷集合
     */
    public List<TestPaper> selectTestPaperList(TestPaper testPaper);

    /**
     * 新增试卷，一个试卷库包含多个试卷
     * 
     * @param testPaper 试卷，一个试卷库包含多个试卷
     * @return 结果
     */
    public int insertTestPaper(TestPaper testPaper);

    /**
     * 修改试卷，一个试卷库包含多个试卷
     * 
     * @param testPaper 试卷，一个试卷库包含多个试卷
     * @return 结果
     */
    public int updateTestPaper(TestPaper testPaper);

    /**
     * 批量删除试卷，一个试卷库包含多个试卷
     * 
     * @param paperIds 需要删除的试卷，一个试卷库包含多个试卷主键集合
     * @return 结果
     */
    public int deleteTestPaperByPaperIds(Long[] paperIds);

    /**
     * 删除试卷，一个试卷库包含多个试卷信息
     * 
     * @param paperId 试卷，一个试卷库包含多个试卷主键
     * @return 结果
     */
    public int deleteTestPaperByPaperId(Long paperId);

    /**
     * 生成试卷
     * 
     * @param request 试卷生成请求
     * @return 生成的试卷
     */
    public TestPaper generatePaper(PaperGenerateRequest request);
}
