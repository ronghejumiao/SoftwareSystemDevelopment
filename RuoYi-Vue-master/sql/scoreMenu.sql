-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成绩，记录学生的学习成绩信息', '3', '1', 'score', 'system/score/index', 1, 0, 'C', '0', '0', 'system:score:list', '#', 'admin', sysdate(), '', null, '成绩，记录学生的学习成绩信息菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成绩，记录学生的学习成绩信息查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:score:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成绩，记录学生的学习成绩信息新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:score:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成绩，记录学生的学习成绩信息修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:score:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成绩，记录学生的学习成绩信息删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:score:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('成绩，记录学生的学习成绩信息导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:score:export',       '#', 'admin', sysdate(), '', null, '');