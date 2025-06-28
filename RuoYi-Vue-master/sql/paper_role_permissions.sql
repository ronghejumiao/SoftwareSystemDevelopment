-- 试卷权限SQL
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('试卷', '2000', '1', 'paper', 'system/paper/index', 1, 0, 'C', '0', '0', 'system:paper:list', '#', 'admin', NOW(), '', null, '试卷菜单');

-- 获取插入的菜单ID
SET @parentId = LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('试卷查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:paper:query',        '#', 'admin', NOW(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('试卷新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:paper:add',          '#', 'admin', NOW(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('试卷修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:paper:edit',         '#', 'admin', NOW(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('试卷删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:paper:remove',       '#', 'admin', NOW(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('试卷导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:paper:export',       '#', 'admin', NOW(), '', null, '');

-- 为学生角色分配查询权限
INSERT INTO sys_role_menu (role_id, menu_id) 
SELECT 3, menu_id FROM sys_menu WHERE perms = 'system:paper:query' OR perms = 'system:paper:list';

-- 为教师角色分配所有权限
INSERT INTO sys_role_menu (role_id, menu_id) 
SELECT 2, menu_id FROM sys_menu WHERE perms LIKE 'system:paper:%';

-- 为管理员角色分配所有权限
INSERT INTO sys_role_menu (role_id, menu_id) 
SELECT 1, menu_id FROM sys_menu WHERE perms LIKE 'system:paper:%'; 