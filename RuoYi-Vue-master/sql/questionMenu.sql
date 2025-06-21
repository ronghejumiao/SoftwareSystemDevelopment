-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('题目，存储题库中的题目信息', '3', '1', 'question', 'system/question/index', 1, 0, 'C', '0', '0', 'system:question:list', '#', 'admin', sysdate(), '', null, '题目，存储题库中的题目信息菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('题目，存储题库中的题目信息查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:question:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('题目，存储题库中的题目信息新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:question:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('题目，存储题库中的题目信息修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:question:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('题目，存储题库中的题目信息删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:question:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('题目，存储题库中的题目信息导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:question:export',       '#', 'admin', sysdate(), '', null, '');