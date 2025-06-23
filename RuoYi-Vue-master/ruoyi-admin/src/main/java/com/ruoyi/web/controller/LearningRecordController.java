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
import com.ruoyi.system.domain.LearningRecord;
import com.ruoyi.system.service.ILearningRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 学习记录，记录学生的课程学习关联信息Controller
 * 
 * @author ruoyi
 * @date 2025-06-21
 */
@RestController
@RequestMapping("/system/learningRecord")
public class LearningRecordController extends BaseController
{
    @Autowired
    private ILearningRecordService learningRecordService;

    /**
     * 查询学习记录，记录学生的课程学习关联信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:learningRecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(LearningRecord learningRecord)
    {
        startPage();
        List<LearningRecord> list = learningRecordService.selectLearningRecordList(learningRecord);
        return getDataTable(list);
    }

    /**
     * 导出学习记录，记录学生的课程学习关联信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:learningRecord:export')")
    @Log(title = "学习记录，记录学生的课程学习关联信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LearningRecord learningRecord)
    {
        List<LearningRecord> list = learningRecordService.selectLearningRecordList(learningRecord);
        ExcelUtil<LearningRecord> util = new ExcelUtil<LearningRecord>(LearningRecord.class);
        util.exportExcel(response, list, "学习记录，记录学生的课程学习关联信息数据");
    }

    /**
     * 获取学习记录，记录学生的课程学习关联信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:learningRecord:query')")
    @GetMapping(value = "/{recordId}")
    public AjaxResult getInfo(@PathVariable("recordId") Long recordId)
    {
        return success(learningRecordService.selectLearningRecordByRecordId(recordId));
    }

    /**
     * 新增学习记录，记录学生的课程学习关联信息
     */
    @PreAuthorize("@ss.hasPermi('system:learningRecord:add')")
    @Log(title = "学习记录，记录学生的课程学习关联信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LearningRecord learningRecord)
    {
        return toAjax(learningRecordService.insertLearningRecord(learningRecord));
    }

    /**
     * 修改学习记录，记录学生的课程学习关联信息
     */
    @PreAuthorize("@ss.hasPermi('system:learningRecord:edit')")
    @Log(title = "学习记录，记录学生的课程学习关联信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LearningRecord learningRecord)
    {
        return toAjax(learningRecordService.updateLearningRecord(learningRecord));
    }

    /**
     * 删除学习记录，记录学生的课程学习关联信息
     */
    @PreAuthorize("@ss.hasPermi('system:learningRecord:remove')")
    @Log(title = "学习记录，记录学生的课程学习关联信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds)
    {
        return toAjax(learningRecordService.deleteLearningRecordByRecordIds(recordIds));
    }

    /**
     * 根据用户ID和课程ID查询学习记录
     */
    @PreAuthorize("@ss.hasPermi('system:learningRecord:list')")
    @GetMapping("/user/{userId}/course/{courseId}")
    public AjaxResult getLearningRecordByUserAndCourse(@PathVariable("userId") Long userId, @PathVariable("courseId") Long courseId)
    {
        LearningRecord learningRecord = new LearningRecord();
        learningRecord.setUserId(userId);
        learningRecord.setCourseId(courseId);
        List<LearningRecord> list = learningRecordService.selectLearningRecordList(learningRecord);
        
        if (list != null && !list.isEmpty()) {
            return success(list.get(0));
        } else {
            // 如果没有找到记录，创建一个新的学习记录
            LearningRecord newRecord = new LearningRecord();
            newRecord.setUserId(userId);
            newRecord.setCourseId(courseId);
            newRecord.setCourseProgress(0L);
            learningRecordService.insertLearningRecord(newRecord);
            return success(newRecord);
        }
    }
}
