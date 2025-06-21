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
import com.ruoyi.system.domain.CoursePaperLibrary;
import com.ruoyi.system.service.ICoursePaperLibraryService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 课程试卷库，一个课程对应一个试卷库Controller
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
@RestController
@RequestMapping("/system/library")
public class CoursePaperLibraryController extends BaseController
{
    @Autowired
    private ICoursePaperLibraryService coursePaperLibraryService;

    /**
     * 查询课程试卷库，一个课程对应一个试卷库列表
     */
    @PreAuthorize("@ss.hasPermi('system:library:list')")
    @GetMapping("/list")
    public TableDataInfo list(CoursePaperLibrary coursePaperLibrary)
    {
        startPage();
        List<CoursePaperLibrary> list = coursePaperLibraryService.selectCoursePaperLibraryList(coursePaperLibrary);
        return getDataTable(list);
    }

    /**
     * 导出课程试卷库，一个课程对应一个试卷库列表
     */
    @PreAuthorize("@ss.hasPermi('system:library:export')")
    @Log(title = "课程试卷库，一个课程对应一个试卷库", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CoursePaperLibrary coursePaperLibrary)
    {
        List<CoursePaperLibrary> list = coursePaperLibraryService.selectCoursePaperLibraryList(coursePaperLibrary);
        ExcelUtil<CoursePaperLibrary> util = new ExcelUtil<CoursePaperLibrary>(CoursePaperLibrary.class);
        util.exportExcel(response, list, "课程试卷库，一个课程对应一个试卷库数据");
    }

    /**
     * 获取课程试卷库，一个课程对应一个试卷库详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:library:query')")
    @GetMapping(value = "/{libraryId}")
    public AjaxResult getInfo(@PathVariable("libraryId") Long libraryId)
    {
        return success(coursePaperLibraryService.selectCoursePaperLibraryByLibraryId(libraryId));
    }

    /**
     * 新增课程试卷库，一个课程对应一个试卷库
     */
    @PreAuthorize("@ss.hasPermi('system:library:add')")
    @Log(title = "课程试卷库，一个课程对应一个试卷库", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CoursePaperLibrary coursePaperLibrary)
    {
        return toAjax(coursePaperLibraryService.insertCoursePaperLibrary(coursePaperLibrary));
    }

    /**
     * 修改课程试卷库，一个课程对应一个试卷库
     */
    @PreAuthorize("@ss.hasPermi('system:library:edit')")
    @Log(title = "课程试卷库，一个课程对应一个试卷库", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CoursePaperLibrary coursePaperLibrary)
    {
        return toAjax(coursePaperLibraryService.updateCoursePaperLibrary(coursePaperLibrary));
    }

    /**
     * 删除课程试卷库，一个课程对应一个试卷库
     */
    @PreAuthorize("@ss.hasPermi('system:library:remove')")
    @Log(title = "课程试卷库，一个课程对应一个试卷库", businessType = BusinessType.DELETE)
	@DeleteMapping("/{libraryIds}")
    public AjaxResult remove(@PathVariable Long[] libraryIds)
    {
        return toAjax(coursePaperLibraryService.deleteCoursePaperLibraryByLibraryIds(libraryIds));
    }
}
