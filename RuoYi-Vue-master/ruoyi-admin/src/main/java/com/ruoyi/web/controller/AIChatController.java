package com.ruoyi.web.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.IAIModelService;
import com.ruoyi.system.domain.StudentSkill;
import com.ruoyi.system.service.IStudentSkillService;
import com.ruoyi.system.service.IVolcengineStorageService;
import com.ruoyi.system.service.IDocumentTextExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/ai")
public class AIChatController {
    @Autowired
    private IAIModelService aiModelService;
    @Autowired
    private IStudentSkillService studentSkillService;
    @Autowired
    private IVolcengineStorageService volcengineStorageService;
    @Autowired
    private IDocumentTextExtractor documentTextExtractor;

    /**
     * AI对话接口，后端自动获取能力值、拼接prompt
     * 前端传递 studentId, courseId, userInput, history（可选）
     */
    @PostMapping("/chat")
    public AjaxResult chat(@RequestBody Map<String, Object> body) {
        Long studentId = null, courseId = null;
        String userInput = null;
        List<Map<String, String>> history = null;
        try {
            studentId = body.get("studentId") != null ? Long.valueOf(body.get("studentId").toString()) : null;
            courseId = body.get("courseId") != null && !"".equals(body.get("courseId").toString()) ? Long.valueOf(body.get("courseId").toString()) : null;
            userInput = (String) body.get("userInput");
            history = (List<Map<String, String>>) body.get("history");
        } catch (Exception ignore) {}
        System.out.println("[AIChatController] 收到参数: studentId=" + studentId + ", courseId=" + courseId + ", userInput=" + userInput + ", history=" + (history != null ? history.toString() : "null"));
        if (studentId == null || userInput == null || userInput.trim().isEmpty()) {
            return AjaxResult.error("studentId、userInput不能为空");
        }
        try {
            List<StudentSkill> studentSkill;
            if (courseId != null) {
                studentSkill = studentSkillService.selectStudentSkillByStudentAndCourse(studentId, courseId);
            } else {
                studentSkill = studentSkillService.selectStudentSkillByStudent(studentId);
            }
            String prompt = com.ruoyi.system.service.impl.AIModelServiceImpl.buildChatPrompt(studentSkill, history, userInput);
            System.out.println("[AIChatController] 拼接prompt:\n" + prompt);
            String aiResult = aiModelService.callAIModelForText(prompt);
            System.out.println("[AIChatController] AI原始响应: " + aiResult);
            String reply = aiResult;
            String thinking = null;
            if (aiResult != null && aiResult.trim().startsWith("{")) {
                try {
                    com.fasterxml.jackson.databind.JsonNode node = new com.fasterxml.jackson.databind.ObjectMapper().readTree(aiResult);
                    if (node.has("reply")) reply = node.get("reply").asText();
                    if (node.has("thinking")) thinking = node.get("thinking").asText();
                } catch (Exception ignore) {}
            }
            Map<String, Object> resp = new HashMap<>();
            resp.put("reply", reply);
            if (thinking != null) resp.put("thinking", thinking);
            System.out.println("[AIChatController] 最终返回: " + resp);
            return AjaxResult.success(resp);
        } catch (Exception e) {
            System.out.println("[AIChatController] AI调用异常: " + e.getMessage());
            return AjaxResult.error("AI调用失败: " + e.getMessage());
        }
    }

    /**
     * AI图片理解接口，支持多图（最多10张）
     */
    @PostMapping("/image-understand")
    public AjaxResult imageUnderstand(@RequestParam("images") MultipartFile[] images,
                                      @RequestParam(value = "prompt", required = false) String prompt) {
        if (images == null || images.length == 0) {
            return AjaxResult.error("请上传图片");
        }
        if (images.length > 10) {
            return AjaxResult.error("最多支持10张图片");
        }
        // 1. 上传图片到TOS
        try {
            java.util.List<File> tempFiles = new java.util.ArrayList<>();
            java.util.List<String> objectKeys = new java.util.ArrayList<>();
            for (MultipartFile img : images) {
                String ext = org.springframework.util.StringUtils.getFilenameExtension(img.getOriginalFilename());
                String objectKey = "ai/tmp/" + UUID.randomUUID() + (ext != null ? ("." + ext) : "");
                File temp = File.createTempFile("aiimg_", ext != null ? ("." + ext) : null);
                img.transferTo(temp);
                tempFiles.add(temp);
                objectKeys.add(objectKey);
            }
            java.util.List<String> imageUrls = volcengineStorageService.uploadFiles(tempFiles, objectKeys);
            // 2. 调用视觉理解模型
            String model = "doubao-1-5-vision-pro-32k-250115";
            String aiPrompt = (prompt != null ? prompt : "请对这些图片进行分析和总结。");
            String aiResult = aiModelService.analyzeImagesWithDoubao(imageUrls, aiPrompt, model);
            // 3. 删除TOS临时文件
            volcengineStorageService.deleteFiles(objectKeys);
            // 4. 删除本地临时文件
            for (File f : tempFiles) try { f.delete(); } catch (Exception ignore) {}
            return AjaxResult.success(aiResult);
        } catch (Exception e) {
            return AjaxResult.error("图片理解失败: " + e.getMessage());
        }
    }

    /**
     * AI文件理解接口，支持单文件
     */
    @PostMapping("/file-understand")
    public AjaxResult fileUnderstand(@RequestParam("file") MultipartFile file,
                                     @RequestParam(value = "prompt", required = false) String prompt) {
        if (file == null || file.isEmpty()) {
            return AjaxResult.error("请上传文件");
        }
        File temp = null;
        try {
            String ext = org.springframework.util.StringUtils.getFilenameExtension(file.getOriginalFilename());
            temp = File.createTempFile("aifile_", ext != null ? ("." + ext) : null);
            file.transferTo(temp);
            // 1. 用Tika提取文本
            String text = documentTextExtractor.extractTextFromFile(temp.getAbsolutePath());
            if (!StringUtils.hasText(text)) {
                return AjaxResult.error("文件内容为空或无法解析");
            }
            // 2. 拼接prompt
            String aiPrompt = (prompt != null ? prompt + "\n" : "") + "请对以下文件内容进行分析和总结：\n" + text;
            String aiResult = aiModelService.callAIModelForText(aiPrompt);
            return AjaxResult.success(aiResult);
        } catch (Exception e) {
            return AjaxResult.error("文件理解失败: " + e.getMessage());
        } finally {
            if (temp != null) try { temp.delete(); } catch (Exception ignore) {}
        }
    }

    /**
     * AI混合理解接口，支持图片和文件同时上传，统一用视觉大模型理解
     */
    @PostMapping("/mixed-understand")
    public AjaxResult mixedUnderstand(
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            @RequestParam(value = "prompt", required = false) String prompt
    ) {
        // 1. 处理图片
        java.util.List<File> tempImageFiles = new java.util.ArrayList<>();
        java.util.List<String> imageObjectKeys = new java.util.ArrayList<>();
        java.util.List<String> imageUrls = new java.util.ArrayList<>();
        try {
            if (images != null && images.length > 0) {
                if (images.length > 10) return AjaxResult.error("最多支持10张图片");
                for (MultipartFile img : images) {
                    String ext = org.springframework.util.StringUtils.getFilenameExtension(img.getOriginalFilename());
                    String objectKey = "ai/tmp/" + UUID.randomUUID() + (ext != null ? ("." + ext) : "");
                    File temp = File.createTempFile("aiimg_", ext != null ? ("." + ext) : null);
                    img.transferTo(temp);
                    tempImageFiles.add(temp);
                    imageObjectKeys.add(objectKey);
                }
                imageUrls = volcengineStorageService.uploadFiles(tempImageFiles, imageObjectKeys);
            }
            // 2. 处理文件
            StringBuilder fileTextBuilder = new StringBuilder();
            java.util.List<File> tempFileFiles = new java.util.ArrayList<>();
            if (files != null && files.length > 0) {
                for (MultipartFile file : files) {
                    String ext = org.springframework.util.StringUtils.getFilenameExtension(file.getOriginalFilename());
                    File temp = File.createTempFile("aifile_", ext != null ? ("." + ext) : null);
                    file.transferTo(temp);
                    tempFileFiles.add(temp);
                    String text = documentTextExtractor.extractTextFromFile(temp.getAbsolutePath());
                    if (org.springframework.util.StringUtils.hasText(text)) {
                        // 分段取文字：共取3000字，分15段，每段连续取200字
                        int total = Math.min(3000, text.length());
                        int segLen = 200;
                        int segNum = 15;
                        StringBuilder segBuilder = new StringBuilder();
                        for (int i = 0; i < segNum; i++) {
                            int start = i * segLen;
                            if (start >= total) break;
                            int end = Math.min(start + segLen, total);
                            segBuilder.append(text, start, end).append("\n");
                        }
                        fileTextBuilder.append("文件《").append(file.getOriginalFilename()).append("》内容分段：\n").append(segBuilder).append("\n");
                        System.out.println("[AIChatController] 文件 " + file.getOriginalFilename() + " 分段内容长度: " + segBuilder.length());
                    }
                }
            }
            // 3. 构造multiContent，统一用视觉大模型
            java.util.List<com.volcengine.ark.runtime.model.completion.chat.ChatCompletionContentPart> multiParts = new java.util.ArrayList<>();
            if (imageUrls != null && !imageUrls.isEmpty()) {
                for (String imageUrl : imageUrls) {
                    multiParts.add(
                        com.volcengine.ark.runtime.model.completion.chat.ChatCompletionContentPart.builder()
                            .type("image_url")
                            .imageUrl(new com.volcengine.ark.runtime.model.completion.chat.ChatCompletionContentPart.ChatCompletionContentPartImageURL(imageUrl))
                            .build()
                    );
                }
            }
            if (fileTextBuilder.length() > 0) {
                multiParts.add(
                    com.volcengine.ark.runtime.model.completion.chat.ChatCompletionContentPart.builder()
                        .type("text")
                        .text(fileTextBuilder.toString())
                        .build()
                );
            }
            // 4. 拼接总prompt
            String finalPrompt = (prompt != null ? prompt + "\n" : "") + "请对这些图片和文件内容进行综合分析和总结。";
            multiParts.add(
                com.volcengine.ark.runtime.model.completion.chat.ChatCompletionContentPart.builder()
                    .type("text")
                    .text(finalPrompt)
                    .build()
            );
            // 5. 调用视觉理解模型
            String model = "doubao-1-5-vision-pro-32k-250115";
            // 构造消息
            java.util.List<com.volcengine.ark.runtime.model.completion.chat.ChatMessage> messages = new java.util.ArrayList<>();
            com.volcengine.ark.runtime.model.completion.chat.ChatMessage userMessage =
                com.volcengine.ark.runtime.model.completion.chat.ChatMessage.builder()
                    .role(com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole.USER)
                    .multiContent(multiParts)
                    .build();
            messages.add(userMessage);
            com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest chatCompletionRequest =
                com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest.builder()
                    .model(model)
                    .messages(messages)
                    .build();
            // ArkService初始化
            String apiKeyEnv = System.getenv("ARK_API_KEY");
            String apiKeyToUse = org.apache.commons.lang3.StringUtils.isNotEmpty(apiKeyEnv) ? apiKeyEnv : ((com.ruoyi.system.service.impl.AIModelServiceImpl)aiModelService).getApiKey();
            String baseUrl = "https://ark.cn-beijing.volces.com/api/v3";
            System.out.println("[AIChatController] 调用视觉模型，图片数:" + (imageUrls != null ? imageUrls.size() : 0) + ", 文本长度:" + fileTextBuilder.length());
            com.volcengine.ark.runtime.service.ArkService service = com.volcengine.ark.runtime.service.ArkService.builder()
                    .baseUrl(baseUrl)
                    .apiKey(apiKeyToUse)
                    .build();
            StringBuilder result = new StringBuilder();
            try {
                service.createChatCompletion(chatCompletionRequest).getChoices().forEach(choice -> {
                    result.append(choice.getMessage().getContent()).append("\n");
                });
                System.out.println("[AIChatController] 视觉模型返回: " + result.toString());
            } catch (Exception e) {
                System.out.println("[AIChatController] 视觉模型调用异常: " + e.getMessage());
                throw e;
            } finally {
                service.shutdownExecutor();
            }
            // 6. 删除TOS临时图片
            if (imageObjectKeys != null && !imageObjectKeys.isEmpty()) {
                volcengineStorageService.deleteFiles(imageObjectKeys);
            }
            // 7. 删除本地临时文件
            for (File f : tempImageFiles) try { f.delete(); } catch (Exception ignore) {}
            for (File f : tempFileFiles) try { f.delete(); } catch (Exception ignore) {}
            System.out.println("[AIChatController] AjaxResult.success: " + result.toString());
            return AjaxResult.success(result.toString());
        } catch (Exception e) {
            System.out.println("[AIChatController] 混合理解异常: " + e.getMessage());
            return AjaxResult.error("混合理解失败: " + e.getMessage());
        }
    }
} 