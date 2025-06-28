-- 查询成绩管理菜单ID
SELECT @menuId := menu_id FROM sys_menu WHERE perms = 'system:score:list';

-- 查询角色ID
SELECT @studentRoleId := role_id FROM sys_role WHERE role_key = 'student';
SELECT @teacherRoleId := role_id FROM sys_role WHERE role_key = 'teacher';

-- 为学生角色添加查看成绩列表权限
INSERT INTO sys_role_menu (role_id, menu_id) 
SELECT @studentRoleId, @menuId
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM sys_role_menu WHERE role_id = @studentRoleId AND menu_id = @menuId
);

-- 查询查询权限菜单ID
SELECT @queryMenuId := menu_id FROM sys_menu WHERE perms = 'system:score:query' AND parent_id = @menuId;

-- 为学生角色添加查询成绩详情权限
INSERT INTO sys_role_menu (role_id, menu_id) 
SELECT @studentRoleId, @queryMenuId
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM sys_role_menu WHERE role_id = @studentRoleId AND menu_id = @queryMenuId
);

-- 为教师角色添加所有成绩相关权限
INSERT INTO sys_role_menu (role_id, menu_id) 
SELECT @teacherRoleId, menu_id
FROM sys_menu 
WHERE parent_id = @menuId
AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu WHERE role_id = @teacherRoleId AND menu_id = sys_menu.menu_id
);

-- 为教师角色添加成绩菜单权限
INSERT INTO sys_role_menu (role_id, menu_id) 
SELECT @teacherRoleId, @menuId
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM sys_role_menu WHERE role_id = @teacherRoleId AND menu_id = @menuId
); 