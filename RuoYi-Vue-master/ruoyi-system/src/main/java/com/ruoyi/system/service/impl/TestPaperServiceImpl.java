package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Map;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TestPaperMapper;
import com.ruoyi.system.mapper.CoursePaperLibraryMapper;
import com.ruoyi.system.domain.TestPaper;
import com.ruoyi.system.domain.CoursePaperLibrary;
import com.ruoyi.system.domain.vo.PaperGenerateRequest;
import com.ruoyi.system.service.ITestPaperService;

/**
 * 试卷，一个试卷库包含多个试卷Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-21
 */
@Service
public class TestPaperServiceImpl implements ITestPaperService 
{
    @Autowired
    private TestPaperMapper testPaperMapper;
    
    @Autowired
    private CoursePaperLibraryMapper coursePaperLibraryMapper;

    /**
     * 查询试卷，一个试卷库包含多个试卷
     * 
     * @param paperId 试卷，一个试卷库包含多个试卷主键
     * @return 试卷，一个试卷库包含多个试卷
     */
    @Override
    public TestPaper selectTestPaperByPaperId(Long paperId)
    {
        return testPaperMapper.selectTestPaperByPaperId(paperId);
    }

    /**
     * 查询试卷，一个试卷库包含多个试卷列表
     * 
     * @param testPaper 试卷，一个试卷库包含多个试卷
     * @return 试卷，一个试卷库包含多个试卷
     */
    @Override
    public List<TestPaper> selectTestPaperList(TestPaper testPaper)
    {
        return testPaperMapper.selectTestPaperList(testPaper);
    }

    /**
     * 新增试卷，一个试卷库包含多个试卷
     * 
     * @param testPaper 试卷，一个试卷库包含多个试卷
     * @return 结果
     */
    @Override
    public int insertTestPaper(TestPaper testPaper)
    {
        testPaper.setCreateTime(DateUtils.getNowDate());
        return testPaperMapper.insertTestPaper(testPaper);
    }

    /**
     * 修改试卷，一个试卷库包含多个试卷
     * 
     * @param testPaper 试卷，一个试卷库包含多个试卷
     * @return 结果
     */
    @Override
    public int updateTestPaper(TestPaper testPaper)
    {
        testPaper.setUpdateTime(DateUtils.getNowDate());
        return testPaperMapper.updateTestPaper(testPaper);
    }

    /**
     * 批量删除试卷，一个试卷库包含多个试卷
     * 
     * @param paperIds 需要删除的试卷，一个试卷库包含多个试卷主键集合
     * @return 结果
     */
    @Override
    public int deleteTestPaperByPaperIds(Long[] paperIds)
    {
        return testPaperMapper.deleteTestPaperByPaperIds(paperIds);
    }

    /**
     * 删除试卷，一个试卷库包含多个试卷信息
     * 
     * @param paperId 试卷，一个试卷库包含多个试卷主键
     * @return 结果
     */
    @Override
    public int deleteTestPaperByPaperId(Long paperId)
    {
        return testPaperMapper.deleteTestPaperByPaperId(paperId);
    }

    /**
     * 生成试卷
     * 
     * @param request 试卷生成请求
     * @return 生成的试卷
     */
    @Override
    public TestPaper generatePaper(PaperGenerateRequest request)
    {
        // 1. 根据课程ID查找对应的试卷库
        CoursePaperLibrary library = coursePaperLibraryMapper.selectCoursePaperLibraryByCourseId(request.getCourseId());
        if (library == null) {
            throw new RuntimeException("未找到课程对应的试卷库，课程ID：" + request.getCourseId());
        }
        
        // 2. 创建试卷对象
        TestPaper testPaper = new TestPaper();
        testPaper.setLibraryId(library.getLibraryId());
        testPaper.setPaperName(request.getPaperName());
        testPaper.setPaperDesc(request.getPaperDesc());
        testPaper.setTotalScore(request.getTotalScore());
        
        // 3. 将题目内容转换为JSON格式存储
        String contentJson = JSON.toJSONString(request.getContent());
        testPaper.setContent(contentJson);
        
        // 4. 设置创建时间
        testPaper.setCreateTime(DateUtils.getNowDate());
        
        // 5. 保存试卷
        int result = testPaperMapper.insertTestPaper(testPaper);
        if (result > 0) {
            return testPaper;
        } else {
            throw new RuntimeException("试卷生成失败");
        }
    }
}
