-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程概念图，存储课程的概念图URL（1对多关系）', '3', '1', 'img', 'system/img/index', 1, 0, 'C', '0', '0', 'system:img:list', '#', 'admin', sysdate(), '', null, '课程概念图，存储课程的概念图URL（1对多关系）菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程概念图，存储课程的概念图URL（1对多关系）查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:img:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程概念图，存储课程的概念图URL（1对多关系）新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:img:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程概念图，存储课程的概念图URL（1对多关系）修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:img:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程概念图，存储课程的概念图URL（1对多关系）删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:img:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('课程概念图，存储课程的概念图URL（1对多关系）导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:img:export',       '#', 'admin', sysdate(), '', null, '');