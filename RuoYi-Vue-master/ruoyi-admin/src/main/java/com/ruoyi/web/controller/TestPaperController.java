package com.ruoyi.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.TestPaper;
import com.ruoyi.system.service.ITestPaperService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 试卷，一个试卷库包含多个试卷Controller
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
@RestController
@RequestMapping("/system/paper")
public class TestPaperController extends BaseController
{
    @Autowired
    private ITestPaperService testPaperService;

    /**
     * 查询试卷，一个试卷库包含多个试卷列表
     */
    @PreAuthorize("@ss.hasPermi('system:paper:list')")
    @GetMapping("/list")
    public TableDataInfo list(TestPaper testPaper)
    {
        startPage();
        List<TestPaper> list = testPaperService.selectTestPaperList(testPaper);
        return getDataTable(list);
    }

    /**
     * 导出试卷，一个试卷库包含多个试卷列表
     */
    @PreAuthorize("@ss.hasPermi('system:paper:export')")
    @Log(title = "试卷，一个试卷库包含多个试卷", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TestPaper testPaper)
    {
        List<TestPaper> list = testPaperService.selectTestPaperList(testPaper);
        ExcelUtil<TestPaper> util = new ExcelUtil<TestPaper>(TestPaper.class);
        util.exportExcel(response, list, "试卷，一个试卷库包含多个试卷数据");
    }

    /**
     * 获取试卷，一个试卷库包含多个试卷详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:paper:query')")
    @GetMapping(value = "/{paperId}")
    public AjaxResult getInfo(@PathVariable("paperId") Long paperId)
    {
        return success(testPaperService.selectTestPaperByPaperId(paperId));
    }

    /**
     * 新增试卷，一个试卷库包含多个试卷
     */
    @PreAuthorize("@ss.hasPermi('system:paper:add')")
    @Log(title = "试卷，一个试卷库包含多个试卷", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TestPaper testPaper)
    {
        return toAjax(testPaperService.insertTestPaper(testPaper));
    }

    /**
     * 修改试卷，一个试卷库包含多个试卷
     */
    @PreAuthorize("@ss.hasPermi('system:paper:edit')")
    @Log(title = "试卷，一个试卷库包含多个试卷", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TestPaper testPaper)
    {
        return toAjax(testPaperService.updateTestPaper(testPaper));
    }

    /**
     * 删除试卷，一个试卷库包含多个试卷
     */
    @PreAuthorize("@ss.hasPermi('system:paper:remove')")
    @Log(title = "试卷，一个试卷库包含多个试卷", businessType = BusinessType.DELETE)
	@DeleteMapping("/{paperIds}")
    public AjaxResult remove(@PathVariable Long[] paperIds)
    {
        return toAjax(testPaperService.deleteTestPaperByPaperIds(paperIds));
    }
}
