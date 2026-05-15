# RBAC0 权限管理系统 - 设计规格

## 概述

基于 Spring Boot 3.x + Vue 3 构建 RBAC0 权限管理系统，实现用户管理、角色管理、权限管理、认证登录四大核心模块。

## 技术栈

- **后端**: Spring Boot 3.x + JDK 17 + MyBatis-Plus 3.5.x + MySQL 8.x + Redis 7.x
- **前端**: Vue 3 + Vite + Element Plus + Pinia + Vue Router
- **认证**: JWT (Access Token) + Redis 黑名单
- **验证码**: 可配置开关（配置项控制）
- **构建**: Maven 多模块 + npm/pnpm

## 架构

```
┌─────────────────────────────────────────────────┐
│                  前端 (Vue 3 SPA)                 │
│  Vue 3 + Vite + Element Plus + Pinia + Vue Router│
└──────────────────┬──────────────────────────────┘
                   │ REST API (JWT Token)
┌──────────────────▼──────────────────────────────┐
│              后端 (Spring Boot 3.x)              │
│  Maven 多模块 ── Controller → Service → Mapper   │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│             MySQL 8.x + Redis 7.x               │
└─────────────────────────────────────────────────┘
```

## Maven 模块结构

```
rbac-parent (父 POM)
├── rbac-common         ─ 通用工具类、常量、统一异常、统一响应体
├── rbac-framework      ─ 安全配置、JWT 过滤器、Redis 操作、验证码
├── rbac-system         ─ 业务逻辑层（User/Role/Permission Service + Mapper）
├── rbac-api            ─ REST Controller 层
└── rbac-generator      ─ MyBatis-Plus 代码生成器（辅助工具）
```

依赖链: `rbac-api → rbac-system → rbac-framework → rbac-common`

## 数据库设计

### sys_user（用户表）

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, AUTO_INCREMENT | 主键 |
| username | varchar(50) | UNIQUE, NOT NULL | 登录用户名 |
| password | varchar(100) | NOT NULL | BCrypt 加密密码 |
| real_name | varchar(50) | | 真实姓名 |
| email | varchar(100) | | 邮箱 |
| phone | varchar(20) | | 手机号 |
| avatar | varchar(200) | | 头像URL |
| status | tinyint | DEFAULT 1 | 0=禁用，1=正常 |
| dept_id | bigint | | 所属部门ID |
| create_by | varchar(50) | | 创建人 |
| create_time | datetime | | 创建时间 |
| update_by | varchar(50) | | 更新人 |
| update_time | datetime | | 更新时间 |
| last_login_ip | varchar(50) | | 最后登录IP |
| last_login_time | datetime | | 最后登录时间 |

### sys_role（角色表）

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, AUTO_INCREMENT | 主键 |
| name | varchar(50) | NOT NULL | 角色名称 |
| code | varchar(50) | UNIQUE | 角色编码 |
| parent_id | bigint | DEFAULT 0 | 父角色ID |
| sort | int | DEFAULT 0 | 排序号 |
| status | tinyint | DEFAULT 1 | 0=禁用，1=正常 |
| built_in | tinyint | DEFAULT 0 | 0=自定义，1=系统内置 |
| remark | varchar(255) | | 备注 |
| create_by | varchar(50) | | 创建人 |
| create_time | datetime | | 创建时间 |
| update_by | varchar(50) | | 更新人 |
| update_time | datetime | | 更新时间 |

### sys_permission（权限表）

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, AUTO_INCREMENT | 主键 |
| name | varchar(50) | NOT NULL | 权限名称 |
| type | tinyint | NOT NULL | 0=目录，1=菜单，2=按钮 |
| parent_id | bigint | DEFAULT 0 | 父权限ID |
| path | varchar(200) | | 路由路径（菜单） |
| component | varchar(200) | | 组件路径（菜单） |
| perms | varchar(100) | | 权限标识 |
| icon | varchar(50) | | 图标 |
| sort | int | DEFAULT 0 | 排序号 |
| visible | tinyint | DEFAULT 1 | 0=隐藏，1=显示 |
| create_by | varchar(50) | | 创建人 |
| create_time | datetime | | 创建时间 |
| update_by | varchar(50) | | 更新人 |
| update_time | datetime | | 更新时间 |

### sys_user_role（用户-角色关联）

| 字段 | 类型 | 约束 |
|------|------|------|
| user_id | bigint | PK, FK → sys_user.id |
| role_id | bigint | PK, FK → sys_role.id |

### sys_role_permission（角色-权限关联）

| 字段 | 类型 | 约束 |
|------|------|------|
| role_id | bigint | PK, FK → sys_role.id |
| permission_id | bigint | PK, FK → sys_permission.id |

## API 接口设计

统一响应格式:
```json
{
  "code": 200,
  "msg": "success",
  "data": { ... }
}
```

分页响应格式:
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "records": [...],
    "total": 100,
    "page": 1,
    "size": 10
  }
}
```

### 认证模块

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/auth/captcha | 获取验证码 |
| POST | /api/auth/login | 登录 |
| POST | /api/auth/logout | 登出 |
| GET | /api/auth/info | 获取当前用户基本信息 |
| GET | /api/auth/permissions | 获取当前用户权限（菜单+按钮标识） |

### 用户管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/system/user/page | 用户分页列表 |
| GET | /api/system/user/{id} | 用户详情 |
| POST | /api/system/user | 新增用户 |
| PUT | /api/system/user/{id} | 修改用户 |
| DELETE | /api/system/user/{id} | 删除用户 |
| PUT | /api/system/user/{id}/status | 修改用户状态 |
| GET | /api/system/user/{id}/roles | 获取用户角色 |
| PUT | /api/system/user/{id}/roles | 分配用户角色 |

### 角色管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/system/role/tree | 角色树形列表 |
| GET | /api/system/role/page | 角色分页列表 |
| GET | /api/system/role/{id} | 角色详情 |
| POST | /api/system/role | 新增角色 |
| PUT | /api/system/role/{id} | 修改角色 |
| DELETE | /api/system/role/{id} | 删除角色 |
| PUT | /api/system/role/{id}/status | 修改角色状态 |
| GET | /api/system/role/{id}/permissions | 获取角色权限 |
| PUT | /api/system/role/{id}/permissions | 分配角色权限 |

### 权限管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/system/permission/tree | 权限树形列表 |
| GET | /api/system/permission/{id} | 权限详情 |
| POST | /api/system/permission | 新增权限 |
| PUT | /api/system/permission/{id} | 修改权限 |
| DELETE | /api/system/permission/{id} | 删除权限 |

## 前端页面结构

```
src/
├── api/                    # API 请求封装
│   ├── auth.js             # 认证接口
│   ├── user.js             # 用户管理接口
│   ├── role.js             # 角色管理接口
│   └── permission.js       # 权限管理接口
├── assets/                 # 静态资源
├── components/             # 公共组件
│   ├── Sidebar.vue         # 侧边栏菜单
│   ├── Navbar.vue          # 顶部导航栏
│   └── TreeTable.vue       # 树形表格组件
├── layouts/                # 布局
│   └── MainLayout.vue      # 主布局（侧边栏 + 内容区）
├── router/                 # 路由配置
│   └── index.js
├── stores/                 # Pinia 状态管理
│   ├── auth.js             # 认证状态
│   └── permission.js       # 权限状态
├── views/                  # 页面
│   ├── login/
│   │   └── Login.vue       # 登录页
│   ├── dashboard/
│   │   └── Dashboard.vue   # 首页仪表盘
│   └── system/
│       ├── user/
│       │   ├── UserList.vue        # 用户列表
│       │   └── UserForm.vue        # 用户表单
│       ├── role/
│       │   ├── RoleList.vue        # 角色列表
│       │   ├── RoleForm.vue        # 角色表单
│       │   └── RolePermission.vue  # 角色分配权限
│       └── permission/
│           └── PermissionTree.vue  # 权限树管理
├── utils/                  # 工具类
│   ├── request.js          # Axios 封装
│   └── auth.js             # 权限判断工具
├── App.vue
└── main.js
```

### 路由规划

| 路径 | 页面 | 权限标识 | 访问控制 |
|------|------|----------|----------|
| /login | 登录页 | - | 公开 |
| /dashboard | 仪表盘 | - | 登录即可 |
| /system/user | 用户列表 | sys:user:list | 权限控制 |
| /system/role | 角色列表 | sys:role:list | 权限控制 |
| /system/permission | 权限管理 | sys:permission:list | 权限控制 |

### 布局方案

侧边栏导航：左侧菜单栏 + 右侧内容区，Element Plus 默认蓝色主题。

## 实现策略

按模块前后端交替实现，每个模块先完成后端 API + 测试，再完成前端页面。

### 实现顺序

1. **项目骨架搭建** — Maven 父 POM、各模块初始化、前端项目初始化
2. **认证模块** — 后端（JWT + Redis + 验证码 + 登录/登出 API）→ 前端（登录页）
3. **用户管理模块** — 后端（用户 CRUD API）→ 前端（用户列表 + 表单页）
4. **角色管理模块** — 后端（角色 CRUD + 树形 API）→ 前端（角色列表 + 表单 + 分配权限）
5. **权限管理模块** — 后端（权限树形 CRUD API）→ 前端（权限树管理页）

## 编码规范

- **后端**: 遵循阿里巴巴 Java 开发手册（编程规约、设计规约、异常日志、MySQL 数据库、工程结构、安全规约）
- **测试**: 遵循阿里巴巴单元测试规约，使用 TDD 方式
- **前端**: Element Plus 最佳实践，组件化开发