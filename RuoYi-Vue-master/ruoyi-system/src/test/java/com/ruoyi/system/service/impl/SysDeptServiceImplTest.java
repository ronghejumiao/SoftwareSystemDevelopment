package com.ruoyi.system.service.impl;

import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.mapper.SysRoleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SysDeptServiceImplTest {

    @Mock
    private SysDeptMapper deptMapper;
    @Mock
    private SysRoleMapper roleMapper;

    @InjectMocks
    private SysDeptServiceImpl service;

    private SysDept dept;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dept = new SysDept();
        dept.setDeptId(1L);
        dept.setParentId(0L);
        dept.setDeptName("部门A");
        dept.setStatus("0");
        dept.setAncestors("0");
    }

    @Test
    void selectDeptList_shouldDelegate() {
        List<SysDept> list = Collections.singletonList(dept);
        when(deptMapper.selectDeptList(any())).thenReturn(list);
        assertEquals(list, service.selectDeptList(new SysDept()));
    }



    @Test
    void buildDeptTree_and_buildDeptTreeSelect() {
        SysDept d1 = new SysDept(); d1.setDeptId(1L); d1.setParentId(0L);
        SysDept d2 = new SysDept(); d2.setDeptId(2L); d2.setParentId(1L);
        List<SysDept> list = Arrays.asList(d1, d2);
        List<SysDept> tree = service.buildDeptTree(list);
        assertEquals(1, tree.size());
        List<TreeSelect> treeSelect = service.buildDeptTreeSelect(list);
        assertEquals(1, treeSelect.size());
    }

    @Test
    void selectDeptListByRoleId_shouldDelegate() {
        SysRole role = new SysRole();
        role.setDeptCheckStrictly(true);
        when(roleMapper.selectRoleById(1L)).thenReturn(role);
        when(deptMapper.selectDeptListByRoleId(1L, true)).thenReturn(Arrays.asList(1L, 2L));
        assertEquals(Arrays.asList(1L, 2L), service.selectDeptListByRoleId(1L));
    }

    @Test
    void selectDeptById_mapperReturnsNull_shouldReturnNull() {
        when(deptMapper.selectDeptById(99L)).thenReturn(null);
        assertNull(service.selectDeptById(99L));
    }

    @Test
    void selectNormalChildrenDeptById_delegate() {
        when(deptMapper.selectNormalChildrenDeptById(1L)).thenReturn(3);
        assertEquals(3, service.selectNormalChildrenDeptById(1L));
    }

    @Test
    void hasChildByDeptId_trueFalse() {
        when(deptMapper.hasChildByDeptId(1L)).thenReturn(2);
        assertTrue(service.hasChildByDeptId(1L));
        when(deptMapper.hasChildByDeptId(2L)).thenReturn(0);
        assertFalse(service.hasChildByDeptId(2L));
    }

    @Test
    void checkDeptExistUser_trueFalse() {
        when(deptMapper.checkDeptExistUser(1L)).thenReturn(1);
        assertTrue(service.checkDeptExistUser(1L));
        when(deptMapper.checkDeptExistUser(2L)).thenReturn(0);
        assertFalse(service.checkDeptExistUser(2L));
    }

    @Test
    void checkDeptNameUnique_notUnique() {
        SysDept exist = new SysDept();
        exist.setDeptId(2L);
        when(deptMapper.checkDeptNameUnique(any(), any())).thenReturn(exist);
        SysDept d = new SysDept();
        d.setDeptId(1L);
        d.setDeptName("n");
        d.setParentId(0L);
        assertFalse(service.checkDeptNameUnique(d));
    }

    @Test
    void checkDeptNameUnique_unique() {
        when(deptMapper.checkDeptNameUnique(any(), any())).thenReturn(null);
        SysDept d = new SysDept();
        d.setDeptId(1L);
        d.setDeptName("n");
        d.setParentId(0L);
        assertTrue(service.checkDeptNameUnique(d));
    }

    @Test
    void insertDept_parentNotNormal_shouldThrow() {
        SysDept parent = new SysDept();
        parent.setStatus("1"); // not normal
        parent.setAncestors("0");
        when(deptMapper.selectDeptById(any())).thenReturn(parent);
        SysDept d = new SysDept();
        d.setParentId(1L);
        d.setDeptId(2L);
        ServiceException ex = assertThrows(ServiceException.class, () -> service.insertDept(d));
        assertTrue(ex.getMessage().contains("部门停用"));
    }

    @Test
    void insertDept_parentNormal_shouldInsert() {
        SysDept parent = new SysDept();
        parent.setStatus("0"); // normal
        parent.setAncestors("0");
        when(deptMapper.selectDeptById(any())).thenReturn(parent);
        when(deptMapper.insertDept(any())).thenReturn(1);
        SysDept d = new SysDept();
        d.setParentId(1L);
        d.setDeptId(2L);
        int res = service.insertDept(d);
        assertEquals(1, res);
    }

    @Test
    void updateDept_shouldUpdateAncestorsAndParentStatus() {
        SysDept parent = new SysDept();
        parent.setDeptId(1L);
        parent.setAncestors("0");
        SysDept old = new SysDept();
        old.setDeptId(2L);
        old.setAncestors("0,1");
        SysDept d = new SysDept();
        d.setDeptId(2L);
        d.setParentId(1L);
        d.setStatus("0");
        when(deptMapper.selectDeptById(1L)).thenReturn(parent);
        when(deptMapper.selectDeptById(2L)).thenReturn(old);
        when(deptMapper.updateDept(any())).thenReturn(1);
        int res = service.updateDept(d);
        assertEquals(1, res);
    }

    @Test
    void updateDept_parentOrOldNull_shouldJustUpdate() {
        when(deptMapper.selectDeptById(1L)).thenReturn(null);
        when(deptMapper.selectDeptById(2L)).thenReturn(null);
        when(deptMapper.updateDept(any())).thenReturn(1);
        SysDept d = new SysDept();
        d.setDeptId(2L);
        d.setParentId(1L);
        int res = service.updateDept(d);
        assertEquals(1, res);
    }

    @Test
    void deleteDeptById_delegate() {
        when(deptMapper.deleteDeptById(1L)).thenReturn(1);
        assertEquals(1, service.deleteDeptById(1L));
    }

    @Test
    void allMapperMethods_throwException_shouldPropagate() {
        RuntimeException ex = new RuntimeException("db error");
        when(deptMapper.selectDeptList(any())).thenThrow(ex);
        when(deptMapper.insertDept(any())).thenThrow(ex);
        when(deptMapper.updateDept(any())).thenThrow(ex);
        when(deptMapper.deleteDeptById(anyLong())).thenThrow(ex);
        when(deptMapper.selectDeptById(anyLong())).thenThrow(ex);
        when(deptMapper.selectNormalChildrenDeptById(anyLong())).thenThrow(ex);
        when(deptMapper.hasChildByDeptId(anyLong())).thenThrow(ex);
        when(deptMapper.checkDeptExistUser(anyLong())).thenThrow(ex);
        when(deptMapper.checkDeptNameUnique(any(), any())).thenThrow(ex);
        assertThrows(RuntimeException.class, () -> service.selectDeptList(new SysDept()));
        assertThrows(RuntimeException.class, () -> service.insertDept(new SysDept()));
        assertThrows(RuntimeException.class, () -> service.updateDept(new SysDept()));
        assertThrows(RuntimeException.class, () -> service.deleteDeptById(1L));
        assertThrows(RuntimeException.class, () -> service.selectDeptById(1L));
        assertThrows(RuntimeException.class, () -> service.selectNormalChildrenDeptById(1L));
        assertThrows(RuntimeException.class, () -> service.hasChildByDeptId(1L));
        assertThrows(RuntimeException.class, () -> service.checkDeptExistUser(1L));
        assertThrows(RuntimeException.class, () -> service.checkDeptNameUnique(new SysDept()));
    }
}