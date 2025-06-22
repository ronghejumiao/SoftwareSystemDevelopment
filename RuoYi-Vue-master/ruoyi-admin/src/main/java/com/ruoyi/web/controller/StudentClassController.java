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
import com.ruoyi.system.domain.StudentClass;
import com.ruoyi.system.service.IStudentClassService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 学生班级关联，实现学生与班级的多对多关系Controller
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
@RestController
@RequestMapping("/system/class")
public class StudentClassController extends BaseController
{
    @Autowired
    private IStudentClassService studentClassService;

    /**
     * 查询学生班级关联，实现学生与班级的多对多关系列表
     */
    @PreAuthorize("@ss.hasPermi('system:class:list')")
    @GetMapping("/list")
    public TableDataInfo list(StudentClass studentClass)
    {
        startPage();
        List<StudentClass> list = studentClassService.selectStudentClassList(studentClass);
        return getDataTable(list);
    }

    /**
     * 导出学生班级关联，实现学生与班级的多对多关系列表
     */
    @PreAuthorize("@ss.hasPermi('system:class:export')")
    @Log(title = "学生班级关联，实现学生与班级的多对多关系", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StudentClass studentClass)
    {
        List<StudentClass> list = studentClassService.selectStudentClassList(studentClass);
        ExcelUtil<StudentClass> util = new ExcelUtil<StudentClass>(StudentClass.class);
        util.exportExcel(response, list, "学生班级关联，实现学生与班级的多对多关系数据");
    }

    /**
     * 获取学生班级关联，实现学生与班级的多对多关系详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:class:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(studentClassService.selectStudentClassById(id));
    }

    /**
     * 新增学生班级关联，实现学生与班级的多对多关系
     */
    @PreAuthorize("@ss.hasPermi('system:class:add')")
    @Log(title = "学生班级关联，实现学生与班级的多对多关系", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StudentClass studentClass)
    {
        return toAjax(studentClassService.insertStudentClass(studentClass));
    }

    /**
     * 修改学生班级关联，实现学生与班级的多对多关系
     */
    @PreAuthorize("@ss.hasPermi('system:class:edit')")
    @Log(title = "学生班级关联，实现学生与班级的多对多关系", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StudentClass studentClass)
    {
        return toAjax(studentClassService.updateStudentClass(studentClass));
    }

    /**
     * 删除学生班级关联，实现学生与班级的多对多关系
     */
    @PreAuthorize("@ss.hasPermi('system:class:remove')")
    @Log(title = "学生班级关联，实现学生与班级的多对多关系", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(studentClassService.deleteStudentClassByIds(ids));
    }
}
