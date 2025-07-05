package com.ruoyi.web.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.config.ServerConfig;
import com.ruoyi.system.domain.LearningResource;
import com.ruoyi.system.service.ILearningResourceAnalysisService;
import com.ruoyi.system.service.ILearningResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 学习资源Controller
 *
 * @author ruoyi
 * @date 2025-06-22
 */
@RestController
@RequestMapping("/system/resource")
public class LearningResourceController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(LearningResourceController.class);

    @Autowired
    private ILearningResourceService learningResourceService;

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private com.ruoyi.system.service.ILearningResourceAnalysisService learningResourceAnalysisService;

    /**
     * 查询学习资源列表
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
     * 导出学习资源列表
     */
    @PreAuthorize("@ss.hasPermi('system:resource:export')")
    @Log(title = "学习资源", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LearningResource learningResource)
    {
        List<LearningResource> list = learningResourceService.selectLearningResourceList(learningResource);
        ExcelUtil<LearningResource> util = new ExcelUtil<LearningResource>(LearningResource.class);
        util.exportExcel(response, list, "学习资源数据");
    }

    /**
     * 获取学习资源详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:resource:query')")
    @GetMapping(value = "/{resourceId}")
    public AjaxResult getInfo(@PathVariable("resourceId") Long resourceId)
    {
        return success(learningResourceService.selectLearningResourceByResourceId(resourceId));
    }

    /**
     * 新增学习资源
     */
    @PreAuthorize("@ss.hasPermi('system:resource:add')")
    @Log(title = "学习资源", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LearningResource learningResource)
    {
        return toAjax(learningResourceService.insertLearningResource(learningResource));
    }

    /**
     * 修改学习资源
     */
    @PreAuthorize("@ss.hasPermi('system:resource:edit')")
    @Log(title = "学习资源", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LearningResource learningResource)
    {
        return toAjax(learningResourceService.updateLearningResource(learningResource));
    }

    /**
     * 删除学习资源
     */
    @PreAuthorize("@ss.hasPermi('system:resource:remove')")
    @Log(title = "学习资源", businessType = BusinessType.DELETE)
    @DeleteMapping("/{resourceIds}")
    public AjaxResult remove(@PathVariable Long[] resourceIds)
    {
        return toAjax(learningResourceService.deleteLearningResourceByResourceIds(resourceIds));
    }

    /**
     * 学习资源文件上传
     */
    @Log(title = "学习资源", businessType = BusinessType.UPDATE)
    @PostMapping("/upload")
    public AjaxResult uploadFile(MultipartFile file) throws Exception
    {
        try
        {
            // 上传文件到指定目录
            String filePath = RuoYiConfig.getProfile();
            // 上传并返回新文件名称（相对路径）
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = serverConfig.getUrl() + fileName;
            AjaxResult ajax = AjaxResult.success();
            ajax.put("url", url);
            ajax.put("fileName", fileName);
            return ajax;
        }
        catch (Exception e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 获取学习资源AI内容分析
     */
    @GetMapping("/analysis/{resourceId}")
    public AjaxResult getResourceAnalysis(@PathVariable Long resourceId) {
        log.info("[DEBUG] getResourceAnalysis 接口调用, resourceId={}", resourceId);
        com.ruoyi.system.domain.LearningResourceAnalysis analysis = learningResourceAnalysisService.getAnalysisByResourceId(resourceId);
        if (analysis == null) {
            analysis = new com.ruoyi.system.domain.LearningResourceAnalysis();
            analysis.setResourceId(resourceId);
            analysis.setContentSummary("");
        }
        log.info("[DEBUG] getResourceAnalysis 返回: {}", analysis);
        return AjaxResult.success(analysis);
    }

    /**
     * 触发AI内容分析
     */
    @PostMapping("/analyze/{resourceId}")
    public AjaxResult analyzeResource(@PathVariable Long resourceId) {
        log.info("[DEBUG] analyzeResource 接口调用, resourceId={}", resourceId);
        learningResourceAnalysisService.analyzeResource(resourceId);
        log.info("[DEBUG] analyzeResource 已触发分析, resourceId={}", resourceId);
        return AjaxResult.success("分析已触发");
    }

    /**
     * 获取全部学习资源（不分页）
     */
    @GetMapping("/allList")
    public AjaxResult allList(@RequestParam(required = false) Long courseId) {
        LearningResource query = new LearningResource();
        query.setCourseId(courseId);
        List<LearningResource> list = learningResourceService.selectLearningResourceList(query);
        return AjaxResult.success(list);
    }

    /**
     * 批量AI内容分析（队列排队）
     */
    @PostMapping("/batchAnalyze")
    public AjaxResult batchAnalyze() {
        try {
            learningResourceAnalysisService.analyzePendingResources();
            return AjaxResult.success("已触发批量分析，任务将按队列排队执行");
        } catch (Exception e) {
            return AjaxResult.error("批量分析触发失败: " + e.getMessage());
        }
    }
}