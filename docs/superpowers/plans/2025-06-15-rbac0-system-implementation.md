# RBAC0 权限管理系统实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 构建基于 Spring Boot 3.x + Vue 3 的 RBAC0 权限管理系统，包含认证、用户管理、角色管理、权限管理四大核心模块。

**Architecture:** Maven 多模块后端（rbac-common / rbac-framework / rbac-system / rbac-api）+ Vue 3 SPA 前端，JWT + Redis 黑名单认证，RESTful API 统一响应。

**Tech Stack:** Spring Boot 3.x + JDK 17 + MyBatis-Plus 3.5.x + MySQL 8.x + Redis 7.x + Vue 3 + Vite + Element Plus + Pinia + Vue Router

---

### Task 1: 后端项目骨架搭建

**Files:**
- Create: `rbac-parent/pom.xml`
- Create: `rbac-common/pom.xml`
- Create: `rbac-framework/pom.xml`
- Create: `rbac-system/pom.xml`
- Create: `rbac-api/pom.xml`
- Create: `rbac-generator/pom.xml`
- Create: `rbac-api/src/main/java/com/rbac/RbacApplication.java`
- Create: `rbac-api/src/main/resources/application.yml`
- Create: `rbac-api/src/main/resources/application-dev.yml`
- Create: `rbac-api/src/main/resources/logback-spring.xml`

- [ ] **Step 1: 创建父 POM**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.rbac</groupId>
    <artifactId>rbac-parent</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>

    <name>RBAC0 Permission System</name>

    <modules>
        <module>rbac-common</module>
        <module>rbac-framework</module>
        <module>rbac-system</module>
        <module>rbac-api</module>
        <module>rbac-generator</module>
    </modules>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
        <relativePath/>
    </parent>

    <properties>
        <java.version>17</java.version>
        <mybatis-plus.version>3.5.7</mybatis-plus.version>
        <jjwt.version>0.12.5</jjwt.version>
        <hutool.version>5.8.28</hutool.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>com.baomidou</groupId>
                <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
                <version>${mybatis-plus.version}</version>
            </dependency>
            <dependency>
                <groupId>com.baomidou</groupId>
                <artifactId>mybatis-plus-jsqlparser</artifactId>
                <version>${mybatis-plus.version}</version>
            </dependency>
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-api</artifactId>
                <version>${jjwt.version}</version>
            </dependency>
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-impl</artifactId>
                <version>${jjwt.version}</version>
                <scope>runtime</scope>
            </dependency>
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-jackson</artifactId>
                <version>${jjwt.version}</version>
                <scope>runtime</scope>
            </dependency>
            <dependency>
                <groupId>cn.hutool</groupId>
                <artifactId>hutool-all</artifactId>
                <version>${hutool.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: 创建 rbac-common 模块**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.rbac</groupId>
        <artifactId>rbac-parent</artifactId>
        <version>1.0.0</version>
    </parent>
    <artifactId>rbac-common</artifactId>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 3: 创建统一响应体类**

Create: `rbac-common/src/main/java/com/rbac/common/response/Result.java`

```java
package com.rbac.common.response;

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    private Result() {}

    private Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    public static <T> Result<T> success() {
        return new Result<>(200, "success", null);
    }

    public static <T> Result<T> error(int code, String msg) {
        return new Result<>(code, msg, null);
    }

    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }
}
```

- [ ] **Step 4: 创建分页响应体**

Create: `rbac-common/src/main/java/com/rbac/common/response/PageResult.java`

```java
package com.rbac.common.response;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private List<T> records;
    private long total;
    private int page;
    private int size;

    public PageResult(List<T> records, long total, int page, int size) {
        this.records = records;
        this.total = total;
        this.page = page;
        this.size = size;
    }

    public static <T> PageResult<T> of(List<T> records, long total, int page, int size) {
        return new PageResult<>(records, total, page, size);
    }
}
```

- [ ] **Step 5: 创建全局异常处理**

Create: `rbac-common/src/main/java/com/rbac/common/exception/BadRequestException.java`

```java
package com.rbac.common.exception;

public class BadRequestException extends RuntimeException {
    private final int code;

    public BadRequestException(String message) {
        super(message);
        this.code = 400;
    }

    public BadRequestException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
```

Create: `rbac-common/src/main/java/com/rbac/common/exception/GlobalExceptionHandler.java`

```java
package com.rbac.common.exception;

import com.rbac.common.response.Result;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBadRequest(BadRequestException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        return Result.error("服务器内部错误: " + e.getMessage());
    }
}
```

- [ ] **Step 6: 创建通用常量类**

Create: `rbac-common/src/main/java/com/rbac/common/constant/SystemConstants.java`

```java
package com.rbac.common.constant;

public interface SystemConstants {
    String TOKEN_HEADER = "Authorization";
    String TOKEN_PREFIX = "Bearer ";
    String REDIS_BLACKLIST_PREFIX = "blacklist:";
    String CAPTCHA_PREFIX = "captcha:";
    long CAPTCHA_EXPIRE_SECONDS = 300;
    long TOKEN_EXPIRE_SECONDS = 86400;
    String ADMIN_ROLE_CODE = "admin";
}
```

- [ ] **Step 7: 创建 rbac-framework 模块**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.rbac</groupId>
        <artifactId>rbac-parent</artifactId>
        <version>1.0.0</version>
    </parent>
    <artifactId>rbac-framework</artifactId>

    <dependencies>
        <dependency>
            <groupId>com.rbac</groupId>
            <artifactId>rbac-common</artifactId>
            <version>1.0.0</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 8: 创建 rbac-system 模块**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.rbac</groupId>
        <artifactId>rbac-parent</artifactId>
        <version>1.0.0</version>
    </parent>
    <artifactId>rbac-system</artifactId>

    <dependencies>
        <dependency>
            <groupId>com.rbac</groupId>
            <artifactId>rbac-framework</artifactId>
            <version>1.0.0</version>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-jsqlparser</artifactId>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 9: 创建 rbac-api 模块**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.rbac</groupId>
        <artifactId>rbac-parent</artifactId>
        <version>1.0.0</version>
    </parent>
    <artifactId>rbac-api</artifactId>

    <dependencies>
        <dependency>
            <groupId>com.rbac</groupId>
            <artifactId>rbac-system</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 10: 创建 Spring Boot 入口类和配置文件**

Create: `rbac-api/src/main/java/com/rbac/RbacApplication.java`

```java
package com.rbac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RbacApplication {
    public static void main(String[] args) {
        SpringApplication.run(RbacApplication.class, args);
    }
}
```

Create: `rbac-api/src/main/resources/application.yml`

```yaml
server:
  port: 8080

spring:
  application:
    name: rbac-system
  profiles:
    active: dev
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/rbac?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: root
  data:
    redis:
      host: localhost
      port: 6379
      database: 0

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

# 验证码开关
captcha:
  enabled: true

# JWT 配置
jwt:
  secret: YTJkM2Y1ZjdhOGI5YzBlMWQ0ZTZmOGcwYTFiMmMzZDVlNmY3YTgwOWIxYzJkM2U0ZjVhNmI3YzhkOWUwZjFhMg==
  expiration: 86400
```

Create: `rbac-api/src/main/resources/application-dev.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/rbac?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: root
  data:
    redis:
      host: localhost
      port: 6379

logging:
  level:
    com.rbac: debug
```

- [ ] **Step 11: 验证后端项目编译**

Run: `mvn clean compile -pl rbac-api -am`
Expected: BUILD SUCCESS

- [ ] **Step 12: 提交骨架代码**

```bash
git add rbac-parent/pom.xml rbac-common/ rbac-framework/ rbac-system/ rbac-api/ rbac-generator/
git commit -m "feat: init maven multi-module project skeleton"
```

---

### Task 2: 数据库建表 + 基础实体

**Files:**
- Create: `rbac-system/src/main/java/com/rbac/system/user/entity/SysUser.java`
- Create: `rbac-system/src/main/java/com/rbac/system/role/entity/SysRole.java`
- Create: `rbac-system/src/main/java/com/rbac/system/permission/entity/SysPermission.java`
- Create: `rbac-system/src/main/java/com/rbac/system/user/entity/SysUserRole.java`
- Create: `rbac-system/src/main/java/com/rbac/system/role/entity/SysRolePermission.java`
- Create: `rbac-system/src/main/resources/mapper/`
- Create: `docs/sql/init.sql`

- [ ] **Step 1: 编写建表 SQL**

Create: `docs/sql/init.sql`

```sql
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
```

- [ ] **Step 2: 创建 MyBatis-Plus 实体类**

Create: `rbac-system/src/main/java/com/rbac/system/user/entity/SysUser.java`

```java
package com.rbac.system.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_user")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;
    private String password;
    private String realName;
    private String email;
    private String phone;
    private String avatar;

    private Integer status;
    private Long deptId;

    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private String lastLoginIp;
    private LocalDateTime lastLoginTime;
}
```

Create: `rbac-system/src/main/java/com/rbac/system/role/entity/SysRole.java`

```java
package com.rbac.system.role.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_role")
public class SysRole {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String code;
    private Long parentId;
    private Integer sort;
    private Integer status;
    private Integer builtIn;
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<SysRole> children;
}
```

Create: `rbac-system/src/main/java/com/rbac/system/permission/entity/SysPermission.java`

```java
package com.rbac.system.permission.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_permission")
public class SysPermission {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private Integer type;
    private Long parentId;
    private String path;
    private String component;
    private String perms;
    private String icon;
    private Integer sort;
    private Integer visible;

    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<SysPermission> children;
}
```

Create: `rbac-system/src/main/java/com/rbac/system/user/entity/SysUserRole.java`

```java
package com.rbac.system.user.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class SysUserRole implements Serializable {
    private Long userId;
    private Long roleId;
}
```

Create: `rbac-system/src/main/java/com/rbac/system/role/entity/SysRolePermission.java`

```java
package com.rbac.system.role.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class SysRolePermission implements Serializable {
    private Long roleId;
    private Long permissionId;
}
```

- [ ] **Step 3: 创建 MyBatis-Plus 元对象处理器**

Create: `rbac-system/src/main/java/com/rbac/system/config/MyMetaObjectHandler.java`

```java
package com.rbac.system.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
    }
}
```

- [ ] **Step 4: 创建 MyBatis-Plus 配置**

Create: `rbac-system/src/main/java/com/rbac/system/config/MyBatisPlusConfig.java`

```java
package com.rbac.system.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

- [ ] **Step 5: 提交数据库和实体代码**

```bash
git add docs/sql/init.sql rbac-system/src/main/java/com/rbac/system/user/entity/ rbac-system/src/main/java/com/rbac/system/role/entity/ rbac-system/src/main/java/com/rbac/system/permission/entity/ rbac-system/src/main/java/com/rbac/system/config/
git commit -m "feat: add database init script and entity classes"
```

---

### Task 3: JWT + Redis + 验证码基础设施

**Files:**
- Create: `rbac-framework/src/main/java/com/rbac/framework/config/RedisConfig.java`
- Create: `rbac-framework/src/main/java/com/rbac/framework/config/CorsConfig.java`
- Create: `rbac-framework/src/main/java/com/rbac/framework/security/JwtUtils.java`
- Create: `rbac-framework/src/main/java/com/rbac/framework/security/JwtAuthenticationFilter.java`
- Create: `rbac-framework/src/main/java/com/rbac/framework/security/UserContext.java`
- Create: `rbac-framework/src/main/java/com/rbac/framework/captcha/CaptchaService.java`
- Create: `rbac-framework/src/main/java/com/rbac/framework/captcha/CaptchaController.java`

- [ ] **Step 1: 创建 Redis 配置**

```java
package com.rbac.framework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        return template;
    }
}
```

- [ ] **Step 2: 创建 CORS 配置**

```java
package com.rbac.framework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
```

- [ ] **Step 3: 创建 JWT 工具类**

```java
package com.rbac.framework.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtils {
    private final SecretKey secretKey;
    private final long expiration;

    public JwtUtils(@Value("${jwt.secret}") String secret,
                    @Value("${jwt.expiration}") long expiration) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expiration = expiration;
    }

    public String generateToken(Long userId, String username) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration * 1000))
                .signWith(secretKey)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
```

- [ ] **Step 4: 创建 UserContext**

```java
package com.rbac.framework.security;

public class UserContext {
    private static final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> usernameHolder = new ThreadLocal<>();

    public static void setUserId(Long userId) { userIdHolder.set(userId); }
    public static Long getUserId() { return userIdHolder.get(); }
    public static void setUsername(String username) { usernameHolder.set(username); }
    public static String getUsername() { return usernameHolder.get(); }
    public static void clear() {
        userIdHolder.remove();
        usernameHolder.remove();
    }
}
```

- [ ] **Step 5: 创建 JWT 认证过滤器**

```java
package com.rbac.framework.security;

import com.rbac.common.constant.SystemConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Claims;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(SystemConstants.TOKEN_HEADER);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(SystemConstants.TOKEN_PREFIX)) {
            String token = authHeader.substring(SystemConstants.TOKEN_PREFIX.length());
            String blacklistKey = SystemConstants.REDIS_BLACKLIST_PREFIX + token;
            Boolean isBlacklisted = redisTemplate.hasKey(blacklistKey);
            if (Boolean.TRUE.equals(isBlacklisted)) {
                response.setStatus(401);
                response.setContentType("application/json");
                response.getWriter().write("{\"code\":401,\"msg\":\"token已被注销\",\"data\":null}");
                return;
            }
            if (jwtUtils.validateToken(token)) {
                Claims claims = jwtUtils.parseToken(token);
                Long userId = Long.valueOf(claims.getSubject());
                String username = claims.get("username", String.class);
                UserContext.setUserId(userId);
                UserContext.setUsername(username);
            }
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }
}
```

- [ ] **Step 6: 创建验证码服务**

```java
package com.rbac.framework.captcha;

import com.rbac.common.constant.SystemConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class CaptchaService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${captcha.enabled:true}")
    private boolean captchaEnabled;

    public String generateCaptcha(String uuid) {
        if (!captchaEnabled) {
            return "";
        }
        String code = String.format("%04d", new Random().nextInt(10000));
        String key = SystemConstants.CAPTCHA_PREFIX + uuid;
        redisTemplate.opsForValue().set(key, code, SystemConstants.CAPTCHA_EXPIRE_SECONDS, TimeUnit.SECONDS);
        return code;
    }

    public boolean validateCaptcha(String uuid, String code) {
        if (!captchaEnabled) {
            return true;
        }
        String key = SystemConstants.CAPTCHA_PREFIX + uuid;
        Object stored = redisTemplate.opsForValue().get(key);
        if (stored == null) {
            return false;
        }
        redisTemplate.delete(key);
        return stored.toString().equalsIgnoreCase(code);
    }
}
```

- [ ] **Step 7: 提交认证基础设施**

```bash
git add rbac-framework/src/main/java/com/rbac/framework/
git commit -m "feat: add JWT auth, Redis config, captcha service"
```

---

### Task 4: 认证模块 — 后端 API + 测试

**Files:**
- Create: `rbac-system/src/main/java/com/rbac/system/user/mapper/SysUserMapper.java`
- Create: `rbac-system/src/main/java/com/rbac/system/user/service/UserService.java`
- Create: `rbac-system/src/main/java/com/rbac/system/user/service/impl/UserServiceImpl.java`
- Create: `rbac-system/src/main/java/com/rbac/system/auth/AuthService.java`
- Create: `rbac-system/src/main/java/com/rbac/system/auth/AuthServiceImpl.java`
- Create: `rbac-system/src/main/java/com/rbac/system/auth/dto/LoginRequest.java`
- Create: `rbac-system/src/main/java/com/rbac/system/auth/dto/LoginResponse.java`
- Create: `rbac-system/src/main/java/com/rbac/api/controller/AuthController.java`
- Create: `rbac-system/src/test/java/com/rbac/system/auth/AuthServiceTest.java`

- [ ] **Step 1: 创建 Mapper 接口**

```java
package com.rbac.system.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rbac.system.user.entity.SysUser;

public interface SysUserMapper extends BaseMapper<SysUser> {
}
```

- [ ] **Step 2: 创建 Service 层**

```java
package com.rbac.system.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rbac.system.user.entity.SysUser;

public interface UserService extends IService<SysUser> {
    SysUser getByUsername(String username);
}
```

```java
package com.rbac.system.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rbac.system.user.entity.SysUser;
import com.rbac.system.user.mapper.SysUserMapper;
import com.rbac.system.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements UserService {
    @Override
    public SysUser getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
    }
}
```

- [ ] **Step 3: 创建认证 DTO**

```java
package com.rbac.system.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String uuid;
    private String captchaCode;
}
```

```java
package com.rbac.system.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private UserInfo user;

    @Data
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String username;
        private String realName;
        private String avatar;
    }
}
```

```java
package com.rbac.system.auth.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserInfoResponse {
    private Long id;
    private String username;
    private String realName;
    private String avatar;
    private List<String> roles;
    private List<String> permissions;
}
```

- [ ] **Step 4: 创建认证 Service**

```java
package com.rbac.system.auth;

import com.rbac.system.auth.dto.LoginRequest;
import com.rbac.system.auth.dto.LoginResponse;
import com.rbac.system.auth.dto.UserInfoResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void logout(String token);
    UserInfoResponse getUserInfo(Long userId);
}
```

```java
package com.rbac.system.auth.impl;

import com.rbac.common.constant.SystemConstants;
import com.rbac.common.exception.BadRequestException;
import com.rbac.framework.captcha.CaptchaService;
import com.rbac.framework.security.JwtUtils;
import com.rbac.system.auth.AuthService;
import com.rbac.system.auth.dto.LoginRequest;
import com.rbac.system.auth.dto.LoginResponse;
import com.rbac.system.auth.dto.UserInfoResponse;
import com.rbac.system.user.entity.SysUser;
import com.rbac.system.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final CaptchaService captchaService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public LoginResponse login(LoginRequest request) {
        if (!captchaService.validateCaptcha(request.getUuid(), request.getCaptchaCode())) {
            throw new BadRequestException("验证码错误或已过期");
        }
        SysUser user = userService.getByUsername(request.getUsername());
        if (user == null) {
            throw new BadRequestException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BadRequestException("账号已被禁用");
        }
        String token = jwtUtils.generateToken(user.getId(), user.getUsername());
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                user.getId(), user.getUsername(), user.getRealName(), user.getAvatar());
        return new LoginResponse(token, userInfo);
    }

    @Override
    public void logout(String token) {
        if (StringUtils.hasText(token)) {
            String blacklistKey = SystemConstants.REDIS_BLACKLIST_PREFIX + token;
            redisTemplate.opsForValue().set(blacklistKey, "1", SystemConstants.TOKEN_EXPIRE_SECONDS, TimeUnit.SECONDS);
        }
    }

    @Override
    public UserInfoResponse getUserInfo(Long userId) {
        SysUser user = userService.getById(userId);
        if (user == null) {
            throw new BadRequestException("用户不存在");
        }
        UserInfoResponse resp = new UserInfoResponse();
        resp.setId(user.getId());
        resp.setUsername(user.getUsername());
        resp.setRealName(user.getRealName());
        resp.setAvatar(user.getAvatar());
        return resp;
    }
}
```

- [ ] **Step 5: 创建认证 Controller**

```java
package com.rbac.api.controller;

import com.rbac.common.constant.SystemConstants;
import com.rbac.common.response.Result;
import com.rbac.framework.captcha.CaptchaService;
import com.rbac.framework.security.UserContext;
import com.rbac.system.auth.AuthService;
import com.rbac.system.auth.dto.LoginRequest;
import com.rbac.system.auth.dto.LoginResponse;
import com.rbac.system.auth.dto.UserInfoResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final CaptchaService captchaService;

    @GetMapping("/captcha")
    public Result<Map<String, String>> captcha() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String code = captchaService.generateCaptcha(uuid);
        Map<String, String> result = new HashMap<>();
        result.put("uuid", uuid);
        result.put("captchaEnabled", String.valueOf(!code.isEmpty()));
        return Result.success(result);
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String authHeader = request.getHeader(SystemConstants.TOKEN_HEADER);
        String token = null;
        if (authHeader != null && authHeader.startsWith(SystemConstants.TOKEN_PREFIX)) {
            token = authHeader.substring(SystemConstants.TOKEN_PREFIX.length());
        }
        authService.logout(token);
        return Result.success();
    }

    @GetMapping("/info")
    public Result<UserInfoResponse> info() {
        Long userId = UserContext.getUserId();
        return Result.success(authService.getUserInfo(userId));
    }
}
```

- [ ] **Step 6: 编写认证 Service 测试**

```java
package com.rbac.system.auth;

import com.rbac.common.exception.BadRequestException;
import com.rbac.framework.captcha.CaptchaService;
import com.rbac.framework.security.JwtUtils;
import com.rbac.system.auth.dto.LoginRequest;
import com.rbac.system.auth.dto.LoginResponse;
import com.rbac.system.auth.impl.AuthServiceImpl;
import com.rbac.system.user.entity.SysUser;
import com.rbac.system.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock private UserService userService;
    @Mock private JwtUtils jwtUtils;
    @Mock private CaptchaService captchaService;
    @Mock private RedisTemplate<String, Object> redisTemplate;
    @Mock private ValueOperations<String, Object> valueOps;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userService, jwtUtils, captchaService, redisTemplate);
    }

    @Test
    void login_withValidCredentials_shouldReturnToken() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");
        request.setUuid("test-uuid");
        request.setCaptchaCode("1234");

        SysUser user = new SysUser();
        user.setId(1L);
        user.setUsername("admin");
        user.setPassword("encoded-password");
        user.setRealName("管理员");
        user.setStatus(1);

        when(captchaService.validateCaptcha("test-uuid", "1234")).thenReturn(true);
        when(userService.getByUsername("admin")).thenReturn(user);
        when(jwtUtils.generateToken(1L, "admin")).thenReturn("test-token");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("test-token", response.getToken());
        assertEquals("admin", response.getUser().getUsername());
    }

    @Test
    void login_withWrongCaptcha_shouldThrowException() {
        LoginRequest request = new LoginRequest();
        request.setUuid("test-uuid");
        request.setCaptchaCode("wrong");

        when(captchaService.validateCaptcha("test-uuid", "wrong")).thenReturn(false);

        assertThrows(BadRequestException.class, () -> authService.login(request));
    }

    @Test
    void login_withDisabledUser_shouldThrowException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("disabled");
        request.setPassword("pwd");
        request.setUuid("uuid");
        request.setCaptchaCode("1234");

        SysUser user = new SysUser();
        user.setStatus(0);

        when(captchaService.validateCaptcha("uuid", "1234")).thenReturn(true);
        when(userService.getByUsername("disabled")).thenReturn(user);

        assertThrows(BadRequestException.class, () -> authService.login(request));
    }

    @Test
    void logout_shouldAddTokenToBlacklist() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        authService.logout("test-token");

        verify(valueOps).set(anyString(), anyString(), anyLong(), any());
    }
}
```

- [ ] **Step 7: 运行测试**

Run: `mvn test -pl rbac-system -Dtest=AuthServiceTest`
Expected: Tests pass (4/4)

- [ ] **Step 8: 提交认证模块后端代码**

```bash
git add rbac-system/src/main/java/com/rbac/system/user/mapper/ rbac-system/src/main/java/com/rbac/system/user/service/ rbac-system/src/main/java/com/rbac/system/auth/ rbac-api/src/main/java/com/rbac/api/controller/AuthController.java rbac-system/src/test/
git commit -m "feat: implement auth module backend API with tests"
```

---

### Task 5: 认证模块 — 前端登录页

**Files:**
- Create: `rbac-ui/package.json`
- Create: `rbac-ui/vite.config.js`
- Create: `rbac-ui/index.html`
- Create: `rbac-ui/src/main.js`
- Create: `rbac-ui/src/App.vue`
- Create: `rbac-ui/src/utils/request.js`
- Create: `rbac-ui/src/api/auth.js`
- Create: `rbac-ui/src/stores/auth.js`
- Create: `rbac-ui/src/router/index.js`
- Create: `rbac-ui/src/views/login/Login.vue`
- Create: `rbac-ui/src/layouts/MainLayout.vue`
- Create: `rbac-ui/src/components/Navbar.vue`
- Create: `rbac-ui/src/components/Sidebar.vue`

- [ ] **Step 1: 初始化前端项目**

```json
{
  "name": "rbac-ui",
  "private": true,
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "vue": "^3.4.0",
    "vue-router": "^4.3.0",
    "pinia": "^2.1.0",
    "element-plus": "^2.7.0",
    "axios": "^1.7.0",
    "@element-plus/icons-vue": "^2.3.0"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.0",
    "vite": "^5.4.0"
  }
}
```

- [ ] **Step 2: 创建 Vite 配置**

```js
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

- [ ] **Step 3: 创建 Axios 封装**

```js
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.msg || '请求失败')
      if (res.code === 401) {
        localStorage.removeItem('token')
        router.push('/login')
      }
      return Promise.reject(new Error(res.msg))
    }
    return res
  },
  error => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
```

- [ ] **Step 4: 创建认证 API**

```js
import request from '../utils/request'

export function getCaptcha() {
  return request.get('/auth/captcha')
}

export function login(data) {
  return request.post('/auth/login', data)
}

export function logout() {
  return request.post('/auth/logout')
}

export function getUserInfo() {
  return request.get('/auth/info')
}
```

- [ ] **Step 5: 创建认证 Store**

```js
import { defineStore } from 'pinia'
import { login as loginApi, logout as logoutApi, getUserInfo } from '../api/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: null
  }),
  getters: {
    isLoggedIn: (state) => !!state.token
  },
  actions: {
    async login(loginData) {
      const res = await loginApi(loginData)
      this.token = res.data.token
      this.userInfo = res.data.user
      localStorage.setItem('token', res.data.token)
    },
    async fetchUserInfo() {
      const res = await getUserInfo()
      this.userInfo = res.data
    },
    async logout() {
      try {
        await logoutApi()
      } finally {
        this.token = ''
        this.userInfo = null
        localStorage.removeItem('token')
      }
    }
  }
})
```

- [ ] **Step 6: 创建路由配置**

```js
import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '../layouts/MainLayout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: MainLayout,
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/dashboard/Dashboard.vue'),
        meta: { title: '仪表盘' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth !== false && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
```

- [ ] **Step 7: 创建登录页**

```vue
<template>
  <div class="login-container">
    <div class="login-card">
      <h2 class="login-title">RBAC 权限管理系统</h2>
      <el-form ref="formRef" :model="loginForm" :rules="rules" size="large">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="captchaCode" v-if="captchaEnabled">
          <div style="display:flex;gap:8px;width:100%">
            <el-input v-model="loginForm.captchaCode" placeholder="验证码" style="flex:1" />
            <div class="captcha-box" @click="loadCaptcha">{{ captchaText }}</div>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width:100%" :loading="loading" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { getCaptcha } from '../../api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref(null)
const loading = ref(false)
const captchaEnabled = ref(false)
const captchaText = ref('')
const captchaUuid = ref('')

const loginForm = reactive({
  username: '',
  password: '',
  uuid: '',
  captchaCode: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function loadCaptcha() {
  try {
    const res = await getCaptcha()
    captchaUuid.value = res.data.uuid
    captchaEnabled.value = res.data.captchaEnabled === 'true'
    if (captchaEnabled.value) {
      captchaText.value = '获取验证码'
      loginForm.uuid = captchaUuid.value
    }
  } catch (e) {
    captchaEnabled.value = false
  }
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await authStore.login({
      ...loginForm,
      uuid: captchaUuid.value
    })
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (e) {
    if (captchaEnabled.value) loadCaptcha()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadCaptcha()
})
</script>

<style scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}
.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
  font-size: 24px;
}
.captcha-box {
  width: 120px;
  height: 40px;
  background: #f0f2f5;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  user-select: none;
  font-size: 14px;
  color: #606266;
}
</style>
```

- [ ] **Step 8: 创建布局组件**

```vue
<template>
  <div class="layout-container">
    <Sidebar />
    <div class="layout-main">
      <Navbar />
      <div class="layout-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import Sidebar from '../components/Sidebar.vue'
import Navbar from '../components/Navbar.vue'
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
}
.layout-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.layout-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: #f5f7fa;
}
</style>
```

```vue
<template>
  <div class="navbar">
    <span class="navbar-title">RBAC 权限管理系统</span>
    <div class="navbar-right">
      <span class="navbar-user">{{ authStore.userInfo?.realName || authStore.userInfo?.username }}</span>
      <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

async function handleLogout() {
  await authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  padding: 0 20px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
}
.navbar-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.navbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.navbar-user {
  font-size: 14px;
  color: #606266;
}
</style>
```

```vue
<template>
  <div class="sidebar">
    <div class="sidebar-logo">RBAC</div>
    <el-menu
      :default-active="route.path"
      router
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409eff"
    >
      <el-menu-item index="/dashboard">
        <el-icon><HomeFilled /></el-icon>
        <span>仪表盘</span>
      </el-menu-item>
      <el-sub-menu index="/system">
        <template #title>
          <el-icon><Setting /></el-icon>
          <span>系统管理</span>
        </template>
        <el-menu-item index="/system/user">用户管理</el-menu-item>
        <el-menu-item index="/system/role">角色管理</el-menu-item>
        <el-menu-item index="/system/permission">权限管理</el-menu-item>
      </el-sub-menu>
    </el-menu>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
import { HomeFilled, Setting } from '@element-plus/icons-vue'

const route = useRoute()
</script>

<style scoped>
.sidebar {
  width: 220px;
  background: #304156;
  overflow-y: auto;
}
.sidebar-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}
.sidebar .el-menu {
  border-right: none;
}
</style>
```

- [ ] **Step 9: 创建仪表盘页面**

```vue
<template>
  <div class="dashboard">
    <h2>欢迎使用 RBAC 权限管理系统</h2>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="6" v-for="item in stats" :key="item.label">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-value">{{ item.value }}</div>
            <div class="stat-label">{{ item.label }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
const stats = [
  { label: '用户总数', value: '-' },
  { label: '角色总数', value: '-' },
  { label: '权限总数', value: '-' },
  { label: '系统版本', value: 'v1.0.0' }
]
</script>

<style scoped>
.dashboard h2 {
  color: #303133;
}
.stat-card {
  text-align: center;
  padding: 10px 0;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
}
.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}
</style>
```

- [ ] **Step 10: 创建 main.js 和 App.vue**

```js
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(ElementPlus)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')
```

```vue
<template>
  <router-view />
</template>
```

- [ ] **Step 11: 创建 index.html**

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>RBAC 权限管理系统</title>
</head>
<body>
  <div id="app"></div>
  <script type="module" src="/src/main.js"></script>
</body>
</html>
```

- [ ] **Step 12: 安装依赖并验证前端构建**

Run: `cd rbac-ui && npm install && npm run build`
Expected: Build success

- [ ] **Step 13: 提交前端项目骨架和登录页**

```bash
git add rbac-ui/
git commit -m "feat: add frontend project skeleton with login page"
```

---

### Task 6: 用户管理模块 — 后端 API + 测试

**Files:**
- Create: `rbac-system/src/main/java/com/rbac/system/user/dto/UserPageRequest.java`
- Create: `rbac-system/src/main/java/com/rbac/system/user/dto/UserCreateRequest.java`
- Create: `rbac-system/src/main/java/com/rbac/system/user/dto/UserUpdateRequest.java`
- Create: `rbac-system/src/main/java/com/rbac/system/user/dto/UserPageVO.java`
- Modify: `rbac-system/src/main/java/com/rbac/system/user/service/UserService.java`
- Modify: `rbac-system/src/main/java/com/rbac/system/user/service/impl/UserServiceImpl.java`
- Create: `rbac-system/src/main/java/com/rbac/system/user/mapper/SysUserRoleMapper.java`
- Create: `rbac-api/src/main/java/com/rbac/api/controller/UserController.java`
- Create: `rbac-system/src/test/java/com/rbac/system/user/service/UserServiceTest.java`

- [ ] **Step 1: 创建 DTO**

```java
package com.rbac.system.user.dto;

import lombok.Data;

@Data
public class UserPageRequest {
    private int page = 1;
    private int size = 10;
    private String keyword;
    private Integer status;
}
```

```java
package com.rbac.system.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCreateRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String realName;
    private String email;
    private String phone;
    private Integer status;
    private Long deptId;
}
```

```java
package com.rbac.system.user.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String realName;
    private String email;
    private String phone;
    private Integer status;
    private Long deptId;
}
```

```java
package com.rbac.system.user.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserPageVO {
    private Long id;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime lastLoginTime;
    private List<Long> roleIds;
}
```

- [ ] **Step 2: 创建 SysUserRoleMapper**

```java
package com.rbac.system.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rbac.system.user.entity.SysUserRole;

public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
}
```

- [ ] **Step 3: 扩展 UserService**

```java
package com.rbac.system.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rbac.system.user.dto.*;
import com.rbac.system.user.entity.SysUser;
import java.util.List;

public interface UserService extends IService<SysUser> {
    SysUser getByUsername(String username);
    IPage<SysUser> page(UserPageRequest request);
    Long createUser(UserCreateRequest request);
    void updateUser(Long id, UserUpdateRequest request);
    void updateStatus(Long id, Integer status);
    void deleteUser(Long id);
    List<Long> getUserRoleIds(Long userId);
    void assignRoles(Long userId, List<Long> roleIds);
}
```

```java
package com.rbac.system.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rbac.common.exception.BadRequestException;
import com.rbac.system.user.dto.*;
import com.rbac.system.user.entity.SysUser;
import com.rbac.system.user.entity.SysUserRole;
import com.rbac.system.user.mapper.SysUserMapper;
import com.rbac.system.user.mapper.SysUserRoleMapper;
import com.rbac.system.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements UserService {
    private final SysUserRoleMapper userRoleMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public SysUser getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
    }

    @Override
    public IPage<SysUser> page(UserPageRequest request) {
        Page<SysUser> page = new Page<>(request.getPage(), request.getSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.like(SysUser::getUsername, request.getKeyword())
                   .or().like(SysUser::getRealName, request.getKeyword());
        }
        if (request.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, request.getStatus());
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    @Transactional
    public Long createUser(UserCreateRequest request) {
        if (getByUsername(request.getUsername()) != null) {
            throw new BadRequestException("用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        user.setDeptId(request.getDeptId());
        save(user);
        return user.getId();
    }

    @Override
    @Transactional
    public void updateUser(Long id, UserUpdateRequest request) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BadRequestException("用户不存在");
        }
        user.setRealName(request.getRealName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(request.getStatus());
        user.setDeptId(request.getDeptId());
        updateById(user);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BadRequestException("用户不存在");
        }
        user.setStatus(status);
        updateById(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        removeById(id);
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        return userRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId))
                .stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        if (roleIds != null && !roleIds.isEmpty()) {
            List<SysUserRole> list = roleIds.stream().map(roleId -> {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                return ur;
            }).collect(Collectors.toList());
            list.forEach(userRoleMapper::insert);
        }
    }
}
```

- [ ] **Step 4: 创建 UserController**

```java
package com.rbac.api.controller;

import com.rbac.common.response.PageResult;
import com.rbac.common.response.Result;
import com.rbac.system.user.dto.*;
import com.rbac.system.user.entity.SysUser;
import com.rbac.system.user.service.UserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/page")
    public Result<PageResult<SysUser>> page(@ModelAttribute UserPageRequest request) {
        IPage<SysUser> page = userService.page(request);
        PageResult<SysUser> result = PageResult.of(
                page.getRecords(), page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<SysUser> get(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @PostMapping
    public Result<Long> create(@Valid @RequestBody UserCreateRequest request) {
        return Result.success(userService.createUser(request));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        userService.updateUser(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> status(@PathVariable Long id, @RequestBody Integer status) {
        userService.updateStatus(id, status);
        return Result.success();
    }

    @GetMapping("/{id}/roles")
    public Result<List<Long>> roles(@PathVariable Long id) {
        return Result.success(userService.getUserRoleIds(id));
    }

    @PutMapping("/{id}/roles")
    public Result<Void> roles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        userService.assignRoles(id, roleIds);
        return Result.success();
    }
}
```

- [ ] **Step 5: 编写 UserServiceTest**

```java
package com.rbac.system.user.service;

import com.rbac.common.exception.BadRequestException;
import com.rbac.system.user.dto.UserCreateRequest;
import com.rbac.system.user.dto.UserUpdateRequest;
import com.rbac.system.user.entity.SysUser;
import com.rbac.system.user.mapper.SysUserMapper;
import com.rbac.system.user.mapper.SysUserRoleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock private SysUserMapper userMapper;
    @Mock private SysUserRoleMapper userRoleMapper;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userMapper, userRoleMapper);
    }

    @Test
    void createUser_withExistingUsername_shouldThrowException() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("existing");
        when(userMapper.selectOne(any())).thenReturn(new SysUser());

        assertThrows(BadRequestException.class, () -> userService.createUser(request));
    }

    @Test
    void createUser_withValidData_shouldSucceed() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setRealName("新用户");

        when(userMapper.selectOne(any())).thenReturn(null);

        assertDoesNotThrow(() -> userService.createUser(request));
    }

    @Test
    void deleteUser_shouldRemoveRoles() {
        Long userId = 1L;
        userService.deleteUser(userId);
        verify(userRoleMapper).delete(any());
    }
}
```

- [ ] **Step 6: 运行测试**

Run: `mvn test -pl rbac-system -Dtest=UserServiceTest`
Expected: Tests pass

- [ ] **Step 7: 提交用户管理后端代码**

```bash
git add rbac-system/src/main/java/com/rbac/system/user/dto/ rbac-system/src/main/java/com/rbac/system/user/mapper/SysUserRoleMapper.java rbac-system/src/main/java/com/rbac/system/user/service/ rbac-api/src/main/java/com/rbac/api/controller/UserController.java rbac-system/src/test/
git commit -m "feat: implement user management backend API with tests"
```

---

### Task 7: 用户管理模块 — 前端页面

**Files:**
- Create: `rbac-ui/src/api/user.js`
- Create: `rbac-ui/src/views/system/user/UserList.vue`
- Create: `rbac-ui/src/views/system/user/UserForm.vue`
- Modify: `rbac-ui/src/router/index.js`

- [ ] **Step 1: 创建用户 API**

```js
import request from '../../utils/request'

export function getUserPage(params) {
  return request.get('/system/user/page', { params })
}

export function getUser(id) {
  return request.get(`/system/user/${id}`)
}

export function createUser(data) {
  return request.post('/system/user', data)
}

export function updateUser(id, data) {
  return request.put(`/system/user/${id}`, data)
}

export function deleteUser(id) {
  return request.delete(`/system/user/${id}`)
}

export function updateUserStatus(id, status) {
  return request.put(`/system/user/${id}/status`, status, {
    headers: { 'Content-Type': 'application/json' }
  })
}

export function getUserRoles(id) {
  return request.get(`/system/user/${id}/roles`)
}

export function assignUserRoles(id, roleIds) {
  return request.put(`/system/user/${id}/roles`, roleIds)
}
```

- [ ] **Step 2: 创建用户列表页**

```vue
<template>
  <div class="user-page">
    <div class="page-header">
      <h3>用户管理</h3>
      <el-button type="primary" @click="openForm(null)">新增用户</el-button>
    </div>
    <el-card>
      <el-form :inline="true" :model="queryParams" style="margin-bottom:16px">
        <el-form-item label="关键词">
          <el-input v-model="queryParams.keyword" placeholder="用户名/姓名" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width:120px">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openForm(row)">编辑</el-button>
            <el-button link type="primary" size="small" @click="handleAssignRole(row)">角色</el-button>
            <el-button link :type="row.status === 1 ? 'warning' : 'success'" size="small"
                       @click="handleToggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-popconfirm title="确认删除?" @confirm="handleDelete(row)">
              <template #reference>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        style="margin-top:16px;justify-content:flex-end"
        @change="loadData"
      />
    </el-card>

    <UserForm v-model:visible="formVisible" :user="currentUser" @saved="loadData" />

    <el-dialog v-model="roleDialogVisible" title="分配角色" width="400px">
      <el-checkbox-group v-model="selectedRoleIds">
        <el-checkbox v-for="role in roles" :key="role.id" :label="role.id" style="display:flex;margin-bottom:8px">
          {{ role.name }}
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRole">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserPage, deleteUser, updateUserStatus, getUserRoles, assignUserRoles } from '../../../api/user'
import { getRolePage } from '../../../api/role'
import UserForm from './UserForm.vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const formVisible = ref(false)
const currentUser = ref(null)
const roleDialogVisible = ref(false)
const selectedRoleIds = ref([])
const roles = ref([])
const currentRoleUserId = ref(null)

const queryParams = reactive({
  page: 1,
  size: 10,
  keyword: '',
  status: null
})

async function loadData() {
  loading.value = true
  try {
    const res = await getUserPage(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  queryParams.keyword = ''
  queryParams.status = null
  queryParams.page = 1
  loadData()
}

function openForm(user) {
  currentUser.value = user
  formVisible.value = true
}

async function handleDelete(row) {
  await deleteUser(row.id)
  ElMessage.success('删除成功')
  loadData()
}

async function handleToggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  await updateUserStatus(row.id, newStatus)
  ElMessage.success(newStatus === 1 ? '已启用' : '已禁用')
  loadData()
}

async function handleAssignRole(row) {
  currentRoleUserId.value = row.id
  const res = await getRolePage({ page: 1, size: 999 })
  roles.value = res.data.records
  const roleRes = await getUserRoles(row.id)
  selectedRoleIds.value = roleRes.data || []
  roleDialogVisible.value = true
}

async function saveRole() {
  await assignUserRoles(currentRoleUserId.value, selectedRoleIds.value)
  ElMessage.success('角色分配成功')
  roleDialogVisible.value = false
}

onMounted(loadData)
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.page-header h3 {
  margin: 0;
  color: #303133;
}
</style>
```

- [ ] **Step 3: 创建用户表单对话框**

```vue
<template>
  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="500px" @close="handleClose">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" :disabled="isEdit" />
      </el-form-item>
      <el-form-item label="密码" prop="password" v-if="!isEdit">
        <el-input v-model="form.password" type="password" show-password />
      </el-form-item>
      <el-form-item label="真实姓名" prop="realName">
        <el-input v-model="form.realName" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" />
      </el-form-item>
      <el-form-item label="状态">
        <el-radio-group v-model="form.status">
          <el-radio :value="1">正常</el-radio>
          <el-radio :value="0">禁用</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { createUser, updateUser, getUser } from '../../../api/user'

const props = defineProps({ visible: Boolean, user: Object })
const emit = defineEmits(['update:visible', 'saved'])

const formRef = ref(null)
const submitting = ref(false)
const isEdit = computed(() => !!props.user?.id)

const form = reactive({
  username: '',
  password: '',
  realName: '',
  email: '',
  phone: '',
  status: 1
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const dialogVisible = computed({
  get: () => props.visible,
  set: v => emit('update:visible', v)
})

watch(() => props.visible, async (v) => {
  if (v && props.user?.id) {
    const res = await getUser(props.user.id)
    Object.assign(form, res.data)
    form.password = ''
  } else if (v) {
    form.username = ''
    form.password = ''
    form.realName = ''
    form.email = ''
    form.phone = ''
    form.status = 1
  }
})

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateUser(props.user.id, form)
      ElMessage.success('修改成功')
    } else {
      await createUser(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    emit('saved')
  } finally {
    submitting.value = false
  }
}

function handleClose() {
  formRef.value?.resetFields()
}
</script>
```

- [ ] **Step 4: 更新路由**

```js
{
  path: 'system/user',
  name: 'UserList',
  component: () => import('../views/system/user/UserList.vue'),
  meta: { title: '用户管理' }
}
```

- [ ] **Step 5: 提交用户管理前端代码**

```bash
git add rbac-ui/src/api/user.js rbac-ui/src/views/system/user/ rbac-ui/src/router/index.js
git commit -m "feat: add user management frontend pages"
```

---

### Task 8: 角色管理模块 — 后端 API + 测试

**Files:**
- Create: `rbac-system/src/main/java/com/rbac/system/role/mapper/SysRoleMapper.java`
- Create: `rbac-system/src/main/java/com/rbac/system/role/mapper/SysRolePermissionMapper.java`
- Create: `rbac-system/src/main/java/com/rbac/system/role/dto/RoleCreateRequest.java`
- Create: `rbac-system/src/main/java/com/rbac/system/role/dto/RoleUpdateRequest.java`
- Create: `rbac-system/src/main/java/com/rbac/system/role/dto/RolePageRequest.java`
- Create: `rbac-system/src/main/java/com/rbac/system/role/service/RoleService.java`
- Create: `rbac-system/src/main/java/com/rbac/system/role/service/impl/RoleServiceImpl.java`
- Create: `rbac-api/src/main/java/com/rbac/api/controller/RoleController.java`
- Create: `rbac-system/src/test/java/com/rbac/system/role/service/RoleServiceTest.java`

- [ ] **Step 1: 创建 Mapper**

```java
package com.rbac.system.role.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rbac.system.role.entity.SysRole;

public interface SysRoleMapper extends BaseMapper<SysRole> {
}
```

```java
package com.rbac.system.role.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rbac.system.role.entity.SysRolePermission;

public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {
}
```

- [ ] **Step 2: 创建 DTO**

```java
package com.rbac.system.role.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleCreateRequest {
    @NotBlank(message = "角色名称不能为空")
    private String name;

    @NotBlank(message = "角色编码不能为空")
    private String code;

    private Long parentId;
    private Integer sort;
    private Integer status;
    private String remark;
}
```

```java
package com.rbac.system.role.dto;

import lombok.Data;

@Data
public class RoleUpdateRequest {
    private String name;
    private Integer sort;
    private Integer status;
    private String remark;
}
```

```java
package com.rbac.system.role.dto;

import lombok.Data;

@Data
public class RolePageRequest {
    private int page = 1;
    private int size = 10;
    private String keyword;
    private Integer status;
}
```

- [ ] **Step 3: 创建 RoleService**

```java
package com.rbac.system.role.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rbac.system.role.dto.*;
import com.rbac.system.role.entity.SysRole;
import java.util.List;

public interface RoleService extends IService<SysRole> {
    List<SysRole> getTree();
    IPage<SysRole> page(RolePageRequest request);
    Long createRole(RoleCreateRequest request);
    void updateRole(Long id, RoleUpdateRequest request);
    void deleteRole(Long id);
    void updateStatus(Long id, Integer status);
    List<Long> getRolePermissionIds(Long roleId);
    void assignPermissions(Long roleId, List<Long> permissionIds);
}
```

```java
package com.rbac.system.role.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rbac.common.exception.BadRequestException;
import com.rbac.system.role.dto.*;
import com.rbac.system.role.entity.SysRole;
import com.rbac.system.role.entity.SysRolePermission;
import com.rbac.system.role.mapper.SysRoleMapper;
import com.rbac.system.role.mapper.SysRolePermissionMapper;
import com.rbac.system.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements RoleService {
    private final SysRolePermissionMapper rolePermissionMapper;

    @Override
    public List<SysRole> getTree() {
        List<SysRole> all = list(new LambdaQueryWrapper<SysRole>().orderByAsc(SysRole::getSort));
        return buildTree(all, 0L);
    }

    private List<SysRole> buildTree(List<SysRole> all, Long parentId) {
        return all.stream()
                .filter(r -> r.getParentId() != null && r.getParentId().equals(parentId))
                .peek(r -> r.setChildren(buildTree(all, r.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public IPage<SysRole> page(RolePageRequest request) {
        Page<SysRole> page = new Page<>(request.getPage(), request.getSize());
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.like(SysRole::getName, request.getKeyword()).or()
                   .like(SysRole::getCode, request.getKeyword());
        }
        if (request.getStatus() != null) {
            wrapper.eq(SysRole::getStatus, request.getStatus());
        }
        wrapper.orderByAsc(SysRole::getSort);
        return page(page, wrapper);
    }

    @Override
    @Transactional
    public Long createRole(RoleCreateRequest request) {
        SysRole role = new SysRole();
        role.setName(request.getName());
        role.setCode(request.getCode());
        role.setParentId(request.getParentId() != null ? request.getParentId() : 0L);
        role.setSort(request.getSort() != null ? request.getSort() : 0);
        role.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        role.setRemark(request.getRemark());
        save(role);
        return role.getId();
    }

    @Override
    @Transactional
    public void updateRole(Long id, RoleUpdateRequest request) {
        SysRole role = getById(id);
        if (role == null) {
            throw new BadRequestException("角色不存在");
        }
        if (role.getBuiltIn() == 1) {
            throw new BadRequestException("系统内置角色不可修改");
        }
        if (request.getName() != null) role.setName(request.getName());
        if (request.getSort() != null) role.setSort(request.getSort());
        if (request.getStatus() != null) role.setStatus(request.getStatus());
        if (request.getRemark() != null) role.setRemark(request.getRemark());
        updateById(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        SysRole role = getById(id);
        if (role != null && role.getBuiltIn() == 1) {
            throw new BadRequestException("系统内置角色不可删除");
        }
        removeById(id);
        rolePermissionMapper.delete(
                new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, id));
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        SysRole role = getById(id);
        if (role == null) {
            throw new BadRequestException("角色不存在");
        }
        role.setStatus(status);
        updateById(role);
    }

    @Override
    public List<Long> getRolePermissionIds(Long roleId) {
        return rolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, roleId))
                .stream().map(SysRolePermission::getPermissionId).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        rolePermissionMapper.delete(
                new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, roleId));
        if (permissionIds != null && !permissionIds.isEmpty()) {
            permissionIds.forEach(pid -> {
                SysRolePermission rp = new SysRolePermission();
                rp.setRoleId(roleId);
                rp.setPermissionId(pid);
                rolePermissionMapper.insert(rp);
            });
        }
    }
}
```

- [ ] **Step 4: 创建 RoleController**

```java
package com.rbac.api.controller;

import com.rbac.common.response.PageResult;
import com.rbac.common.response.Result;
import com.rbac.system.role.dto.*;
import com.rbac.system.role.entity.SysRole;
import com.rbac.system.role.service.RoleService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/system/role")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping("/tree")
    public Result<List<SysRole>> tree() {
        return Result.success(roleService.getTree());
    }

    @GetMapping("/page")
    public Result<PageResult<SysRole>> page(@ModelAttribute RolePageRequest request) {
        IPage<SysRole> page = roleService.page(request);
        PageResult<SysRole> result = PageResult.of(
                page.getRecords(), page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<SysRole> get(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }

    @PostMapping
    public Result<Long> create(@Valid @RequestBody RoleCreateRequest request) {
        return Result.success(roleService.createRole(request));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody RoleUpdateRequest request) {
        roleService.updateRole(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> status(@PathVariable Long id, @RequestBody Integer status) {
        roleService.updateStatus(id, status);
        return Result.success();
    }

    @GetMapping("/{id}/permissions")
    public Result<List<Long>> permissions(@PathVariable Long id) {
        return Result.success(roleService.getRolePermissionIds(id));
    }

    @PutMapping("/{id}/permissions")
    public Result<Void> permissions(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(id, permissionIds);
        return Result.success();
    }
}
```

- [ ] **Step 5: 编写 RoleServiceTest**

```java
package com.rbac.system.role.service;

import com.rbac.common.exception.BadRequestException;
import com.rbac.system.role.dto.RoleCreateRequest;
import com.rbac.system.role.dto.RoleUpdateRequest;
import com.rbac.system.role.entity.SysRole;
import com.rbac.system.role.mapper.SysRoleMapper;
import com.rbac.system.role.mapper.SysRolePermissionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {
    @Mock private SysRoleMapper roleMapper;
    @Mock private SysRolePermissionMapper rolePermissionMapper;
    @InjectMocks private RoleServiceImpl roleService;

    @Test
    void deleteRole_withBuiltInRole_shouldThrowException() {
        SysRole role = new SysRole();
        role.setId(1L);
        role.setBuiltIn(1);
        when(roleMapper.selectById(1L)).thenReturn(role);

        assertThrows(BadRequestException.class, () -> roleService.deleteRole(1L));
    }

    @Test
    void deleteRole_withCustomRole_shouldSucceed() {
        SysRole role = new SysRole();
        role.setId(2L);
        role.setBuiltIn(0);
        when(roleMapper.selectById(2L)).thenReturn(role);

        assertDoesNotThrow(() -> roleService.deleteRole(2L));
        verify(roleMapper).deleteById(2L);
    }

    @Test
    void createRole_shouldSetDefaultValues() {
        RoleCreateRequest request = new RoleCreateRequest();
        request.setName("测试角色");
        request.setCode("test");

        Long id = roleService.createRole(request);
        assertNotNull(id);
        verify(roleMapper).insert(any(SysRole.class));
    }
}
```

- [ ] **Step 6: 运行测试**

Run: `mvn test -pl rbac-system -Dtest=RoleServiceTest`
Expected: Tests pass

- [ ] **Step 7: 提交角色管理后端代码**

```bash
git add rbac-system/src/main/java/com/rbac/system/role/ rbac-api/src/main/java/com/rbac/api/controller/RoleController.java rbac-system/src/test/
git commit -m "feat: implement role management backend API with tests"
```

---

### Task 9: 角色管理模块 — 前端页面

**Files:**
- Create: `rbac-ui/src/api/role.js`
- Create: `rbac-ui/src/views/system/role/RoleList.vue`
- Create: `rbac-ui/src/views/system/role/RoleForm.vue`
- Create: `rbac-ui/src/views/system/role/RolePermission.vue`
- Modify: `rbac-ui/src/router/index.js`

- [ ] **Step 1: 创建角色 API**

```js
import request from '../../utils/request'

export function getRoleTree() {
  return request.get('/system/role/tree')
}

export function getRolePage(params) {
  return request.get('/system/role/page', { params })
}

export function getRole(id) {
  return request.get(`/system/role/${id}`)
}

export function createRole(data) {
  return request.post('/system/role', data)
}

export function updateRole(id, data) {
  return request.put(`/system/role/${id}`, data)
}

export function deleteRole(id) {
  return request.delete(`/system/role/${id}`)
}

export function updateRoleStatus(id, status) {
  return request.put(`/system/role/${id}/status`, status, {
    headers: { 'Content-Type': 'application/json' }
  })
}

export function getRolePermissions(id) {
  return request.get(`/system/role/${id}/permissions`)
}

export function assignRolePermissions(id, permissionIds) {
  return request.put(`/system/role/${id}/permissions`, permissionIds)
}
```

- [ ] **Step 2: 创建角色列表页**

```vue
<template>
  <div class="role-page">
    <div class="page-header">
      <h3>角色管理</h3>
      <el-button type="primary" @click="openForm(null)">新增角色</el-button>
    </div>
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe row-key="id" default-expand-all :tree-props="{ children: 'children' }">
        <el-table-column prop="name" label="角色名称" width="180" />
        <el-table-column prop="code" label="角色编码" width="150" />
        <el-table-column prop="sort" label="排序" width="60" />
        <el-table-column prop="builtIn" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.builtIn === 1 ? 'warning' : 'info'" size="small">
              {{ row.builtIn === 1 ? '内置' : '自定义' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openForm(row)">编辑</el-button>
            <el-button link type="primary" size="small" @click="handlePermission(row)">权限</el-button>
            <el-button link :type="row.status === 1 ? 'warning' : 'success'" size="small"
                       @click="handleToggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-popconfirm title="确认删除?" @confirm="handleDelete(row)">
              <template #reference>
                <el-button link type="danger" size="small" :disabled="row.builtIn === 1">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <RoleForm v-model:visible="formVisible" :role="currentRole" @saved="loadData" />
    <RolePermission v-model:visible="permVisible" :role="currentPermRole" @saved="loadData" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getRoleTree, deleteRole, updateRoleStatus } from '../../../api/role'
import RoleForm from './RoleForm.vue'
import RolePermission from './RolePermission.vue'

const loading = ref(false)
const tableData = ref([])
const formVisible = ref(false)
const currentRole = ref(null)
const permVisible = ref(false)
const currentPermRole = ref(null)

async function loadData() {
  loading.value = true
  try {
    const res = await getRoleTree()
    tableData.value = res.data
  } finally {
    loading.value = false
  }
}

function openForm(role) {
  currentRole.value = role
  formVisible.value = true
}

async function handleDelete(row) {
  await deleteRole(row.id)
  ElMessage.success('删除成功')
  loadData()
}

async function handleToggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  await updateRoleStatus(row.id, newStatus)
  ElMessage.success(newStatus === 1 ? '已启用' : '已禁用')
  loadData()
}

function handlePermission(row) {
  currentPermRole.value = row
  permVisible.value = true
}

onMounted(loadData)
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.page-header h3 { margin: 0; }
</style>
```

- [ ] **Step 3: 创建角色表单**

```vue
<template>
  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="500px">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="角色名称" prop="name">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="角色编码" prop="code">
        <el-input v-model="form.code" :disabled="isEdit" />
      </el-form-item>
      <el-form-item label="父角色">
        <el-tree-select v-model="form.parentId" :data="roleTree" :props="{ label: 'name', value: 'id' }"
                        placeholder="无" clearable check-strictly />
      </el-form-item>
      <el-form-item label="排序">
        <el-input-number v-model="form.sort" :min="0" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { createRole, updateRole, getRoleTree, getRole } from '../../../api/role'

const props = defineProps({ visible: Boolean, role: Object })
const emit = defineEmits(['update:visible', 'saved'])

const formRef = ref(null)
const submitting = ref(false)
const roleTree = ref([])
const isEdit = computed(() => !!props.role?.id)

const form = reactive({
  name: '',
  code: '',
  parentId: null,
  sort: 0,
  remark: ''
})

const rules = {
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const dialogVisible = computed({
  get: () => props.visible,
  set: v => emit('update:visible', v)
})

watch(() => props.visible, async (v) => {
  if (v) {
    const treeRes = await getRoleTree()
    roleTree.value = treeRes.data
    if (props.role?.id) {
      const res = await getRole(props.role.id)
      Object.assign(form, res.data)
    } else {
      form.name = ''
      form.code = ''
      form.parentId = props.role?.id || null
      form.sort = 0
      form.remark = ''
    }
  }
})

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateRole(props.role.id, form)
      ElMessage.success('修改成功')
    } else {
      await createRole(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    emit('saved')
  } finally {
    submitting.value = false
  }
}
</script>
```

- [ ] **Step 4: 创建角色权限分配对话框**

```vue
<template>
  <el-dialog v-model="dialogVisible" title="分配权限" width="450px">
    <el-tree
      ref="treeRef"
      :data="permissionTree"
      show-checkbox
      node-key="id"
      :props="{ label: 'name', children: 'children' }"
      default-expand-all
      check-strictly
    />
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { getRolePermissions, assignRolePermissions } from '../../../api/role'
import { getPermissionTree } from '../../../api/permission'

const props = defineProps({ visible: Boolean, role: Object })
const emit = defineEmits(['update:visible', 'saved'])

const treeRef = ref(null)
const submitting = ref(false)
const permissionTree = ref([])

const dialogVisible = computed({
  get: () => props.visible,
  set: v => emit('update:visible', v)
})

watch(() => props.visible, async (v) => {
  if (v && props.role) {
    const treeRes = await getPermissionTree()
    permissionTree.value = treeRes.data
    const permRes = await getRolePermissions(props.role.id)
    await nextTick()
    treeRef.value?.setCheckedKeys(permRes.data || [])
  }
})

async function handleSubmit() {
  submitting.value = true
  try {
    const checkedKeys = treeRef.value?.getCheckedKeys() || []
    const halfCheckedKeys = treeRef.value?.getHalfCheckedKeys() || []
    await assignRolePermissions(props.role.id, [...checkedKeys, ...halfCheckedKeys])
    ElMessage.success('权限分配成功')
    dialogVisible.value = false
    emit('saved')
  } finally {
    submitting.value = false
  }
}
</script>
```

- [ ] **Step 5: 更新路由**

```js
{
  path: 'system/role',
  name: 'RoleList',
  component: () => import('../views/system/role/RoleList.vue'),
  meta: { title: '角色管理' }
}
```

- [ ] **Step 6: 提交角色管理前端代码**

```bash
git add rbac-ui/src/api/role.js rbac-ui/src/views/system/role/ rbac-ui/src/router/index.js
git commit -m "feat: add role management frontend pages"
```

---

### Task 10: 权限管理模块 — 后端 API + 测试

**Files:**
- Create: `rbac-system/src/main/java/com/rbac/system/permission/mapper/SysPermissionMapper.java`
- Create: `rbac-system/src/main/java/com/rbac/system/permission/dto/PermissionCreateRequest.java`
- Create: `rbac-system/src/main/java/com/rbac/system/permission/dto/PermissionUpdateRequest.java`
- Create: `rbac-system/src/main/java/com/rbac/system/permission/service/PermissionService.java`
- Create: `rbac-system/src/main/java/com/rbac/system/permission/service/impl/PermissionServiceImpl.java`
- Create: `rbac-api/src/main/java/com/rbac/api/controller/PermissionController.java`
- Create: `rbac-system/src/test/java/com/rbac/system/permission/service/PermissionServiceTest.java`

- [ ] **Step 1: 创建 Mapper**

```java
package com.rbac.system.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rbac.system.permission.entity.SysPermission;

public interface SysPermissionMapper extends BaseMapper<SysPermission> {
}
```

- [ ] **Step 2: 创建 DTO**

```java
package com.rbac.system.permission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PermissionCreateRequest {
    @NotBlank(message = "权限名称不能为空")
    private String name;

    @NotNull(message = "权限类型不能为空")
    private Integer type;

    private Long parentId;
    private String path;
    private String component;
    private String perms;
    private String icon;
    private Integer sort;
    private Integer visible;
}
```

```java
package com.rbac.system.permission.dto;

import lombok.Data;

@Data
public class PermissionUpdateRequest {
    private String name;
    private Integer type;
    private Long parentId;
    private String path;
    private String component;
    private String perms;
    private String icon;
    private Integer sort;
    private Integer visible;
}
```

- [ ] **Step 3: 创建 PermissionService**

```java
package com.rbac.system.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rbac.system.permission.dto.*;
import com.rbac.system.permission.entity.SysPermission;
import java.util.List;

public interface PermissionService extends IService<SysPermission> {
    List<SysPermission> getTree();
    Long createPermission(PermissionCreateRequest request);
    void updatePermission(Long id, PermissionUpdateRequest request);
    void deletePermission(Long id);
}
```

```java
package com.rbac.system.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rbac.common.exception.BadRequestException;
import com.rbac.system.permission.dto.*;
import com.rbac.system.permission.entity.SysPermission;
import com.rbac.system.permission.mapper.SysPermissionMapper;
import com.rbac.system.permission.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements PermissionService {

    @Override
    public List<SysPermission> getTree() {
        List<SysPermission> all = list(new LambdaQueryWrapper<SysPermission>().orderByAsc(SysPermission::getSort));
        return buildTree(all, 0L);
    }

    private List<SysPermission> buildTree(List<SysPermission> all, Long parentId) {
        return all.stream()
                .filter(p -> p.getParentId() != null && p.getParentId().equals(parentId))
                .peek(p -> p.setChildren(buildTree(all, p.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long createPermission(PermissionCreateRequest request) {
        SysPermission perm = new SysPermission();
        perm.setName(request.getName());
        perm.setType(request.getType());
        perm.setParentId(request.getParentId() != null ? request.getParentId() : 0L);
        perm.setPath(request.getPath());
        perm.setComponent(request.getComponent());
        perm.setPerms(request.getPerms());
        perm.setIcon(request.getIcon());
        perm.setSort(request.getSort() != null ? request.getSort() : 0);
        perm.setVisible(request.getVisible() != null ? request.getVisible() : 1);
        save(perm);
        return perm.getId();
    }

    @Override
    @Transactional
    public void updatePermission(Long id, PermissionUpdateRequest request) {
        SysPermission perm = getById(id);
        if (perm == null) {
            throw new BadRequestException("权限不存在");
        }
        if (request.getName() != null) perm.setName(request.getName());
        if (request.getType() != null) perm.setType(request.getType());
        if (request.getParentId() != null) perm.setParentId(request.getParentId());
        if (request.getPath() != null) perm.setPath(request.getPath());
        if (request.getComponent() != null) perm.setComponent(request.getComponent());
        if (request.getPerms() != null) perm.setPerms(request.getPerms());
        if (request.getIcon() != null) perm.setIcon(request.getIcon());
        if (request.getSort() != null) perm.setSort(request.getSort());
        if (request.getVisible() != null) perm.setVisible(request.getVisible());
        updateById(perm);
    }

    @Override
    @Transactional
    public void deletePermission(Long id) {
        List<SysPermission> children = list(new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getParentId, id));
        if (!children.isEmpty()) {
            throw new BadRequestException("存在子权限，无法删除");
        }
        removeById(id);
    }
}
```

- [ ] **Step 4: 创建 PermissionController**

```java
package com.rbac.api.controller;

import com.rbac.common.response.Result;
import com.rbac.system.permission.dto.*;
import com.rbac.system.permission.entity.SysPermission;
import com.rbac.system.permission.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/system/permission")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @GetMapping("/tree")
    public Result<List<SysPermission>> tree() {
        return Result.success(permissionService.getTree());
    }

    @GetMapping("/{id}")
    public Result<SysPermission> get(@PathVariable Long id) {
        return Result.success(permissionService.getById(id));
    }

    @PostMapping
    public Result<Long> create(@Valid @RequestBody PermissionCreateRequest request) {
        return Result.success(permissionService.createPermission(request));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody PermissionUpdateRequest request) {
        permissionService.updatePermission(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return Result.success();
    }
}
```

- [ ] **Step 5: 编写 PermissionServiceTest**

```java
package com.rbac.system.permission.service;

import com.rbac.common.exception.BadRequestException;
import com.rbac.system.permission.dto.PermissionCreateRequest;
import com.rbac.system.permission.entity.SysPermission;
import com.rbac.system.permission.mapper.SysPermissionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {
    @Mock private SysPermissionMapper permissionMapper;
    @InjectMocks private PermissionServiceImpl permissionService;

    @Test
    void deletePermission_withChildren_shouldThrowException() {
        SysPermission child = new SysPermission();
        child.setId(2L);
        child.setParentId(1L);
        when(permissionMapper.selectList(any())).thenReturn(List.of(child));

        assertThrows(BadRequestException.class, () -> permissionService.deletePermission(1L));
    }

    @Test
    void deletePermission_withoutChildren_shouldSucceed() {
        when(permissionMapper.selectList(any())).thenReturn(List.of());

        assertDoesNotThrow(() -> permissionService.deletePermission(1L));
        verify(permissionMapper).deleteById(1L);
    }

    @Test
    void createPermission_shouldSetDefaultParentId() {
        PermissionCreateRequest request = new PermissionCreateRequest();
        request.setName("测试权限");
        request.setType(2);
        request.setPerms("test:perm");

        Long id = permissionService.createPermission(request);
        assertNotNull(id);
        verify(permissionMapper).insert(argThat(p -> p.getParentId() == 0L));
    }
}
```

- [ ] **Step 6: 运行测试**

Run: `mvn test -pl rbac-system -Dtest=PermissionServiceTest`
Expected: Tests pass

- [ ] **Step 7: 提交权限管理后端代码**

```bash
git add rbac-system/src/main/java/com/rbac/system/permission/ rbac-api/src/main/java/com/rbac/api/controller/PermissionController.java rbac-system/src/test/
git commit -m "feat: implement permission management backend API with tests"
```

---

### Task 11: 权限管理模块 — 前端页面

**Files:**
- Create: `rbac-ui/src/api/permission.js`
- Create: `rbac-ui/src/views/system/permission/PermissionTree.vue`
- Modify: `rbac-ui/src/router/index.js`

- [ ] **Step 1: 创建权限 API**

```js
import request from '../../utils/request'

export function getPermissionTree() {
  return request.get('/system/permission/tree')
}

export function getPermission(id) {
  return request.get(`/system/permission/${id}`)
}

export function createPermission(data) {
  return request.post('/system/permission', data)
}

export function updatePermission(id, data) {
  return request.put(`/system/permission/${id}`, data)
}

export function deletePermission(id) {
  return request.delete(`/system/permission/${id}`)
}
```

- [ ] **Step 2: 创建权限树管理页**

```vue
<template>
  <div class="perm-page">
    <div class="page-header">
      <h3>权限管理</h3>
      <el-button type="primary" @click="openForm(null)">新增权限</el-button>
    </div>
    <el-row :gutter="20">
      <el-col :span="10">
        <el-card>
          <el-tree :data="treeData" :props="treeProps" node-key="id" default-expand-all highlight-current
                   @node-click="handleNodeClick" />
        </el-card>
      </el-col>
      <el-col :span="14">
        <el-card v-if="selectedPerm">
          <template #header>
            <span>权限详情</span>
            <div style="float:right">
              <el-button size="small" @click="openForm(selectedPerm)">编辑</el-button>
              <el-popconfirm title="确认删除?" @confirm="handleDelete(selectedPerm)">
                <template #reference>
                  <el-button size="small" type="danger">删除</el-button>
                </template>
              </el-popconfirm>
            </div>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="名称">{{ selectedPerm.name }}</el-descriptions-item>
            <el-descriptions-item label="类型">
              <el-tag :type="typeTag(selectedPerm.type)" size="small">
                {{ typeLabel(selectedPerm.type) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="权限标识">{{ selectedPerm.perms || '-' }}</el-descriptions-item>
            <el-descriptions-item label="路由路径">{{ selectedPerm.path || '-' }}</el-descriptions-item>
            <el-descriptions-item label="组件">{{ selectedPerm.component || '-' }}</el-descriptions-item>
            <el-descriptions-item label="图标">{{ selectedPerm.icon || '-' }}</el-descriptions-item>
            <el-descriptions-item label="排序">{{ selectedPerm.sort }}</el-descriptions-item>
            <el-descriptions-item label="显示">
              <el-tag :type="selectedPerm.visible === 1 ? 'success' : 'info'" size="small">
                {{ selectedPerm.visible === 1 ? '显示' : '隐藏' }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
        <el-card v-else>
          <p style="color:#909399;text-align:center">请在左侧选择一个权限节点</p>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="formVisible" :title="isEdit ? '编辑权限' : '新增权限'" width="500px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="90px">
        <el-form-item label="权限名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="权限类型" prop="type">
          <el-select v-model="form.type" style="width:100%">
            <el-option label="目录" :value="0" />
            <el-option label="菜单" :value="1" />
            <el-option label="按钮" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="上级权限">
          <el-tree-select v-model="form.parentId" :data="treeData" :props="{ label: 'name', value: 'id' }"
                          placeholder="顶级" clearable check-strictly />
        </el-form-item>
        <el-form-item label="权限标识">
          <el-input v-model="form.perms" placeholder="如 sys:user:add" />
        </el-form-item>
        <el-form-item label="路由路径" v-if="form.type === 1">
          <el-input v-model="form.path" placeholder="如 /system/user" />
        </el-form-item>
        <el-form-item label="组件路径" v-if="form.type === 1">
          <el-input v-model="form.component" placeholder="如 system/user/UserList" />
        </el-form-item>
        <el-form-item label="图标" v-if="form.type === 0 || form.type === 1">
          <el-input v-model="form.icon" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="是否显示" v-if="form.type === 0 || form.type === 1">
          <el-radio-group v-model="form.visible">
            <el-radio :value="1">显示</el-radio>
            <el-radio :value="0">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getPermissionTree, getPermission, createPermission, updatePermission, deletePermission } from '../../../api/permission'

const treeData = ref([])
const selectedPerm = ref(null)
const formVisible = ref(false)
const formRef = ref(null)
const submitting = ref(false)
const isEdit = computed(() => !!selectedPerm?.value?.id)

const treeProps = { label: 'name', children: 'children' }

const form = ref({
  name: '', type: 1, parentId: null, path: '', component: '', perms: '', icon: '', sort: 0, visible: 1
})

const formRules = {
  name: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择权限类型', trigger: 'change' }]
}

async function loadTree() {
  const res = await getPermissionTree()
  treeData.value = res.data
}

function handleNodeClick(data) {
  selectedPerm.value = data
}

function openForm(perm) {
  if (perm) {
    form.value = { ...perm }
  } else {
    form.value = { name: '', type: 1, parentId: selectedPerm.value?.id || null, path: '', component: '', perms: '', icon: '', sort: 0, visible: 1 }
  }
  formVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (selectedPerm.value?.id && isEdit.value) {
      await updatePermission(selectedPerm.value.id, form.value)
      ElMessage.success('修改成功')
    } else {
      await createPermission(form.value)
      ElMessage.success('新增成功')
    }
    formVisible.value = false
    loadTree()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(perm) {
  await deletePermission(perm.id)
  ElMessage.success('删除成功')
  selectedPerm.value = null
  loadTree()
}

function typeLabel(type) {
  return ['目录', '菜单', '按钮'][type] || '未知'
}

function typeTag(type) {
  return ['', 'success', 'warning'][type] || 'info'
}

onMounted(loadTree)
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.page-header h3 { margin: 0; }
</style>
```

- [ ] **Step 3: 更新路由**

```js
{
  path: 'system/permission',
  name: 'PermissionTree',
  component: () => import('../views/system/permission/PermissionTree.vue'),
  meta: { title: '权限管理' }
}
```

- [ ] **Step 4: 提交权限管理前端代码**

```bash
git add rbac-ui/src/api/permission.js rbac-ui/src/views/system/permission/ rbac-ui/src/router/index.js
git commit -m "feat: add permission management frontend page"
```