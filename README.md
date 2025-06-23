# ProgramBook

本项目是一个基于微信小程序的面向程序员的书城，简称微书。前端使用 Uniapp 框架，后端采用 SpringBoot。

[English README](README_en.md)

## 后端
后端去把 `application.yml` 中的端口、redis、mongo 和 MySQL 的地址修改一下，然后到 `com.book.controller.user.UserController` 里面把阿里云 OSS 的配置修改一下：

![Snipaste_2023-06-04_10-57-07](/images/Snipaste_2023-06-04_10-57-07.png)

之后导入 mongodb 和 MySQL 的数据，启动就可以了。

## 前端
前端的话就是直接使用 hbuilderx 打开，然后把 `/utils/api/api.js` 中的 HOST_URL 的 IP 改成你自己，之后运行到小程序模拟器

![image-20230404181954029](/images/image-20230404181954029.png)

## 项目目录
- `afterEnd/ProgramBook` 后端代码
- `frontEnd/book` 前端代码

## 主要功能
- 用户微信登录，Token 保存在 Redis
- 书籍搜索和分类、排行榜
- 阅读进度同步以及统计阅读时长
- 个人书架、评论、签到和消息提醒
- 阿里云 OSS 存储用户头像

## 运行界面：

![image-20230404182232643](/images/image-20230404182232643.png)

![image-20230404182401471](/images/image-20230404182401471.png)

![image-20230404182610721](/images/image-20230404182610721.png)

![image-20230404182651488](/images/image-20230404182651488.png)

![image-20230404182802737](/images/image-20230404182802737.png)

## 贡献

本人

## 许可

GPL
