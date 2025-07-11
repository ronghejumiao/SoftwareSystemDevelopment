package com.ruoyi.web.controller;

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
import com.ruoyi.system.domain.QuestionBank;
import com.ruoyi.system.service.IQuestionBankService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 题库，存储课程的题库信息Controller
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
@RestController
@RequestMapping("/system/bank")
public class QuestionBankController extends BaseController
{
    @Autowired
    private IQuestionBankService questionBankService;

    /**
     * 查询题库，存储课程的题库信息列表
     */
    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('teacher')")
    @GetMapping("/list")
    public TableDataInfo list(QuestionBank questionBank)
    {
        startPage();
        List<QuestionBank> list = questionBankService.selectQuestionBankList(questionBank);
        return getDataTable(list);
    }

    /**
     * 导出题库，存储课程的题库信息列表
     */
    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('teacher')")
    @Log(title = "题库，存储课程的题库信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, QuestionBank questionBank)
    {
        List<QuestionBank> list = questionBankService.selectQuestionBankList(questionBank);
        ExcelUtil<QuestionBank> util = new ExcelUtil<QuestionBank>(QuestionBank.class);
        util.exportExcel(response, list, "题库，存储课程的题库信息数据");
    }

    /**
     * 获取题库，存储课程的题库信息详细信息
     */
    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('teacher')")
    @GetMapping(value = "/{bankId}")
    public AjaxResult getInfo(@PathVariable("bankId") Long bankId)
    {
        return success(questionBankService.selectQuestionBankByBankId(bankId));
    }

    /**
     * 新增题库，存储课程的题库信息
     */
    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('teacher')")
    @Log(title = "题库，存储课程的题库信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody QuestionBank questionBank)
    {
        return toAjax(questionBankService.insertQuestionBank(questionBank));
    }

    /**
     * 修改题库，存储课程的题库信息
     */
    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('teacher')")
    @Log(title = "题库，存储课程的题库信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody QuestionBank questionBank)
    {
        return toAjax(questionBankService.updateQuestionBank(questionBank));
    }

    /**
     * 删除题库，存储课程的题库信息
     */
    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('teacher')")
    @Log(title = "题库，存储课程的题库信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{bankIds}")
    public AjaxResult remove(@PathVariable Long[] bankIds)
    {
        return toAjax(questionBankService.deleteQuestionBankByBankIds(bankIds));
    }
}
