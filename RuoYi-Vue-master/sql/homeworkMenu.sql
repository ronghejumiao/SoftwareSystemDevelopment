-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程作业', '3', '1', 'homework', 'system/homework/index', 1, 0, 'C', '0', '0', 'system:homework:list', '#', 'admin', sysdate(), '', null, '课程作业菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程作业查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:homework:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程作业新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:homework:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程作业修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:homework:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程作业删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:homework:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程作业导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:homework:export',       '#', 'admin', sysdate(), '', null, '');