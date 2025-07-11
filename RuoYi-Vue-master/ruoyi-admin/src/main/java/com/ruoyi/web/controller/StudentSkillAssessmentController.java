package com.ruoyi.web.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.service.IStudentSkillAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 学生能力评估控制器
 *
 * @author ruoyi
 * @date 2025-01-20
 */
@RestController
@RequestMapping("/system/assessment")
public class StudentSkillAssessmentController extends BaseController {
    
    @Autowired
    private IStudentSkillAssessmentService studentSkillAssessmentService;

    /**
     * 评估单个学生的能力
     */
    @PostMapping("/assess/{studentId}/{courseId}")
    @Log(title = "学生能力评估", businessType = BusinessType.OTHER)
    public AjaxResult assessStudentSkill(@PathVariable Long studentId, @PathVariable Long courseId) {
        try {
            Map<Long, Double> result = studentSkillAssessmentService.assessStudentSkill(studentId, courseId);
            return AjaxResult.success("评估完成", result);
        } catch (Exception e) {
            return AjaxResult.error("评估失败: " + e.getMessage());
        }
    }

    /**
     * 批量评估课程中所有学生的能力
     */
    @PostMapping("/batch-assess/{courseId}")
    @Log(title = "批量能力评估", businessType = BusinessType.OTHER)
    public AjaxResult batchAssessAllStudentsSkill(@PathVariable Long courseId) {
        try {
            List<Map<String, Object>> result = studentSkillAssessmentService.batchAssessAllStudentsSkill(courseId);
            return AjaxResult.success("批量评估完成", result);
        } catch (Exception e) {
            return AjaxResult.error("批量评估失败: " + e.getMessage());
        }
    }

    /**
     * 获取学生能力评估报告
     */
    @GetMapping("/report/{studentId}/{courseId}")
    public AjaxResult getStudentSkillReport(@PathVariable Long studentId, @PathVariable Long courseId) {
        try {
            Map<String, Object> report = studentSkillAssessmentService.getStudentSkillReport(studentId, courseId);
            return AjaxResult.success(report);
        } catch (Exception e) {
            return AjaxResult.error("获取报告失败: " + e.getMessage());
        }
    }

    /**
     * 获取课程所有学生的能力评估报告
     */
    @GetMapping("/course-report/{courseId}")
    public AjaxResult getCourseSkillReport(@PathVariable Long courseId) {
        try {
            List<Map<String, Object>> reports = studentSkillAssessmentService.getCourseSkillReport(courseId);
            return AjaxResult.success(reports);
        } catch (Exception e) {
            return AjaxResult.error("获取课程报告失败: " + e.getMessage());
        }
    }

    /**
     * 人工调整能力分数
     */
    @PostMapping("/adjust")
    @Log(title = "人工调整能力分数", businessType = BusinessType.UPDATE)
    public AjaxResult manualAdjustSkillScore(@RequestBody Map<String, Object> params) {
        try {
            Long studentId = Long.valueOf(params.get("studentId").toString());
            Long requirementId = Long.valueOf(params.get("requirementId").toString());
            Double score = Double.valueOf(params.get("score").toString());
            String reason = params.get("reason").toString();
            
            boolean success = studentSkillAssessmentService.manualAdjustSkillScore(studentId, requirementId, score, reason);
            if (success) {
                return AjaxResult.success("调整成功");
            } else {
                return AjaxResult.error("调整失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("调整失败: " + e.getMessage());
        }
    }

    /**
     * 触发数据变更时的能力评估
     */
    @PostMapping("/trigger/{studentId}/{courseId}")
    public AjaxResult triggerAssessmentOnDataChange(@PathVariable Long studentId, @PathVariable Long courseId) {
        try {
            studentSkillAssessmentService.triggerAssessmentOnDataChange(studentId, courseId);
            return AjaxResult.success("触发评估成功");
        } catch (Exception e) {
            return AjaxResult.error("触发评估失败: " + e.getMessage());
        }
    }
} 