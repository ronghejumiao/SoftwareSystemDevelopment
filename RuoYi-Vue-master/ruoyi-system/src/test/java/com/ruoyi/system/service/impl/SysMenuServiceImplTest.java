package com.ruoyi.system.service.impl;

import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.core.domain.entity.SysMenu;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.domain.vo.MetaVo;
import com.ruoyi.system.domain.vo.RouterVo;
import com.ruoyi.system.mapper.SysMenuMapper;
import com.ruoyi.system.mapper.SysRoleMapper;
import com.ruoyi.system.mapper.SysRoleMenuMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SysMenuServiceImplTest {

    @Mock private SysMenuMapper menuMapper;
    @Mock private SysRoleMapper roleMapper;
    @Mock private SysRoleMenuMapper roleMenuMapper;
    @InjectMocks private SysMenuServiceImpl service;

    private SysMenu menu;

    @BeforeEach
    void setUp() {
        menu = new SysMenu();
        menu.setMenuId(1L);
        menu.setParentId(0L);
        menu.setMenuName("菜单A");
        menu.setMenuType("C");
        menu.setIsFrame("1");
        menu.setPath("test");
        menu.setComponent("Comp");
        menu.setVisible("0");
        menu.setIsCache("1");
    }



    @Test
    void selectMenuPermsByUserId_andByRoleId() {
        when(menuMapper.selectMenuPermsByUserId(1L)).thenReturn(Arrays.asList("a,b", "c"));
        Set<String> perms = service.selectMenuPermsByUserId(1L);
        assertTrue(perms.contains("a") && perms.contains("b") && perms.contains("c"));

        when(menuMapper.selectMenuPermsByRoleId(2L)).thenReturn(Arrays.asList("x,y", "z"));
        Set<String> perms2 = service.selectMenuPermsByRoleId(2L);
        assertTrue(perms2.contains("x") && perms2.contains("y") && perms2.contains("z"));
    }


    @Test
    void selectMenuListByRoleId_shouldDelegate() {
        SysRole role = new SysRole();
        role.setMenuCheckStrictly(true);
        when(roleMapper.selectRoleById(1L)).thenReturn(role);
        when(menuMapper.selectMenuListByRoleId(1L, true)).thenReturn(Arrays.asList(1L, 2L));
        assertEquals(Arrays.asList(1L, 2L), service.selectMenuListByRoleId(1L));
    }

    @Test
    void buildMenus_variousTypes() {
        SysMenu dir = new SysMenu();
        dir.setMenuId(1L); dir.setParentId(0L); dir.setMenuType(UserConstants.TYPE_DIR); dir.setVisible("1"); dir.setIsCache("1"); dir.setPath("dir");
        dir.setMenuName("目录"); dir.setIcon("icon"); dir.setChildren(Collections.singletonList(menu));
        SysMenu frame = new SysMenu();
        frame.setMenuId(2L); frame.setParentId(0L); frame.setMenuType(UserConstants.TYPE_MENU); frame.setIsFrame(UserConstants.NO_FRAME); frame.setPath("frame"); frame.setMenuName("菜单"); frame.setIcon("icon");
        SysMenu inner = new SysMenu();
        inner.setMenuId(3L); inner.setParentId(0L); inner.setMenuType(UserConstants.TYPE_MENU); inner.setIsFrame(UserConstants.NO_FRAME); inner.setPath("http://test.com"); inner.setMenuName("内链"); inner.setIcon("icon");
        List<SysMenu> menus = Arrays.asList(dir, frame, inner, menu);
        List<RouterVo> routers = service.buildMenus(menus);
        assertFalse(routers.isEmpty());
    }

    @Test
    void buildMenuTree_and_buildMenuTreeSelect() {
        SysMenu d1 = new SysMenu(); d1.setMenuId(1L); d1.setParentId(0L);
        SysMenu d2 = new SysMenu(); d2.setMenuId(2L); d2.setParentId(1L);
        List<SysMenu> list = Arrays.asList(d1, d2);
        List<SysMenu> tree = service.buildMenuTree(list);
        assertEquals(1, tree.size());
        List<TreeSelect> treeSelect = service.buildMenuTreeSelect(list);
        assertEquals(1, treeSelect.size());
    }

    @Test
    void selectMenuById_shouldDelegate() {
        when(menuMapper.selectMenuById(1L)).thenReturn(menu);
        assertEquals(menu, service.selectMenuById(1L));
    }

    @Test
    void hasChildByMenuId_trueFalse() {
        when(menuMapper.hasChildByMenuId(1L)).thenReturn(2);
        assertTrue(service.hasChildByMenuId(1L));
        when(menuMapper.hasChildByMenuId(2L)).thenReturn(0);
        assertFalse(service.hasChildByMenuId(2L));
    }

    @Test
    void checkMenuExistRole_trueFalse() {
        when(roleMenuMapper.checkMenuExistRole(1L)).thenReturn(1);
        assertTrue(service.checkMenuExistRole(1L));
        when(roleMenuMapper.checkMenuExistRole(2L)).thenReturn(0);
        assertFalse(service.checkMenuExistRole(2L));
    }

    @Test
    void insertUpdateDelete_delegate() {
        when(menuMapper.insertMenu(menu)).thenReturn(1);
        when(menuMapper.updateMenu(menu)).thenReturn(1);
        when(menuMapper.deleteMenuById(1L)).thenReturn(1);
        assertEquals(1, service.insertMenu(menu));
        assertEquals(1, service.updateMenu(menu));
        assertEquals(1, service.deleteMenuById(1L));
    }

    @Test
    void checkMenuNameUnique_notUnique() {
        SysMenu exist = new SysMenu();
        exist.setMenuId(2L);
        when(menuMapper.checkMenuNameUnique(any(), any())).thenReturn(exist);
        SysMenu m = new SysMenu();
        m.setMenuId(1L);
        m.setMenuName("n");
        m.setParentId(0L);
        assertFalse(service.checkMenuNameUnique(m));
    }

    @Test
    void checkMenuNameUnique_unique() {
        when(menuMapper.checkMenuNameUnique(any(), any())).thenReturn(null);
        SysMenu m = new SysMenu();
        m.setMenuId(1L);
        m.setMenuName("n");
        m.setParentId(0L);
        assertTrue(service.checkMenuNameUnique(m));
    }

    @Test
    void getRouteName_and_getRouterPath_and_getComponent() {
        SysMenu m = new SysMenu();
        m.setMenuId(1L); m.setParentId(0L); m.setMenuType(UserConstants.TYPE_MENU); m.setIsFrame(UserConstants.NO_FRAME); m.setPath("test");
        assertEquals("", service.getRouteName(m));
        assertEquals("/", service.getRouterPath(m));
        m.setMenuType(UserConstants.TYPE_DIR); m.setIsFrame("1");
        assertEquals("/test", service.getRouterPath(m));
        m.setParentId(1L); m.setMenuType(UserConstants.TYPE_DIR);
        assertEquals(UserConstants.PARENT_VIEW, service.getComponent(m));
    }

    @Test
    void isMenuFrame_isInnerLink_isParentView() {
        SysMenu m = new SysMenu();
        m.setMenuId(1L); m.setParentId(0L); m.setMenuType(UserConstants.TYPE_MENU); m.setIsFrame(UserConstants.NO_FRAME); m.setPath("http://test.com");
        assertTrue(service.isMenuFrame(m));
        assertTrue(service.isInnerLink(m));
        m.setParentId(1L); m.setMenuType(UserConstants.TYPE_DIR);
        assertTrue(service.isParentView(m));
    }

    @Test
    void innerLinkReplaceEach_shouldReplace() {
        String url = "https://www.test.com:8080";
        String replaced = service.innerLinkReplaceEach(url);

    }


}