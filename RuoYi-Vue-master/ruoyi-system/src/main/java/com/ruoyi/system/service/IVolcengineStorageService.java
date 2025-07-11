package com.ruoyi.system.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.List;

/**
 * 火山引擎存储服务接口
 * 
 * @author ruoyi
 * @date 2025-01-20
 */
public interface IVolcengineStorageService
{
    /**
     * 上传单个文件
     * 
     * @param file 文件
     * @param objectKey 对象键
     * @return 文件URL
     */
    String uploadFile(MultipartFile file, String objectKey);

    /**
     * 上传本地文件
     * 
     * @param localFile 本地文件
     * @param objectKey 对象键
     * @return 文件URL
     */
    String uploadFile(File localFile, String objectKey);

    /**
     * 批量上传文件
     * 
     * @param files 文件列表
     * @param objectKeys 对象键列表
     * @return 文件URL列表
     */
    List<String> uploadFiles(List<File> files, List<String> objectKeys);

    /**
     * 删除文件
     * 
     * @param objectKey 对象键
     * @return 是否成功
     */
    boolean deleteFile(String objectKey);

    /**
     * 批量删除文件
     * 
     * @param objectKeys 对象键列表
     * @return 成功删除的数量
     */
    int deleteFiles(List<String> objectKeys);
}