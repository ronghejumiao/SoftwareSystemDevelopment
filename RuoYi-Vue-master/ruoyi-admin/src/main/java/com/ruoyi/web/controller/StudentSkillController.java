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
import com.ruoyi.system.domain.StudentSkill;
import com.ruoyi.system.service.IStudentSkillService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 学生能力，基于课程能力要求构建Controller
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
@RestController
@RequestMapping("/system/skill")
public class StudentSkillController extends BaseController
{
    @Autowired
    private IStudentSkillService studentSkillService;

    /**
     * 查询学生能力，基于课程能力要求构建列表
     */
    @PreAuthorize("@ss.hasPermi('system:skill:list')")
    @GetMapping("/list")
    public TableDataInfo list(StudentSkill studentSkill)
    {
        startPage();
        List<StudentSkill> list = studentSkillService.selectStudentSkillList(studentSkill);
        return getDataTable(list);
    }

    /**
     * 导出学生能力，基于课程能力要求构建列表
     */
    @PreAuthorize("@ss.hasPermi('system:skill:export')")
    @Log(title = "学生能力，基于课程能力要求构建", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StudentSkill studentSkill)
    {
        List<StudentSkill> list = studentSkillService.selectStudentSkillList(studentSkill);
        ExcelUtil<StudentSkill> util = new ExcelUtil<StudentSkill>(StudentSkill.class);
        util.exportExcel(response, list, "学生能力，基于课程能力要求构建数据");
    }

    /**
     * 获取学生能力，基于课程能力要求构建详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:skill:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(studentSkillService.selectStudentSkillById(id));
    }

    /**
     * 新增学生能力，基于课程能力要求构建
     */
    @PreAuthorize("@ss.hasPermi('system:skill:add')")
    @Log(title = "学生能力，基于课程能力要求构建", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StudentSkill studentSkill)
    {
        return toAjax(studentSkillService.insertStudentSkill(studentSkill));
    }

    /**
     * 修改学生能力，基于课程能力要求构建
     */
    @PreAuthorize("@ss.hasPermi('system:skill:edit')")
    @Log(title = "学生能力，基于课程能力要求构建", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StudentSkill studentSkill)
    {
        return toAjax(studentSkillService.updateStudentSkill(studentSkill));
    }

    /**
     * 删除学生能力，基于课程能力要求构建
     */
    @PreAuthorize("@ss.hasPermi('system:skill:remove')")
    @Log(title = "学生能力，基于课程能力要求构建", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(studentSkillService.deleteStudentSkillByIds(ids));
    }
}
