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
import com.ruoyi.system.domain.TaskSubmission;
import com.ruoyi.system.service.ITaskSubmissionService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 任务提交记录Controller
 * 
 * @author ruoyi
 * @date 2025-06-24
 */
@RestController
@RequestMapping("/system/submission")
public class TaskSubmissionController extends BaseController
{
    @Autowired
    private ITaskSubmissionService taskSubmissionService;

    /**
     * 查询任务提交记录列表
     */
    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('teacher') or @ss.hasRole('student')")
    @GetMapping("/list")
    public TableDataInfo list(TaskSubmission taskSubmission)
    {
        startPage();
        // 学生用户只能查看自己的任务提交记录
        if (SecurityUtils.isStudent()) {
            taskSubmission.getParams().put("userId", SecurityUtils.getUserId());
        }
        List<TaskSubmission> list = taskSubmissionService.selectTaskSubmissionList(taskSubmission);
        return getDataTable(list);
    }

    /**
     * 导出任务提交记录列表
     */
    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('teacher')")
    @Log(title = "任务提交记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TaskSubmission taskSubmission)
    {
        // 学生用户只能导出自己的记录
        if (SecurityUtils.isStudent()) {
            taskSubmission.getParams().put("userId", SecurityUtils.getUserId());
        }
        List<TaskSubmission> list = taskSubmissionService.selectTaskSubmissionList(taskSubmission);
        ExcelUtil<TaskSubmission> util = new ExcelUtil<TaskSubmission>(TaskSubmission.class);
        util.exportExcel(response, list, "任务提交记录数据");
    }

    /**
     * 获取任务提交记录详细信息
     */
    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('teacher') or @ss.hasRole('student')")
    @GetMapping(value = "/{submissionId}")
    public AjaxResult getInfo(@PathVariable("submissionId") Long submissionId)
    {
        return success(taskSubmissionService.selectTaskSubmissionBySubmissionId(submissionId));
    }

    /**
     * 新增任务提交记录
     */
    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('teacher') or @ss.hasRole('student')")
    @Log(title = "任务提交记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TaskSubmission taskSubmission)
    {
        System.out.println("[DEBUG] 收到新增任务提交请求: " + taskSubmission);
        AjaxResult result = toAjax(taskSubmissionService.insertTaskSubmission(taskSubmission));
        System.out.println("[DEBUG] 新增任务提交结果: " + result);
        return result;
    }

    /**
     * 修改任务提交记录
     */
    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('teacher')")
    @Log(title = "任务提交记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TaskSubmission taskSubmission)
    {
        return toAjax(taskSubmissionService.updateTaskSubmission(taskSubmission));
    }

    /**
     * 删除任务提交记录
     */
    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('teacher')")
    @Log(title = "任务提交记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{submissionIds}")
    public AjaxResult remove(@PathVariable Long[] submissionIds)
    {
        return toAjax(taskSubmissionService.deleteTaskSubmissionBySubmissionIds(submissionIds));
    }
}
