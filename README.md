

# ProgramBook

本项目是一个基于微信小程序的面向程序员的书城，简称“微书”。前端使用 Uniapp 框架，后端采用 SpringBoot，实现了完整的图书浏览、搜索、阅读、评论和用户管理功能。

[English README](README_en.md)

## 运行界面

|                            首页 & 书架                             |                               书城 & 分类                               |
| :----------------------------------------------------------: | :----------------------------------------------------------: |
| ![首页](https://raw.githubusercontent.com/qq13456116532/ProgramBook/main/images/image-20230404182232643.png) | ![书城](https://raw.githubusercontent.com/qq13456116532/ProgramBook/main/images/image-20230404182401471.png) |

|                         书籍详情 & 阅读器                          |                               搜索 & 用户                               |
| :----------------------------------------------------------: | :----------------------------------------------------------: |
| ![详情](https://raw.githubusercontent.com/qq13456116532/ProgramBook/main/images/image-20230404182610721.png) | ![搜索](https://raw.githubusercontent.com/qq13456116532/ProgramBook/main/images/image-20230404182651488.png) |

## 技术栈

**后端 (`afterend/ProgramBook`)**
- **核心框架:** Spring Boot 3
- **编程语言:** Java 17
- **数据库:**
    - MySQL: 存储用户信息、评论、阅读时长等关系型数据。
    - MongoDB: 存储书籍、章节等文档型数据。
    - Redis: 用于缓存、Token管理、排行榜（如热门书籍）。
- **数据访问:** MyBatis-Plus
- **对象存储:** 阿里云 OSS (用于存储用户头像)
- **构建工具:** Maven

**前端 (`frontend/book`)**
- **核心框架:** Uniapp (基于 Vue.js)
- **UI框架:** WEUI 样式 & 自定义组件
- **开发工具:** HBuilderX
- **运行环境:** 微信小程序

## 核心功能

- **用户系统:** 微信授权登录、个人信息展示与编辑。
- **书城:** 书籍搜索、分类浏览、热门推荐、排行榜。
- **书架:** 用户个人书籍收藏与管理。
- **阅读器:**
    - 沉浸式阅读体验，支持翻页、章节切换。
    - 自定义设置（字体大小、背景主题、亮度调节）。
    - 阅读历史、阅读时长记录。
- **互动系统:**
    - 书籍详情页展示，支持发表评论、回复评论。
    - 评论点赞功能。
- **任务与消息:** 每日签到、任务推荐、系统消息通知。

## 环境准备

在开始之前，请确保您已安装以下环境：
- JDK 17
- Maven 3.x
- MySQL 8.x
- MongoDB
- Redis
- HBuilderX
- 微信开发者工具

## 部署指南

### 1. 后端 (`afterend/ProgramBook`)

1.  **克隆项目**
    ```bash
    git clone https://github.com/your-username/ProgramBook.git
    cd ProgramBook/afterend/ProgramBook
    ```
2.  **数据库初始化**
    - **MySQL:** 创建名为 `programbook` 的数据库，并导入项目根目录下的 SQL 文件。
    - **MongoDB:** 创建名为 `myapp` 的数据库，并导入书籍和章节数据（`book`, `chapter` 集合）。

3.  **修改配置文件**
    打开 `src/main/resources/application.yml` 文件，根据您的本地环境修改以下配置：
    - **数据库连接:**
        - `spring.data.redis` (host, port, password)
        - `spring.data.mongodb` (uri)
        - `spring.datasource` (url, username, password)
    - **阿里云OSS:** (用于上传用户头像)
        - `aliyun.oss.endpoint`
        - `aliyun.oss.access-key-id`
        - `aliyun.oss.access-key-secret`
        - `aliyun.oss.bucket-name`
    - **微信小程序:**
        - `wechat.appid`
        - `wechat.secret`
    
    > **注意:** `application-example.yml` 提供了一个配置模板。

4.  **启动后端服务**
    使用您的 IDE (如 IntelliJ IDEA) 或通过 Maven 命令行启动 Spring Boot 应用：
    ```bash
    mvn spring-boot:run
    ```

### 2. 前端 (`frontend/book`)

1.  **打开项目**
    - 使用 HBuilderX 打开 `frontend/book` 目录。

2.  **修改API地址**
    - 打开 `frontend/book/utils/api/api.js` 文件。
    - 找到 `HOST_URL` 变量，将其值修改为您后端服务的地址（例如 `http://localhost:8080`）。

3.  **配置微信开发者工具**
    - 在 HBuilderX 中，点击菜单栏 `运行` -> `运行到小程序模拟器` -> `运行设置`，配置好您的微信开发者工具安装路径。

4.  **运行项目**
    - 点击菜单栏 `运行` -> `运行到小程序模拟器` -> `微信开发者工具`。
    - HBuilderX 将自动编译项目并在微信开发者工具中打开。
    
    ![运行到小程序](https://raw.githubusercontent.com/qq13456116532/ProgramBook/main/images/image-20230404181954029.png)

## 贡献

本人

## 许可

[GPL](https://www.gnu.org/licenses/gpl-3.0.html)