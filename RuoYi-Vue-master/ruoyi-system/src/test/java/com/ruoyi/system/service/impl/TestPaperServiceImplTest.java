package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.TestPaper;
import com.ruoyi.system.domain.vo.PaperGenerateRequest;
import com.ruoyi.system.mapper.TestPaperMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * TestPaperServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
class TestPaperServiceImplTest {

    @Mock
    private TestPaperMapper mapper;

    @InjectMocks
    private TestPaperServiceImpl service;

    private TestPaper paper;

    @BeforeEach
    void setUp() {
        paper = new TestPaper();
        paper.setPaperId(1L);
    }

    @Test
    void insert_update_delegate() {
        when(mapper.insertTestPaper(any())).thenReturn(1);
        when(mapper.updateTestPaper(any())).thenReturn(1);
        assertEquals(1, service.insertTestPaper(paper));
        assertEquals(1, service.updateTestPaper(paper));
    }

    @Test
    void select_and_delete_delegate() {
        when(mapper.selectTestPaperByPaperId(1L)).thenReturn(paper);
        when(mapper.selectTestPaperList(any())).thenReturn(Collections.singletonList(paper));
        when(mapper.deleteTestPaperByPaperIds(any())).thenReturn(2);
        when(mapper.deleteTestPaperByPaperId(1L)).thenReturn(1);
        assertEquals(paper, service.selectTestPaperByPaperId(1L));
        assertEquals(1, service.selectTestPaperList(new TestPaper()).size());
        assertEquals(2, service.deleteTestPaperByPaperIds(new Long[]{1L,2L}));
        assertEquals(1, service.deleteTestPaperByPaperId(1L));
    }

    @Test
    void selectTestPaperByPaperId_mapperReturnsNull_shouldReturnNull() {
        when(mapper.selectTestPaperByPaperId(99L)).thenReturn(null);
        assertNull(service.selectTestPaperByPaperId(99L));
    }

    @Test
    void selectTestPaperList_mapperReturnsNull_shouldReturnNull() {
        when(mapper.selectTestPaperList(any())).thenReturn(null);
        assertNull(service.selectTestPaperList(new TestPaper()));
    }

    @Test
    void deleteTestPaperByPaperIds_emptyArray_shouldReturnZero() {
        when(mapper.deleteTestPaperByPaperIds(new Long[0])).thenReturn(0);
        assertEquals(0, service.deleteTestPaperByPaperIds(new Long[0]));
    }

    @Test
    void allMapperMethods_throwException_shouldPropagate() {
        RuntimeException ex = new RuntimeException("db error");
        when(mapper.selectTestPaperByPaperId(anyLong())).thenThrow(ex);
        when(mapper.selectTestPaperList(any())).thenThrow(ex);
        when(mapper.insertTestPaper(any())).thenThrow(ex);
        when(mapper.updateTestPaper(any())).thenThrow(ex);
        when(mapper.deleteTestPaperByPaperIds(any())).thenThrow(ex);
        when(mapper.deleteTestPaperByPaperId(anyLong())).thenThrow(ex);
        assertThrows(RuntimeException.class, () -> service.selectTestPaperByPaperId(1L));
        assertThrows(RuntimeException.class, () -> service.selectTestPaperList(new TestPaper()));
        assertThrows(RuntimeException.class, () -> service.insertTestPaper(new TestPaper()));
        assertThrows(RuntimeException.class, () -> service.updateTestPaper(new TestPaper()));
        assertThrows(RuntimeException.class, () -> service.deleteTestPaperByPaperIds(new Long[]{1L}));
        assertThrows(RuntimeException.class, () -> service.deleteTestPaperByPaperId(1L));
    }

    @Mock
    private com.ruoyi.system.mapper.CoursePaperLibraryMapper coursePaperLibraryMapper;

    @Test
    void generatePaper_libraryNotFound_shouldThrow() {
        com.ruoyi.system.domain.vo.PaperGenerateRequest req = new com.ruoyi.system.domain.vo.PaperGenerateRequest();
        req.setCourseId(1L);
        when(coursePaperLibraryMapper.selectCoursePaperLibraryByCourseId(1L)).thenReturn(null);
        assertThrows(RuntimeException.class, () -> service.generatePaper(req));
    }

    @Test
    void generatePaper_insertFail_shouldThrow() {
        com.ruoyi.system.domain.vo.PaperGenerateRequest req = new com.ruoyi.system.domain.vo.PaperGenerateRequest();
        req.setCourseId(1L);
        req.setPaperName("n");
        com.ruoyi.system.domain.CoursePaperLibrary lib = new com.ruoyi.system.domain.CoursePaperLibrary();
        lib.setLibraryId(2L);
        when(coursePaperLibraryMapper.selectCoursePaperLibraryByCourseId(1L)).thenReturn(lib);
        when(mapper.insertTestPaper(any())).thenReturn(0);
        assertThrows(RuntimeException.class, () -> service.generatePaper(req));
    }
} 