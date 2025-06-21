-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('知识图谱节点，存储课程的知识图谱结构', '3', '1', 'node', 'system/node/index', 1, 0, 'C', '0', '0', 'system:node:list', '#', 'admin', sysdate(), '', null, '知识图谱节点，存储课程的知识图谱结构菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('知识图谱节点，存储课程的知识图谱结构查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:node:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('知识图谱节点，存储课程的知识图谱结构新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:node:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('知识图谱节点，存储课程的知识图谱结构修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:node:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('知识图谱节点，存储课程的知识图谱结构删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:node:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('知识图谱节点，存储课程的知识图谱结构导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:node:export',       '#', 'admin', sysdate(), '', null, '');