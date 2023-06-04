/*
SQLyog Ultimate v12.08 (64 bit)
MySQL - 8.0.32 : Database - programbook
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`programbook` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `programbook`;

/*Table structure for table `bookofuser` */

DROP TABLE IF EXISTS `bookofuser`;

CREATE TABLE `bookofuser` (
  `userid` varchar(255) NOT NULL,
  `bookid` varchar(255) DEFAULT NULL,
  `hasRead` int DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

/*Data for the table `bookofuser` */

insert  into `bookofuser`(`userid`,`bookid`,`hasRead`) values ('oXmrC5Ik_xXfPn3bKQbS03SC7nrs','36a57699-fa98-4825-a56a-a2490d8c02c8',1),('oXmrC5PfOfI541v3rwPEuJdNc7Ko','5110dd34-6391-4b4f-bb4f-985fe20ff59c',1),('oXmrC5Ik_xXfPn3bKQbS03SC7nrs','741d488c-47ad-4b55-9b82-26fbe52633d3',6);

/*Table structure for table `comments` */

DROP TABLE IF EXISTS `comments`;

CREATE TABLE `comments` (
  `commentid` varchar(255) NOT NULL,
  `bookid` varchar(255) NOT NULL,
  `userid` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '发布者的ID',
  `nickname` varchar(255) DEFAULT NULL,
  `reply` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '被回复的评论的nickname',
  `content` blob,
  `avatar` varchar(255) DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  `father` varchar(255) DEFAULT NULL,
  `likenum` int DEFAULT NULL,
  `length` int DEFAULT NULL,
  `isOpenMoreComment` tinyint(1) DEFAULT NULL,
  `isLike` int DEFAULT NULL,
  PRIMARY KEY (`commentid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

/*Data for the table `comments` */

insert  into `comments`(`commentid`,`bookid`,`userid`,`nickname`,`reply`,`content`,`avatar`,`time`,`father`,`likenum`,`length`,`isOpenMoreComment`,`isLike`) values ('08850911-79f0-4e66-93c3-6ab2f69b6f75','36a57699-fa98-4825-a56a-a2490d8c02c8','oXmrC5OtuBGq9dOs1dya_aG45UtY','    滴滴','irving','你很秀','https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/2c3aa4f3-93c2-4589-b50e-1dafb9b2a7b8.jpg','2023-06-02 12:49:17','ac96bf9a-8240-4979-b368-5091ff1bfc78',0,3,0,0),('308b31b9-137a-4ea4-87b9-c30069cb1d33','36a57699-fa98-4825-a56a-a2490d8c02c8','oXmrC5OtuBGq9dOs1dya_aG45UtY','    滴滴','irving','你很秀','https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/2c3aa4f3-93c2-4589-b50e-1dafb9b2a7b8.jpg','2023-06-02 12:49:02','ac96bf9a-8240-4979-b368-5091ff1bfc78',0,3,0,0),('4319c3d1-6798-4037-877a-7e47a55d777b','36a57699-fa98-4825-a56a-a2490d8c02c8','oXmrC5Ik_xXfPn3bKQbS03SC7nrs','irving',NULL,'可以的','https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/bf1e846f-6d1d-44cb-af46-737e21275219.jpg','2023-05-31 16:14:24','root',0,3,0,0),('59b1d3f1-eb4f-405b-9975-42a7626e9c58','5110dd34-6391-4b4f-bb4f-985fe20ff59c','oXmrC5Ik_xXfPn3bKQbS03SC7nrs','irving',NULL,'好书','https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/bf1e846f-6d1d-44cb-af46-737e21275219.jpg','2023-05-31 15:00:31','root',0,2,0,0),('8714c25a-c1bd-4a40-afbd-f8c82bbbc173','36a57699-fa98-4825-a56a-a2490d8c02c8','oXmrC5OtuBGq9dOs1dya_aG45UtY','    滴滴','irving','你很秀','https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/2c3aa4f3-93c2-4589-b50e-1dafb9b2a7b8.jpg','2023-06-02 12:49:09','ac96bf9a-8240-4979-b368-5091ff1bfc78',0,3,0,0),('8dada0f3-76cc-4cd3-9b03-7dd80dd72eaf','36a57699-fa98-4825-a56a-a2490d8c02c8','oXmrC5PfOfI541v3rwPEuJdNc7Ko','666','irving','666','https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/387be44d-7ec1-4d52-bc0d-8691df315448.jpg','2023-06-02 10:14:58','4319c3d1-6798-4037-877a-7e47a55d777b',0,3,0,0),('9eec21da-8941-4780-aa50-964a20b94944','36a57699-fa98-4825-a56a-a2490d8c02c8','oXmrC5PfOfI541v3rwPEuJdNc7Ko','666','irving','666','https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/387be44d-7ec1-4d52-bc0d-8691df315448.jpg','2023-06-02 10:14:49','4319c3d1-6798-4037-877a-7e47a55d777b',0,3,0,0),('a81318a1-b33e-4f22-9216-7afdfa884a63','36a57699-fa98-4825-a56a-a2490d8c02c8','oXmrC5OtuBGq9dOs1dya_aG45UtY','    滴滴','irving','你很秀','https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/2c3aa4f3-93c2-4589-b50e-1dafb9b2a7b8.jpg','2023-06-02 12:49:07','ac96bf9a-8240-4979-b368-5091ff1bfc78',0,3,0,0),('ab133e4b-14ff-4ae0-a19d-3501cf066a04','741d488c-47ad-4b55-9b82-26fbe52633d3','oXmrC5OtuBGq9dOs1dya_aG45UtY','   ',NULL,'富贵花','https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/e737c096-1303-4a82-8507-0fa4fc9df52b.jpg','2023-05-31 15:22:29','root',0,3,0,0),('ac96bf9a-8240-4979-b368-5091ff1bfc78','36a57699-fa98-4825-a56a-a2490d8c02c8','oXmrC5Ik_xXfPn3bKQbS03SC7nrs','irving',NULL,'test','https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/bf1e846f-6d1d-44cb-af46-737e21275219.jpg','2023-05-31 16:19:14','root',0,4,0,0),('b0a7839b-c49d-40aa-85e4-17812644b5e4','5110dd34-6391-4b4f-bb4f-985fe20ff59c','oXmrC5OtuBGq9dOs1dya_aG45UtY','   ','irving','规划好','https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/e737c096-1303-4a82-8507-0fa4fc9df52b.jpg','2023-05-31 15:22:48','59b1d3f1-eb4f-405b-9975-42a7626e9c58',0,3,0,0),('b375de15-5f63-4875-b105-0418427469dc','36a57699-fa98-4825-a56a-a2490d8c02c8','oXmrC5Ik_xXfPn3bKQbS03SC7nrs','irving',NULL,'test1','https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/bf1e846f-6d1d-44cb-af46-737e21275219.jpg','2023-05-31 16:26:02','root',0,5,0,0),('c69d6346-c981-44af-a312-bf9413cede46','36a57699-fa98-4825-a56a-a2490d8c02c8','oXmrC5PfOfI541v3rwPEuJdNc7Ko','666','irving','666','https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/387be44d-7ec1-4d52-bc0d-8691df315448.jpg','2023-06-02 10:14:55','4319c3d1-6798-4037-877a-7e47a55d777b',0,3,0,0);

/*Table structure for table `email` */

DROP TABLE IF EXISTS `email`;

CREATE TABLE `email` (
  `id` varchar(255) NOT NULL,
  `userid` varchar(255) DEFAULT NULL,
  `bookid` varchar(255) DEFAULT NULL,
  `commentid` varchar(255) DEFAULT NULL,
  `isRead` tinyint(1) DEFAULT '0',
  `bgImgUrl` varchar(255) DEFAULT NULL,
  `DigestAuthorName` varchar(255) DEFAULT NULL,
  `DigestAuthorImg` varchar(255) DEFAULT NULL,
  `DigestDes` char(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '宣传部 or 用户',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `contentButtonText` varchar(500) DEFAULT NULL,
  `contentButtonUrl` varchar(255) DEFAULT NULL,
  `contentButtonEventHandler` char(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'goToUrl' COMMENT 'goToUrl',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `email` */

insert  into `email`(`id`,`userid`,`bookid`,`commentid`,`isRead`,`bgImgUrl`,`DigestAuthorName`,`DigestAuthorImg`,`DigestDes`,`title`,`contentButtonText`,`contentButtonUrl`,`contentButtonEventHandler`) values ('30c03f50-12e1-4ae1-ad02-4052c72bd70b','oXmrC5Ik_xXfPn3bKQbS03SC7nrs','36a57699-fa98-4825-a56a-a2490d8c02c8','c69d6346-c981-44af-a312-bf9413cede46',0,'https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/387be44d-7ec1-4d52-bc0d-8691df315448.jpg','666','https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/387be44d-7ec1-4d52-bc0d-8691df315448.jpg','用户','666...','查看详情','../book_detail/book_detail?bookid=36a57699-fa98-4825-a56a-a2490d8c02c8','goToUrl'),('334120e1-e4b9-40ed-990f-609a3d848bd0','oXmrC5OtuBGq9dOs1dya_aG45UtY',NULL,NULL,0,'https://img0.baidu.com/it/u=1397706616,1605176518&fm=253&fmt=auto&app=138&f=JPEG?w=755&h=500','熊猫老师','https://img0.baidu.com/it/u=1397706616,1605176518&fm=253&fmt=auto&app=138&f=JPEG?w=755&h=500','宣传部','“阅读推行赠书计划”已经开始','加入计划','../shop/shop','goToUrl'),('5fb9cdd6-0a8b-488a-956e-9f761de38ee8','oXmrC5Ik_xXfPn3bKQbS03SC7nrs',NULL,NULL,0,'https://img0.baidu.com/it/u=1397706616,1605176518&fm=253&fmt=auto&app=138&f=JPEG?w=755&h=500','熊猫老师','https://img0.baidu.com/it/u=1397706616,1605176518&fm=253&fmt=auto&app=138&f=JPEG?w=755&h=500','宣传部','“阅读推行赠书计划”已经开始','加入计划','../shop/shop','goToUrl'),('7ec293b6-bc52-4ebc-bd8b-afdca6d550d4','oXmrC5PfOfI541v3rwPEuJdNc7Ko',NULL,NULL,0,'https://img0.baidu.com/it/u=1397706616,1605176518&fm=253&fmt=auto&app=138&f=JPEG?w=755&h=500','熊猫老师','https://img0.baidu.com/it/u=1397706616,1605176518&fm=253&fmt=auto&app=138&f=JPEG?w=755&h=500','宣传部','“阅读推行赠书计划”已经开始','加入计划','../shop/shop','goToUrl'),('916aced8-58a9-46d8-b1fb-1feb19982b92','oXmrC5Ik_xXfPn3bKQbS03SC7nrs','36a57699-fa98-4825-a56a-a2490d8c02c8','8714c25a-c1bd-4a40-afbd-f8c82bbbc173',1,'https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/2c3aa4f3-93c2-4589-b50e-1dafb9b2a7b8.jpg','    滴滴','https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/2c3aa4f3-93c2-4589-b50e-1dafb9b2a7b8.jpg','用户','你很秀...','查看详情','../book_detail/book_detail?bookid=36a57699-fa98-4825-a56a-a2490d8c02c8','goToUrl'),('b252526a-9d6d-4bca-b4ca-b963214f08cd','oXmrC5Ik_xXfPn3bKQbS03SC7nrs','36a57699-fa98-4825-a56a-a2490d8c02c8','308b31b9-137a-4ea4-87b9-c30069cb1d33',0,'https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/2c3aa4f3-93c2-4589-b50e-1dafb9b2a7b8.jpg','    滴滴','https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/2c3aa4f3-93c2-4589-b50e-1dafb9b2a7b8.jpg','用户','你很秀...','查看详情','../book_detail/book_detail?bookid=36a57699-fa98-4825-a56a-a2490d8c02c8','goToUrl'),('be839fce-176e-4113-8cf5-4a5bc5ac7078','oXmrC5Ik_xXfPn3bKQbS03SC7nrs','36a57699-fa98-4825-a56a-a2490d8c02c8','a81318a1-b33e-4f22-9216-7afdfa884a63',0,'https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/2c3aa4f3-93c2-4589-b50e-1dafb9b2a7b8.jpg','    滴滴','https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/2c3aa4f3-93c2-4589-b50e-1dafb9b2a7b8.jpg','用户','你很秀...','查看详情','../book_detail/book_detail?bookid=36a57699-fa98-4825-a56a-a2490d8c02c8','goToUrl'),('c4f1c69d-d6f0-4705-b73a-a5e1d55e7212','oXmrC5Ik_xXfPn3bKQbS03SC7nrs','36a57699-fa98-4825-a56a-a2490d8c02c8','8dada0f3-76cc-4cd3-9b03-7dd80dd72eaf',0,'https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/387be44d-7ec1-4d52-bc0d-8691df315448.jpg','666','https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/387be44d-7ec1-4d52-bc0d-8691df315448.jpg','用户','666...','查看详情','../book_detail/book_detail?bookid=36a57699-fa98-4825-a56a-a2490d8c02c8','goToUrl'),('cd12239c-9c77-438b-a697-46fe53cc8902','oXmrC5Ik_xXfPn3bKQbS03SC7nrs','36a57699-fa98-4825-a56a-a2490d8c02c8','9eec21da-8941-4780-aa50-964a20b94944',0,'https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/387be44d-7ec1-4d52-bc0d-8691df315448.jpg','666','https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/387be44d-7ec1-4d52-bc0d-8691df315448.jpg','用户','666...','查看详情','../book_detail/book_detail?bookid=36a57699-fa98-4825-a56a-a2490d8c02c8','goToUrl'),('faeac3e0-c18f-40cb-9ea7-9a9ad2033092','oXmrC5Ik_xXfPn3bKQbS03SC7nrs','36a57699-fa98-4825-a56a-a2490d8c02c8','08850911-79f0-4e66-93c3-6ab2f69b6f75',0,'https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/2c3aa4f3-93c2-4589-b50e-1dafb9b2a7b8.jpg','    滴滴','https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/2c3aa4f3-93c2-4589-b50e-1dafb9b2a7b8.jpg','用户','你很秀...','查看详情','../book_detail/book_detail?bookid=36a57699-fa98-4825-a56a-a2490d8c02c8','goToUrl');

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `openid` varchar(30) NOT NULL,
  `avatar` varchar(150) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `nickName` varchar(30) DEFAULT NULL,
  `hasReadTime` int NOT NULL DEFAULT '0' COMMENT '有效阅读时长',
  `continueReadDay` int DEFAULT '0' COMMENT '坚持阅读天数',
  `hasBookNum` int DEFAULT '0' COMMENT '已阅读的书籍',
  `totalEmailsNum` int DEFAULT '0' COMMENT '所有邮件',
  PRIMARY KEY (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

/*Data for the table `user` */

insert  into `user`(`openid`,`avatar`,`nickName`,`hasReadTime`,`continueReadDay`,`hasBookNum`,`totalEmailsNum`) values ('oXmrC5Ik_xXfPn3bKQbS03SC7nrs','https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/6a7afee8-3cd8-4c43-842f-243c2894e588.jpg','irving',0,1,0,0),('oXmrC5OtuBGq9dOs1dya_aG45UtY','https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/2c3aa4f3-93c2-4589-b50e-1dafb9b2a7b8.jpg','    滴滴',0,1,0,0),('oXmrC5PfOfI541v3rwPEuJdNc7Ko','https://wk-gulimall.oss-cn-beijing.aliyuncs.com/ProgramBook/387be44d-7ec1-4d52-bc0d-8691df315448.jpg','666',0,1,0,0);

/*Table structure for table `userlogin` */

DROP TABLE IF EXISTS `userlogin`;

CREATE TABLE `userlogin` (
  `userid` varchar(255) NOT NULL,
  `logindate` date DEFAULT NULL,
  `logintime` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

/*Data for the table `userlogin` */

insert  into `userlogin`(`userid`,`logindate`,`logintime`) values ('oXmrC5Ik_xXfPn3bKQbS03SC7nrs','2023-05-31','2023-05-31 14:41:36'),('494aea5a-ee68-466e-8495-8a38aa3692e4','2023-05-31','2023-05-31 14:47:50'),('2a7635d8-c869-4693-bfa5-bd3502dd43a9','2023-05-31','2023-05-31 15:01:03'),('3ef0db5b-05dd-4d1d-8219-c9d3a2459d08','2023-05-31','2023-05-31 15:13:32'),('','2023-05-31','2023-05-31 15:21:49'),('oXmrC5OtuBGq9dOs1dya_aG45UtY','2023-05-31','2023-05-31 15:22:16'),('acf99303-e153-40ea-b0c2-03d5e44dc3ac','2023-05-31','2023-05-31 16:22:14'),('3a6624b6-77d7-49a5-83b1-ae87adfcda96','2023-05-31','2023-05-31 17:13:36'),('oXmrC5Ik_xXfPn3bKQbS03SC7nrs','2023-05-31','2023-05-31 19:02:30'),('2a7635d8-c869-4693-bfa5-bd3502dd43a9','2023-06-01','2023-06-01 00:19:29'),('a90e9300-00f1-430d-8c3b-f8eba014ea90','2023-06-01','2023-06-01 12:59:19'),('fa35e2f3-7d35-4b12-9e6f-7d068c6a38c0','2023-06-01','2023-06-01 13:10:41'),('837f3cbf-e5c4-49d6-b839-2404d4c77816','2023-06-01','2023-06-01 23:14:04'),('fa35e2f3-7d35-4b12-9e6f-7d068c6a38c0','2023-06-02','2023-06-02 10:10:47'),('oXmrC5PfOfI541v3rwPEuJdNc7Ko','2023-06-02','2023-06-02 10:14:36'),('9b70384d-83b9-4948-bfdb-ba51c9821e86','2023-06-02','2023-06-02 10:51:09'),('','2023-06-02','2023-06-02 10:56:17'),('oXmrC5OtuBGq9dOs1dya_aG45UtY','2023-06-02','2023-06-02 12:48:43'),('837f3cbf-e5c4-49d6-b839-2404d4c77816','2023-06-02','2023-06-02 12:49:27'),('f7d1ef14-837b-4a81-9e53-5e7a3d931873','2023-06-02','2023-06-02 13:04:12');

/*Table structure for table `userreading` */

DROP TABLE IF EXISTS `userreading`;

CREATE TABLE `userreading` (
  `id` varchar(255) NOT NULL,
  `userid` varchar(255) DEFAULT NULL,
  `bookid` varchar(255) DEFAULT NULL,
  `start` timestamp NULL DEFAULT NULL,
  `end` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

/*Data for the table `userreading` */

insert  into `userreading`(`id`,`userid`,`bookid`,`start`,`end`) values ('024dbef8-4e84-4a3e-850b-f7d789363132','oXmrC5Ik_xXfPn3bKQbS03SC7nrs','5110dd34-6391-4b4f-bb4f-985fe20ff59c','2023-06-01 22:56:52','2023-06-01 22:57:07'),('07793ab7-c735-4380-a8b0-da3bd265b921','oXmrC5Ik_xXfPn3bKQbS03SC7nrs','741d488c-47ad-4b55-9b82-26fbe52633d3','2023-06-01 14:16:37','2023-06-01 14:17:10'),('15dab6c1-ef8d-4d2b-b11d-9da70021dbf8','oXmrC5Ik_xXfPn3bKQbS03SC7nrs','741d488c-47ad-4b55-9b82-26fbe52633d3','2023-06-02 13:04:51','2023-06-02 13:05:00'),('53969df8-06cd-4fac-bf9a-f5dd1046ba68','oXmrC5PfOfI541v3rwPEuJdNc7Ko','5110dd34-6391-4b4f-bb4f-985fe20ff59c','2023-06-02 10:15:25','2023-06-02 10:15:26'),('6808b97d-4a33-4179-a4c1-96c9496beaa4','oXmrC5Ik_xXfPn3bKQbS03SC7nrs','741d488c-47ad-4b55-9b82-26fbe52633d3','2023-06-02 13:05:13','2023-06-02 13:07:00'),('705f7ae7-4551-4164-987b-62e8dc0b1094','oXmrC5Ik_xXfPn3bKQbS03SC7nrs','741d488c-47ad-4b55-9b82-26fbe52633d3','2023-06-01 22:57:48','2023-06-01 22:57:58'),('8181f9fd-5ff1-4f57-9841-50ac60a2ce32','oXmrC5PfOfI541v3rwPEuJdNc7Ko','5110dd34-6391-4b4f-bb4f-985fe20ff59c','2023-06-02 10:15:26','2023-06-02 10:15:30'),('9b7c20ef-925b-4d2c-8c14-1a62eeaaf196','oXmrC5Ik_xXfPn3bKQbS03SC7nrs','741d488c-47ad-4b55-9b82-26fbe52633d3','2023-06-02 10:11:29','2023-06-02 10:11:43'),('b4713d55-f2f1-47e1-ab08-c0c9c61ae001','oXmrC5Ik_xXfPn3bKQbS03SC7nrs','741d488c-47ad-4b55-9b82-26fbe52633d3','2023-06-01 22:57:07','2023-06-01 22:57:42'),('e1278536-4419-47f4-badb-b83d28449caa','oXmrC5Ik_xXfPn3bKQbS03SC7nrs','5110dd34-6391-4b4f-bb4f-985fe20ff59c','2023-06-01 22:56:52','2023-06-01 22:56:53'),('eb2c1ed4-7e17-49f2-a4ad-ce4e12f8e84f','oXmrC5Ik_xXfPn3bKQbS03SC7nrs','741d488c-47ad-4b55-9b82-26fbe52633d3','2023-06-02 12:58:58','2023-06-02 12:59:08'),('f9d7ea0a-dcf8-4efe-8dd5-63e9d55af1d6','oXmrC5Ik_xXfPn3bKQbS03SC7nrs','741d488c-47ad-4b55-9b82-26fbe52633d3','2023-06-01 22:58:06','2023-06-01 22:58:24');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
