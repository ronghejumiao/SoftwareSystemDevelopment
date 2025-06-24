package com.ruoyi.web.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.VideoResource;
import com.ruoyi.system.service.IVideoResourceService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.utils.VideoUtils;

/**
 * 视频学习资源Controller
 * 
 * @author ruoyi
 * @date 2025-06-23
 */
@RestController
@RequestMapping("/system/videoresource")
public class VideoResourceController extends BaseController
{
    @Autowired
    private IVideoResourceService videoResourceService;

    /**
     * 查询视频学习资源列表
     */
    @PreAuthorize("@ss.hasPermi('system:videoresource:list')")
    @GetMapping("/list")
    public TableDataInfo list(VideoResource videoResource)
    {
        startPage();
        List<VideoResource> list = videoResourceService.selectVideoResourceList(videoResource);
        return getDataTable(list);
    }

    /**
     * 导出视频学习资源列表
     */
    @PreAuthorize("@ss.hasPermi('system:videoresource:export')")
    @Log(title = "视频学习资源", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, VideoResource videoResource)
    {
        List<VideoResource> list = videoResourceService.selectVideoResourceList(videoResource);
        ExcelUtil<VideoResource> util = new ExcelUtil<VideoResource>(VideoResource.class);
        util.exportExcel(response, list, "视频学习资源数据");
    }

    /**
     * 获取视频学习资源详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:videoresource:query')")
    @GetMapping(value = "/{videoId}")
    public AjaxResult getInfo(@PathVariable("videoId") Long videoId)
    {
        return success(videoResourceService.selectVideoResourceByVideoId(videoId));
    }

        /**
         * 上传视频
         */
        @PreAuthorize("@ss.hasPermi('system:videoresource:add')")
        @Log(title = "视频学习资源", businessType = BusinessType.INSERT)
        @PostMapping("/uploadVideo")
        @ResponseBody
        public AjaxResult uploadVideo(@RequestParam("file") MultipartFile file) throws IOException
        {
            if (file.isEmpty())
            {
                return error("请选择要上传的视频文件");
            }

            String originalFilename = file.getOriginalFilename();
            String extension = StringUtils.getFilenameExtension(originalFilename);

            // 视频文件类型验证
            if (!"mp4".equalsIgnoreCase(extension))
            {
                return error("不支持的视频格式，仅支持mp4格式");
            }

            try
            {
            // 上传视频文件
            String videoPath = FileUploadUtils.upload(RuoYiConfig.getProfile() + "/video", file);
            
            // 确保路径以/profile开头
            if (!videoPath.startsWith("/profile")) {
                videoPath = "/profile" + videoPath;
            }
            
            // 打印上传后的文件信息
            String basePath = RuoYiConfig.getProfile();
            String relativePath = videoPath.startsWith("/profile") ? 
                videoPath.substring("/profile".length()) : videoPath;
            File uploadedFile = new File(basePath + relativePath);
            
            System.out.println("基础路径: " + basePath);
            System.out.println("相对路径: " + relativePath);
            System.out.println("完整路径: " + uploadedFile.getAbsolutePath());
            System.out.println("上传的视频文件是否存在: " + uploadedFile.exists());
            System.out.println("上传的视频文件大小: " + uploadedFile.length());
            
                long fileSize = (long) Math.ceil(file.getSize() / (1024.0 * 1024.0));

            // 生成首帧图片
            String coverImage = VideoUtils.generateFirstFrame(videoPath);
            // 确保封面图片路径也以/profile开头
            if (!coverImage.startsWith("/profile")) {
                coverImage = "/profile" + coverImage;
            }

                // 返回结果
                AjaxResult ajax = AjaxResult.success();
            ajax.put("videoUrl", videoPath);
                ajax.put("fileName", originalFilename);
                ajax.put("fileSize", fileSize);
            ajax.put("duration", 0L);
                ajax.put("uploadTime", DateUtils.getTime());
            ajax.put("coverImage", coverImage);
                return ajax;
            }
            catch (Exception e)
            {
            e.printStackTrace();
            return AjaxResult.error("视频处理失败: " + e.getMessage());
            }
        }

    /**
     * 上传视频封面
     */
    @PreAuthorize("@ss.hasPermi('system:videoresource:add')")
    @Log(title = "视频学习资源", businessType = BusinessType.INSERT)
    @PostMapping("/uploadThumbnail")
    @ResponseBody
    public AjaxResult uploadThumbnail(@RequestParam("file") MultipartFile file) throws IOException
    {
        if (file.isEmpty())
        {
            return error("请选择要上传的封面图片");
        }

        try
        {
            // 上传文件
            String filePath = RuoYiConfig.getUploadPath();
            String thumbnailUrl = FileUploadUtils.upload(filePath, file, MimeTypeUtils.IMAGE_EXTENSION);
            
            // 确保路径以/profile开头
            if (!thumbnailUrl.startsWith("/profile")) {
                thumbnailUrl = "/profile" + thumbnailUrl;
            }
            
            AjaxResult ajax = AjaxResult.success();
            ajax.put("thumbnailUrl", thumbnailUrl);
            return ajax;
        }
        catch (Exception e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }

        /**
         * 新增视频学习资源
         */
        @PreAuthorize("@ss.hasPermi('system:videoresource:add')")
        @Log(title = "视频学习资源", businessType = BusinessType.INSERT)
        @PostMapping
        public AjaxResult add(@RequestBody VideoResource videoResource)
        {
            // 如果没有上传封面,则使用默认封面
            if (videoResource.getThumbnail() == null || videoResource.getThumbnail().isEmpty())
            {
                videoResource.setThumbnail("/profile/default/video-thumbnail.png");
            }
            return toAjax(videoResourceService.insertVideoResource(videoResource));
        }

    /**
     * 修改视频学习资源
     */
    @PreAuthorize("@ss.hasPermi('system:videoresource:edit')")
    @Log(title = "视频学习资源", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody VideoResource videoResource)
    {
        return toAjax(videoResourceService.updateVideoResource(videoResource));
    }

    /**
     * 删除视频学习资源
     */
    @PreAuthorize("@ss.hasPermi('system:videoresource:remove')")
    @Log(title = "视频学习资源", businessType = BusinessType.DELETE)
	@DeleteMapping("/{videoIds}")
    public AjaxResult remove(@PathVariable Long[] videoIds)
    {
        return toAjax(videoResourceService.deleteVideoResourceByVideoIds(videoIds));
    }
}
