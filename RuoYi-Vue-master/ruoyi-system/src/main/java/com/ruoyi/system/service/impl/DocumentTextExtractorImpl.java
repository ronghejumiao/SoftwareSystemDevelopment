package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.service.IDocumentTextExtractor;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 文档文字提取服务实现类
 * 
 * @author ruoyi
 */
@Service
public class DocumentTextExtractorImpl implements IDocumentTextExtractor {
    
    private static final Logger log = LoggerFactory.getLogger(DocumentTextExtractorImpl.class);
    
    private final Tika tika = new Tika();
    
    @Override
    public String extractTextFromFile(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return "";
        }
        
        try {
            // 将相对路径转换为绝对路径
            String absolutePath = getAbsolutePath(filePath);
            
            // 检查文件是否存在
            File file = new File(absolutePath);
            if (!file.exists()) {
                log.warn("文件不存在: {}", absolutePath);
                return "";
            }
            
            // 检查文件大小（限制为10MB）
            if (file.length() > 10 * 1024 * 1024) {
                log.warn("文件过大: {} ({} bytes)", absolutePath, file.length());
                return "";
            }
            
            // 使用Tika提取文字
            String text = tika.parseToString(file);
            log.info("成功提取文件文字: {}, 长度: {}", absolutePath, text.length());
            return text;
            
        } catch (IOException | TikaException e) {
            log.error("提取文件文字失败: {}", filePath, e);
            return "";
        }
    }
    
    @Override
    public String extractTextFromFiles(String[] filePaths) {
        if (filePaths == null || filePaths.length == 0) {
            return "";
        }
        
        StringBuilder result = new StringBuilder();
        for (String filePath : filePaths) {
            String text = extractTextFromFile(filePath);
            if (StringUtils.isNotEmpty(text)) {
                result.append(text).append("\n\n");
            }
        }
        
        return result.toString();
    }
    
    /**
     * 获取文件的绝对路径
     * 
     * @param filePath 文件路径
     * @return 绝对路径
     */
    private String getAbsolutePath(String filePath) {
        // 如果是/profile/开头的路径，需要替换为实际的uploadPath
        if (filePath.startsWith("/profile/")) {
            // 从配置中获取uploadPath
            String uploadPath = getUploadPath();
            return filePath.replace("/profile/", uploadPath + "/");
        }
        
        // 如果已经是绝对路径，直接返回
        if (filePath.startsWith("/") || filePath.matches("^[A-Za-z]:.*")) {
            return filePath;
        }
        
        // 如果是相对路径，添加项目根目录
        String projectRoot = System.getProperty("user.dir");
        return projectRoot + "/" + filePath;
    }
    
    /**
     * 获取上传路径
     */
    private String getUploadPath() {
        // 从配置中获取uploadPath，如果没有配置则使用默认值
        String uploadPath = System.getProperty("ruoyi.profile");
        if (uploadPath == null || uploadPath.isEmpty()) {
            // 默认路径
            uploadPath = "C:\\SoftwareSystemDevelopment\\ruoyi\\uploadPath";
        }
        return uploadPath;
    }
} 