-- 学习任务权限SQL
-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('学习任务', '2000', '1', 'task', 'system/task/index', 1, 0, 'C', '0', '0', 'system:task:list', '#', 'admin', sysdate(), '', null, '学习任务菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('学习任务查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:task:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('学习任务新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:task:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('学习任务修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:task:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('学习任务删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:task:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('学习任务导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:task:export',       '#', 'admin', sysdate(), '', null, '');

-- 角色权限分配
-- 为学生角色(role_id=100)分配查询权限
INSERT INTO sys_role_menu(role_id, menu_id) 
SELECT 100, menu_id FROM sys_menu WHERE perms = 'system:task:list' OR perms = 'system:task:query';

-- 为教师角色(role_id=101)分配所有权限
INSERT INTO sys_role_menu(role_id, menu_id) 
SELECT 101, menu_id FROM sys_menu WHERE perms LIKE 'system:task:%';

-- 为管理员角色(role_id=1)分配所有权限
INSERT INTO sys_role_menu(role_id, menu_id) 
SELECT 1, menu_id FROM sys_menu WHERE perms LIKE 'system:task:%'; 