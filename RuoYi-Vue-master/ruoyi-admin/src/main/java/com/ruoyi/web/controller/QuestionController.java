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
import com.ruoyi.system.domain.Question;
import com.ruoyi.system.domain.TestPaper;
import com.ruoyi.system.domain.vo.PaperGenerateRequest;
import com.ruoyi.system.service.IQuestionService;
import com.ruoyi.system.service.IPaperGenerateService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 题目，存储题库中的题目信息Controller
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
@RestController
@RequestMapping("/system/question")
public class QuestionController extends BaseController
{
    @Autowired
    private IQuestionService questionService;
    
    @Autowired
    private IPaperGenerateService paperGenerateService;

    /**
     * 查询题目，存储题库中的题目信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:question:list')")
    @GetMapping("/list")
    public TableDataInfo list(Question question)
    {
        startPage();
        List<Question> list = questionService.selectQuestionList(question);
        return getDataTable(list);
    }

    /**
     * 导出题目，存储题库中的题目信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:question:export')")
    @Log(title = "题目，存储题库中的题目信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Question question)
    {
        List<Question> list = questionService.selectQuestionList(question);
        ExcelUtil<Question> util = new ExcelUtil<Question>(Question.class);
        util.exportExcel(response, list, "题目，存储题库中的题目信息数据");
    }

    /**
     * 获取题目，存储题库中的题目信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:question:query')")
    @GetMapping(value = "/{questionId}")
    public AjaxResult getInfo(@PathVariable("questionId") Long questionId)
    {
        return success(questionService.selectQuestionByQuestionId(questionId));
    }

    /**
     * 新增题目，存储题库中的题目信息
     */
    @PreAuthorize("@ss.hasPermi('system:question:add')")
    @Log(title = "题目，存储题库中的题目信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Question question)
    {
        return toAjax(questionService.insertQuestion(question));
    }

    /**
     * 修改题目，存储题库中的题目信息
     */
    @PreAuthorize("@ss.hasPermi('system:question:edit')")
    @Log(title = "题目，存储题库中的题目信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Question question)
    {
        return toAjax(questionService.updateQuestion(question));
    }

    /**
     * 删除题目，存储题库中的题目信息
     */
    @PreAuthorize("@ss.hasPermi('system:question:remove')")
    @Log(title = "题目，存储题库中的题目信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{questionIds}")
    public AjaxResult remove(@PathVariable Long[] questionIds)
    {
        return toAjax(questionService.deleteQuestionByQuestionIds(questionIds));
    }

    /**
     * 根据课程ID获取题目列表
     */
    @PreAuthorize("@ss.hasPermi('system:question:list')")
    @GetMapping("/course/{courseId}")
    public AjaxResult getQuestionsByCourseId(@PathVariable("courseId") Long courseId)
    {
        List<Question> list = questionService.selectQuestionsByCourseId(courseId);
        return success(list);
    }

    /**
     * 生成试卷
     */
    @PreAuthorize("@ss.hasPermi('system:question:generate')")
    @Log(title = "生成试卷", businessType = BusinessType.INSERT)
    @PostMapping("/generate")
    public AjaxResult generatePaper(@RequestBody PaperGenerateRequest request)
    {
        try {
            TestPaper testPaper = paperGenerateService.generatePaper(request);
            return success(testPaper);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }
}
