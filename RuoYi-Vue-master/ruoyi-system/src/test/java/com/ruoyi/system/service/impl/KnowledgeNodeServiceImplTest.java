package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.KnowledgeNode;
import com.ruoyi.system.mapper.KnowledgeNodeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * KnowledgeNodeServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
class KnowledgeNodeServiceImplTest {

    @Mock
    private KnowledgeNodeMapper mapper;

    @InjectMocks
    private KnowledgeNodeServiceImpl service;

    private KnowledgeNode node;

    @BeforeEach
    void setUp() {
        node = new KnowledgeNode();
        node.setNodeId(1L);
    }

    @Test
    void insert_setsCreateTime() {
        when(mapper.insertKnowledgeNode(any())).thenReturn(1);
        assertEquals(1, service.insertKnowledgeNode(node));
        assertNotNull(node.getCreateTime());
    }

    @Test
    void update_setsUpdateTime() {
        when(mapper.updateKnowledgeNode(any())).thenReturn(1);
        assertEquals(1, service.updateKnowledgeNode(node));
        assertNotNull(node.getUpdateTime());
    }

    @Test
    void select_and_delete_delegate() {
        when(mapper.selectKnowledgeNodeByNodeId(1L)).thenReturn(node);
        when(mapper.selectKnowledgeNodeList(any())).thenReturn(Collections.singletonList(node));
        when(mapper.deleteKnowledgeNodeByNodeIds(any())).thenReturn(2);
        when(mapper.deleteKnowledgeNodeByNodeId(1L)).thenReturn(1);

        assertEquals(node, service.selectKnowledgeNodeByNodeId(1L));
        assertEquals(1, service.selectKnowledgeNodeList(new KnowledgeNode()).size());
        assertEquals(2, service.deleteKnowledgeNodeByNodeIds(new Long[]{1L,2L}));
        assertEquals(1, service.deleteKnowledgeNodeByNodeId(1L));
    }

    @Test
    void selectMethods_mapperReturnsNull_shouldReturnNull() {
        when(mapper.selectKnowledgeNodeByNodeId(99L)).thenReturn(null);
        when(mapper.selectKnowledgeNodeList(any())).thenReturn(null);
        assertNull(service.selectKnowledgeNodeByNodeId(99L));
        assertNull(service.selectKnowledgeNodeList(new KnowledgeNode()));
    }

    @Test
    void deleteByNodeIds_empty_shouldReturnZero() {
        when(mapper.deleteKnowledgeNodeByNodeIds(any())).thenReturn(0);
        assertEquals(0, service.deleteKnowledgeNodeByNodeIds(new Long[0]));
    }

    @Test
    void mapperExceptions_shouldPropagate() {
        RuntimeException dbErr = new RuntimeException("DB error");
        when(mapper.insertKnowledgeNode(any())).thenThrow(dbErr);
        when(mapper.updateKnowledgeNode(any())).thenThrow(dbErr);
        when(mapper.selectKnowledgeNodeByNodeId(anyLong())).thenThrow(dbErr);
        when(mapper.selectKnowledgeNodeList(any())).thenThrow(dbErr);
        when(mapper.deleteKnowledgeNodeByNodeIds(any())).thenThrow(dbErr);
        when(mapper.deleteKnowledgeNodeByNodeId(anyLong())).thenThrow(dbErr);

        KnowledgeNode kn = new KnowledgeNode();
        assertThrows(RuntimeException.class, () -> service.insertKnowledgeNode(kn));
        assertThrows(RuntimeException.class, () -> service.updateKnowledgeNode(kn));
        assertThrows(RuntimeException.class, () -> service.selectKnowledgeNodeByNodeId(1L));
        assertThrows(RuntimeException.class, () -> service.selectKnowledgeNodeList(kn));
        assertThrows(RuntimeException.class, () -> service.deleteKnowledgeNodeByNodeIds(new Long[]{1L}));
        assertThrows(RuntimeException.class, () -> service.deleteKnowledgeNodeByNodeId(1L));
    }
} 