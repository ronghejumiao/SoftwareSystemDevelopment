package com.ruoyi.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.Score;
import com.ruoyi.system.service.IScoreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScoreController.class)
@DisplayName("成绩管理Controller测试 (100%覆盖率目标)")
class ScoreControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IScoreService scoreService;
    @MockBean
    private RedisCache redisCache;
    @MockBean
    private TokenService tokenService;

    private static final String MOCK_USER = "testUser";
    private static final String PERMISSION_PREFIX = "system:score:";
    private static final Long STUDENT_USER_ID = 100L;
    private static final Long OTHER_USER_ID = 101L;

    @Nested
    @DisplayName("管理员或教师视角测试")
    class AdminOrTeacherTests {

        private void asAdmin() {
            // 在这个代码块内，SecurityUtils.isStudent() 将返回 false
        }

        @Test
        @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "list")
        @DisplayName("查询成绩列表")
        void testList() throws Exception {
            try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
                mockedSecurity.when(SecurityUtils::isStudent).thenReturn(false);
                when(scoreService.selectScoreList(any(Score.class))).thenReturn(Collections.singletonList(new Score()));

                mockMvc.perform(get("/system/score/list"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.total").value(1));
            }
        }

        @Test
        @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "export")
        @DisplayName("导出成绩列表")
        void testExport() throws Exception {
            try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
                mockedSecurity.when(SecurityUtils::isStudent).thenReturn(false);

                mockMvc.perform(post("/system/score/export").with(csrf()))
                        .andExpect(status().isOk());

                verify(scoreService).selectScoreList(any(Score.class));
            }
        }

        @Test
        @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "query")
        @DisplayName("获取任意成绩详情 - 成功")
        void testGetInfo_Success() throws Exception {
            try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
                mockedSecurity.when(SecurityUtils::isStudent).thenReturn(false);
                when(scoreService.selectScoreByScoreId(anyLong())).thenReturn(new Score());

                mockMvc.perform(get("/system/score/{scoreId}", 1L))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
            }
        }

        @Test
        @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "add")
        @DisplayName("新增成绩")
        void testAdd() throws Exception {
            when(scoreService.insertScore(any(Score.class))).thenReturn(1);
            mockMvc.perform(post("/system/score").with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new Score())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
        }

        @Test
        @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
        @DisplayName("修改成绩 - 成功")
        void testEdit_Success() throws Exception {
            try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
                mockedSecurity.when(SecurityUtils::isStudent).thenReturn(false);
                when(scoreService.updateScore(any(Score.class))).thenReturn(1);

                mockMvc.perform(put("/system/score").with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new Score())))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
            }
        }

        @Test
        @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
        @DisplayName("修改成绩 - 失败(Service层返回0)")
        void testEdit_Failure() throws Exception {
            try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
                mockedSecurity.when(SecurityUtils::isStudent).thenReturn(false);
                when(scoreService.updateScore(any(Score.class))).thenReturn(0);

                mockMvc.perform(put("/system/score").with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new Score())))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
            }
        }

        @Test
        @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
        @DisplayName("删除成绩 - 成功")
        void testRemove_Success() throws Exception {
            try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
                mockedSecurity.when(SecurityUtils::isStudent).thenReturn(false);
                when(scoreService.deleteScoreByScoreIds(any())).thenReturn(1);

                mockMvc.perform(delete("/system/score/{scoreIds}", "1").with(csrf()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
            }
        }

        @Test
        @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
        @DisplayName("删除成绩 - 失败(Service层返回0)")
        void testRemove_Failure() throws Exception {
            try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
                mockedSecurity.when(SecurityUtils::isStudent).thenReturn(false);
                when(scoreService.deleteScoreByScoreIds(any())).thenReturn(0);

                mockMvc.perform(delete("/system/score/{scoreIds}", "1").with(csrf()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.code").value(HttpStatus.ERROR));
            }
        }

        @Test
        @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "list")
        @DisplayName("根据用户ID和课程ID查询成绩")
        void testGetScoreByUserAndCourse() throws Exception {
            try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
                mockedSecurity.when(SecurityUtils::isStudent).thenReturn(false);
                when(scoreService.selectScoreByUserAndCourse(anyLong(), anyLong())).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/system/score/user/{userId}/course/{courseId}", OTHER_USER_ID, 1L))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
            }
        }

        @Test
        @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "list")
        @DisplayName("根据用户ID查询成绩")
        void testGetScoreByUserId() throws Exception {
            try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
                mockedSecurity.when(SecurityUtils::isStudent).thenReturn(false);
                when(scoreService.selectScoreByUserId(anyLong())).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/system/score/user/{userId}", OTHER_USER_ID))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
            }
        }
    }

    @Nested
    @DisplayName("学生视角测试")
    class StudentTests {

        @Test
        @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "list")
        @DisplayName("查询自己的成绩列表 - 无特定筛选")
        void testList_NoFilter() throws Exception {
            ArgumentCaptor<Score> captor = ArgumentCaptor.forClass(Score.class);
            try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
                mockedSecurity.when(SecurityUtils::isStudent).thenReturn(true);
                mockedSecurity.when(SecurityUtils::getUserId).thenReturn(STUDENT_USER_ID);

                mockMvc.perform(get("/system/score/list"))
                        .andExpect(status().isOk());

                verify(scoreService).selectScoreList(captor.capture());
                assertEquals(STUDENT_USER_ID, captor.getValue().getParams().get("userId"));
            }
        }

        @Test
        @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "list")
        @DisplayName("查询自己的成绩列表 - 带特定筛选")
        void testList_WithFilter() throws Exception {
            ArgumentCaptor<Score> captor = ArgumentCaptor.forClass(Score.class);
            try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
                mockedSecurity.when(SecurityUtils::isStudent).thenReturn(true);
                mockedSecurity.when(SecurityUtils::getUserId).thenReturn(STUDENT_USER_ID);

                mockMvc.perform(get("/system/score/list").param("taskId", "123"))
                        .andExpect(status().isOk());

                verify(scoreService).selectScoreList(captor.capture());
                assertEquals(STUDENT_USER_ID, captor.getValue().getParams().get("userId"));
                assertEquals(123L, captor.getValue().getTaskId());
            }
        }

        @Test
        @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "export")
        @DisplayName("导出自己的成绩列表")
        void testExport_AsStudent() throws Exception {
            ArgumentCaptor<Score> captor = ArgumentCaptor.forClass(Score.class);
            try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
                mockedSecurity.when(SecurityUtils::isStudent).thenReturn(true);
                mockedSecurity.when(SecurityUtils::getUserId).thenReturn(STUDENT_USER_ID);

                mockMvc.perform(post("/system/score/export").with(csrf()))
                        .andExpect(status().isOk());

                verify(scoreService).selectScoreList(captor.capture());
                assertEquals(STUDENT_USER_ID, captor.getValue().getParams().get("userId"));
            }
        }

        @Test
        @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "query")
        @DisplayName("获取自己的成绩详情 - 成功")
        void testGetInfo_Success() throws Exception {
            try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
                mockedSecurity.when(SecurityUtils::isStudent).thenReturn(true);
                mockedSecurity.when(SecurityUtils::getUserId).thenReturn(STUDENT_USER_ID);

                when(scoreService.selectScoreByScoreId(anyLong())).thenReturn(new Score());
                when(scoreService.selectScoreList(any(Score.class))).thenReturn(Collections.singletonList(new Score()));

                mockMvc.perform(get("/system/score/{scoreId}", 1L))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
            }
        }

        @Test
        @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "query")
        @DisplayName("获取他人成绩详情 - 被阻止")
        void testGetInfo_Forbidden() throws Exception {
            try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
                mockedSecurity.when(SecurityUtils::isStudent).thenReturn(true);
                mockedSecurity.when(SecurityUtils::getUserId).thenReturn(STUDENT_USER_ID);

                when(scoreService.selectScoreByScoreId(anyLong())).thenReturn(new Score());
                when(scoreService.selectScoreList(any(Score.class))).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/system/score/{scoreId}", 1L))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.code").value(HttpStatus.ERROR))
                        .andExpect(jsonPath("$.msg").value("您没有权限查看该成绩记录"));
            }
        }

        @Test
        @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "edit")
        @DisplayName("修改成绩 - 被阻止")
        void testEdit_Forbidden() throws Exception {
            try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
                mockedSecurity.when(SecurityUtils::isStudent).thenReturn(true);
                mockedSecurity.when(SecurityUtils::isTeacher).thenReturn(false);
                mockedSecurity.when(() -> SecurityUtils.hasRole("admin")).thenReturn(false);

                mockMvc.perform(put("/system/score").with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new Score())))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.msg").value("学生用户无权修改成绩记录"));
            }
        }

        @Test
        @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "remove")
        @DisplayName("删除成绩 - 被阻止")
        void testRemove_Forbidden() throws Exception {
            try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
                mockedSecurity.when(SecurityUtils::isStudent).thenReturn(true);
                mockedSecurity.when(SecurityUtils::isTeacher).thenReturn(false);
                mockedSecurity.when(() -> SecurityUtils.hasRole("admin")).thenReturn(false);

                mockMvc.perform(delete("/system/score/{scoreIds}", "1").with(csrf()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.msg").value("学生用户无权删除成绩记录"));
            }
        }

        @Test
        @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "list")
        @DisplayName("根据用户ID查询自己的成绩 - 成功")
        void testGetOwnScoreByUserId_Success() throws Exception {
            try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
                mockedSecurity.when(SecurityUtils::isStudent).thenReturn(true);
                mockedSecurity.when(SecurityUtils::getUserId).thenReturn(STUDENT_USER_ID);

                mockMvc.perform(get("/system/score/user/{userId}", STUDENT_USER_ID))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.code").value(HttpStatus.SUCCESS));
            }
        }

        @Test
        @WithMockUser(username = MOCK_USER, authorities = PERMISSION_PREFIX + "list")
        @DisplayName("根据用户ID查询他人成绩 - 被阻止")
        void testGetOtherScoreByUserId_Forbidden() throws Exception {
            try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
                mockedSecurity.when(SecurityUtils::isStudent).thenReturn(true);
                mockedSecurity.when(SecurityUtils::getUserId).thenReturn(STUDENT_USER_ID);

                mockMvc.perform(get("/system/score/user/{userId}", OTHER_USER_ID))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.msg").value("学生用户只能查看自己的成绩记录"));
            }
        }
    }
}