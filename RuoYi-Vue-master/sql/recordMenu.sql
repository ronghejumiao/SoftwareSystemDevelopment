-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('视频学习记录，记录学生观看视频的行为数据', '3', '1', 'record', 'system/record/index', 1, 0, 'C', '0', '0', 'system:record:list', '#', 'admin', sysdate(), '', null, '视频学习记录，记录学生观看视频的行为数据菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('视频学习记录，记录学生观看视频的行为数据查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:record:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('视频学习记录，记录学生观看视频的行为数据新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:record:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('视频学习记录，记录学生观看视频的行为数据修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:record:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('视频学习记录，记录学生观看视频的行为数据删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:record:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('视频学习记录，记录学生观看视频的行为数据导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:record:export',       '#', 'admin', sysdate(), '', null, '');