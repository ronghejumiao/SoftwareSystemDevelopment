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
import com.ruoyi.system.domain.CourseSkillRequirement;
import com.ruoyi.system.service.ICourseSkillRequirementService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 课程能力要求，一个课程包含多个能力要求Controller
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
@RestController
@RequestMapping("/system/requirement")
public class CourseSkillRequirementController extends BaseController
{
    @Autowired
    private ICourseSkillRequirementService courseSkillRequirementService;

    /**
     * 查询课程能力要求，一个课程包含多个能力要求列表
     */
    @PreAuthorize("@ss.hasPermi('system:requirement:list')")
    @GetMapping("/list")
    public TableDataInfo list(CourseSkillRequirement courseSkillRequirement)
    {
        startPage();
        List<CourseSkillRequirement> list = courseSkillRequirementService.selectCourseSkillRequirementList(courseSkillRequirement);
        return getDataTable(list);
    }

    /**
     * 导出课程能力要求，一个课程包含多个能力要求列表
     */
    @PreAuthorize("@ss.hasPermi('system:requirement:export')")
    @Log(title = "课程能力要求，一个课程包含多个能力要求", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CourseSkillRequirement courseSkillRequirement)
    {
        List<CourseSkillRequirement> list = courseSkillRequirementService.selectCourseSkillRequirementList(courseSkillRequirement);
        ExcelUtil<CourseSkillRequirement> util = new ExcelUtil<CourseSkillRequirement>(CourseSkillRequirement.class);
        util.exportExcel(response, list, "课程能力要求，一个课程包含多个能力要求数据");
    }

    /**
     * 获取课程能力要求，一个课程包含多个能力要求详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:requirement:query')")
    @GetMapping(value = "/{requirementId}")
    public AjaxResult getInfo(@PathVariable("requirementId") Long requirementId)
    {
        return success(courseSkillRequirementService.selectCourseSkillRequirementByRequirementId(requirementId));
    }

    /**
     * 新增课程能力要求，一个课程包含多个能力要求
     */
    @PreAuthorize("@ss.hasPermi('system:requirement:add')")
    @Log(title = "课程能力要求，一个课程包含多个能力要求", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CourseSkillRequirement courseSkillRequirement)
    {
        return toAjax(courseSkillRequirementService.insertCourseSkillRequirement(courseSkillRequirement));
    }

    /**
     * 修改课程能力要求，一个课程包含多个能力要求
     */
    @PreAuthorize("@ss.hasPermi('system:requirement:edit')")
    @Log(title = "课程能力要求，一个课程包含多个能力要求", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CourseSkillRequirement courseSkillRequirement)
    {
        return toAjax(courseSkillRequirementService.updateCourseSkillRequirement(courseSkillRequirement));
    }

    /**
     * 删除课程能力要求，一个课程包含多个能力要求
     */
    @PreAuthorize("@ss.hasPermi('system:requirement:remove')")
    @Log(title = "课程能力要求，一个课程包含多个能力要求", businessType = BusinessType.DELETE)
	@DeleteMapping("/{requirementIds}")
    public AjaxResult remove(@PathVariable Long[] requirementIds)
    {
        return toAjax(courseSkillRequirementService.deleteCourseSkillRequirementByRequirementIds(requirementIds));
    }
}
