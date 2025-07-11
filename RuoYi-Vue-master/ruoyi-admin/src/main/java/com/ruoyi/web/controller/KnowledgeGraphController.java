package com.ruoyi.web.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/system/kg")
public class KnowledgeGraphController {

    @Value("${kg.api.url:http://127.0.0.1:5005}")
    private String kgApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // 1. 多文件上传/路径触发知识图谱抽取（仅后端自动用，不建议前端直接用）
    @PostMapping("/extract")
    public ResponseEntity<?> extractKg(@RequestBody Map<String, Object> body) throws IOException {
        String courseId = String.valueOf(body.get("courseId"));
        String url = kgApiUrl + "/api/kg/extract";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        if (body.containsKey("files")) {
            // 文件上传
            MultipartFile[] files = (MultipartFile[]) body.get("files");
            MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
            form.add("courseId", courseId);
            for (MultipartFile file : files) {
                form.add("files", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
            }
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(form, headers);
            return restTemplate.postForEntity(url, requestEntity, String.class);
        } else if (body.containsKey("pdf_paths")) {
            // 路径传参
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, Object> json = new HashMap<>();
            json.put("courseId", courseId);
            String profile = "C:/SoftwareSystemDevelopment/ruoyi/uploadPath";
            List<String> pdfPaths = ((List<?>) body.get("pdf_paths")).stream()
                .map(Object::toString)
                .map(path -> {
                    String fixedPath = path; // 直接用前端传来的
                    return fixedPath;
                })
                .collect(Collectors.toList());
            json.put("pdf_paths", pdfPaths);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(json, headers);
            return restTemplate.postForEntity(url, requestEntity, String.class);
        } else {
            return ResponseEntity.badRequest().body("请上传文件或提供pdf_paths参数");
        }
    }

    // 2. 查询知识图谱（推荐前端只用此接口，POST，参数放body）
    @PostMapping("/graph")
    public ResponseEntity<?> getKgGraph(@RequestBody Map<String, Object> body) {
        // body 里 pdfNames 应该是 List<String>，每个元素如 "/2025/06/30/xxx.pdf"
        String url = kgApiUrl + "/api/kg/graph";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        // 不做任何路径处理，直接转发
        return restTemplate.postForEntity(url, requestEntity, String.class);
    }

    // 辅助类：用于文件流上传
    public static class MultipartInputStreamFileResource extends InputStreamResource {
        private final String filename;
        public MultipartInputStreamFileResource(InputStream inputStream, String filename) {
            super(inputStream);
            this.filename = filename;
        }
        @Override
        public String getFilename() {
            return this.filename;
        }
        @Override
        public long contentLength() throws IOException {
            return -1;
        }
    }
} 