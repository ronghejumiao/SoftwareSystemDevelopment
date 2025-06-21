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
import com.ruoyi.system.domain.Classinfo;
import com.ruoyi.system.service.IClassinfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 班级信息，存储班级的基本信息Controller
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
@RestController
@RequestMapping("/system/classinfo")
public class ClassinfoController extends BaseController
{
    @Autowired
    private IClassinfoService classinfoService;

    /**
     * 查询班级信息，存储班级的基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:classinfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(Classinfo classinfo)
    {
        startPage();
        List<Classinfo> list = classinfoService.selectClassinfoList(classinfo);
        return getDataTable(list);
    }

    /**
     * 导出班级信息，存储班级的基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:classinfo:export')")
    @Log(title = "班级信息，存储班级的基本信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Classinfo classinfo)
    {
        List<Classinfo> list = classinfoService.selectClassinfoList(classinfo);
        ExcelUtil<Classinfo> util = new ExcelUtil<Classinfo>(Classinfo.class);
        util.exportExcel(response, list, "班级信息，存储班级的基本信息数据");
    }

    /**
     * 获取班级信息，存储班级的基本信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:classinfo:query')")
    @GetMapping(value = "/{classId}")
    public AjaxResult getInfo(@PathVariable("classId") Long classId)
    {
        return success(classinfoService.selectClassinfoByClassId(classId));
    }

    /**
     * 新增班级信息，存储班级的基本信息
     */
    @PreAuthorize("@ss.hasPermi('system:classinfo:add')")
    @Log(title = "班级信息，存储班级的基本信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Classinfo classinfo)
    {
        return toAjax(classinfoService.insertClassinfo(classinfo));
    }

    /**
     * 修改班级信息，存储班级的基本信息
     */
    @PreAuthorize("@ss.hasPermi('system:classinfo:edit')")
    @Log(title = "班级信息，存储班级的基本信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Classinfo classinfo)
    {
        return toAjax(classinfoService.updateClassinfo(classinfo));
    }

    /**
     * 删除班级信息，存储班级的基本信息
     */
    @PreAuthorize("@ss.hasPermi('system:classinfo:remove')")
    @Log(title = "班级信息，存储班级的基本信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{classIds}")
    public AjaxResult remove(@PathVariable Long[] classIds)
    {
        return toAjax(classinfoService.deleteClassinfoByClassIds(classIds));
    }
}
