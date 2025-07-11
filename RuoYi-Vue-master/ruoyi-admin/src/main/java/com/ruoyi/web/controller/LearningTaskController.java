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
import com.ruoyi.system.domain.LearningTask;
import com.ruoyi.system.service.ILearningTaskService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 学习任务，存储课程的学习任务信息Controller
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
@RestController
@RequestMapping("/system/task")
public class LearningTaskController extends BaseController
{
    @Autowired
    private ILearningTaskService learningTaskService;

    /**
     * 查询学习任务，存储课程的学习任务信息列表
     */
    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('teacher') or @ss.hasRole('student')")
    @GetMapping("/list")
    public TableDataInfo list(LearningTask learningTask)
    {
        startPage();
        List<LearningTask> list = learningTaskService.selectLearningTaskList(learningTask);
        return getDataTable(list);
    }

    /**
     * 导出学习任务，存储课程的学习任务信息列表
     */
    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('teacher')")
    @Log(title = "学习任务，存储课程的学习任务信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LearningTask learningTask)
    {
        List<LearningTask> list = learningTaskService.selectLearningTaskList(learningTask);
        ExcelUtil<LearningTask> util = new ExcelUtil<LearningTask>(LearningTask.class);
        util.exportExcel(response, list, "学习任务，存储课程的学习任务信息数据");
    }

    /**
     * 获取学习任务，存储课程的学习任务信息详细信息
     */
    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('teacher') or @ss.hasRole('student')")
    @GetMapping(value = "/{taskId}")
    public AjaxResult getInfo(@PathVariable("taskId") Long taskId)
    {
        return success(learningTaskService.selectLearningTaskByTaskId(taskId));
    }

    /**
     * 新增学习任务，存储课程的学习任务信息
     */
    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('teacher')")
    @Log(title = "学习任务，存储课程的学习任务信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LearningTask learningTask)
    {
        return toAjax(learningTaskService.insertLearningTask(learningTask));
    }

    /**
     * 修改学习任务，存储课程的学习任务信息
     */
    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('teacher')")
    @Log(title = "学习任务，存储课程的学习任务信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LearningTask learningTask)
    {
        return toAjax(learningTaskService.updateLearningTask(learningTask));
    }

    /**
     * 删除学习任务，存储课程的学习任务信息
     */
    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('teacher')")
    @Log(title = "学习任务，存储课程的学习任务信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{taskIds}")
    public AjaxResult remove(@PathVariable Long[] taskIds)
    {
        return toAjax(learningTaskService.deleteLearningTaskByTaskIds(taskIds));
    }
}
