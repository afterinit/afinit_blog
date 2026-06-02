# AFINIT BLOG

> 基于springboot开发的个人博客网站

# 一、所用技术

- 核心应用框架（Core Framework)：SpringBoot，SpringMvc
- 持久层（ORM框架）：MyBatis-Plus
- 数据库驱动：MySQL Connector
- 开发辅助与工具类库：Lombok，Hutool，baomidou
- 测试框架：Spring Boot Test
- 项目构建工具：Maven
- 版本控制工具：Git，GitHub
- 数据库： MySQL，Redis
- JDK版本：JDK21

# 二、所需配置

> **安全警告**：请妥善保管好生产环境的敏感配置（如各项 Secret Key、密码等）。在将代码提交至 GitHub 等公开远程仓库前，务必将含有真实密钥的 `application.yml` 加入 `.gitignore` 忽略名单，或改用 `application-prod.yml` 配合环境变量动态注入！

## MySQL数据库

- E-R图

  ![数据库E-R图all](https://img.afinit.top/image/数据库E-R图all.jpg)

  

- 建表SQL语句

  ```mysql
  CREATE DATABASE `afinit_blog` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
  
  USE `afinit_blog`;
  
  CREATE TABLE `tb_user` (
    `id` bigint(20) NOT NULL COMMENT '主键ID（雪花ID）',
    `username` varchar(50) NOT NULL COMMENT '用户名/账号',
    `password` varchar(100) NOT NULL COMMENT '加密后的密码',
    `nickname` varchar(50) DEFAULT NULL COMMENT '用户昵称',
    `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
    `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
    `role` tinyint(4) NOT NULL DEFAULT '0' COMMENT '角色：0-普通用户，1-管理员',
    `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '帐号状态：0-停用，1-正常',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除：1-删除，0-未删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`) COMMENT '唯一索引：防止账号重复',
    KEY `idx_status_create_time` (`status`,`create_time`) COMMENT '复合索引：方便后台按状态和时间筛选用户'
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';
  
  CREATE TABLE `tb_blog` (
    `id` bigint(20) NOT NULL COMMENT '主键ID（雪花ID）',
    `user_id` bigint(20) NOT NULL COMMENT '归属用户ID/作者ID',
    `title` varchar(128) NOT NULL COMMENT '文章标题',
    `summary` varchar(256) DEFAULT NULL COMMENT '文章摘要（用于列表展示）',
    `content` longtext NOT NULL COMMENT 'Markdown纯文本正文（含图片公网链接）',
    `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '发布状态：0-草稿，1-已发布',
    `view_count` int(11) NOT NULL DEFAULT '0' COMMENT '阅读量（实际由Redis承载高频写，定时刷入此字段）',
    `like_count` int(11) NOT NULL DEFAULT '0' COMMENT '点赞数（实际由Redis承载高频写，定时刷入此字段）',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除,1-删除,0-未删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`) COMMENT '普通索引：优化查询个人博客列表的性能',
    KEY `idx_status_create_time` (`status`,`create_time`) COMMENT '复合索引：优化首页列表的倒序查询性能'
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客文章表';
  ```

  

- yml配置

	```yaml
	spring:
  	datasource:
    		driver-class-name: com.mysql.cj.jdbc.Driver
    		url: jdbc:mysql://localhost:3306/your_database?serverTimezone=UTC
	    	username: mysql用户名
    		password: mysql密码
	```



## Redis数据库

- yml配置

  ```yaml
  spring:
    data:
      redis:
        host: 127.0.0.1
        port: 6379
        password: your-redis-password
        database: 0
        timeout: 5s
        lettuce:
          pool:
            max-active: 8
            max-idle: 8
            min-idle: 0
            max-wait: 100
  ```



## 邮箱SMTP授权（qq邮箱为例）

- 获取SEMTP授权码

  1. 电脑登录qq邮箱网页版
  2. 进入设置，账号与安全
     ![image-20260602131558257](https://img.afinit.top/image/image-20260602131558257.png)

  3. 点击开启服务，生成SMTP授权码

     ![image-20260602120259865](https://img.afinit.top/image/image-20260602120259865.png)

  4. 通过发送验证码完成认证
  5. 设置备注并复制SMTP/IMAP授权码保存好！（关闭页面后不可见）



- yml配置

  ```yaml
  spring:
    mail:
      host: smtp.qq.com
      username: your-qq-email@qq.com
      password: your-smtp-password
      default-encoding: UTF-8
      properties:
        mail:
          smtp:
            auth: true
            ssl:
              enable: true
  ```

  - username：qq邮箱
  - password：SMTP/IMAP授权码



## JWT token生成算法

- yml配置

  ```yaml
  jwt:
    secret: your-jwt-secret-key-replace-me
    expiration: 18000000
    token-type: Bearer
    header: Authorization
  ```

  - secret：随机安全字符，建议使用至少256位（32字节），可通过ai生成



## Cloudflare Turnstile人机验证

- 获取Turnstile小组件站点密钥和密钥

  1. 登录Cloudflare主页

  2. 点击应用程序安全 -> Turnstile -> 添加小组件

     ![image-20260602123020511](https://img.afinit.top/image/image-20260602123020511.png)

  3. 参数设置

     小组件名称：按自己的喜好取名

     主机名管理：添加自己的服务器（方便服务器部署）或本机localhost（方便本地测试环境），也可两个都填

     ![image-20260602123519938](https://img.afinit.top/image/image-20260602123519938.png)

  4. 点击创建
  5. 得到站点密钥和密钥
     站点密钥用于前端，以vue3项目为例，在`.env`文件中配置`VITE_TURNSTILE_SITE_KEY=#站点密钥`
     密钥用于后端，配置在yml文件中

- yml配置
  ```yaml
  #Cloudflare人机验证
  cloudflare:
    turnstile:
      # 校验接口完整URL
      verify-url: https://challenges.cloudflare.com/turnstile/v0/siteverify
      # SecretKey
      secret-key: your-turnstile-secret-key
  ```

  -  secret-key：填入密钥



## Cloudflare R2图片存储

- 获取Cloudflare R2密钥

  1. 登录Cloudflare进入主页

  2. 点击存储和数据库 -> R2对象存储 -> 概述 ->创建存储桶

     ![image-20260602124506757](https://img.afinit.top/image/image-20260602124506757.png)

  3. 设置存储桶名称，点击创建存储桶

  4. 设置公开访问

     - 有域名
       点击设置，添加自定义域：添加托管在该Cloudflare账户下域名（若没有域名可以购买后托管在Cloudflare里），建议加上前缀`img`，如`img.example.com`
       点击继续 -> 连接域
     - 无域名
       点击设置，启用公共开发URL，输入`allow`允许
       得到公共开发URL

  5. 等待初始化完成即可在对象看到公共访问已启用
     ![image-20260602125302751](https://img.afinit.top/image/image-20260602125302751.png)

  6. 获取相关密钥
     退出到R2对象存储，在账户详情点击管理
     ![image-20260602125701229](https://img.afinit.top/image/image-20260602125701229.png)

     点击创建Account API令牌

     设置令牌名称，根据自己的喜好来，**权限选择对象读和写**（重要！！！）
     然后创建Account API令牌

     ![image-20260602125920410](https://img.afinit.top/image/image-20260602125920410.png)

  7. 保存内容
     建议先保存整页内容后再进行选择填写，关闭页面后不可见

  

- yml配置

  ```yaml
  #Cloudflare R2
  r2:
    access-key: your-r2-access-key
    secret-key: your-r2-secret-key
    endpoint: https://your-account-id.r2.cloudflarestorage.com
    bucket-name: your-bucket-name
    public-domain: https://your-public-domain
  
  ```

  - access-key：对应`为S3客户端使用以下凭据`访问密钥ID
  - secret-key：对应`为S3客户端使用以下凭证`机密访问密钥
  - endpoint：对应`为 S3 客户端使用管辖权地特定的终结点`默认
  - bucket-name：对应你的R2桶名
  - public-domain：对应你的自定义域`https://img.example.com`或公共开发URL



# 返回码

## 核心规则

| **编码位**    | **含义**       | **规则说明**                                                 | **示例解析 (20011 登录成功)** |
| ------------- | -------------- | ------------------------------------------------------------ | ----------------------------- |
| **第 1-2 位** | **业务大模块** | `10`-通用基础，`20`-用户与权限，`30`-博客业务，`40`-底层基建 | **`20`** 代表这是用户模块     |
| **第 3-4 位** | **子模块分类** | `00`-核心业务，`04`-系统异常/网络拦截，`41`-认证授权，`50`-安全验证 | **`01`** 代表这是登录相关业务 |
| **第 5 位**   | **最终结果**   | `0`-操作失败，`1`-操作成功，`2~9`-具体的非预期异常原因       | **`1`** 代表最终结果是成功    |

## 1. 通用与系统级模块(`10xxx`)

| **状态码** | **英文枚举名**       | **提示信息 (Message)** | **场景说明**                   |
| ---------- | -------------------- | ---------------------- | ------------------------------ |
| **10200**  | `SUCCESS`            | 操作成功               | **【强推】全局唯一标准成功码** |
| 10401      | `PARAM_ERR`          | 参数不合法             | 前端传参格式错误、正则未通过   |
| 10402      | `PARAM_IS_BLANK`     | 缺少参数               | 必填项为空 (`@NotBlank` 拦截)  |
| 10404      | `DATA_NOT_EXIST`     | 请求的数据资源不存在   | 查数据库为空时兜底             |
| 10405      | `METHOD_NOT_ALLOWED` | 请求方法不允许         | GET/POST 用错                  |
| 10500      | `ERROR`              | 系统运行异常           | 兜底的全局严重崩溃             |
| 10502      | `SYSTEM_TIMEOUT_ERR` | 系统请求超时           | 接口响应时间过长               |
| 10599      | `SYSTEM_UNKNOWN_ERR` | 系统未知错误           | 未被捕捉到的其他异常           |
| 10600      | `BUSINESS_ERR`       | 业务处理异常           | 通用业务流转中断               |

## 2. 用户认证与安全模块(`20xxx`)

| **状态码**    | **英文枚举名**                     | **提示信息 (Message)**                 | **场景说明**                  |
| :------------ | ---------------------------------- | -------------------------------------- | ----------------------------- |
| 20011 / 20010 | `USER_LOGIN_OK / ERR`              | 登录成功 / 登录失败,请稍后再试         | 账号登录动作                  |
| 20031 / 20030 | `GET_USER_OK / ERR`                | 获取用户信息成功 / 失败                | 查个人中心数据                |
| 20041 / 20040 | `UPDATE_USER_OK / ERR`             | 更新用户信息成功 / 失败                | 改昵称、改头像                |
| 20051 / 20050 | `USER_REGISTER_OK / ERR`           | 注册成功 / 注册失败,请稍后再试         | 新用户注册动作                |
| 20052 / 20053 | `NOT_EXIST / ALREADY_EXIST`        | 用户不存在 / 用户名已被占用            | 账号冲突与状态判定            |
| 20054 / 20055 | `PASSWORD_ERR / ACCOUNT_LOCKED`    | 密码错误 / 账号已被冻结                | 鉴权失败具体原因              |
| 20411 / 20410 | `AUTH_REFRESH_OK / TOKEN_EXPIRED`  | 刷新凭证成功 / 登录已过期              | Token 生命周期                |
| 20412 / 20413 | `TOKEN_INVALID / TOKEN_MISSING`    | 凭证无效 / 缺少访问凭证                | Token 伪造或未传              |
| 20414         | `AUTH_PERMISSION_DENIED`           | 权限不足,拒绝访问                      | 越权访问拦截 (如普通用户删文) |
| 20501         | `CAPTCHA_ERR`                      | 人机验证未通过或已失效                 | Cloudflare Turnstile 拦截     |
| 20502 / 20503 | `SEND_CODE_OK / TOO_FREQUENT`      | 发送验证码成功 / 发送过于频繁          | 邮箱验证码流控防刷            |
| 20504         | `VERIFICATION_CODE_ERR`            | 验证码错误或已失效                     | 用户填错验证码                |
| 20061 / 20060 | `DELETE_USER_OK / DELETE_USER_ERR` | 删除用户成功 / 删除用户失败,请稍后再试 | 注销或删除用户                |

## 3.博客核心业务模块(`30xxx`)

| **英文枚举名**           | **状态码**    | **提示信息 (Message)**         |    **场景说明**    |
| :----------------------- | :------------ | ------------------------------ | :----------------: |
| `SAVE_OK / SAVE_ERR`     | 30011 / 30010 | 保存成功 / 保存失败,请稍后再试 |     发布新文章     |
| `DELETE_OK / DELETE_ERR` | 30021 / 30020 | 删除成功 / 删除失败,请稍后再试 |    删除已有文章    |
| `UPDATE_OK / UPDATE_ERR` | 30031 / 30030 | 更新成功 / 更新失败,请稍后再试 |    修改文章内容    |
| `GET_OK / GET_ERR`       | 30041 / 30040 | 查询成功 / 查询失败,请稍后再试 | 获取文章列表或详情 |

## 4. 底层基建模块(`40xxx`)

| **状态码** | **英文枚举名**         | **提示信息 (Message)** | **场景说明**            |
| ---------- | ---------------------- | ---------------------- | ----------------------- |
| 40001      | `REDIS_KEY_NOT_FOUND`  | 缓存key不存在          | 查 Redis 发生穿透或过期 |
| 40002      | `REDIS_CONNECTION_FLR` | 重连缓存失败           | Redis 宕机保护          |
