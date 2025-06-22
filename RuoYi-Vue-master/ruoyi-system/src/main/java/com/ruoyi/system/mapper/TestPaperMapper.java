package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TestPaper;

/**
 * 试卷，一个试卷库包含多个试卷Mapper接口
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public interface TestPaperMapper 
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
     * 删除试卷，一个试卷库包含多个试卷
     * 
     * @param paperId 试卷，一个试卷库包含多个试卷主键
     * @return 结果
     */
    public int deleteTestPaperByPaperId(Long paperId);

    /**
     * 批量删除试卷，一个试卷库包含多个试卷
     * 
     * @param paperIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTestPaperByPaperIds(Long[] paperIds);
}
