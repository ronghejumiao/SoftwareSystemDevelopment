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
import com.ruoyi.system.domain.CourseImg;
import com.ruoyi.system.service.ICourseImgService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 课程概念图，存储课程的概念图URL（1对多关系）Controller
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
@RestController
@RequestMapping("/system/img")
public class CourseImgController extends BaseController
{
    @Autowired
    private ICourseImgService courseImgService;

    /**
     * 查询课程概念图，存储课程的概念图URL（1对多关系）列表
     */
    @PreAuthorize("@ss.hasPermi('system:img:list')")
    @GetMapping("/list")
    public TableDataInfo list(CourseImg courseImg)
    {
        startPage();
        List<CourseImg> list = courseImgService.selectCourseImgList(courseImg);
        return getDataTable(list);
    }

    /**
     * 导出课程概念图，存储课程的概念图URL（1对多关系）列表
     */
    @PreAuthorize("@ss.hasPermi('system:img:export')")
    @Log(title = "课程概念图，存储课程的概念图URL（1对多关系）", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CourseImg courseImg)
    {
        List<CourseImg> list = courseImgService.selectCourseImgList(courseImg);
        ExcelUtil<CourseImg> util = new ExcelUtil<CourseImg>(CourseImg.class);
        util.exportExcel(response, list, "课程概念图，存储课程的概念图URL（1对多关系）数据");
    }

    /**
     * 获取课程概念图，存储课程的概念图URL（1对多关系）详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:img:query')")
    @GetMapping(value = "/{mapId}")
    public AjaxResult getInfo(@PathVariable("mapId") Long mapId)
    {
        return success(courseImgService.selectCourseImgByMapId(mapId));
    }

    /**
     * 新增课程概念图，存储课程的概念图URL（1对多关系）
     */
    @PreAuthorize("@ss.hasPermi('system:img:add')")
    @Log(title = "课程概念图，存储课程的概念图URL（1对多关系）", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CourseImg courseImg)
    {
        return toAjax(courseImgService.insertCourseImg(courseImg));
    }

    /**
     * 修改课程概念图，存储课程的概念图URL（1对多关系）
     */
    @PreAuthorize("@ss.hasPermi('system:img:edit')")
    @Log(title = "课程概念图，存储课程的概念图URL（1对多关系）", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CourseImg courseImg)
    {
        return toAjax(courseImgService.updateCourseImg(courseImg));
    }

    /**
     * 删除课程概念图，存储课程的概念图URL（1对多关系）
     */
    @PreAuthorize("@ss.hasPermi('system:img:remove')")
    @Log(title = "课程概念图，存储课程的概念图URL（1对多关系）", businessType = BusinessType.DELETE)
	@DeleteMapping("/{mapIds}")
    public AjaxResult remove(@PathVariable Long[] mapIds)
    {
        return toAjax(courseImgService.deleteCourseImgByMapIds(mapIds));
    }
}
