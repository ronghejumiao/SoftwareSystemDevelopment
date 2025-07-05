package com.ruoyi.system.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.system.domain.CoursePaperLibrary;
import com.ruoyi.system.domain.Question;
import com.ruoyi.system.domain.TestPaper;
import com.ruoyi.system.domain.vo.PaperGenerateRequest;
import com.ruoyi.system.mapper.TestPaperMapper;
import com.ruoyi.system.service.ICoursePaperLibraryService;
import com.ruoyi.system.service.IQuestionService;

@ExtendWith(MockitoExtension.class)
class PaperGenerateServiceImplTest {

    @Mock
    private IQuestionService questionService;

    @Mock
    private ICoursePaperLibraryService coursePaperLibraryService;

    @Mock
    private TestPaperMapper testPaperMapper;

    @InjectMocks
    private PaperGenerateServiceImpl paperGenerateService;

    @Test
    void testGeneratePaperManualMode() {
        Long courseId = 1L;
        PaperGenerateRequest request = new PaperGenerateRequest();
        request.setCourseId(courseId);
        request.setMode("manual");
        request.setPaperName("Test Paper");
        request.setTotalScore(BigDecimal.valueOf(100));

        List<Question> questions = new ArrayList<>();
        Question q1 = new Question();
        q1.setQuestionId(1L);
        q1.setQuestionType("选择");
        questions.add(q1);
        request.setContent(questions);

        CoursePaperLibrary library = new CoursePaperLibrary();
        library.setLibraryId(1L);

        when(coursePaperLibraryService.selectCoursePaperLibraryByCourseId(courseId)).thenReturn(library);
        when(testPaperMapper.insertTestPaper(any(TestPaper.class))).thenReturn(1);

        TestPaper result = paperGenerateService.generatePaper(request);

        assertNotNull(result);
        assertEquals(request.getPaperName(), result.getPaperName());
        assertEquals(library.getLibraryId(), result.getLibraryId());
        assertTrue(JSON.toJSONString(questions).equals(result.getContent()));
        verify(testPaperMapper).insertTestPaper(any(TestPaper.class));
    }

    @Test
    void testGeneratePaperRandomMode() {
        Long courseId = 1L;
        PaperGenerateRequest request = new PaperGenerateRequest();
        request.setCourseId(courseId);
        request.setMode("random");
        request.setPaperName("Test Paper");
        request.setTotalScore(BigDecimal.valueOf(100));

        PaperGenerateRequest.AutoConfig config = new PaperGenerateRequest.AutoConfig();
        config.setTotalQuestions(10);
        config.setChoiceRatio(40);
        config.setFillRatio(30);
        config.setSortRule("difficulty");
        request.setConfig(config);

        CoursePaperLibrary library = new CoursePaperLibrary();
        library.setLibraryId(1L);

        List<Question> allQuestions = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Question q = new Question();
            q.setQuestionId((long) i);
            if (i < 10) {
                q.setQuestionType("选择");
                q.setDifficultyLevel(i < 3 ? "简单" : (i < 7 ? "中等" : "困难"));
            } else if (i < 15) {
                q.setQuestionType("填空");
                q.setDifficultyLevel(i < 12 ? "简单" : (i < 14 ? "中等" : "困难"));
            } else {
                q.setQuestionType("简答");
                q.setDifficultyLevel(i < 17 ? "简单" : (i < 19 ? "中等" : "困难"));
            }
            allQuestions.add(q);
        }

        when(coursePaperLibraryService.selectCoursePaperLibraryByCourseId(courseId)).thenReturn(library);
        when(questionService.selectQuestionsByCourseId(courseId)).thenReturn(allQuestions);
        when(testPaperMapper.insertTestPaper(any(TestPaper.class))).thenReturn(1);

        TestPaper result = paperGenerateService.generatePaper(request);

        assertNotNull(result);
        assertEquals(request.getPaperName(), result.getPaperName());
        assertEquals(library.getLibraryId(), result.getLibraryId());
        assertNotNull(result.getContent());
        verify(testPaperMapper).insertTestPaper(any(TestPaper.class));
    }

    @Test
    void testGeneratePaperLibraryNotFound() {
        Long courseId = 1L;
        PaperGenerateRequest request = new PaperGenerateRequest();
        request.setCourseId(courseId);

        when(coursePaperLibraryService.selectCoursePaperLibraryByCourseId(courseId)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            paperGenerateService.generatePaper(request);
        });
    }



    @Test
    void testGeneratePaperRandomMode_noQuestions_shouldThrow() {
        Long courseId = 1L;
        PaperGenerateRequest request = new PaperGenerateRequest();
        request.setCourseId(courseId);
        request.setMode("random");
        request.setPaperName("Test Paper");
        request.setTotalScore(BigDecimal.valueOf(100));
        PaperGenerateRequest.AutoConfig config = new PaperGenerateRequest.AutoConfig();
        config.setTotalQuestions(10);
        config.setChoiceRatio(100);
        config.setFillRatio(0);
        config.setSortRule("difficulty");
        request.setConfig(config);

        CoursePaperLibrary library = new CoursePaperLibrary();
        library.setLibraryId(1L);

        when(coursePaperLibraryService.selectCoursePaperLibraryByCourseId(courseId)).thenReturn(library);
        when(questionService.selectQuestionsByCourseId(courseId)).thenReturn(new ArrayList<>());

        assertThrows(RuntimeException.class, () -> paperGenerateService.generatePaper(request));
    }

    @Test
    void testGeneratePaper_insertPaperThrows_shouldPropagate() {
        Long courseId = 1L;
        PaperGenerateRequest request = new PaperGenerateRequest();
        request.setCourseId(courseId);
        request.setMode("manual");
        request.setPaperName("Test Paper");
        request.setTotalScore(BigDecimal.valueOf(100));
        List<Question> questions = new ArrayList<>();
        Question q1 = new Question();
        q1.setQuestionId(1L);
        q1.setQuestionType("选择");
        questions.add(q1);
        request.setContent(questions);

        CoursePaperLibrary library = new CoursePaperLibrary();
        library.setLibraryId(1L);

        when(coursePaperLibraryService.selectCoursePaperLibraryByCourseId(courseId)).thenReturn(library);
        when(testPaperMapper.insertTestPaper(any(TestPaper.class))).thenThrow(new RuntimeException("db error"));

        assertThrows(RuntimeException.class, () -> paperGenerateService.generatePaper(request));
    }
}