package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.service.IVolcengineStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.volcengine.tos.TOSV2;
import com.volcengine.tos.TOSV2ClientBuilder;
import com.volcengine.tos.credential.StaticCredentialsProvider;
import com.volcengine.tos.model.object.PutObjectInput;
import com.volcengine.tos.model.object.PutObjectOutput;
import com.volcengine.tos.model.object.DeleteObjectInput;
import com.volcengine.tos.model.object.DeleteObjectOutput;
import java.io.FileInputStream;

/**
 * 火山引擎存储服务实现类
 * 
 * @author ruoyi
 * @date 2025-01-20
 */
@Service
public class VolcengineStorageServiceImpl implements IVolcengineStorageService 
{
    private static final Logger log = LoggerFactory.getLogger(VolcengineStorageServiceImpl.class);

    @Value("${volcengine.tos.endpoint}")
    private String endpoint;

    @Value("${volcengine.tos.region}")
    private String region;

    @Value("${volcengine.tos.bucket}")
    private String bucket;

    @Value("${volcengine.tos.access-key}")
    private String accessKey;

    @Value("${volcengine.tos.secret-key}")
    private String secretKey;

    private TOSV2 tosClient;

    // 构造方法初始化TOSClient
    public VolcengineStorageServiceImpl(
            @Value("${volcengine.tos.endpoint}") String endpoint,
            @Value("${volcengine.tos.region}") String region,
            @Value("${volcengine.tos.bucket}") String bucket,
            @Value("${volcengine.tos.access-key}") String accessKey,
            @Value("${volcengine.tos.secret-key}") String secretKey) {
        this.endpoint = endpoint;
        this.region = region;
        this.bucket = bucket;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.tosClient = new TOSV2ClientBuilder().build(region, endpoint, accessKey, secretKey);
    }

    /**
     * 上传单个文件
     * 
     * @param file 文件
     * @param objectKey 对象键
     * @return 文件URL
     */
    @Override
    public String uploadFile(MultipartFile file, String objectKey)
    {
        try {
            // 创建临时文件
            File tempFile = File.createTempFile("upload_", "_" + file.getOriginalFilename());
            file.transferTo(tempFile);
            
            String url = uploadFile(tempFile, objectKey);
            
            // 清理临时文件
            tempFile.delete();
            
            return url;
        } catch (IOException e) {
            log.error("上传文件失败: {}", e.getMessage(), e);
            throw new RuntimeException("上传文件失败", e);
        }
    }

    /**
     * 上传本地文件
     * 
     * @param localFile 本地文件
     * @param objectKey 对象键
     * @return 文件URL
     */
    @Override
    public String uploadFile(File localFile, String objectKey)
    {
        try (FileInputStream fis = new FileInputStream(localFile)) {
            PutObjectInput input = new PutObjectInput()
                .setBucket(bucket)
                .setKey(objectKey)
                .setContent(fis);
            PutObjectOutput output = tosClient.putObject(input);
            String fileUrl = generateFileUrl(objectKey);
            log.info("文件上传成功: {} -> {}", localFile.getName(), fileUrl);
            return fileUrl;
        } catch (Exception e) {
            log.error("上传文件失败: {}", e.getMessage(), e);
            throw new RuntimeException("上传文件失败", e);
        }
    }

    /**
     * 批量上传文件
     * 
     * @param files 文件列表
     * @param objectKeys 对象键列表
     * @return 文件URL列表
     */
    @Override
    public List<String> uploadFiles(List<File> files, List<String> objectKeys)
    {
        if (files.size() != objectKeys.size()) {
            throw new IllegalArgumentException("文件列表和对象键列表长度不匹配");
        }

        List<String> urls = new ArrayList<>();
        
        for (int i = 0; i < files.size(); i++) {
            try {
                String url = uploadFile(files.get(i), objectKeys.get(i));
                urls.add(url);
            } catch (Exception e) {
                log.error("批量上传文件失败，文件: {}, 错误: {}", files.get(i).getName(), e.getMessage());
                // 继续处理其他文件
            }
        }

        log.info("批量上传完成，成功上传 {} 个文件", urls.size());
        return urls;
    }

    /**
     * 删除文件
     * 
     * @param objectKey 对象键
     * @return 是否成功
     */
    @Override
    public boolean deleteFile(String objectKey)
    {
        try {
            log.info("开始删除文件: {}", objectKey);
            DeleteObjectInput input = DeleteObjectInput.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .build();
            DeleteObjectOutput output = tosClient.deleteObject(input);
            log.info("文件删除成功: {}", objectKey);
            return true;
        } catch (Exception e) {
            log.error("删除文件失败: {}, 错误: {}", objectKey, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 批量删除文件
     * 
     * @param objectKeys 对象键列表
     * @return 成功删除的数量
     */
    @Override
    public int deleteFiles(List<String> objectKeys)
    {
        int successCount = 0;
        
        for (String objectKey : objectKeys) {
            if (deleteFile(objectKey)) {
                successCount++;
            }
        }
        
        log.info("批量删除完成，成功删除 {} 个文件", successCount);
        return successCount;
    }

    /**
     * 生成文件URL
     * 
     * @param objectKey 对象键
     * @return 文件URL
     */
    private String generateFileUrl(String objectKey)
    {
        if (StringUtils.isEmpty(objectKey)) {
            return "";
        }
        // 修正：只保留 endpoint 的主域名部分
        String endpointHost = endpoint.replaceFirst("https?://", "");
        int slashIdx = endpointHost.indexOf('/');
        if (slashIdx > 0) {
            endpointHost = endpointHost.substring(0, slashIdx);
        }
        return String.format("https://%s.%s/%s", bucket, endpointHost, objectKey);
    }
}