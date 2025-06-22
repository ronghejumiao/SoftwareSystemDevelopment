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
import com.ruoyi.system.domain.VideoLearningRecord;
import com.ruoyi.system.service.IVideoLearningRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 视频学习记录，记录学生观看视频的行为数据Controller
 * 
 * @author ruoyi
 * @date 2025-06-20
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
    @PreAuthorize("@ss.hasPermi('system:videoLearningRecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(VideoLearningRecord videoLearningRecord)
    {
        startPage();
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
        List<VideoLearningRecord> list = videoLearningRecordService.selectVideoLearningRecordList(videoLearningRecord);
        ExcelUtil<VideoLearningRecord> util = new ExcelUtil<VideoLearningRecord>(VideoLearningRecord.class);
        util.exportExcel(response, list, "视频学习记录，记录学生观看视频的行为数据数据");
    }

    /**
     * 获取视频学习记录，记录学生观看视频的行为数据详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:videoLearningRecord:query')")
    @GetMapping(value = "/{recordId}")
    public AjaxResult getInfo(@PathVariable("recordId") Long recordId)
    {
        return success(videoLearningRecordService.selectVideoLearningRecordByRecordId(recordId));
    }

    /**
     * 新增视频学习记录，记录学生观看视频的行为数据
     */
    @PreAuthorize("@ss.hasPermi('system:videoLearningRecord:add')")
    @Log(title = "视频学习记录，记录学生观看视频的行为数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody VideoLearningRecord videoLearningRecord)
    {
        return toAjax(videoLearningRecordService.insertVideoLearningRecord(videoLearningRecord));
    }

    /**
     * 修改视频学习记录，记录学生观看视频的行为数据
     */
    @PreAuthorize("@ss.hasPermi('system:videoLearningRecord:edit')")
    @Log(title = "视频学习记录，记录学生观看视频的行为数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody VideoLearningRecord videoLearningRecord)
    {
        return toAjax(videoLearningRecordService.updateVideoLearningRecord(videoLearningRecord));
    }

    /**
     * 删除视频学习记录，记录学生观看视频的行为数据
     */
    @PreAuthorize("@ss.hasPermi('system:videoLearningRecord:remove')")
    @Log(title = "视频学习记录，记录学生观看视频的行为数据", businessType = BusinessType.DELETE)
	@DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds)
    {
        return toAjax(videoLearningRecordService.deleteVideoLearningRecordByRecordIds(recordIds));
    }
}
