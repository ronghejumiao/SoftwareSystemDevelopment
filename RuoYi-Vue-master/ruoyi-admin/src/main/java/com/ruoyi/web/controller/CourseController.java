package com.ruoyi.web.controller;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.framework.config.ServerConfig;
import com.ruoyi.system.service.IAIGradingService;
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
import com.ruoyi.system.domain.Course;
import com.ruoyi.system.service.ICourseService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 课程信息，存储课程的基本信息Controller
 *
 * @author ruoyi
 * @date 2025-06-22
 */
@RestController
@RequestMapping("/system/course")
public class CourseController extends BaseController
{
    @Autowired
    private ICourseService courseService;

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private IAIGradingService aiGradingService;

    /**
     * 查询课程信息，存储课程的基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:course:list')")
    @GetMapping("/list")
    public TableDataInfo list(Course course)
    {
        startPage();
        List<Course> list = courseService.selectCourseList(course);
        return getDataTable(list);
    }

    /**
     * 导出课程信息，存储课程的基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:course:export')")
    @Log(title = "课程信息，存储课程的基本信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Course course)
    {
        List<Course> list = courseService.selectCourseList(course);
        ExcelUtil<Course> util = new ExcelUtil<Course>(Course.class);
        util.exportExcel(response, list, "课程信息，存储课程的基本信息数据");
    }

    /**
     * 获取课程信息，存储课程的基本信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:course:query')")
    @GetMapping(value = "/{courseId}")
    public AjaxResult getInfo(@PathVariable("courseId") Long courseId)
    {
        return success(courseService.selectCourseByCourseId(courseId));
    }

    /**
     * 新增课程信息，存储课程的基本信息
     */
    @PreAuthorize("@ss.hasPermi('system:course:add')")
    @Log(title = "课程信息，存储课程的基本信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Course course)
    {
        return toAjax(courseService.insertCourse(course));
    }

    /**
     * 修改课程信息，存储课程的基本信息
     */
    @PreAuthorize("@ss.hasPermi('system:course:edit')")
    @Log(title = "课程信息，存储课程的基本信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Course course)
    {
        return toAjax(courseService.updateCourse(course));
    }

    /**
     * 删除课程信息，存储课程的基本信息
     */
    @PreAuthorize("@ss.hasPermi('system:course:remove')")
    @Log(title = "课程信息，存储课程的基本信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{courseIds}")
    public AjaxResult remove(@PathVariable Long[] courseIds)
    {
        return toAjax(courseService.deleteCourseByCourseIds(courseIds));
    }

    /**
     * 课程图片上传
     */
    @Log(title = "课程图片", businessType = BusinessType.UPDATE)
    @PostMapping("/upload")
    public AjaxResult uploadFile(MultipartFile file) throws Exception
    {
        try
        {
            // 上传文件路径
            String filePath = RuoYiConfig.getUploadPath();
            // 上传并返回新文件名称
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
     * AI评分接口
     */
    @PreAuthorize("@ss.hasPermi('system:course:grade')")
    @Log(title = "AI评分", businessType = BusinessType.UPDATE)
    @PostMapping("/ai-grade")
    public AjaxResult aiGrade(@RequestBody Map<String, Object> request)
    {
        try
        {
            Object courseIdObj = request.get("courseId");
            Object homeworkIdObj = request.get("homeworkId");
            Object submissionIdObj = request.get("submissionId");

            if (courseIdObj == null) {
                return AjaxResult.error("参数courseId缺失");
            }
            if (homeworkIdObj == null) {
                return AjaxResult.error("参数homeworkId缺失");
            }
            if (submissionIdObj == null) {
                return AjaxResult.error("参数submissionId缺失");
            }

            Long courseId = Long.valueOf(courseIdObj.toString());
            Long homeworkId = Long.valueOf(homeworkIdObj.toString());
            Long submissionId = Long.valueOf(submissionIdObj.toString());
            
            IAIGradingService.AIGradingResult result = aiGradingService.gradeHomework(courseId, homeworkId, submissionId);
            
            if (result.isSuccess()) {
                AjaxResult ajax = AjaxResult.success();
                ajax.put("score", result.getScore());
                ajax.put("comment", result.getComment());
                return ajax;
            } else {
                return AjaxResult.error(result.getError());
            }
        }
        catch (Exception e)
        {
            return AjaxResult.error("AI评分失败: " + e.getMessage());
        }
    }
}