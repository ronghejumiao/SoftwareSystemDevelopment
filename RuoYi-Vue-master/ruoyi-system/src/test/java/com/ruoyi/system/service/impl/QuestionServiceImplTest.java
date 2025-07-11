package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.Question;
import com.ruoyi.system.domain.KnowledgeNode;
import com.ruoyi.system.mapper.QuestionMapper;
import com.ruoyi.system.mapper.KnowledgeNodeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * QuestionServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
class QuestionServiceImplTest {

    @Mock private QuestionMapper questionMapper;
    @Mock private KnowledgeNodeMapper nodeMapper;
    @InjectMocks private QuestionServiceImpl service;

    private Question q;

    @BeforeEach
    void setUp() {
        q = new Question();
        q.setQuestionId(1L);
        q.setKnowledgeNodeId(10L);
    }

    @Test
    void insert_withInvalidKnowledgeNode_shouldResetId() {
        when(nodeMapper.selectKnowledgeNodeByNodeId(10L)).thenReturn(null); // 不存在
        when(questionMapper.insertQuestion(any())).thenReturn(1);

        service.insertQuestion(q);
        assertNull(q.getKnowledgeNodeId());
    }

    @Test
    void insert_withValidKnowledgeNode_keepsId() {
        KnowledgeNode node = new KnowledgeNode();
        node.setNodeId(10L);
        when(nodeMapper.selectKnowledgeNodeByNodeId(10L)).thenReturn(node);
        when(questionMapper.insertQuestion(any())).thenReturn(1);

        service.insertQuestion(q);
        assertEquals(10L, q.getKnowledgeNodeId());
    }

    @Test
    void insert_withNullKnowledgeNodeId_shouldNotCheckNode() {
        q.setKnowledgeNodeId(null);
        when(questionMapper.insertQuestion(any())).thenReturn(1);
        service.insertQuestion(q);
        // knowledgeNodeId 仍为null
        assertNull(q.getKnowledgeNodeId());
    }

    @Test
    void update_invalidKnowledgeNode_setsNull() {
        when(nodeMapper.selectKnowledgeNodeByNodeId(10L)).thenReturn(null);
        when(questionMapper.updateQuestion(any())).thenReturn(1);

        service.updateQuestion(q);
        assertNull(q.getKnowledgeNodeId());
    }

    @Test
    void update_withNullKnowledgeNodeId_shouldNotCheckNode() {
        q.setKnowledgeNodeId(null);
        when(questionMapper.updateQuestion(any())).thenReturn(1);
        service.updateQuestion(q);
        assertNull(q.getKnowledgeNodeId());
    }

    @Test
    void selectAndDelete_delegate() {
        when(questionMapper.selectQuestionByQuestionId(1L)).thenReturn(q);
        when(questionMapper.selectQuestionList(any())).thenReturn(Collections.singletonList(q));
        when(questionMapper.deleteQuestionByQuestionIds(any())).thenReturn(2);
        when(questionMapper.deleteQuestionByQuestionId(1L)).thenReturn(1);
        when(questionMapper.selectQuestionsByCourseId(5L)).thenReturn(Collections.singletonList(q));

        assertEquals(q, service.selectQuestionByQuestionId(1L));
        assertEquals(1, service.selectQuestionList(new Question()).size());
        assertEquals(2, service.deleteQuestionByQuestionIds(new Long[]{1L,2L}));
        assertEquals(1, service.deleteQuestionByQuestionId(1L));
        assertEquals(1, service.selectQuestionsByCourseId(5L).size());
    }

    @Test
    void selectById_mapperReturnsNull_shouldReturnNull() {
        when(questionMapper.selectQuestionByQuestionId(99L)).thenReturn(null);
        assertNull(service.selectQuestionByQuestionId(99L));
    }

    @Test
    void selectList_mapperReturnsNull_shouldReturnNull() {
        when(questionMapper.selectQuestionList(any())).thenReturn(null);
        assertNull(service.selectQuestionList(new Question()));
    }

    @Test
    void selectQuestionsByCourseId_mapperReturnsNull_shouldReturnNull() {
        when(questionMapper.selectQuestionsByCourseId(5L)).thenReturn(null);
        assertNull(service.selectQuestionsByCourseId(5L));
    }

    @Test
    void deleteByQuestionIds_emptyArray_shouldReturnZero() {
        when(questionMapper.deleteQuestionByQuestionIds(new Long[0])).thenReturn(0);
        assertEquals(0, service.deleteQuestionByQuestionIds(new Long[0]));
    }

    @Test
    void allMapperMethods_throwException_shouldPropagate() {
        RuntimeException ex = new RuntimeException("db error");
        when(questionMapper.selectQuestionByQuestionId(anyLong())).thenThrow(ex);
        when(questionMapper.selectQuestionList(any())).thenThrow(ex);
        when(questionMapper.insertQuestion(any())).thenThrow(ex);
        when(questionMapper.updateQuestion(any())).thenThrow(ex);
        when(questionMapper.deleteQuestionByQuestionIds(any())).thenThrow(ex);
        when(questionMapper.deleteQuestionByQuestionId(anyLong())).thenThrow(ex);
        when(questionMapper.selectQuestionsByCourseId(anyLong())).thenThrow(ex);
        assertThrows(RuntimeException.class, () -> service.selectQuestionByQuestionId(1L));
        assertThrows(RuntimeException.class, () -> service.selectQuestionList(new Question()));
        assertThrows(RuntimeException.class, () -> service.insertQuestion(q));
        assertThrows(RuntimeException.class, () -> service.updateQuestion(q));
        assertThrows(RuntimeException.class, () -> service.deleteQuestionByQuestionIds(new Long[]{1L}));
        assertThrows(RuntimeException.class, () -> service.deleteQuestionByQuestionId(1L));
        assertThrows(RuntimeException.class, () -> service.selectQuestionsByCourseId(1L));
    }
} 