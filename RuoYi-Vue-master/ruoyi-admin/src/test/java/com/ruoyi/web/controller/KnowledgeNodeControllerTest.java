package com.ruoyi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.KnowledgeNode;
import com.ruoyi.system.service.IKnowledgeNodeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * KnowledgeNodeController 的单元测试类
 *
 * @see com.ruoyi.web.controller.KnowledgeNodeController
 */
@WebMvcTest(KnowledgeNodeController.class)
@DisplayName("知识图谱节点Controller测试")
class KnowledgeNodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IKnowledgeNodeService knowledgeNodeService;

    //--- 模拟框架依赖 ---
    @MockBean
    private RedisCache redisCache;
    @MockBean
    private TokenService tokenService;

    private static final String MOCK_USER = "testUser";
    private static final String PERMISSION_PREFIX = "system:node:";

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "list")
    @DisplayName("测试查询知识图谱节点列表 - 成功")
    void testList_Success() throws Exception {
        when(knowledgeNodeService.selectKnowledgeNodeList(any(KnowledgeNode.class)))
                .thenReturn(Collections.singletonList(new KnowledgeNode()));

        mockMvc.perform(get("/system/node/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1));

        verify(knowledgeNodeService).selectKnowledgeNodeList(any(KnowledgeNode.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "export")
    @DisplayName("测试导出知识图谱节点列表 - 成功")
    void testExport_Success() throws Exception {
        when(knowledgeNodeService.selectKnowledgeNodeList(any(KnowledgeNode.class)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(post("/system/node/export").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"));

        verify(knowledgeNodeService).selectKnowledgeNodeList(any(KnowledgeNode.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "query")
    @DisplayName("测试根据ID获取知识图谱节点 - 成功")
    void testGetInfo_Success() throws Exception {
        long nodeId = 1L;
        KnowledgeNode node = new KnowledgeNode();
        node.setNodeId(nodeId);

        when(knowledgeNodeService.selectKnowledgeNodeByNodeId(nodeId)).thenReturn(node);

        mockMvc.perform(get("/system/node/{nodeId}", nodeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS))
                .andExpect(jsonPath("$.data.nodeId").value(nodeId));

        verify(knowledgeNodeService).selectKnowledgeNodeByNodeId(nodeId);
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "query")
    @DisplayName("测试根据ID获取知识图谱节点 - 未找到")
    void testGetInfo_NotFound() throws Exception {
        long nodeId = 99L;
        when(knowledgeNodeService.selectKnowledgeNodeByNodeId(nodeId)).thenReturn(null);

        mockMvc.perform(get("/system/node/{nodeId}", nodeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(knowledgeNodeService).selectKnowledgeNodeByNodeId(nodeId);
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试新增知识图谱节点 - 成功")
    void testAdd_Success() throws Exception {
        KnowledgeNode node = new KnowledgeNode();
        node.setNodeName("新节点");

        when(knowledgeNodeService.insertKnowledgeNode(any(KnowledgeNode.class))).thenReturn(1);

        mockMvc.perform(post("/system/node").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));

        verify(knowledgeNodeService).insertKnowledgeNode(any(KnowledgeNode.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
    @DisplayName("测试新增知识图谱节点 - 失败")
    void testAdd_Failure() throws Exception {
        KnowledgeNode node = new KnowledgeNode();
        when(knowledgeNodeService.insertKnowledgeNode(any(KnowledgeNode.class))).thenReturn(0);

        mockMvc.perform(post("/system/node").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("测试修改知识图谱节点 - 成功")
    void testEdit_Success() throws Exception {
        KnowledgeNode node = new KnowledgeNode();
        node.setNodeId(1L);

        when(knowledgeNodeService.updateKnowledgeNode(any(KnowledgeNode.class))).thenReturn(1);

        mockMvc.perform(put("/system/node").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));

        verify(knowledgeNodeService).updateKnowledgeNode(any(KnowledgeNode.class));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
    @DisplayName("测试修改知识图谱节点 - 失败")
    void testEdit_Failure() throws Exception {
        KnowledgeNode node = new KnowledgeNode();
        node.setNodeId(1L);
        when(knowledgeNodeService.updateKnowledgeNode(any(KnowledgeNode.class))).thenReturn(0);

        mockMvc.perform(put("/system/node").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("测试删除知识图谱节点 - 成功")
    void testRemove_Success() throws Exception {
        Long[] nodeIds = {1L, 2L};
        when(knowledgeNodeService.deleteKnowledgeNodeByNodeIds(nodeIds)).thenReturn(nodeIds.length);

        mockMvc.perform(delete("/system/node/{nodeIds}", "1,2").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));

        verify(knowledgeNodeService).deleteKnowledgeNodeByNodeIds(nodeIds);
    }

    @Test
    @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
    @DisplayName("测试删除知识图谱节点 - 失败")
    void testRemove_Failure() throws Exception {
        Long[] nodeIds = {99L};
        when(knowledgeNodeService.deleteKnowledgeNodeByNodeIds(nodeIds)).thenReturn(0);

        mockMvc.perform(delete("/system/node/{nodeIds}", "99").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
    }
}