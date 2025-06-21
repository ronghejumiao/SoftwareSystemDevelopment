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
import com.ruoyi.system.domain.LearningResource;
import com.ruoyi.system.service.ILearningResourceService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 学习资源，存储课程的学习资源信息Controller
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
@RestController
@RequestMapping("/system/resource")
public class LearningResourceController extends BaseController
{
    @Autowired
    private ILearningResourceService learningResourceService;

    /**
     * 查询学习资源，存储课程的学习资源信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:resource:list')")
    @GetMapping("/list")
    public TableDataInfo list(LearningResource learningResource)
    {
        startPage();
        List<LearningResource> list = learningResourceService.selectLearningResourceList(learningResource);
        return getDataTable(list);
    }

    /**
     * 导出学习资源，存储课程的学习资源信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:resource:export')")
    @Log(title = "学习资源，存储课程的学习资源信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LearningResource learningResource)
    {
        List<LearningResource> list = learningResourceService.selectLearningResourceList(learningResource);
        ExcelUtil<LearningResource> util = new ExcelUtil<LearningResource>(LearningResource.class);
        util.exportExcel(response, list, "学习资源，存储课程的学习资源信息数据");
    }

    /**
     * 获取学习资源，存储课程的学习资源信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:resource:query')")
    @GetMapping(value = "/{resourceId}")
    public AjaxResult getInfo(@PathVariable("resourceId") Long resourceId)
    {
        return success(learningResourceService.selectLearningResourceByResourceId(resourceId));
    }

    /**
     * 新增学习资源，存储课程的学习资源信息
     */
    @PreAuthorize("@ss.hasPermi('system:resource:add')")
    @Log(title = "学习资源，存储课程的学习资源信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LearningResource learningResource)
    {
        return toAjax(learningResourceService.insertLearningResource(learningResource));
    }

    /**
     * 修改学习资源，存储课程的学习资源信息
     */
    @PreAuthorize("@ss.hasPermi('system:resource:edit')")
    @Log(title = "学习资源，存储课程的学习资源信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LearningResource learningResource)
    {
        return toAjax(learningResourceService.updateLearningResource(learningResource));
    }

    /**
     * 删除学习资源，存储课程的学习资源信息
     */
    @PreAuthorize("@ss.hasPermi('system:resource:remove')")
    @Log(title = "学习资源，存储课程的学习资源信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{resourceIds}")
    public AjaxResult remove(@PathVariable Long[] resourceIds)
    {
        return toAjax(learningResourceService.deleteLearningResourceByResourceIds(resourceIds));
    }
}
