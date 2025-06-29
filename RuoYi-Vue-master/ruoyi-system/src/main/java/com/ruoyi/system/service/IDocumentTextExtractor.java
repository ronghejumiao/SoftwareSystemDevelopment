package com.ruoyi.system.service;

/**
 * 文档文字提取服务接口
 * 
 * @author ruoyi
 */
public interface IDocumentTextExtractor {
    
    /**
     * 从文件中提取文字
     * 
     * @param filePath 文件路径
     * @return 提取的文字内容
     */
    String extractTextFromFile(String filePath);
    
    /**
     * 从多个文件中提取文字
     * 
     * @param filePaths 文件路径数组
     * @return 提取的文字内容
     */
    String extractTextFromFiles(String[] filePaths);
} 