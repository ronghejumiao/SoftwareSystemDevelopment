package com.ruoyi.web.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.IResourceRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/resource")
public class ResourceRecommendationController extends BaseController {
    @Autowired
    private IResourceRecommendationService resourceRecommendationService;

    /**
     * 个性化学习资源推荐
     */
    @GetMapping("/recommend/{studentId}/{courseId}")
    public AjaxResult recommend(@PathVariable Long studentId, @PathVariable Long courseId, @RequestParam(value = "forceRefresh", required = false, defaultValue = "false") boolean forceRefresh) {
        return AjaxResult.success(resourceRecommendationService.recommendResourcesForStudent(studentId, courseId, forceRefresh));
    }
} 