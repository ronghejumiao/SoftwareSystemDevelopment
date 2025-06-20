-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程试卷库，一个课程对应一个试卷库', '3', '1', 'library', 'system/library/index', 1, 0, 'C', '0', '0', 'system:library:list', '#', 'admin', sysdate(), '', null, '课程试卷库，一个课程对应一个试卷库菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程试卷库，一个课程对应一个试卷库查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:library:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程试卷库，一个课程对应一个试卷库新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:library:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程试卷库，一个课程对应一个试卷库修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:library:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程试卷库，一个课程对应一个试卷库删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:library:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程试卷库，一个课程对应一个试卷库导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:library:export',       '#', 'admin', sysdate(), '', null, '');