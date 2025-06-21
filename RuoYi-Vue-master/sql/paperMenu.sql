-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('试卷，一个试卷库包含多个试卷', '3', '1', 'paper', 'system/paper/index', 1, 0, 'C', '0', '0', 'system:paper:list', '#', 'admin', sysdate(), '', null, '试卷，一个试卷库包含多个试卷菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('试卷，一个试卷库包含多个试卷查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:paper:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('试卷，一个试卷库包含多个试卷新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:paper:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('试卷，一个试卷库包含多个试卷修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:paper:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('试卷，一个试卷库包含多个试卷删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:paper:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('试卷，一个试卷库包含多个试卷导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:paper:export',       '#', 'admin', sysdate(), '', null, '');