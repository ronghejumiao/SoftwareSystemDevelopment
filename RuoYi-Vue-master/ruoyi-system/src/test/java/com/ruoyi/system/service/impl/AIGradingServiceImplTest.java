package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.Course;
import com.ruoyi.system.domain.CourseHomework;
import com.ruoyi.system.domain.TaskSubmission;
import com.ruoyi.system.mapper.CourseHomeworkMapper;
import com.ruoyi.system.mapper.CourseMapper;
import com.ruoyi.system.mapper.TaskSubmissionMapper;
import com.ruoyi.system.service.IAIGradingService;
import com.ruoyi.system.service.IDocumentTextExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIGradingServiceImplTest {
    @Mock
    private CourseMapper courseMapper;
    @Mock
    private CourseHomeworkMapper courseHomeworkMapper;
    @Mock
    private TaskSubmissionMapper taskSubmissionMapper;
    @Mock
    private IDocumentTextExtractor documentTextExtractor;

    @InjectMocks
    private AIGradingServiceImpl aiGradingService;

    @BeforeEach
    void setUp() {
        // 设置AI密钥等配置
        ReflectionTestUtils.setField(aiGradingService, "arkApiKey", "test-key");
        ReflectionTestUtils.setField(aiGradingService, "arkModelId", "test-model");
        ReflectionTestUtils.setField(aiGradingService, "arkBaseUrl", "http://test-url");
    }

    @Test
    void testGradeHomework_CourseNotFound() {
        when(courseMapper.selectCourseByCourseId(anyLong())).thenReturn(null);
        IAIGradingService.AIGradingResult result = aiGradingService.gradeHomework(1L, 1L, 1L);
        assertFalse(result.isSuccess());
        assertEquals("课程不存在", result.getError());
    }

    @Test
    void testGradeHomework_SubmissionNotFound() {
        when(courseMapper.selectCourseByCourseId(anyLong())).thenReturn(new Course());
        when(taskSubmissionMapper.selectTaskSubmissionBySubmissionId(anyLong())).thenReturn(null);
        IAIGradingService.AIGradingResult result = aiGradingService.gradeHomework(1L, 1L, 1L);
        assertFalse(result.isSuccess());
        assertEquals("提交记录不存在", result.getError());
    }

    @Test
    void testGradeHomework_HomeworkNotFound() {
        Course course = new Course();
        TaskSubmission submission = new TaskSubmission();
        submission.setTaskId(null);
        when(courseMapper.selectCourseByCourseId(anyLong())).thenReturn(course);
        when(taskSubmissionMapper.selectTaskSubmissionBySubmissionId(anyLong())).thenReturn(submission);
        when(courseHomeworkMapper.selectCourseHomeworkByHomeworkId(anyLong())).thenReturn(null);
        IAIGradingService.AIGradingResult result = aiGradingService.gradeHomework(1L, 1L, 1L);
        assertFalse(result.isSuccess());
        assertEquals("作业不存在", result.getError());
    }

    @Test
    void testGradeHomework_HomeworkByTaskIdDefault() {
        Course course = new Course();
        course.setCourseName("测试课程");
        TaskSubmission submission = new TaskSubmission();
        submission.setTaskId(123L);
        submission.setUserId(456L);
        submission.setSubmissionContent("学生答案");
        when(courseMapper.selectCourseByCourseId(anyLong())).thenReturn(course);
        when(taskSubmissionMapper.selectTaskSubmissionBySubmissionId(anyLong())).thenReturn(submission);
        when(courseHomeworkMapper.selectCourseHomeworkByHomeworkId(anyLong())).thenReturn(null);
        when(documentTextExtractor.extractTextFromFiles(any())).thenReturn("");
        // mock AI调用
        ReflectionTestUtils.setField(aiGradingService, "arkApiKey", ""); // 让AI密钥为空，走密钥异常分支
        IAIGradingService.AIGradingResult result = aiGradingService.gradeHomework(1L, 1L, 1L);
        assertFalse(result.isSuccess());
        assertEquals("未配置AI模型API密钥", result.getError());
    }

    @Test
    void testGradeHomework_NormalFlow_JsonAIResponse() {
        Course course = new Course();
        course.setCourseName("测试课程");
        TaskSubmission submission = new TaskSubmission();
        submission.setTaskId(123L);
        submission.setUserId(456L);
        submission.setSubmissionContent("学生答案");
        CourseHomework homework = new CourseHomework();
        homework.setHomeworkName("作业1");
        homework.setHomeworkDesc("描述");
        homework.setFilePaths("[]");
        when(courseMapper.selectCourseByCourseId(anyLong())).thenReturn(course);
        when(taskSubmissionMapper.selectTaskSubmissionBySubmissionId(anyLong())).thenReturn(submission);
        when(courseHomeworkMapper.selectCourseHomeworkByHomeworkId(anyLong())).thenReturn(homework);
        when(documentTextExtractor.extractTextFromFiles(any())).thenReturn("");
        // mock AI调用
        AIGradingServiceImpl spyService = Mockito.spy(aiGradingService);
        // 通过反射让callAIModel可见
        ReflectionTestUtils.setField(spyService, "arkApiKey", "test-key");
        ReflectionTestUtils.setField(spyService, "arkModelId", "test-model");
        ReflectionTestUtils.setField(spyService, "arkBaseUrl", "http://test-url");
        Mockito.doReturn(new IAIGradingService.AIGradingResult(88, "很好")).when(spyService).callAIModel(anyString());
        IAIGradingService.AIGradingResult result = spyService.gradeHomework(1L, 1L, 1L);
        assertTrue(result.isSuccess());
        assertEquals(88, result.getScore());
        assertEquals("很好", result.getComment());
    }

    @Test
    void testExtractHomeworkText_EmptyFilePaths() {
        CourseHomework homework = new CourseHomework();
        homework.setFilePaths("");
        AIGradingServiceImpl service = new AIGradingServiceImpl();
        String text = ReflectionTestUtils.invokeMethod(service, "extractHomeworkText", homework);
        assertEquals("", text);
    }

    @Test
    void testExtractSubmissionText_EmptySubmissionFile() {
        TaskSubmission submission = new TaskSubmission();
        submission.setSubmissionFile("");
        submission.setSubmissionContent("内容");
        AIGradingServiceImpl service = new AIGradingServiceImpl();
        String text = ReflectionTestUtils.invokeMethod(service, "extractSubmissionText", submission);
        assertEquals("内容", text);
    }

    @Test
    void testExtractSubmissionText_NullContentAndFile() {
        TaskSubmission submission = new TaskSubmission();
        submission.setSubmissionFile(null);
        submission.setSubmissionContent(null);
        AIGradingServiceImpl service = new AIGradingServiceImpl();
        String text = ReflectionTestUtils.invokeMethod(service, "extractSubmissionText", submission);
        assertEquals("", text);
    }

    @Test
    void testParseAIResponse_OnlyComment() {
        AIGradingServiceImpl service = new AIGradingServiceImpl();
        IAIGradingService.AIGradingResult result = ReflectionTestUtils.invokeMethod(service, "parseAIResponse", "优秀的作业");
        assertFalse(result.isSuccess());
    }

    @Test
    void testParseAIResponse_JsonNullFields() {
        AIGradingServiceImpl service = new AIGradingServiceImpl();
        IAIGradingService.AIGradingResult result = ReflectionTestUtils.invokeMethod(service, "parseAIResponse", "{\"score\":null,\"comment\":null}");
        assertFalse(result.isSuccess());
    }

    @Test
    void testCallAIModel_ThrowsException() {
        AIGradingServiceImpl service = new AIGradingServiceImpl();
        ReflectionTestUtils.setField(service, "arkApiKey", "test-key");
        ReflectionTestUtils.setField(service, "arkModelId", "test-model");
        ReflectionTestUtils.setField(service, "arkBaseUrl", "http://test-url");
        // 让ArkService.builder()抛异常
        String prompt = "test";
        // 这里可以用PowerMockito等高级mock工具mock ArkService静态方法抛异常
        // 或直接测试callAIModel的catch分支
        IAIGradingService.AIGradingResult result = ReflectionTestUtils.invokeMethod(service, "callAIModel", prompt);
        assertFalse(result.isSuccess());
    }

    @Test
    void testCallAIModel_JsonResponse() {
        // 直接测试AI响应解析
        AIGradingServiceImpl service = new AIGradingServiceImpl();
        IAIGradingService.AIGradingResult result = ReflectionTestUtils.invokeMethod(service, "parseAIResponse", "{\"score\":90,\"comment\":\"优秀\"}");
        assertTrue(result.isSuccess());
        assertEquals(90, result.getScore());
        assertEquals("优秀", result.getComment());
    }

    @Test
    void testCallAIModel_TextResponse() {
        AIGradingServiceImpl service = new AIGradingServiceImpl();
        IAIGradingService.AIGradingResult result = ReflectionTestUtils.invokeMethod(service, "parseAIResponse", "85 很棒");
        assertTrue(result.isSuccess());
        assertEquals(85, result.getScore());
        assertTrue(result.getComment().contains("很棒"));
    }

    @Test
    void testCallAIModel_InvalidResponse() {
        AIGradingServiceImpl service = new AIGradingServiceImpl();
        IAIGradingService.AIGradingResult result = ReflectionTestUtils.invokeMethod(service, "parseAIResponse", "无法识别的内容");
        assertFalse(result.isSuccess());
        assertEquals("无法解析AI响应", result.getError());
    }

    @Test
    void testExtractHomeworkText_Exception() {
        CourseHomework homework = new CourseHomework();
        homework.setFilePaths("[\"file1.docx\"]");
        when(documentTextExtractor.extractTextFromFiles(any())).thenThrow(new RuntimeException("error"));
        AIGradingServiceImpl service = new AIGradingServiceImpl();
        ReflectionTestUtils.setField(service, "documentTextExtractor", documentTextExtractor);
        String text = ReflectionTestUtils.invokeMethod(service, "extractHomeworkText", homework);
        assertEquals("", text);
    }

    @Test
    void testExtractSubmissionText_FileAndContent() {
        TaskSubmission submission = new TaskSubmission();
        submission.setSubmissionFile("[\"file1.docx\"]");
        submission.setSubmissionContent("文本内容");
        when(documentTextExtractor.extractTextFromFiles(any())).thenReturn("文件内容");
        AIGradingServiceImpl service = new AIGradingServiceImpl();
        ReflectionTestUtils.setField(service, "documentTextExtractor", documentTextExtractor);
        String text = ReflectionTestUtils.invokeMethod(service, "extractSubmissionText", submission);
        assertTrue(text.contains("文本内容"));
        assertTrue(text.contains("文件内容"));
    }

    @Test
    void testExtractSubmissionText_OnlyContent() {
        TaskSubmission submission = new TaskSubmission();
        submission.setSubmissionContent("文本内容");
        submission.setSubmissionFile(null);
        AIGradingServiceImpl service = new AIGradingServiceImpl();
        String text = ReflectionTestUtils.invokeMethod(service, "extractSubmissionText", submission);
        assertEquals("文本内容", text);
    }
}
