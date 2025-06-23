-- 启用用户注册功能
UPDATE sys_config SET config_value = 'true' WHERE config_key = 'sys.account.registerUser';

-- 创建教师角色
INSERT INTO sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, remark) 
VALUES (3, '教师', 'teacher', 3, '1', 1, 1, '0', '0', 'admin', NOW(), '教师角色');

-- 创建学生角色
INSERT INTO sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, remark) 
VALUES (4, '学生', 'student', 4, '1', 1, 1, '0', '0', 'admin', NOW(), '学生角色'); 