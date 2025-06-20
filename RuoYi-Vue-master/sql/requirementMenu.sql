-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程能力要求，一个课程包含多个能力要求', '3', '1', 'requirement', 'system/requirement/index', 1, 0, 'C', '0', '0', 'system:requirement:list', '#', 'admin', sysdate(), '', null, '课程能力要求，一个课程包含多个能力要求菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程能力要求，一个课程包含多个能力要求查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:requirement:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程能力要求，一个课程包含多个能力要求新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:requirement:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程能力要求，一个课程包含多个能力要求修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:requirement:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程能力要求，一个课程包含多个能力要求删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:requirement:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程能力要求，一个课程包含多个能力要求导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:requirement:export',       '#', 'admin', sysdate(), '', null, '');