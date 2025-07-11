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
import com.ruoyi.system.domain.VideoLearningRecord;
import com.ruoyi.system.service.IVideoLearningRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 视频学习记录，记录学生观看视频的行为数据Controller
 * 
 * @author ruoyi
 * @date 2025-06-24
 */
@RestController
@RequestMapping("/system/videoLearningRecord")
public class VideoLearningRecordController extends BaseController
{
    @Autowired
    private IVideoLearningRecordService videoLearningRecordService;

    /**
     * 查询视频学习记录，记录学生观看视频的行为数据列表
     */
    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('teacher') or @ss.hasRole('student')")
    @GetMapping("/list")
    public TableDataInfo list(VideoLearningRecord videoLearningRecord)
    {
        startPage();
        // 判断当前用户角色，如果是学生角色，则只能查看自己的记录
        if (SecurityUtils.isStudent()) {
            // 设置查询条件为当前登录用户ID
            videoLearningRecord.setUserId(SecurityUtils.getUserId());
            
            // 学生只能通过视频资源和完成状态进行搜索，清除其他搜索条件
            // 保留resourceName和completionStatus相关字段，其他查询条件清空
            // 不清空resourceName，允许学生通过视频资源名称搜索
            
            // 其他条件清空
            videoLearningRecord.setStudentName(null);
            videoLearningRecord.setLastWatchTime(null);
        }
        List<VideoLearningRecord> list = videoLearningRecordService.selectVideoLearningRecordList(videoLearningRecord);
        return getDataTable(list);
    }

    /**
     * 导出视频学习记录，记录学生观看视频的行为数据列表
     */
    @PreAuthorize("@ss.hasPermi('system:videoLearningRecord:export')")
    @Log(title = "视频学习记录，记录学生观看视频的行为数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, VideoLearningRecord videoLearningRecord)
    {
        // 判断当前用户角色，如果是学生角色，则只能导出自己的记录
        if (SecurityUtils.isStudent()) {
            // 设置查询条件为当前登录用户ID
            videoLearningRecord.setUserId(SecurityUtils.getUserId());
        }
        List<VideoLearningRecord> list = videoLearningRecordService.selectVideoLearningRecordList(videoLearningRecord);
        ExcelUtil<VideoLearningRecord> util = new ExcelUtil<VideoLearningRecord>(VideoLearningRecord.class);
        util.exportExcel(response, list, "视频学习记录，记录学生观看视频的行为数据数据");
    }

    /**
     * 获取视频学习记录，记录学生观看视频的行为数据详细信息
     */
    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('teacher') or @ss.hasRole('student')")
    @GetMapping(value = "/{recordId}")
    public AjaxResult getInfo(@PathVariable("recordId") Long recordId)
    {
        VideoLearningRecord record = videoLearningRecordService.selectVideoLearningRecordByRecordId(recordId);
        
        // 判断当前用户角色，如果是学生角色，则只能查看自己的记录
        if (SecurityUtils.isStudent() && record.getUserId() != null && !SecurityUtils.getUserId().equals(record.getUserId())) {
            return AjaxResult.error("没有权限查看其他学生的学习记录");
        }
        
        return success(record);
    }

    /**
     * 新增视频学习记录，记录学生观看视频的行为数据
     */
    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('teacher') or @ss.hasRole('student')")
    @Log(title = "视频学习记录，记录学生观看视频的行为数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody VideoLearningRecord videoLearningRecord)
    {
        // 学生只能添加自己的记录
        if (SecurityUtils.isStudent()) {
            videoLearningRecord.setUserId(SecurityUtils.getUserId());
        }
        videoLearningRecordService.saveOrUpdate(videoLearningRecord);
        return success(videoLearningRecord);
    }

    /**
     * 修改视频学习记录，记录学生观看视频的行为数据
     */
    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('teacher') or @ss.hasRole('student')")
    @Log(title = "视频学习记录，记录学生观看视频的行为数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody VideoLearningRecord videoLearningRecord)
    {
        // 判断当前用户角色，如果是学生角色，则只能修改自己的记录
        if (SecurityUtils.isStudent()) {
            VideoLearningRecord original = videoLearningRecordService.selectVideoLearningRecordByRecordId(videoLearningRecord.getRecordId());
            if (original != null && original.getUserId() != null && !SecurityUtils.getUserId().equals(original.getUserId())) {
                return AjaxResult.error("没有权限修改其他学生的学习记录");
            }
        }
        
        return toAjax(videoLearningRecordService.updateVideoLearningRecord(videoLearningRecord));
    }

    /**
     * 删除视频学习记录，记录学生观看视频的行为数据
     */
    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('teacher') or @ss.hasRole('student')")
    @Log(title = "视频学习记录，记录学生观看视频的行为数据", businessType = BusinessType.DELETE)
	@DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds)
    {
        // 判断当前用户角色，如果是学生角色，则只能删除自己的记录
        if (SecurityUtils.isStudent()) {
            for (Long recordId : recordIds) {
                VideoLearningRecord record = videoLearningRecordService.selectVideoLearningRecordByRecordId(recordId);
                if (record != null && record.getUserId() != null && !SecurityUtils.getUserId().equals(record.getUserId())) {
                    return AjaxResult.error("没有权限删除其他学生的学习记录");
                }
            }
        }
        
        return toAjax(videoLearningRecordService.deleteVideoLearningRecordByRecordIds(recordIds));
    }
}
