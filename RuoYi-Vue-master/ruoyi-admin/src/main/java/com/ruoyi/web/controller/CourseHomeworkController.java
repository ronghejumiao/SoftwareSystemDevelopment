package com.ruoyi.web.controller;

import java.util.*;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.CourseHomework;
import com.ruoyi.system.domain.LearningTask;
import com.ruoyi.system.domain.LearningRecord;
import com.ruoyi.system.domain.TaskSubmission;
import com.ruoyi.system.service.ICourseHomeworkService;
import com.ruoyi.system.service.ILearningTaskService;
import com.ruoyi.system.service.ILearningRecordService;
import com.ruoyi.system.service.ITaskSubmissionService;
import org.apache.commons.io.FilenameUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.config.RuoYiConfig;

/**
 * 课程作业Controller
 * 
 * @author ruoyi
 * @date 2025-06-25
 */
@RestController
@RequestMapping("/system/homework")
public class CourseHomeworkController extends BaseController
{
    @Autowired
    private ICourseHomeworkService courseHomeworkService;
    @Autowired
    private ILearningTaskService learningTaskService;
    @Autowired
    private ILearningRecordService learningRecordService;
    @Autowired
    private ITaskSubmissionService taskSubmissionService;

    /**
     * 查询课程作业列表
     */
    @PreAuthorize("@ss.hasPermi('system:homework:list')")
    @GetMapping("/list")
    public TableDataInfo list(CourseHomework courseHomework)
    {
        startPage();
        List<CourseHomework> list = courseHomeworkService.selectCourseHomeworkList(courseHomework);
        return getDataTable(list);
    }

    /**
     * 导出课程作业列表
     */
    @PreAuthorize("@ss.hasPermi('system:homework:export')")
    @Log(title = "课程作业", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CourseHomework courseHomework)
    {
        List<CourseHomework> list = courseHomeworkService.selectCourseHomeworkList(courseHomework);
        ExcelUtil<CourseHomework> util = new ExcelUtil<CourseHomework>(CourseHomework.class);
        util.exportExcel(response, list, "课程作业数据");
    }

    /**
     * 获取课程作业详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:homework:query')")
    @GetMapping(value = "/{homeworkId}")
    public AjaxResult getInfo(@PathVariable("homeworkId") Long homeworkId)
    {
        return success(courseHomeworkService.selectCourseHomeworkByHomeworkId(homeworkId));
    }

    /**
     * 新增课程作业
     */
    @PreAuthorize("@ss.hasPermi('system:homework:add')")
    @Log(title = "课程作业", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CourseHomework courseHomework)
    {
        System.out.println("[DEBUG] 新增作业请求: " + courseHomework);
        System.out.println("[DEBUG] 新增作业请求 filePaths: " + courseHomework.getFilePaths());
        int result = courseHomeworkService.insertCourseHomework(courseHomework);
        System.out.println("[DEBUG] 插入作业后 filePaths: " + courseHomework.getFilePaths());
        return toAjax(result);
    }

    /**
     * 修改课程作业
     */
    @PreAuthorize("@ss.hasPermi('system:homework:edit')")
    @Log(title = "课程作业", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CourseHomework courseHomework)
    {
        return toAjax(courseHomeworkService.updateCourseHomework(courseHomework));
    }

    /**
     * 删除课程作业
     */
    @PreAuthorize("@ss.hasPermi('system:homework:remove')")
    @Log(title = "课程作业", businessType = BusinessType.DELETE)
	@DeleteMapping("/{homeworkIds}")
    public AjaxResult remove(@PathVariable Long[] homeworkIds)
    {
        return toAjax(courseHomeworkService.deleteCourseHomeworkByHomeworkIds(homeworkIds));
    }

    /**
     * 获取用户作业完成情况
     */
    @GetMapping("/user/status")
    public AjaxResult getUserHomeworkStatus(@RequestParam Long courseId, @RequestParam Long userId) {
        // 1. 查询所有submit_method=作业完成的learning_task，且homework_id不为空
        List<LearningTask> taskList = learningTaskService.selectLearningTaskListByCourseIdAndSubmitMethod(courseId, "作业完成");
        // 2. 查询所有被引用的作业ID
        Set<Long> homeworkIds = new HashSet<>();
        Map<Long, LearningTask> taskMap = new HashMap<>();
        for (LearningTask task : taskList) {
            if (task.getHomeworkId() != null) {
                homeworkIds.add(task.getHomeworkId());
                taskMap.put(task.getHomeworkId(), task);
            }
        }
        // 3. 查询作业详情
        List<CourseHomework> homeworkList = homeworkIds.isEmpty() ? Collections.emptyList() : courseHomeworkService.selectCourseHomeworkListByIds(new ArrayList<>(homeworkIds));
        Map<Long, CourseHomework> homeworkMap = new HashMap<>();
        for (CourseHomework hw : homeworkList) {
            homeworkMap.put(hw.getHomeworkId(), hw);
        }
        // 4. 查询当前用户的learning_record
        LearningRecord record = learningRecordService.selectByUserIdAndCourseId(userId, courseId);
        Long recordId = record != null ? record.getRecordId() : null;
        // 5. 查询当前用户所有task_submission
        List<TaskSubmission> submissionList = recordId == null ? Collections.emptyList() : taskSubmissionService.selectByRecordId(recordId);
        Set<Long> completedTaskIds = new HashSet<>();
        Map<Long, TaskSubmission> submissionMap = new HashMap<>();
        for (TaskSubmission sub : submissionList) {
            completedTaskIds.add(sub.getTaskId());
            submissionMap.put(sub.getTaskId(), sub);
        }
        // 6. 组装返回结构
        List<Map<String, Object>> completed = new ArrayList<>();
        List<Map<String, Object>> uncompleted = new ArrayList<>();
        for (LearningTask task : taskList) {
            if (task.getHomeworkId() == null) continue;
            CourseHomework hw = homeworkMap.get(task.getHomeworkId());
            if (hw == null) continue;
            Map<String, Object> item = new HashMap<>();
            item.put("task", task);
            item.put("homework", hw);
            if (completedTaskIds.contains(task.getTaskId())) {
                item.put("submission", submissionMap.get(task.getTaskId()));
                completed.add(item);
            } else {
                uncompleted.add(item);
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("completed", completed);
        result.put("uncompleted", uncompleted);
        return AjaxResult.success(result);
    }

    /**
     * 提交作业
     */
    @PostMapping("/submit")
    public AjaxResult submitHomework(@RequestBody TaskSubmission submission)
    {
        // 调试日志：打印收到的参数
        System.out.println("[DEBUG] /submit 收到参数: " + submission);
        System.out.println("[DEBUG] taskId=" + submission.getTaskId() + ", userId=" + submission.getUserId());
        // 1. 获取courseId（通过taskId查找）
        Long courseId = null;
        if (submission.getTaskId() != null) {
            LearningTask task = learningTaskService.selectLearningTaskByTaskId(submission.getTaskId());
            if (task != null) {
                courseId = task.getCourseId();
                System.out.println("[DEBUG] 通过taskId查到courseId=" + courseId);
            } else {
                System.out.println("[DEBUG] 未查到taskId=" + submission.getTaskId() + " 对应的LearningTask");
            }
        }
        if (courseId == null) {
            System.out.println("[DEBUG] 未能确定课程ID，所有参数：" + submission);
            return error("未能确定课程ID");
        }
        // 2. 查 learning_record
        LearningRecord record = learningRecordService.selectByUserIdAndCourseId(submission.getUserId(), courseId);
        if (record == null) {
            System.out.println("[DEBUG] 未找到学习记录 userId=" + submission.getUserId() + ", courseId=" + courseId);
            return error("未找到学习记录");
        }
        submission.setRecordId(record.getRecordId());
        submission.setSubmissionTime(new Date());
        System.out.println("[DEBUG] 最终写入submission: " + submission);
        return toAjax(taskSubmissionService.insertTaskSubmission(submission));
    }

    /**
     * 上传作业附件
     */
    @PostMapping("/upload")
    public AjaxResult uploadFile(@RequestParam("file") MultipartFile file,
                                 @RequestParam("courseId") Long courseId,
                                 @RequestParam("userId") Long userId,
                                 @RequestParam("role") String role) throws Exception
    {
        System.out.println("[DEBUG] /upload 参数: courseId=" + courseId + ", userId=" + userId + ", role=" + role + ", fileName=" + file.getOriginalFilename());
        if (!file.isEmpty())
        {
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            if (!MimeTypeUtils.ALLOWED_EXTENSION.contains(extension))
            {
                System.out.println("[DEBUG] /upload 返回: 文件格式不正确");
                return error("文件格式不正确，请上传PDF、DOC、DOCX、TXT格式的文件");
            }
            if (file.getSize() > 10 * 1024 * 1024)
            {
                System.out.println("[DEBUG] /upload 返回: 文件过大");
                return error("文件大小不能超过10MB");
            }
            String basePath = RuoYiConfig.getProfile() + "/homework/" + courseId + "/" + role + "/" + userId;
            String filePath = FileUploadUtils.upload(basePath, file);
            System.out.println("[DEBUG] /upload 保存路径: " + filePath);
            AjaxResult result = success(filePath);
            System.out.println("[DEBUG] /upload 返回: " + result);
            return result;
        }
        System.out.println("[DEBUG] /upload 返回: 请选择要上传的文件");
        return error("请选择要上传的文件");
    }
}
