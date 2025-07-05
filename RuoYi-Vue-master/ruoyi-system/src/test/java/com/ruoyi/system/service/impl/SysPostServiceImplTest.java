package com.ruoyi.system.service.impl;

import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysPost;
import com.ruoyi.system.mapper.SysPostMapper;
import com.ruoyi.system.mapper.SysUserPostMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SysPostServiceImplTest {

    @Mock
    private SysPostMapper postMapper;
    @Mock
    private SysUserPostMapper userPostMapper;
    @InjectMocks
    private SysPostServiceImpl service;

    private SysPost post;

    @BeforeEach
    void setUp() {
        post = new SysPost();
        post.setPostId(1L);
        post.setPostName("岗位A");
        post.setPostCode("codeA");
    }

    @Test
    void selectPostList_shouldDelegate() {
        List<SysPost> list = Collections.singletonList(post);
        when(postMapper.selectPostList(any())).thenReturn(list);
        assertEquals(list, service.selectPostList(new SysPost()));
    }

    @Test
    void selectPostAll_shouldDelegate() {
        List<SysPost> list = Collections.singletonList(post);
        when(postMapper.selectPostAll()).thenReturn(list);
        assertEquals(list, service.selectPostAll());
    }

    @Test
    void selectPostById_shouldDelegate() {
        when(postMapper.selectPostById(1L)).thenReturn(post);
        assertEquals(post, service.selectPostById(1L));
    }

    @Test
    void selectPostListByUserId_shouldDelegate() {
        List<Long> ids = Arrays.asList(1L, 2L);
        when(postMapper.selectPostListByUserId(2L)).thenReturn(ids);
        assertEquals(ids, service.selectPostListByUserId(2L));
    }

    @Test
    void checkPostNameUnique_notUnique() {
        SysPost exist = new SysPost();
        exist.setPostId(2L);
        when(postMapper.checkPostNameUnique(anyString())).thenReturn(exist);
        SysPost p = new SysPost();
        p.setPostId(1L);
        p.setPostName("岗位A");
        assertFalse(service.checkPostNameUnique(p));
    }

    @Test
    void checkPostNameUnique_unique() {
        when(postMapper.checkPostNameUnique(anyString())).thenReturn(null);
        SysPost p = new SysPost();
        p.setPostId(1L);
        p.setPostName("岗位A");
        assertTrue(service.checkPostNameUnique(p));
    }

    @Test
    void checkPostCodeUnique_notUnique() {
        SysPost exist = new SysPost();
        exist.setPostId(2L);
        when(postMapper.checkPostCodeUnique(anyString())).thenReturn(exist);
        SysPost p = new SysPost();
        p.setPostId(1L);
        p.setPostCode("codeA");
        assertFalse(service.checkPostCodeUnique(p));
    }

    @Test
    void checkPostCodeUnique_unique() {
        when(postMapper.checkPostCodeUnique(anyString())).thenReturn(null);
        SysPost p = new SysPost();
        p.setPostId(1L);
        p.setPostCode("codeA");
        assertTrue(service.checkPostCodeUnique(p));
    }

    @Test
    void countUserPostById_shouldDelegate() {
        when(userPostMapper.countUserPostById(1L)).thenReturn(3);
        assertEquals(3, service.countUserPostById(1L));
    }

    @Test
    void deletePostById_shouldDelegate() {
        when(postMapper.deletePostById(1L)).thenReturn(1);
        assertEquals(1, service.deletePostById(1L));
    }

    @Test
    void deletePostByIds_shouldThrowIfAssigned() {
        SysPost p = new SysPost();
        p.setPostId(1L);
        p.setPostName("岗位A");
        when(postMapper.selectPostById(1L)).thenReturn(p);
        when(userPostMapper.countUserPostById(1L)).thenReturn(1);
        ServiceException ex = assertThrows(ServiceException.class, () -> service.deletePostByIds(new Long[]{1L}));
        assertTrue(ex.getMessage().contains("已分配"));
    }

    @Test
    void deletePostByIds_shouldDelete() {
        SysPost p = new SysPost();
        p.setPostId(1L);
        p.setPostName("岗位A");
        when(postMapper.selectPostById(1L)).thenReturn(p);
        when(userPostMapper.countUserPostById(1L)).thenReturn(0);
        when(postMapper.deletePostByIds(any())).thenReturn(1);
        assertEquals(1, service.deletePostByIds(new Long[]{1L}));
    }

    @Test
    void insertPost_shouldDelegate() {
        when(postMapper.insertPost(post)).thenReturn(1);
        assertEquals(1, service.insertPost(post));
    }

    @Test
    void updatePost_shouldDelegate() {
        when(postMapper.updatePost(post)).thenReturn(1);
        assertEquals(1, service.updatePost(post));
    }


}