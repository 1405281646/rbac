# RBAC0 权限管理系统 — 领域上下文

## 领域术语

| 术语 | 定义 |
|------|------|
| **User** | 系统用户，通过角色关联获取权限。包含审计字段（创建人/时间、更新人/时间、最后登录IP/时间） |
| **Role** | 角色，支持 parent_id 自引用无限级树形层级。系统内置角色不可删除，自定义角色可删除 |
| **Permission** | 权限，粒度分为两级：「菜单权限」（控制页面可见性）和「按钮/接口权限」（控制页面内操作），通过角色分配给用户 |
| **User–Role** | N:M — 通过 sys_user_role 关联表实现 |
| **Role–Permission** | N:M — 通过 sys_role_permission 关联表实现 |

## 第一期范围（当前实现）

仅实现认证 + 用户管理 + 角色管理 + 权限管理四个核心模块。公司管理、部门管理不在第一期范围内。

## 技术栈

- **后端**: Spring Boot 3.x + JDK 17 + MyBatis-Plus 3.5.x + MySQL 8.x + Redis 7.x
- **构建**: Maven 多模块（rbac-common / rbac-framework / rbac-system / rbac-api / rbac-generator）
- **前端**: Vue 3 + Vite + Element Plus + Pinia + Vue Router（标准 SPA，侧边栏布局，默认蓝色主题）
- **认证**: JWT (Access Token) + Redis 黑名单
- **验证码**: 可配置开关（配置项控制）
- **API**: RESTful 风格，统一响应体 `{code, msg, data}`

## 架构决策

| 决策 | 选择 | 说明 |
|------|------|------|
| 角色树形实现 | parent_id 自引用 | 通过 parent_id 字段指向父角色，递归构建树 |
| 权限粒度 | 菜单 + 按钮/接口两级 | type: 0=目录, 1=菜单, 2=按钮 |
| 实现策略 | 按模块前后端交替 | 每个模块先完成后端 API+测试，再完成前端页面 |
| 编码规范 | 阿里巴巴 Java 开发手册 | 涵盖编程规约、设计规约、异常日志、MySQL、工程结构、安全规约、单元测试 |
| 前端设计 | Element Plus 最佳实践 | 组件化开发，侧边栏导航布局 |