-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('学生能力，基于课程能力要求构建', '3', '1', 'skill', 'system/skill/index', 1, 0, 'C', '0', '0', 'system:skill:list', '#', 'admin', sysdate(), '', null, '学生能力，基于课程能力要求构建菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('学生能力，基于课程能力要求构建查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:skill:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('学生能力，基于课程能力要求构建新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:skill:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('学生能力，基于课程能力要求构建修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:skill:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('学生能力，基于课程能力要求构建删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:skill:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('学生能力，基于课程能力要求构建导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:skill:export',       '#', 'admin', sysdate(), '', null, '');