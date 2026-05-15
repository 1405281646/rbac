CREATE DATABASE IF NOT EXISTS rbac DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE rbac;

CREATE TABLE sys_user (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    username varchar(50) NOT NULL UNIQUE,
    password varchar(100) NOT NULL,
    real_name varchar(50) DEFAULT NULL,
    email varchar(100) DEFAULT NULL,
    phone varchar(20) DEFAULT NULL,
    avatar varchar(200) DEFAULT NULL,
    status tinyint DEFAULT 1 COMMENT '0=禁用 1=正常',
    dept_id bigint DEFAULT NULL,
    create_by varchar(50) DEFAULT NULL,
    create_time datetime DEFAULT CURRENT_TIMESTAMP,
    update_by varchar(50) DEFAULT NULL,
    update_time datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_ip varchar(50) DEFAULT NULL,
    last_login_time datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE sys_role (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    name varchar(50) NOT NULL,
    code varchar(50) UNIQUE,
    parent_id bigint DEFAULT 0,
    sort int DEFAULT 0,
    status tinyint DEFAULT 1 COMMENT '0=禁用 1=正常',
    built_in tinyint DEFAULT 0 COMMENT '0=自定义 1=系统内置',
    remark varchar(255) DEFAULT NULL,
    create_by varchar(50) DEFAULT NULL,
    create_time datetime DEFAULT CURRENT_TIMESTAMP,
    update_by varchar(50) DEFAULT NULL,
    update_time datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

CREATE TABLE sys_permission (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    name varchar(50) NOT NULL,
    type tinyint NOT NULL COMMENT '0=目录 1=菜单 2=按钮',
    parent_id bigint DEFAULT 0,
    path varchar(200) DEFAULT NULL,
    component varchar(200) DEFAULT NULL,
    perms varchar(100) DEFAULT NULL,
    icon varchar(50) DEFAULT NULL,
    sort int DEFAULT 0,
    visible tinyint DEFAULT 1 COMMENT '0=隐藏 1=显示',
    create_by varchar(50) DEFAULT NULL,
    create_time datetime DEFAULT CURRENT_TIMESTAMP,
    update_by varchar(50) DEFAULT NULL,
    update_time datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

CREATE TABLE sys_user_role (
    user_id bigint NOT NULL,
    role_id bigint NOT NULL,
    PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

CREATE TABLE sys_role_permission (
    role_id bigint NOT NULL,
    permission_id bigint NOT NULL,
    PRIMARY KEY (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 初始化管理员用户 (密码: admin123)
INSERT INTO sys_user (username, password, real_name, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 1);

-- 初始化超级管理员角色
INSERT INTO sys_role (name, code, sort, built_in, remark) VALUES
('超级管理员', 'admin', 1, 1, '系统超级管理员，拥有所有权限');

-- 初始化权限数据
INSERT INTO sys_permission (name, type, parent_id, path, component, perms, icon, sort) VALUES
('系统管理', 0, 0, '/system', '', '', 'Setting', 1),
('用户管理', 1, 1, '/system/user', 'system/user/UserList', 'sys:user:list', 'User', 1),
('新增用户', 2, 2, '', '', 'sys:user:add', '', 1),
('修改用户', 2, 2, '', '', 'sys:user:edit', '', 2),
('删除用户', 2, 2, '', '', 'sys:user:delete', '', 3),
('角色管理', 1, 1, '/system/role', 'system/role/RoleList', 'sys:role:list', 'Role', 2),
('新增角色', 2, 6, '', '', 'sys:role:add', '', 1),
('修改角色', 2, 6, '', '', 'sys:role:edit', '', 2),
('删除角色', 2, 6, '', '', 'sys:role:delete', '', 3),
('权限管理', 1, 1, '/system/permission', 'system/permission/PermissionTree', 'sys:permission:list', 'Lock', 3);

-- 关联管理员角色
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- 关联角色拥有所有权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission;