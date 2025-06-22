package com.ruoyi.system.service;

import com.ruoyi.system.domain.vo.PaperGenerateRequest;
import com.ruoyi.system.domain.TestPaper;

/**
 * 试卷生成Service接口
 * 
 * @author ruoyi
 * @date 2025-01-01
 */
public interface IPaperGenerateService 
{
    /**
     * 生成试卷
     * 
     * @param request 试卷生成请求
     * @return 生成的试卷
     */
    public TestPaper generatePaper(PaperGenerateRequest request);
} 