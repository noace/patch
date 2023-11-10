/*
SQLyog  v12.2.6 (64 bit)
MySQL - 5.5.19 : Database - his_upgrade_publish
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
USE `his_upgrade_publish`;

/*Table structure for table `gen_table` */

DROP TABLE IF EXISTS `gen_table`;

CREATE TABLE `gen_table` (
  `table_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作 sub主子表操作）',
  `package_name` varchar(100) DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) DEFAULT NULL COMMENT '生成功能作者',
  `gen_type` char(1) DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COMMENT='代码生成业务表';

/*Data for the table `gen_table` */

insert  into `gen_table`(`table_id`,`table_name`,`table_comment`,`sub_table_name`,`sub_table_fk_name`,`class_name`,`tpl_category`,`package_name`,`module_name`,`business_name`,`function_name`,`function_author`,`gen_type`,`gen_path`,`options`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) values 
(1,'up_product','产品表','',NULL,'UpProduct','crud','com.kyee.upgrade','upgrade','product','产品管理','lijunqiang','0','/','{\"parentMenuId\":\"1062\",\"treeName\":\"\",\"treeParentCode\":\"\",\"parentMenuName\":\"补丁管理\",\"treeCode\":\"\"}','admin','2021-06-10 00:04:52','','2021-06-10 00:12:29',''),
(2,'up_project','项目表','',NULL,'UpProject','crud','com.kyee.upgrade','upgrade','project','项目管理','lijunqiang','0','/','{\"parentMenuId\":\"1062\",\"treeName\":\"\",\"treeParentCode\":\"\",\"parentMenuName\":\"补丁管理\",\"treeCode\":\"\"}','admin','2021-06-10 00:54:44','','2021-06-13 10:14:18',''),
(3,'up_project_product','项目产品表','',NULL,'UpProjectProduct','crud','com.kyee.upgrade','upgrade','projectProduct','项目产品','lijunqiang','1','E:\\git_repository\\LAWU\\RuoYi\\ruoyi-admin\\src','{\"parentMenuId\":\"1071\",\"treeName\":\"\",\"treeParentCode\":\"\",\"parentMenuName\":\"项目管理\",\"treeCode\":\"\"}','admin','2021-06-10 01:01:43','','2021-06-10 12:41:47',''),
(5,'up_project_server','项目应用服务表','',NULL,'UpProjectServer','crud','com.kyee.upgrade','upgrade','projectServer','项目应用服务','lijunqiang','0','/','{\"parentMenuId\":\"1062\",\"treeName\":\"\",\"treeParentCode\":\"\",\"parentMenuName\":\"补丁管理\",\"treeCode\":\"\"}','admin','2021-06-10 22:48:59','','2021-06-10 23:03:58',''),
(9,'up_patch','补丁表','',NULL,'UpPatch','crud','com.kyee.upgrade','upgrade','patch','补丁包管理','lijunqiang','0','/','{\"parentMenuId\":\"1062\",\"treeName\":\"\",\"treeParentCode\":\"\",\"parentMenuName\":\"补丁管理\",\"treeCode\":\"\"}','admin','2021-06-12 21:32:50','','2021-06-12 22:52:20',''),
(10,'up_upgrade_record','升级记录表','up_upgrade_log','server_id','UpUpgradeRecord','crud','com.kyee.upgrade','upgrade','uprecord','升级管理','lijunqiang','0','/','{\"parentMenuId\":\"1062\",\"treeName\":\"\",\"treeParentCode\":\"\",\"parentMenuName\":\"补丁管理\",\"treeCode\":\"\"}','admin','2021-06-13 20:51:19','','2021-06-13 22:03:07',''),
(11,'up_upgrade_log','升级日志表',NULL,NULL,'UpUpgradeLog','crud','com.kyee.upgrade','upgrade','log','升级日志','lijunqiang','0','/',NULL,'admin','2021-06-13 20:54:47','',NULL,NULL);

/*Table structure for table `gen_table_column` */

DROP TABLE IF EXISTS `gen_table_column`;

CREATE TABLE `gen_table_column` (
  `column_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` varchar(64) DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) DEFAULT '' COMMENT '字典类型',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`)
) ENGINE=InnoDB AUTO_INCREMENT=123 DEFAULT CHARSET=utf8 COMMENT='代码生成业务表字段';

/*Data for the table `gen_table_column` */

insert  into `gen_table_column`(`column_id`,`table_id`,`column_name`,`column_comment`,`column_type`,`java_type`,`java_field`,`is_pk`,`is_increment`,`is_required`,`is_insert`,`is_edit`,`is_list`,`is_query`,`query_type`,`html_type`,`dict_type`,`sort`,`create_by`,`create_time`,`update_by`,`update_time`) values 
(1,'1','product_id','产品ID','bigint(5) unsigned','Integer','productId','1','1',NULL,'1',NULL,NULL,NULL,'EQ','input','',1,'admin','2021-06-10 00:04:52',NULL,'2021-06-10 00:12:29'),
(2,'1','product_name','产品名','varchar(30)','String','productName','0','0','1','1','1','1','1','LIKE','input','',2,'admin','2021-06-10 00:04:52',NULL,'2021-06-10 00:12:29'),
(4,'2','project_id','项目ID','bigint(5) unsigned','Integer','projectId','1','1',NULL,'1',NULL,NULL,NULL,'EQ','input','',1,'admin','2021-06-10 00:54:44',NULL,'2021-06-13 10:14:18'),
(5,'2','project_name','项目名','varchar(100)','String','projectName','0','0','1','1','1','1','1','LIKE','input','',2,'admin','2021-06-10 00:54:44',NULL,'2021-06-13 10:14:18'),
(6,'3','project_product_id','项目产品ID','bigint(10) unsigned','Integer','projectProductId','1','1',NULL,'1',NULL,NULL,NULL,'EQ','input','',1,'admin','2021-06-10 01:01:43',NULL,'2021-06-10 12:41:47'),
(7,'3','project_id','项目ID','bigint(5)','Integer','projectId','0','0','1','1','1','1','1','EQ','select','sys_oper_type',2,'admin','2021-06-10 01:01:43',NULL,'2021-06-10 12:41:47'),
(8,'3','product_id','产品ID','bigint(5)','Integer','productId','0','0','1','1','1','1','1','EQ','select','sys_notice_type',3,'admin','2021-06-10 01:01:43',NULL,'2021-06-10 12:41:48'),
(9,'3','source_branch_name','项目代码分支','varchar(100)','String','sourceBranchName','0','0','1','1','1','1','1','LIKE','input','',4,'admin','2021-06-10 01:01:43',NULL,'2021-06-10 12:41:48'),
(10,'3','db_version','数据库版本','varchar(20)','String','dbVersion','0','0',NULL,NULL,'1','1',NULL,'EQ','input','',5,'admin','2021-06-10 01:01:43',NULL,'2021-06-10 12:41:48'),
(11,'3','min_server_version','最小服务版本','varchar(20)','String','minServerVersion','0','0',NULL,NULL,'1','1',NULL,'EQ','input','',6,'admin','2021-06-10 01:01:43',NULL,'2021-06-10 12:41:48'),
(12,'3','max_server_version','最大服务版本','varchar(20)','String','maxServerVersion','0','0',NULL,NULL,'1','1',NULL,'EQ','input','',7,'admin','2021-06-10 01:01:43',NULL,'2021-06-10 12:41:48'),
(13,'3','last_upgrade_time','最后升级时间','datetime','Date','lastUpgradeTime','0','0',NULL,NULL,'1','1',NULL,'EQ','datetime','',8,'admin','2021-06-10 01:01:43',NULL,'2021-06-10 12:41:48'),
(20,'5','server_id','服务ID','bigint(10) unsigned','Integer','serverId','0','1','1','1','1','1',NULL,'EQ','input','',1,'admin','2021-06-10 22:48:59',NULL,'2021-06-10 23:03:58'),
(21,'5','project_id','项目ID','bigint(5)','Integer','projectId','0','0','1','1','1','1','1','EQ','select','',2,'admin','2021-06-10 22:48:59',NULL,'2021-06-10 23:03:58'),
(22,'5','product_id','产品ID','bigint(5)','Integer','productId','0','0','1','1','1','1','1','EQ','select','',3,'admin','2021-06-10 22:48:59',NULL,'2021-06-10 23:03:58'),
(23,'5','server_ip','服务IP','varchar(50)','String','serverIp','0','0','1','1','1','1','1','EQ','input','',4,'admin','2021-06-10 22:48:59',NULL,'2021-06-10 23:03:58'),
(24,'5','server_port','服务端口号','int(5)','Integer','serverPort','0','0','1','1','1','1',NULL,'EQ','input','',5,'admin','2021-06-10 22:48:59',NULL,'2021-06-10 23:03:58'),
(52,'9','patch_id','补丁ID','bigint(15) unsigned','Long','patchId','1','1',NULL,'1',NULL,NULL,NULL,'EQ','input','',1,'admin','2021-06-12 21:32:50',NULL,'2021-06-12 22:52:20'),
(53,'9','product_id','产品ID','bigint(5)','Integer','productId','0','0','1','1','1','1','1','EQ','select','',2,'admin','2021-06-12 21:32:50',NULL,'2021-06-12 22:52:20'),
(54,'9','project_id','项目ID','bigint(5)','Integer','projectId','0','0','1','1','1','1','1','EQ','select','',3,'admin','2021-06-12 21:32:50',NULL,'2021-06-12 22:52:20'),
(55,'9','patch_file_name','补丁文件名','varchar(100)','String','patchFileName','0','0','1','1','1','1','1','LIKE','input','',4,'admin','2021-06-12 21:32:50',NULL,'2021-06-12 22:52:20'),
(56,'9','patch_file_url','补丁文件URL','varchar(500)','String','patchFileUrl','0','0','1','1','1','1',NULL,'EQ','textarea','',5,'admin','2021-06-12 21:32:50',NULL,'2021-06-12 22:52:20'),
(57,'9','patch_status','补丁状态','varchar(20)','String','patchStatus','0','0',NULL,'1','1','1','1','EQ','select','up_patch_status',6,'admin','2021-06-12 21:32:50',NULL,'2021-06-12 22:52:20'),
(58,'9','bug_flag','该补丁是否有bug','char(1)','String','bugFlag','0','0',NULL,'1','1','1','1','EQ','select','sys_yes_no',7,'admin','2021-06-12 21:32:50',NULL,'2021-06-12 22:52:20'),
(59,'9','bugfix_flag','该补丁是否用于修复bug','char(1)','String','bugfixFlag','0','0',NULL,'1','1','1',NULL,'EQ','select','sys_yes_no',8,'admin','2021-06-12 21:32:50',NULL,'2021-06-12 22:52:20'),
(60,'9','bugfix_patch','修复哪个补丁号的bug','bigint(15)','Long','bugfixPatch','0','0',NULL,'1','1','1',NULL,'EQ','input','',9,'admin','2021-06-12 21:32:50',NULL,'2021-06-12 22:52:20'),
(61,'9','sql_flag','该补丁是否含SQL脚本','char(1)','String','sqlFlag','0','0',NULL,'1','1','1','1','EQ','input','',6,'','2021-06-12 22:26:58',NULL,'2021-06-12 22:52:20'),
(62,'9','create_time','创建时间','timestamp','Date','createTime','0','0','1','1',NULL,NULL,'1','EQ','datetime','',11,'','2021-06-12 22:26:58',NULL,'2021-06-12 22:52:20'),
(65,'9','task_list','任务列表','varchar(2000)','String','taskList','0','0',NULL,'1','1','1','1','LIKE','textarea','',7,'','2021-06-12 22:50:33',NULL,'2021-06-12 22:52:20'),
(66,'9','create_by','创建者','varchar(64)','String','createBy','0','0','1','1',NULL,NULL,NULL,'EQ','input','',12,'','2021-06-13 10:13:21','',NULL),
(67,'9','update_by','更新者','varchar(64)','String','updateBy','0','0','1','1','1',NULL,NULL,'EQ','input','',14,'','2021-06-13 10:13:21','',NULL),
(68,'9','update_time','更新时间','datetime','Date','updateTime','0','0','1','1','1',NULL,NULL,'EQ','datetime','',15,'','2021-06-13 10:13:21','',NULL),
(69,'9','del_flag','删除标识','char(1)','String','delFlag','0','0','1','1',NULL,NULL,NULL,'EQ','input','',16,'','2021-06-13 10:13:21','',NULL),
(70,'9','remark','备注','varchar(500)','String','remark','0','0',NULL,'1','1','1',NULL,'EQ','textarea','',17,'','2021-06-13 10:13:21','',NULL),
(71,'5','create_by','创建者','varchar(64)','String','createBy','0','0',NULL,'1',NULL,NULL,NULL,'EQ','input','',7,'','2021-06-13 10:13:24','',NULL),
(72,'5','create_time','创建时间','datetime','Date','createTime','0','0',NULL,'1',NULL,NULL,NULL,'EQ','datetime','',8,'','2021-06-13 10:13:24','',NULL),
(73,'5','update_by','更新者','varchar(64)','String','updateBy','0','0',NULL,'1','1',NULL,NULL,'EQ','input','',9,'','2021-06-13 10:13:24','',NULL),
(74,'5','update_time','更新时间','datetime','Date','updateTime','0','0',NULL,'1','1',NULL,NULL,'EQ','datetime','',10,'','2021-06-13 10:13:24','',NULL),
(75,'5','del_flag','删除标识','char(1)','String','delFlag','0','0',NULL,'1',NULL,NULL,NULL,'EQ','input','',11,'','2021-06-13 10:13:24','',NULL),
(76,'5','remark','备注','varchar(500)','String','remark','0','0',NULL,'1','1','1',NULL,'EQ','textarea','',12,'','2021-06-13 10:13:24','',NULL),
(77,'3','create_by','创建者','varchar(64)','String','createBy','0','0',NULL,'1',NULL,NULL,NULL,'EQ','input','',9,'','2021-06-13 10:13:27','',NULL),
(78,'3','create_time','创建时间','datetime','Date','createTime','0','0',NULL,'1',NULL,NULL,NULL,'EQ','datetime','',10,'','2021-06-13 10:13:27','',NULL),
(79,'3','update_by','更新者','varchar(64)','String','updateBy','0','0',NULL,'1','1',NULL,NULL,'EQ','input','',11,'','2021-06-13 10:13:27','',NULL),
(80,'3','update_time','更新时间','datetime','Date','updateTime','0','0',NULL,'1','1',NULL,NULL,'EQ','datetime','',12,'','2021-06-13 10:13:27','',NULL),
(81,'3','del_flag','删除标识','char(1)','String','delFlag','0','0',NULL,'1',NULL,NULL,NULL,'EQ','input','',13,'','2021-06-13 10:13:27','',NULL),
(82,'3','remark','备注','varchar(500)','String','remark','0','0',NULL,'1','1','1',NULL,'EQ','textarea','',14,'','2021-06-13 10:13:27','',NULL),
(83,'2','create_by','创建者','varchar(64)','String','createBy','0','0',NULL,'1',NULL,NULL,NULL,'EQ','input','',3,'','2021-06-13 10:13:29',NULL,'2021-06-13 10:14:18'),
(84,'2','create_time','创建时间','datetime','Date','createTime','0','0',NULL,'1',NULL,NULL,NULL,'EQ','datetime','',4,'','2021-06-13 10:13:29',NULL,'2021-06-13 10:14:18'),
(85,'2','update_by','更新者','varchar(64)','String','updateBy','0','0',NULL,'1','1',NULL,NULL,'EQ','input','',5,'','2021-06-13 10:13:29',NULL,'2021-06-13 10:14:18'),
(86,'2','update_time','更新时间','datetime','Date','updateTime','0','0',NULL,'1','1',NULL,NULL,'EQ','datetime','',6,'','2021-06-13 10:13:29',NULL,'2021-06-13 10:14:18'),
(87,'2','del_flag','删除标识','char(1)','String','delFlag','0','0',NULL,'1',NULL,NULL,NULL,'EQ','input','',7,'','2021-06-13 10:13:29',NULL,'2021-06-13 10:14:18'),
(88,'2','remark','备注','varchar(500)','String','remark','0','0',NULL,'1','1','1',NULL,'EQ','textarea','',8,'','2021-06-13 10:13:29',NULL,'2021-06-13 10:14:18'),
(89,'1','create_by','创建者','varchar(64)','String','createBy','0','0',NULL,'1',NULL,NULL,NULL,'EQ','input','',3,'','2021-06-13 10:13:32','',NULL),
(90,'1','create_time','创建时间','datetime','Date','createTime','0','0',NULL,'1',NULL,NULL,NULL,'EQ','datetime','',4,'','2021-06-13 10:13:32','',NULL),
(91,'1','update_by','更新者','varchar(64)','String','updateBy','0','0',NULL,'1','1',NULL,NULL,'EQ','input','',5,'','2021-06-13 10:13:32','',NULL),
(92,'1','update_time','更新时间','datetime','Date','updateTime','0','0',NULL,'1','1',NULL,NULL,'EQ','datetime','',6,'','2021-06-13 10:13:32','',NULL),
(93,'1','del_flag','删除标识','char(1)','String','delFlag','0','0',NULL,'1',NULL,NULL,NULL,'EQ','input','',7,'','2021-06-13 10:13:32','',NULL),
(94,'1','remark','产品备注','varchar(200)','String','remark','0','0',NULL,'1','1','1',NULL,'EQ','input','',8,'','2021-06-13 10:13:32','',NULL),
(95,'5','server_name','服务名称','varchar(100)','String','serverName','0','0','1','1','1','1','1','LIKE','input','',2,'','2021-06-13 11:18:25','',NULL),
(96,'10','upgrade_id','记录ID','bigint(15) unsigned','Long','upgradeId','1','1',NULL,'1',NULL,NULL,NULL,'EQ','input','',1,'admin','2021-06-13 20:51:19',NULL,'2021-06-13 22:03:07'),
(97,'10','server_id','服务ID','bigint(15)','Long','serverId','0','0','1','1','1','1','1','EQ','select','',2,'admin','2021-06-13 20:51:19',NULL,'2021-06-13 22:03:07'),
(98,'10','patch_id','补丁ID','bigint(15)','Long','patchId','0','0','1','1','1','1','1','EQ','select','',3,'admin','2021-06-13 20:51:19',NULL,'2021-06-13 22:03:07'),
(99,'10','up_status','升级状态','char(2)','String','upStatus','0','0',NULL,'1','1','1','1','EQ','select','up_upgrade_status',4,'admin','2021-06-13 20:51:19',NULL,'2021-06-13 22:03:07'),
(100,'10','up_times','升级次数','bigint(5)','Integer','upTimes','0','0',NULL,'1','1','1',NULL,'EQ','input','',5,'admin','2021-06-13 20:51:19',NULL,'2021-06-13 22:03:07'),
(101,'10','last_up_worker','最后升级人员','varchar(30)','String','lastUpWorker','0','0',NULL,'1','1','1','1','EQ','input','',6,'admin','2021-06-13 20:51:19',NULL,'2021-06-13 22:03:07'),
(102,'10','last_up_time','最后升级时间','datetime','Date','lastUpTime','0','0',NULL,'1','1','1',NULL,'EQ','datetime','',7,'admin','2021-06-13 20:51:19',NULL,'2021-06-13 22:03:07'),
(103,'10','create_by','创建者','varchar(64)','String','createBy','0','0',NULL,NULL,NULL,NULL,NULL,'EQ','input','',8,'admin','2021-06-13 20:51:19',NULL,'2021-06-13 22:03:07'),
(104,'10','create_time','创建时间','datetime','Date','createTime','0','0',NULL,NULL,NULL,NULL,NULL,'EQ','datetime','',9,'admin','2021-06-13 20:51:19',NULL,'2021-06-13 22:03:07'),
(105,'10','update_by','更新者','varchar(64)','String','updateBy','0','0',NULL,NULL,NULL,NULL,NULL,'EQ','input','',10,'admin','2021-06-13 20:51:19',NULL,'2021-06-13 22:03:07'),
(106,'10','update_time','更新时间','datetime','Date','updateTime','0','0',NULL,NULL,NULL,NULL,NULL,'EQ','datetime','',11,'admin','2021-06-13 20:51:19',NULL,'2021-06-13 22:03:07'),
(107,'10','del_flag','删除标识','char(1)','String','delFlag','0','0',NULL,NULL,NULL,NULL,NULL,'EQ','input','',12,'admin','2021-06-13 20:51:19',NULL,'2021-06-13 22:03:07'),
(108,'10','remark','备注','varchar(500)','String','remark','0','0',NULL,'1','1',NULL,NULL,'EQ','textarea','',13,'admin','2021-06-13 20:51:19',NULL,'2021-06-13 22:03:07'),
(109,'11','log_id','日志ID','bigint(15) unsigned','Long','logId','1','1',NULL,'1',NULL,NULL,NULL,'EQ','input','',1,'admin','2021-06-13 20:54:47','',NULL),
(110,'11','server_id','服务ID','bigint(15)','Long','serverId','0','0',NULL,'1','1','1','1','EQ','input','',2,'admin','2021-06-13 20:54:47','',NULL),
(111,'11','patch_id','补丁ID','bigint(15)','Long','patchId','0','0',NULL,'1','1','1','1','EQ','input','',3,'admin','2021-06-13 20:54:47','',NULL),
(112,'11','up_worker','升级人员','varchar(50)','String','upWorker','0','0',NULL,'1','1','1','1','EQ','input','',4,'admin','2021-06-13 20:54:47','',NULL),
(113,'11','up_time','升级时间','datetime','Date','upTime','0','0',NULL,'1','1','1','1','EQ','datetime','',5,'admin','2021-06-13 20:54:47','',NULL),
(114,'11','log_content','日志内容','text','String','logContent','0','0',NULL,'1','1','1','1','EQ','summernote','',6,'admin','2021-06-13 20:54:47','',NULL),
(115,'11','pre_db_version','升级前数据库版本','varchar(30)','String','preDbVersion','0','0',NULL,'1','1','1','1','EQ','input','',7,'admin','2021-06-13 20:54:47','',NULL),
(116,'11','pre_server_version','升级前服务版本','varchar(30)','String','preServerVersion','0','0',NULL,'1','1','1','1','EQ','input','',8,'admin','2021-06-13 20:54:47','',NULL),
(117,'11','create_by','创建者','varchar(64)','String','createBy','0','0',NULL,'1',NULL,NULL,NULL,'EQ','input','',9,'admin','2021-06-13 20:54:47','',NULL),
(118,'11','create_time','创建时间','datetime','Date','createTime','0','0',NULL,'1',NULL,NULL,NULL,'EQ','datetime','',10,'admin','2021-06-13 20:54:47','',NULL),
(119,'11','update_by','更新者','varchar(64)','String','updateBy','0','0',NULL,'1','1',NULL,NULL,'EQ','input','',11,'admin','2021-06-13 20:54:47','',NULL),
(120,'11','update_time','更新时间','datetime','Date','updateTime','0','0',NULL,'1','1',NULL,NULL,'EQ','datetime','',12,'admin','2021-06-13 20:54:47','',NULL),
(121,'11','del_flag','删除标识','char(1)','String','delFlag','0','0',NULL,'1',NULL,NULL,NULL,'EQ','input','',13,'admin','2021-06-13 20:54:47','',NULL),
(122,'11','remark','备注','varchar(500)','String','remark','0','0',NULL,'1','1','1',NULL,'EQ','textarea','',14,'admin','2021-06-13 20:54:47','',NULL);

/*Table structure for table `qrtz_blob_triggers` */

DROP TABLE IF EXISTS `qrtz_blob_triggers`;

CREATE TABLE `qrtz_blob_triggers` (
  `sched_name` varchar(120) NOT NULL,
  `trigger_name` varchar(200) NOT NULL,
  `trigger_group` varchar(200) NOT NULL,
  `blob_data` blob,
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_blob_triggers` */

/*Table structure for table `qrtz_calendars` */

DROP TABLE IF EXISTS `qrtz_calendars`;

CREATE TABLE `qrtz_calendars` (
  `sched_name` varchar(120) NOT NULL,
  `calendar_name` varchar(200) NOT NULL,
  `calendar` blob NOT NULL,
  PRIMARY KEY (`sched_name`,`calendar_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_calendars` */

/*Table structure for table `qrtz_cron_triggers` */

DROP TABLE IF EXISTS `qrtz_cron_triggers`;

CREATE TABLE `qrtz_cron_triggers` (
  `sched_name` varchar(120) NOT NULL,
  `trigger_name` varchar(200) NOT NULL,
  `trigger_group` varchar(200) NOT NULL,
  `cron_expression` varchar(200) NOT NULL,
  `time_zone_id` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_cron_triggers` */

insert  into `qrtz_cron_triggers`(`sched_name`,`trigger_name`,`trigger_group`,`cron_expression`,`time_zone_id`) values 
('RuoyiScheduler','TASK_CLASS_NAME1','DEFAULT','0/10 * * * * ?','Asia/Shanghai'),
('RuoyiScheduler','TASK_CLASS_NAME2','DEFAULT','0/15 * * * * ?','Asia/Shanghai'),
('RuoyiScheduler','TASK_CLASS_NAME3','DEFAULT','0/20 * * * * ?','Asia/Shanghai');

/*Table structure for table `qrtz_fired_triggers` */

DROP TABLE IF EXISTS `qrtz_fired_triggers`;

CREATE TABLE `qrtz_fired_triggers` (
  `sched_name` varchar(120) NOT NULL,
  `entry_id` varchar(95) NOT NULL,
  `trigger_name` varchar(200) NOT NULL,
  `trigger_group` varchar(200) NOT NULL,
  `instance_name` varchar(200) NOT NULL,
  `fired_time` bigint(13) NOT NULL,
  `sched_time` bigint(13) NOT NULL,
  `priority` int(11) NOT NULL,
  `state` varchar(16) NOT NULL,
  `job_name` varchar(200) DEFAULT NULL,
  `job_group` varchar(200) DEFAULT NULL,
  `is_nonconcurrent` varchar(1) DEFAULT NULL,
  `requests_recovery` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`sched_name`,`entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_fired_triggers` */

/*Table structure for table `qrtz_job_details` */

DROP TABLE IF EXISTS `qrtz_job_details`;

CREATE TABLE `qrtz_job_details` (
  `sched_name` varchar(120) NOT NULL,
  `job_name` varchar(200) NOT NULL,
  `job_group` varchar(200) NOT NULL,
  `description` varchar(250) DEFAULT NULL,
  `job_class_name` varchar(250) NOT NULL,
  `is_durable` varchar(1) NOT NULL,
  `is_nonconcurrent` varchar(1) NOT NULL,
  `is_update_data` varchar(1) NOT NULL,
  `requests_recovery` varchar(1) NOT NULL,
  `job_data` blob,
  PRIMARY KEY (`sched_name`,`job_name`,`job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_job_details` */

insert  into `qrtz_job_details`(`sched_name`,`job_name`,`job_group`,`description`,`job_class_name`,`is_durable`,`is_nonconcurrent`,`is_update_data`,`requests_recovery`,`job_data`) values 
('RuoyiScheduler','TASK_CLASS_NAME1','DEFAULT',NULL,'com.ruoyi.quartz.util.QuartzDisallowConcurrentExecution','0','1','0','0','��\0sr\0org.quartz.JobDataMap���迩��\0\0xr\0&org.quartz.utils.StringKeyDirtyFlagMap�����](\0Z\0allowsTransientDataxr\0org.quartz.utils.DirtyFlagMap�.�(v\n�\0Z\0dirtyL\0mapt\0Ljava/util/Map;xpsr\0java.util.HashMap���`�\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0TASK_PROPERTIESsr\0com.ruoyi.quartz.domain.SysJob\0\0\0\0\0\0\0\0L\0\nconcurrentt\0Ljava/lang/String;L\0cronExpressionq\0~\0	L\0invokeTargetq\0~\0	L\0jobGroupq\0~\0	L\0jobIdt\0Ljava/lang/Long;L\0jobNameq\0~\0	L\0\rmisfirePolicyq\0~\0	L\0statusq\0~\0	xr\0\'com.ruoyi.common.core.domain.BaseEntity\0\0\0\0\0\0\0\0L\0createByq\0~\0	L\0\ncreateTimet\0Ljava/util/Date;L\0delFlagq\0~\0	L\0paramsq\0~\0L\0remarkq\0~\0	L\0searchValueq\0~\0	L\0updateByq\0~\0	L\0\nupdateTimeq\0~\0xpt\0adminsr\0java.util.Datehj�KYt\0\0xpw\0\0y��(xt\0Npt\0\0pppt\01t\00/10 * * * * ?t\0ryTask.ryNoParamst\0DEFAULTsr\0java.lang.Long;��̏#�\0J\0valuexr\0java.lang.Number������\0\0xp\0\0\0\0\0\0\0t\0系统默认（无参）t\03t\01x\0'),
('RuoyiScheduler','TASK_CLASS_NAME2','DEFAULT',NULL,'com.ruoyi.quartz.util.QuartzDisallowConcurrentExecution','0','1','0','0','��\0sr\0org.quartz.JobDataMap���迩��\0\0xr\0&org.quartz.utils.StringKeyDirtyFlagMap�����](\0Z\0allowsTransientDataxr\0org.quartz.utils.DirtyFlagMap�.�(v\n�\0Z\0dirtyL\0mapt\0Ljava/util/Map;xpsr\0java.util.HashMap���`�\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0TASK_PROPERTIESsr\0com.ruoyi.quartz.domain.SysJob\0\0\0\0\0\0\0\0L\0\nconcurrentt\0Ljava/lang/String;L\0cronExpressionq\0~\0	L\0invokeTargetq\0~\0	L\0jobGroupq\0~\0	L\0jobIdt\0Ljava/lang/Long;L\0jobNameq\0~\0	L\0\rmisfirePolicyq\0~\0	L\0statusq\0~\0	xr\0\'com.ruoyi.common.core.domain.BaseEntity\0\0\0\0\0\0\0\0L\0createByq\0~\0	L\0\ncreateTimet\0Ljava/util/Date;L\0delFlagq\0~\0	L\0paramsq\0~\0L\0remarkq\0~\0	L\0searchValueq\0~\0	L\0updateByq\0~\0	L\0\nupdateTimeq\0~\0xpt\0adminsr\0java.util.Datehj�KYt\0\0xpw\0\0y��(xt\0Npt\0\0pppt\01t\00/15 * * * * ?t\0ryTask.ryParams(\'ry\')t\0DEFAULTsr\0java.lang.Long;��̏#�\0J\0valuexr\0java.lang.Number������\0\0xp\0\0\0\0\0\0\0t\0系统默认（有参）t\03t\01x\0'),
('RuoyiScheduler','TASK_CLASS_NAME3','DEFAULT',NULL,'com.ruoyi.quartz.util.QuartzDisallowConcurrentExecution','0','1','0','0','��\0sr\0org.quartz.JobDataMap���迩��\0\0xr\0&org.quartz.utils.StringKeyDirtyFlagMap�����](\0Z\0allowsTransientDataxr\0org.quartz.utils.DirtyFlagMap�.�(v\n�\0Z\0dirtyL\0mapt\0Ljava/util/Map;xpsr\0java.util.HashMap���`�\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0TASK_PROPERTIESsr\0com.ruoyi.quartz.domain.SysJob\0\0\0\0\0\0\0\0L\0\nconcurrentt\0Ljava/lang/String;L\0cronExpressionq\0~\0	L\0invokeTargetq\0~\0	L\0jobGroupq\0~\0	L\0jobIdt\0Ljava/lang/Long;L\0jobNameq\0~\0	L\0\rmisfirePolicyq\0~\0	L\0statusq\0~\0	xr\0\'com.ruoyi.common.core.domain.BaseEntity\0\0\0\0\0\0\0\0L\0createByq\0~\0	L\0\ncreateTimet\0Ljava/util/Date;L\0delFlagq\0~\0	L\0paramsq\0~\0L\0remarkq\0~\0	L\0searchValueq\0~\0	L\0updateByq\0~\0	L\0\nupdateTimeq\0~\0xpt\0adminsr\0java.util.Datehj�KYt\0\0xpw\0\0y��(xt\0Npt\0\0pppt\01t\00/20 * * * * ?t\08ryTask.ryMultipleParams(\'ry\', true, 2000L, 316.50D, 100)t\0DEFAULTsr\0java.lang.Long;��̏#�\0J\0valuexr\0java.lang.Number������\0\0xp\0\0\0\0\0\0\0t\0系统默认（多参）t\03t\01x\0');

/*Table structure for table `qrtz_locks` */

DROP TABLE IF EXISTS `qrtz_locks`;

CREATE TABLE `qrtz_locks` (
  `sched_name` varchar(120) NOT NULL,
  `lock_name` varchar(40) NOT NULL,
  PRIMARY KEY (`sched_name`,`lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_locks` */

insert  into `qrtz_locks`(`sched_name`,`lock_name`) values 
('RuoyiScheduler','STATE_ACCESS'),
('RuoyiScheduler','TRIGGER_ACCESS');

/*Table structure for table `qrtz_paused_trigger_grps` */

DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;

CREATE TABLE `qrtz_paused_trigger_grps` (
  `sched_name` varchar(120) NOT NULL,
  `trigger_group` varchar(200) NOT NULL,
  PRIMARY KEY (`sched_name`,`trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_paused_trigger_grps` */

/*Table structure for table `qrtz_scheduler_state` */

DROP TABLE IF EXISTS `qrtz_scheduler_state`;

CREATE TABLE `qrtz_scheduler_state` (
  `sched_name` varchar(120) NOT NULL,
  `instance_name` varchar(200) NOT NULL,
  `last_checkin_time` bigint(13) NOT NULL,
  `checkin_interval` bigint(13) NOT NULL,
  PRIMARY KEY (`sched_name`,`instance_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_scheduler_state` */

insert  into `qrtz_scheduler_state`(`sched_name`,`instance_name`,`last_checkin_time`,`checkin_interval`) values 
('RuoyiScheduler','KY-LJQ1623727625648',1623736614928,15000);

/*Table structure for table `qrtz_simple_triggers` */

DROP TABLE IF EXISTS `qrtz_simple_triggers`;

CREATE TABLE `qrtz_simple_triggers` (
  `sched_name` varchar(120) NOT NULL,
  `trigger_name` varchar(200) NOT NULL,
  `trigger_group` varchar(200) NOT NULL,
  `repeat_count` bigint(7) NOT NULL,
  `repeat_interval` bigint(12) NOT NULL,
  `times_triggered` bigint(10) NOT NULL,
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_simple_triggers` */

/*Table structure for table `qrtz_simprop_triggers` */

DROP TABLE IF EXISTS `qrtz_simprop_triggers`;

CREATE TABLE `qrtz_simprop_triggers` (
  `sched_name` varchar(120) NOT NULL,
  `trigger_name` varchar(200) NOT NULL,
  `trigger_group` varchar(200) NOT NULL,
  `str_prop_1` varchar(512) DEFAULT NULL,
  `str_prop_2` varchar(512) DEFAULT NULL,
  `str_prop_3` varchar(512) DEFAULT NULL,
  `int_prop_1` int(11) DEFAULT NULL,
  `int_prop_2` int(11) DEFAULT NULL,
  `long_prop_1` bigint(20) DEFAULT NULL,
  `long_prop_2` bigint(20) DEFAULT NULL,
  `dec_prop_1` decimal(13,4) DEFAULT NULL,
  `dec_prop_2` decimal(13,4) DEFAULT NULL,
  `bool_prop_1` varchar(1) DEFAULT NULL,
  `bool_prop_2` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_simprop_triggers` */

/*Table structure for table `qrtz_triggers` */

DROP TABLE IF EXISTS `qrtz_triggers`;

CREATE TABLE `qrtz_triggers` (
  `sched_name` varchar(120) NOT NULL,
  `trigger_name` varchar(200) NOT NULL,
  `trigger_group` varchar(200) NOT NULL,
  `job_name` varchar(200) NOT NULL,
  `job_group` varchar(200) NOT NULL,
  `description` varchar(250) DEFAULT NULL,
  `next_fire_time` bigint(13) DEFAULT NULL,
  `prev_fire_time` bigint(13) DEFAULT NULL,
  `priority` int(11) DEFAULT NULL,
  `trigger_state` varchar(16) NOT NULL,
  `trigger_type` varchar(8) NOT NULL,
  `start_time` bigint(13) NOT NULL,
  `end_time` bigint(13) DEFAULT NULL,
  `calendar_name` varchar(200) DEFAULT NULL,
  `misfire_instr` smallint(2) DEFAULT NULL,
  `job_data` blob,
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  KEY `sched_name` (`sched_name`,`job_name`,`job_group`),
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `qrtz_job_details` (`sched_name`, `job_name`, `job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qrtz_triggers` */

insert  into `qrtz_triggers`(`sched_name`,`trigger_name`,`trigger_group`,`job_name`,`job_group`,`description`,`next_fire_time`,`prev_fire_time`,`priority`,`trigger_state`,`trigger_type`,`start_time`,`end_time`,`calendar_name`,`misfire_instr`,`job_data`) values 
('RuoyiScheduler','TASK_CLASS_NAME1','DEFAULT','TASK_CLASS_NAME1','DEFAULT',NULL,1623727630000,-1,5,'PAUSED','CRON',1623727625000,0,NULL,2,''),
('RuoyiScheduler','TASK_CLASS_NAME2','DEFAULT','TASK_CLASS_NAME2','DEFAULT',NULL,1623727635000,-1,5,'PAUSED','CRON',1623727625000,0,NULL,2,''),
('RuoyiScheduler','TASK_CLASS_NAME3','DEFAULT','TASK_CLASS_NAME3','DEFAULT',NULL,1623727640000,-1,5,'PAUSED','CRON',1623727625000,0,NULL,2,'');

/*Table structure for table `sys_config` */

DROP TABLE IF EXISTS `sys_config`;

CREATE TABLE `sys_config` (
  `config_id` int(5) NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='参数配置表';

/*Data for the table `sys_config` */

insert  into `sys_config`(`config_id`,`config_name`,`config_key`,`config_value`,`config_type`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) values 
(1,'主框架页-默认皮肤样式名称','sys.index.skinName','skin-blue','Y','admin','2021-06-08 21:53:43','',NULL,'蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow'),
(2,'用户管理-账号初始密码','sys.user.initPassword','123456','Y','admin','2021-06-08 21:53:43','',NULL,'初始化密码 123456'),
(3,'主框架页-侧边栏主题','sys.index.sideTheme','theme-dark','Y','admin','2021-06-08 21:53:43','',NULL,'深黑主题theme-dark，浅色主题theme-light，深蓝主题theme-blue'),
(4,'账号自助-是否开启用户注册功能','sys.account.registerUser','false','Y','admin','2021-06-08 21:53:43','',NULL,'是否开启注册用户功能（true开启，false关闭）'),
(5,'用户管理-密码字符范围','sys.account.chrtype','0','Y','admin','2021-06-08 21:53:43','',NULL,'默认任意字符范围，0任意（密码可以输入任意字符），1数字（密码只能为0-9数字），2英文字母（密码只能为a-z和A-Z字母），3字母和数字（密码必须包含字母，数字）,4字母数字和特殊字符（目前支持的特殊字符包括：~!@#$%^&*()-=_+）'),
(6,'用户管理-初始密码修改策略','sys.account.initPasswordModify','0','Y','admin','2021-06-08 21:53:43','',NULL,'0：初始密码修改策略关闭，没有任何提示，1：提醒用户，如果未修改初始密码，则在登录时就会提醒修改密码对话框'),
(7,'用户管理-账号密码更新周期','sys.account.passwordValidateDays','0','Y','admin','2021-06-08 21:53:43','',NULL,'密码更新周期（填写数字，数据初始化值为0不限制，若修改必须为大于0小于365的正整数），如果超过这个周期登录系统时，则在登录时就会提醒修改密码对话框'),
(8,'主框架页-菜单导航显示风格','sys.index.menuStyle','topnav','Y','admin','2021-06-08 21:53:43','admin','2021-06-09 21:35:41','菜单导航显示风格（default为左侧导航菜单，topnav为顶部导航菜单）'),
(9,'主框架页-是否开启页脚','sys.index.ignoreFooter','true','Y','admin','2021-06-08 21:53:43','',NULL,'是否开启底部页脚显示（true显示，false隐藏）'),
(10,'补丁管理端OR升级端','up.sys.usetype','0','Y','admin','2021-06-13 17:56:23','admin','2021-06-13 18:02:55','0-管理端：配置产品、项目、补丁包\r\n1-升级端：升级操作');

/*Table structure for table `sys_dept` */

DROP TABLE IF EXISTS `sys_dept`;

CREATE TABLE `sys_dept` (
  `dept_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父部门id',
  `ancestors` varchar(50) DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) DEFAULT '' COMMENT '部门名称',
  `order_num` int(4) DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(20) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `status` char(1) DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=110 DEFAULT CHARSET=utf8 COMMENT='部门表';

/*Data for the table `sys_dept` */

insert  into `sys_dept`(`dept_id`,`parent_id`,`ancestors`,`dept_name`,`order_num`,`leader`,`phone`,`email`,`status`,`del_flag`,`create_by`,`create_time`,`update_by`,`update_time`) values 
(100,0,'0','医疗云',0,'医疗云','15899999999','lijunqiang@kyee.com.cn','0','0','admin','2021-06-08 21:53:26','admin','2021-06-08 22:17:14'),
(101,100,'0,100','云HIS研发',1,'','15888888888','ry@qq.com','0','0','admin','2021-06-08 21:53:26','admin','2021-06-08 22:17:50'),
(102,100,'0,100','实施',2,'','15888888888','ry@qq.com','0','0','admin','2021-06-08 21:53:26','admin','2021-06-08 22:18:30'),
(103,101,'0,100,101','研发部门',1,'若依','15888888888','ry@qq.com','0','0','admin','2021-06-08 21:53:26','admin','2021-06-08 22:17:50'),
(104,101,'0,100,101','市场部门',2,'若依','15888888888','ry@qq.com','0','2','admin','2021-06-08 21:53:26','admin','2021-06-08 22:17:50'),
(105,101,'0,100,101','测试部门',3,'若依','15888888888','ry@qq.com','0','0','admin','2021-06-08 21:53:26','admin','2021-06-08 22:17:50'),
(106,101,'0,100,101','财务部门',4,'若依','15888888888','ry@qq.com','0','2','admin','2021-06-08 21:53:26','admin','2021-06-08 22:17:50'),
(107,101,'0,100,101','运维部门',5,'若依','15888888888','ry@qq.com','0','2','admin','2021-06-08 21:53:26','admin','2021-06-08 22:17:50'),
(108,102,'0,100,102','市场部门',1,'若依','15888888888','ry@qq.com','0','0','admin','2021-06-08 21:53:26','admin','2021-06-08 22:18:30'),
(109,102,'0,100,102','财务部门',2,'若依','15888888888','ry@qq.com','0','0','admin','2021-06-08 21:53:26','admin','2021-06-08 22:18:30');

/*Table structure for table `sys_dict_data` */

DROP TABLE IF EXISTS `sys_dict_data`;

CREATE TABLE `sys_dict_data` (
  `dict_code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int(4) DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8 COMMENT='字典数据表';

/*Data for the table `sys_dict_data` */

insert  into `sys_dict_data`(`dict_code`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) values 
(1,1,'男','0','sys_user_sex','','','Y','0','admin','2021-06-08 21:53:40','',NULL,'性别男'),
(2,2,'女','1','sys_user_sex','','','N','0','admin','2021-06-08 21:53:40','',NULL,'性别女'),
(3,3,'未知','2','sys_user_sex','','','N','0','admin','2021-06-08 21:53:40','',NULL,'性别未知'),
(4,1,'显示','0','sys_show_hide','','primary','Y','0','admin','2021-06-08 21:53:40','',NULL,'显示菜单'),
(5,2,'隐藏','1','sys_show_hide','','danger','N','0','admin','2021-06-08 21:53:40','',NULL,'隐藏菜单'),
(6,1,'正常','0','sys_normal_disable','','primary','Y','0','admin','2021-06-08 21:53:40','',NULL,'正常状态'),
(7,2,'停用','1','sys_normal_disable','','danger','N','0','admin','2021-06-08 21:53:40','',NULL,'停用状态'),
(8,1,'正常','0','sys_job_status','','primary','Y','0','admin','2021-06-08 21:53:40','',NULL,'正常状态'),
(9,2,'暂停','1','sys_job_status','','danger','N','0','admin','2021-06-08 21:53:40','',NULL,'停用状态'),
(10,1,'默认','DEFAULT','sys_job_group','','','Y','0','admin','2021-06-08 21:53:40','',NULL,'默认分组'),
(11,2,'系统','SYSTEM','sys_job_group','','','N','0','admin','2021-06-08 21:53:40','',NULL,'系统分组'),
(12,1,'是','Y','sys_yes_no','','primary','Y','0','admin','2021-06-08 21:53:40','',NULL,'系统默认是'),
(13,2,'否','N','sys_yes_no','','danger','N','0','admin','2021-06-08 21:53:40','',NULL,'系统默认否'),
(14,1,'通知','1','sys_notice_type','','warning','Y','0','admin','2021-06-08 21:53:40','',NULL,'通知'),
(15,2,'公告','2','sys_notice_type','','success','N','0','admin','2021-06-08 21:53:40','',NULL,'公告'),
(16,1,'正常','0','sys_notice_status','','primary','Y','0','admin','2021-06-08 21:53:40','',NULL,'正常状态'),
(17,2,'关闭','1','sys_notice_status','','danger','N','0','admin','2021-06-08 21:53:40','',NULL,'关闭状态'),
(18,99,'其他','0','sys_oper_type','','info','N','0','admin','2021-06-08 21:53:41','',NULL,'其他操作'),
(19,1,'新增','1','sys_oper_type','','info','N','0','admin','2021-06-08 21:53:41','',NULL,'新增操作'),
(20,2,'修改','2','sys_oper_type','','info','N','0','admin','2021-06-08 21:53:41','',NULL,'修改操作'),
(21,3,'删除','3','sys_oper_type','','danger','N','0','admin','2021-06-08 21:53:41','',NULL,'删除操作'),
(22,4,'授权','4','sys_oper_type','','primary','N','0','admin','2021-06-08 21:53:41','',NULL,'授权操作'),
(23,5,'导出','5','sys_oper_type','','warning','N','0','admin','2021-06-08 21:53:41','',NULL,'导出操作'),
(24,6,'导入','6','sys_oper_type','','warning','N','0','admin','2021-06-08 21:53:41','',NULL,'导入操作'),
(25,7,'强退','7','sys_oper_type','','danger','N','0','admin','2021-06-08 21:53:41','',NULL,'强退操作'),
(26,8,'生成代码','8','sys_oper_type','','warning','N','0','admin','2021-06-08 21:53:41','',NULL,'生成操作'),
(27,9,'清空数据','9','sys_oper_type','','danger','N','0','admin','2021-06-08 21:53:41','',NULL,'清空操作'),
(28,1,'成功','0','sys_common_status','','primary','N','0','admin','2021-06-08 21:53:41','',NULL,'正常状态'),
(29,2,'失败','1','sys_common_status','','danger','N','0','admin','2021-06-08 21:53:41','',NULL,'停用状态'),
(30,10,'登记','10','up_patch_status','','default','Y','0','admin','2021-06-12 21:19:20','admin','2021-06-12 21:25:40',''),
(31,20,'打包','20','up_patch_status','','','Y','0','admin','2021-06-12 21:19:42','admin','2021-06-12 21:25:45',''),
(32,30,'部分升级','30','up_patch_status',NULL,'default','Y','0','admin','2021-06-12 21:21:30','',NULL,NULL),
(33,40,'全部升级','40','up_patch_status',NULL,NULL,'Y','0','admin','2021-06-12 21:25:36','',NULL,NULL),
(34,50,'回退','50','up_patch_status',NULL,NULL,'Y','0','admin','2021-06-12 21:26:03','',NULL,NULL),
(35,60,'作废','60','up_patch_status',NULL,NULL,'Y','0','admin','2021-06-12 21:26:15','',NULL,NULL),
(36,70,'关闭','70','up_patch_status',NULL,NULL,'Y','0','admin','2021-06-12 21:26:47','',NULL,NULL),
(37,15,'发布','15','up_patch_status',NULL,NULL,'Y','0','admin','2021-06-13 17:41:00','',NULL,NULL),
(38,10,'未升级','10','up_upgrade_status',NULL,NULL,'Y','0','admin','2021-06-13 19:15:27','',NULL,NULL),
(39,20,'已升级','20','up_upgrade_status',NULL,NULL,'Y','0','admin','2021-06-13 19:15:40','',NULL,NULL),
(40,80,'已回退','80','up_upgrade_status',NULL,NULL,'Y','0','admin','2021-06-13 19:16:03','',NULL,NULL);

/*Table structure for table `sys_dict_type` */

DROP TABLE IF EXISTS `sys_dict_type`;

CREATE TABLE `sys_dict_type` (
  `dict_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`),
  UNIQUE KEY `dict_type` (`dict_type`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COMMENT='字典类型表';

/*Data for the table `sys_dict_type` */

insert  into `sys_dict_type`(`dict_id`,`dict_name`,`dict_type`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) values 
(1,'用户性别','sys_user_sex','0','admin','2021-06-08 21:53:38','',NULL,'用户性别列表'),
(2,'菜单状态','sys_show_hide','0','admin','2021-06-08 21:53:38','',NULL,'菜单状态列表'),
(3,'系统开关','sys_normal_disable','0','admin','2021-06-08 21:53:38','',NULL,'系统开关列表'),
(4,'任务状态','sys_job_status','0','admin','2021-06-08 21:53:38','',NULL,'任务状态列表'),
(5,'任务分组','sys_job_group','0','admin','2021-06-08 21:53:38','',NULL,'任务分组列表'),
(6,'系统是否','sys_yes_no','0','admin','2021-06-08 21:53:38','',NULL,'系统是否列表'),
(7,'通知类型','sys_notice_type','0','admin','2021-06-08 21:53:38','',NULL,'通知类型列表'),
(8,'通知状态','sys_notice_status','0','admin','2021-06-08 21:53:38','',NULL,'通知状态列表'),
(9,'操作类型','sys_oper_type','0','admin','2021-06-08 21:53:38','',NULL,'操作类型列表'),
(10,'系统状态','sys_common_status','0','admin','2021-06-08 21:53:38','',NULL,'登录状态列表'),
(11,'补丁包状态','up_patch_status','0','admin','2021-06-12 21:17:32','',NULL,NULL),
(12,'升级状态','up_upgrade_status','0','admin','2021-06-13 19:14:56','',NULL,NULL);

/*Table structure for table `sys_job` */

DROP TABLE IF EXISTS `sys_job`;

CREATE TABLE `sys_job` (
  `job_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`,`job_name`,`job_group`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='定时任务调度表';

/*Data for the table `sys_job` */

insert  into `sys_job`(`job_id`,`job_name`,`job_group`,`invoke_target`,`cron_expression`,`misfire_policy`,`concurrent`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) values 
(1,'系统默认（无参）','DEFAULT','ryTask.ryNoParams','0/10 * * * * ?','3','1','1','admin','2021-06-08 21:53:45','',NULL,''),
(2,'系统默认（有参）','DEFAULT','ryTask.ryParams(\'ry\')','0/15 * * * * ?','3','1','1','admin','2021-06-08 21:53:45','',NULL,''),
(3,'系统默认（多参）','DEFAULT','ryTask.ryMultipleParams(\'ry\', true, 2000L, 316.50D, 100)','0/20 * * * * ?','3','1','1','admin','2021-06-08 21:53:45','',NULL,'');

/*Table structure for table `sys_job_log` */

DROP TABLE IF EXISTS `sys_job_log`;

CREATE TABLE `sys_job_log` (
  `job_log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) DEFAULT NULL COMMENT '日志信息',
  `status` char(1) DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) DEFAULT '' COMMENT '异常信息',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='定时任务调度日志表';

/*Data for the table `sys_job_log` */

/*Table structure for table `sys_logininfor` */

DROP TABLE IF EXISTS `sys_logininfor`;

CREATE TABLE `sys_logininfor` (
  `info_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `login_name` varchar(50) DEFAULT '' COMMENT '登录账号',
  `ipaddr` varchar(128) DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) DEFAULT '' COMMENT '操作系统',
  `status` char(1) DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) DEFAULT '' COMMENT '提示消息',
  `login_time` datetime DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`)
) ENGINE=InnoDB AUTO_INCREMENT=197 DEFAULT CHARSET=utf8 COMMENT='系统访问记录';

/*Data for the table `sys_logininfor` */

insert  into `sys_logininfor`(`info_id`,`login_name`,`ipaddr`,`login_location`,`browser`,`os`,`status`,`msg`,`login_time`) values 
(100,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','1','验证码错误','2021-06-08 22:11:51'),
(101,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-08 22:11:55'),
(102,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','退出成功','2021-06-08 23:19:13'),
(103,'ceshi','127.0.0.1','内网IP','Chrome 9','Windows 10','1','密码输入错误1次','2021-06-08 23:19:25'),
(104,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-08 23:20:00'),
(105,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','退出成功','2021-06-08 23:20:20'),
(106,'ceshi','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-08 23:20:34'),
(107,'ceshi','127.0.0.1','内网IP','Chrome 9','Windows 10','0','退出成功','2021-06-08 23:33:18'),
(108,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-08 23:33:22'),
(109,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-09 08:36:17'),
(110,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','退出成功','2021-06-09 08:38:46'),
(111,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-09 08:38:50'),
(112,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-09 12:52:55'),
(113,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-09 12:55:04'),
(114,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-09 20:48:38'),
(115,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-09 21:30:28'),
(116,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','退出成功','2021-06-09 21:33:03'),
(117,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-09 21:35:09'),
(118,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-09 22:25:17'),
(119,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-10 00:04:07'),
(120,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-10 00:24:46'),
(121,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-10 00:53:18'),
(122,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-10 00:57:46'),
(123,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-10 01:11:52'),
(124,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','退出成功','2021-06-10 01:17:33'),
(125,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-10 12:33:25'),
(126,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','1','验证码错误','2021-06-10 12:48:48'),
(127,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-10 12:48:53'),
(128,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-10 13:51:44'),
(129,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','退出成功','2021-06-10 13:51:48'),
(130,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-10 13:51:55'),
(131,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-10 14:39:42'),
(132,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-10 14:57:17'),
(133,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-10 22:23:21'),
(134,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','1','验证码错误','2021-06-10 22:31:01'),
(135,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-10 22:31:04'),
(136,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-10 22:33:06'),
(137,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-10 22:57:49'),
(138,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-10 23:03:21'),
(139,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-12 20:39:20'),
(140,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','1','验证码错误','2021-06-12 20:51:29'),
(141,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-12 20:51:33'),
(142,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-12 20:55:54'),
(143,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','1','验证码错误','2021-06-12 20:57:21'),
(144,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-12 20:57:25'),
(145,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-12 20:59:07'),
(146,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-12 21:04:21'),
(147,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-12 21:11:22'),
(148,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-12 21:45:47'),
(149,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-12 21:55:48'),
(150,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-12 22:02:14'),
(151,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-12 22:14:20'),
(152,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-12 22:26:51'),
(153,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-12 23:13:50'),
(154,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-12 23:17:57'),
(155,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-12 23:34:56'),
(156,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-12 23:38:06'),
(157,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-12 23:40:17'),
(158,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-13 10:13:11'),
(159,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-13 10:23:11'),
(160,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','1','验证码错误','2021-06-13 10:44:43'),
(161,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-13 10:44:47'),
(162,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-13 10:49:36'),
(163,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-13 11:27:49'),
(164,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-13 11:34:35'),
(165,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-13 11:40:02'),
(166,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-13 11:45:59'),
(167,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-13 11:48:54'),
(168,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-13 11:50:20'),
(169,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-13 12:10:19'),
(170,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-13 12:20:20'),
(171,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-13 12:22:00'),
(172,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-13 13:15:25'),
(173,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','退出成功','2021-06-13 13:19:11'),
(174,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','1','验证码错误','2021-06-13 17:20:10'),
(175,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-13 17:21:14'),
(176,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-13 18:33:18'),
(177,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-13 19:14:07'),
(178,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-13 20:45:12'),
(179,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','退出成功','2021-06-13 21:15:09'),
(180,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-13 21:15:12'),
(181,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','退出成功','2021-06-13 21:17:24'),
(182,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-13 21:17:27'),
(183,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','退出成功','2021-06-13 21:18:21'),
(184,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-13 21:18:24'),
(185,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-13 21:24:19'),
(186,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-13 21:57:56'),
(187,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-13 22:04:59'),
(188,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-13 22:27:03'),
(189,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-13 22:30:48'),
(190,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-13 23:41:21'),
(191,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-14 00:03:15'),
(192,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-14 09:30:23'),
(193,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-14 12:50:39'),
(194,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-15 11:20:20'),
(195,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-15 11:27:20'),
(196,'admin','127.0.0.1','内网IP','Chrome 9','Windows 10','0','登录成功','2021-06-15 11:41:15');

/*Table structure for table `sys_menu` */

DROP TABLE IF EXISTS `sys_menu`;

CREATE TABLE `sys_menu` (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父菜单ID',
  `order_num` int(4) DEFAULT '0' COMMENT '显示顺序',
  `url` varchar(200) DEFAULT '#' COMMENT '请求地址',
  `target` varchar(20) DEFAULT '' COMMENT '打开方式（menuItem页签 menuBlank新窗口）',
  `menu_type` char(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `is_refresh` char(1) DEFAULT '1' COMMENT '是否刷新（0刷新 1不刷新）',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1101 DEFAULT CHARSET=utf8 COMMENT='菜单权限表';

/*Data for the table `sys_menu` */

insert  into `sys_menu`(`menu_id`,`menu_name`,`parent_id`,`order_num`,`url`,`target`,`menu_type`,`visible`,`is_refresh`,`perms`,`icon`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) values 
(1,'系统管理',0,10,'#','menuItem','M','0','1','','fa fa-gear','admin','2021-06-08 21:53:28','admin','2021-06-10 00:08:44','系统管理目录'),
(2,'系统监控',0,90,'#','menuItem','M','0','1','','fa fa-video-camera','admin','2021-06-08 21:53:28','admin','2021-06-10 00:08:54','系统监控目录'),
(3,'系统工具',0,30,'#','menuItem','M','0','1','','fa fa-bars','admin','2021-06-08 21:53:28','admin','2021-06-10 00:09:13','系统工具目录'),
(4,'演示环境',0,999,'https://demo.kyee.com.cn/auth_web/new_login','menuBlank','C','1','1','','fa fa-location-arrow','admin','2021-06-08 21:53:28','admin','2021-06-13 21:21:05','演示环境地址'),
(100,'用户管理',1,1,'/system/user','','C','0','1','system:user:view','fa fa-user-o','admin','2021-06-08 21:53:28','',NULL,'用户管理菜单'),
(101,'角色管理',1,2,'/system/role','','C','0','1','system:role:view','fa fa-user-secret','admin','2021-06-08 21:53:28','',NULL,'角色管理菜单'),
(102,'菜单管理',1,3,'/system/menu','','C','0','1','system:menu:view','fa fa-th-list','admin','2021-06-08 21:53:28','',NULL,'菜单管理菜单'),
(103,'部门管理',1,4,'/system/dept','','C','0','1','system:dept:view','fa fa-outdent','admin','2021-06-08 21:53:28','',NULL,'部门管理菜单'),
(104,'岗位管理',1,5,'/system/post','','C','0','1','system:post:view','fa fa-address-card-o','admin','2021-06-08 21:53:28','',NULL,'岗位管理菜单'),
(105,'字典管理',1,6,'/system/dict','','C','0','1','system:dict:view','fa fa-bookmark-o','admin','2021-06-08 21:53:28','',NULL,'字典管理菜单'),
(106,'参数设置',1,7,'/system/config','','C','0','1','system:config:view','fa fa-sun-o','admin','2021-06-08 21:53:28','',NULL,'参数设置菜单'),
(107,'通知公告',1,8,'/system/notice','','C','0','1','system:notice:view','fa fa-bullhorn','admin','2021-06-08 21:53:28','',NULL,'通知公告菜单'),
(108,'日志管理',1,9,'#','','M','0','1','','fa fa-pencil-square-o','admin','2021-06-08 21:53:28','',NULL,'日志管理菜单'),
(109,'在线用户',2,1,'/monitor/online','','C','0','1','monitor:online:view','fa fa-user-circle','admin','2021-06-08 21:53:28','',NULL,'在线用户菜单'),
(110,'定时任务',2,2,'/monitor/job','','C','0','1','monitor:job:view','fa fa-tasks','admin','2021-06-08 21:53:28','',NULL,'定时任务菜单'),
(111,'数据监控',2,3,'/monitor/data','','C','0','1','monitor:data:view','fa fa-bug','admin','2021-06-08 21:53:28','',NULL,'数据监控菜单'),
(112,'服务监控',2,4,'/monitor/server','','C','0','1','monitor:server:view','fa fa-server','admin','2021-06-08 21:53:28','',NULL,'服务监控菜单'),
(113,'缓存监控',2,5,'/monitor/cache','','C','0','1','monitor:cache:view','fa fa-cube','admin','2021-06-08 21:53:28','',NULL,'缓存监控菜单'),
(114,'表单构建',3,1,'/tool/build','','C','0','1','tool:build:view','fa fa-wpforms','admin','2021-06-08 21:53:28','',NULL,'表单构建菜单'),
(115,'代码生成',3,2,'/tool/gen','','C','0','1','tool:gen:view','fa fa-code','admin','2021-06-08 21:53:28','',NULL,'代码生成菜单'),
(116,'系统接口',3,3,'/tool/swagger','','C','0','1','tool:swagger:view','fa fa-gg','admin','2021-06-08 21:53:28','',NULL,'系统接口菜单'),
(500,'操作日志',108,1,'/monitor/operlog','','C','0','1','monitor:operlog:view','fa fa-address-book','admin','2021-06-08 21:53:28','',NULL,'操作日志菜单'),
(501,'登录日志',108,2,'/monitor/logininfor','','C','0','1','monitor:logininfor:view','fa fa-file-image-o','admin','2021-06-08 21:53:28','',NULL,'登录日志菜单'),
(1000,'用户查询',100,1,'#','','F','0','1','system:user:list','#','admin','2021-06-08 21:53:28','',NULL,''),
(1001,'用户新增',100,2,'#','','F','0','1','system:user:add','#','admin','2021-06-08 21:53:28','',NULL,''),
(1002,'用户修改',100,3,'#','','F','0','1','system:user:edit','#','admin','2021-06-08 21:53:28','',NULL,''),
(1003,'用户删除',100,4,'#','','F','0','1','system:user:remove','#','admin','2021-06-08 21:53:28','',NULL,''),
(1004,'用户导出',100,5,'#','','F','0','1','system:user:export','#','admin','2021-06-08 21:53:28','',NULL,''),
(1005,'用户导入',100,6,'#','','F','0','1','system:user:import','#','admin','2021-06-08 21:53:28','',NULL,''),
(1006,'重置密码',100,7,'#','','F','0','1','system:user:resetPwd','#','admin','2021-06-08 21:53:28','',NULL,''),
(1007,'角色查询',101,1,'#','','F','0','1','system:role:list','#','admin','2021-06-08 21:53:28','',NULL,''),
(1008,'角色新增',101,2,'#','','F','0','1','system:role:add','#','admin','2021-06-08 21:53:28','',NULL,''),
(1009,'角色修改',101,3,'#','','F','0','1','system:role:edit','#','admin','2021-06-08 21:53:28','',NULL,''),
(1010,'角色删除',101,4,'#','','F','0','1','system:role:remove','#','admin','2021-06-08 21:53:28','',NULL,''),
(1011,'角色导出',101,5,'#','','F','0','1','system:role:export','#','admin','2021-06-08 21:53:28','',NULL,''),
(1012,'菜单查询',102,1,'#','','F','0','1','system:menu:list','#','admin','2021-06-08 21:53:28','',NULL,''),
(1013,'菜单新增',102,2,'#','','F','0','1','system:menu:add','#','admin','2021-06-08 21:53:28','',NULL,''),
(1014,'菜单修改',102,3,'#','','F','0','1','system:menu:edit','#','admin','2021-06-08 21:53:28','',NULL,''),
(1015,'菜单删除',102,4,'#','','F','0','1','system:menu:remove','#','admin','2021-06-08 21:53:28','',NULL,''),
(1016,'部门查询',103,1,'#','','F','0','1','system:dept:list','#','admin','2021-06-08 21:53:28','',NULL,''),
(1017,'部门新增',103,2,'#','','F','0','1','system:dept:add','#','admin','2021-06-08 21:53:28','',NULL,''),
(1018,'部门修改',103,3,'#','','F','0','1','system:dept:edit','#','admin','2021-06-08 21:53:28','',NULL,''),
(1019,'部门删除',103,4,'#','','F','0','1','system:dept:remove','#','admin','2021-06-08 21:53:28','',NULL,''),
(1020,'岗位查询',104,1,'#','','F','0','1','system:post:list','#','admin','2021-06-08 21:53:28','',NULL,''),
(1021,'岗位新增',104,2,'#','','F','0','1','system:post:add','#','admin','2021-06-08 21:53:28','',NULL,''),
(1022,'岗位修改',104,3,'#','','F','0','1','system:post:edit','#','admin','2021-06-08 21:53:28','',NULL,''),
(1023,'岗位删除',104,4,'#','','F','0','1','system:post:remove','#','admin','2021-06-08 21:53:28','',NULL,''),
(1024,'岗位导出',104,5,'#','','F','0','1','system:post:export','#','admin','2021-06-08 21:53:28','',NULL,''),
(1025,'字典查询',105,1,'#','','F','0','1','system:dict:list','#','admin','2021-06-08 21:53:28','',NULL,''),
(1026,'字典新增',105,2,'#','','F','0','1','system:dict:add','#','admin','2021-06-08 21:53:28','',NULL,''),
(1027,'字典修改',105,3,'#','','F','0','1','system:dict:edit','#','admin','2021-06-08 21:53:28','',NULL,''),
(1028,'字典删除',105,4,'#','','F','0','1','system:dict:remove','#','admin','2021-06-08 21:53:28','',NULL,''),
(1029,'字典导出',105,5,'#','','F','0','1','system:dict:export','#','admin','2021-06-08 21:53:28','',NULL,''),
(1030,'参数查询',106,1,'#','','F','0','1','system:config:list','#','admin','2021-06-08 21:53:28','',NULL,''),
(1031,'参数新增',106,2,'#','','F','0','1','system:config:add','#','admin','2021-06-08 21:53:28','',NULL,''),
(1032,'参数修改',106,3,'#','','F','0','1','system:config:edit','#','admin','2021-06-08 21:53:28','',NULL,''),
(1033,'参数删除',106,4,'#','','F','0','1','system:config:remove','#','admin','2021-06-08 21:53:28','',NULL,''),
(1034,'参数导出',106,5,'#','','F','0','1','system:config:export','#','admin','2021-06-08 21:53:28','',NULL,''),
(1035,'公告查询',107,1,'#','','F','0','1','system:notice:list','#','admin','2021-06-08 21:53:28','',NULL,''),
(1036,'公告新增',107,2,'#','','F','0','1','system:notice:add','#','admin','2021-06-08 21:53:28','',NULL,''),
(1037,'公告修改',107,3,'#','','F','0','1','system:notice:edit','#','admin','2021-06-08 21:53:28','',NULL,''),
(1038,'公告删除',107,4,'#','','F','0','1','system:notice:remove','#','admin','2021-06-08 21:53:28','',NULL,''),
(1039,'操作查询',500,1,'#','','F','0','1','monitor:operlog:list','#','admin','2021-06-08 21:53:28','',NULL,''),
(1040,'操作删除',500,2,'#','','F','0','1','monitor:operlog:remove','#','admin','2021-06-08 21:53:28','',NULL,''),
(1041,'详细信息',500,3,'#','','F','0','1','monitor:operlog:detail','#','admin','2021-06-08 21:53:28','',NULL,''),
(1042,'日志导出',500,4,'#','','F','0','1','monitor:operlog:export','#','admin','2021-06-08 21:53:28','',NULL,''),
(1043,'登录查询',501,1,'#','','F','0','1','monitor:logininfor:list','#','admin','2021-06-08 21:53:28','',NULL,''),
(1044,'登录删除',501,2,'#','','F','0','1','monitor:logininfor:remove','#','admin','2021-06-08 21:53:28','',NULL,''),
(1045,'日志导出',501,3,'#','','F','0','1','monitor:logininfor:export','#','admin','2021-06-08 21:53:28','',NULL,''),
(1046,'账户解锁',501,4,'#','','F','0','1','monitor:logininfor:unlock','#','admin','2021-06-08 21:53:28','',NULL,''),
(1047,'在线查询',109,1,'#','','F','0','1','monitor:online:list','#','admin','2021-06-08 21:53:28','',NULL,''),
(1048,'批量强退',109,2,'#','','F','0','1','monitor:online:batchForceLogout','#','admin','2021-06-08 21:53:28','',NULL,''),
(1049,'单条强退',109,3,'#','','F','0','1','monitor:online:forceLogout','#','admin','2021-06-08 21:53:28','',NULL,''),
(1050,'任务查询',110,1,'#','','F','0','1','monitor:job:list','#','admin','2021-06-08 21:53:28','',NULL,''),
(1051,'任务新增',110,2,'#','','F','0','1','monitor:job:add','#','admin','2021-06-08 21:53:28','',NULL,''),
(1052,'任务修改',110,3,'#','','F','0','1','monitor:job:edit','#','admin','2021-06-08 21:53:28','',NULL,''),
(1053,'任务删除',110,4,'#','','F','0','1','monitor:job:remove','#','admin','2021-06-08 21:53:28','',NULL,''),
(1054,'状态修改',110,5,'#','','F','0','1','monitor:job:changeStatus','#','admin','2021-06-08 21:53:28','',NULL,''),
(1055,'任务详细',110,6,'#','','F','0','1','monitor:job:detail','#','admin','2021-06-08 21:53:28','',NULL,''),
(1056,'任务导出',110,7,'#','','F','0','1','monitor:job:export','#','admin','2021-06-08 21:53:28','',NULL,''),
(1057,'生成查询',115,1,'#','','F','0','1','tool:gen:list','#','admin','2021-06-08 21:53:28','',NULL,''),
(1058,'生成修改',115,2,'#','','F','0','1','tool:gen:edit','#','admin','2021-06-08 21:53:28','',NULL,''),
(1059,'生成删除',115,3,'#','','F','0','1','tool:gen:remove','#','admin','2021-06-08 21:53:28','',NULL,''),
(1060,'预览代码',115,4,'#','','F','0','1','tool:gen:preview','#','admin','2021-06-08 21:53:28','',NULL,''),
(1061,'生成代码',115,5,'#','','F','0','1','tool:gen:code','#','admin','2021-06-08 21:53:28','',NULL,''),
(1062,'补丁管理',0,20,'#','menuItem','M','0','1','','fa fa-rocket','admin','2021-06-10 00:08:21','admin','2021-06-10 00:09:03',''),
(1065,'产品管理',1062,1,'/upgrade/product','menuItem','C','0','1','upgrade:product:view','fa fa-cart-plus','admin','2021-06-10 00:13:40','admin','2021-06-13 17:30:44','产品管理菜单'),
(1066,'产品查询',1065,1,'#','menuItem','F','0','1','upgrade:product:list','#','admin','2021-06-10 00:13:40','admin','2021-06-10 00:58:21',''),
(1067,'产品新增',1065,2,'#','menuItem','F','0','1','upgrade:product:add','#','admin','2021-06-10 00:13:40','admin','2021-06-10 00:58:35',''),
(1068,'产品修改',1065,3,'#','menuItem','F','0','1','upgrade:product:edit','#','admin','2021-06-10 00:13:40','admin','2021-06-10 00:58:47',''),
(1069,'产品删除',1065,4,'#','menuItem','F','0','1','upgrade:product:remove','#','admin','2021-06-10 00:13:40','admin','2021-06-10 00:58:56',''),
(1070,'产品导出',1065,5,'#','menuItem','F','0','1','upgrade:product:export','#','admin','2021-06-10 00:13:40','admin','2021-06-10 00:59:05',''),
(1071,'项目管理',1062,1,'/upgrade/project','menuItem','C','0','1','upgrade:project:view','fa fa-handshake-o','admin','2021-06-10 00:56:24','admin','2021-06-13 17:31:39','项目菜单'),
(1072,'项目查询',1071,1,'#','menuItem','F','0','1','upgrade:project:list','#','admin','2021-06-10 00:56:24','',NULL,''),
(1073,'项目新增',1071,2,'#','menuItem','F','0','1','upgrade:project:add','#','admin','2021-06-10 00:56:24','',NULL,''),
(1074,'项目修改',1071,3,'#','menuItem','F','0','1','upgrade:project:edit','#','admin','2021-06-10 00:56:24','',NULL,''),
(1075,'项目删除',1071,4,'#','menuItem','F','0','1','upgrade:project:remove','#','admin','2021-06-10 00:56:24','',NULL,''),
(1076,'项目导出',1071,5,'#','menuItem','F','0','1','upgrade:project:export','#','admin','2021-06-10 00:56:24','',NULL,''),
(1077,'项目产品管理',1062,1,'/upgrade/projectProduct','menuItem','C','0','1','upgrade:projectProduct:view','fa fa-asl-interpreting','admin','2021-06-10 01:10:58','admin','2021-06-13 17:32:13','项目产品菜单'),
(1078,'项目产品查询',1077,1,'#','menuItem','F','0','1','upgrade:projectProduct:list','#','admin','2021-06-10 01:10:58','',NULL,''),
(1079,'项目产品新增',1077,2,'#','menuItem','F','0','1','upgrade:projectProduct:add','#','admin','2021-06-10 01:10:58','',NULL,''),
(1080,'项目产品修改',1077,3,'#','menuItem','F','0','1','upgrade:projectProduct:edit','#','admin','2021-06-10 01:10:58','',NULL,''),
(1081,'项目产品删除',1077,4,'#','menuItem','F','0','1','upgrade:projectProduct:remove','#','admin','2021-06-10 01:10:58','',NULL,''),
(1082,'项目产品导出',1077,5,'#','menuItem','F','0','1','upgrade:projectProduct:export','#','admin','2021-06-10 01:10:58','',NULL,''),
(1083,'项目应用服务',1062,1,'/upgrade/projectServer','menuItem','C','0','1','upgrade:projectServer:view','fa fa-server','admin','2021-06-10 23:04:46','admin','2021-06-13 17:33:02','项目应用服务菜单'),
(1084,'项目应用服务查询',1083,1,'#','','F','0','1','upgrade:projectServer:list','#','admin','2021-06-10 23:04:46','',NULL,''),
(1085,'项目应用服务新增',1083,2,'#','','F','0','1','upgrade:projectServer:add','#','admin','2021-06-10 23:04:46','',NULL,''),
(1086,'项目应用服务修改',1083,3,'#','','F','0','1','upgrade:projectServer:edit','#','admin','2021-06-10 23:04:46','',NULL,''),
(1087,'项目应用服务删除',1083,4,'#','','F','0','1','upgrade:projectServer:remove','#','admin','2021-06-10 23:04:46','',NULL,''),
(1088,'项目应用服务导出',1083,5,'#','','F','0','1','upgrade:projectServer:export','#','admin','2021-06-10 23:04:46','',NULL,''),
(1089,'补丁包管理',1062,1,'/upgrade/patch','menuItem','C','0','1','upgrade:patch:view','fa fa-bug','admin','2021-06-12 21:34:51','admin','2021-06-13 21:32:52','补丁包管理菜单'),
(1090,'补丁包管理查询',1089,1,'#','','F','0','1','upgrade:patch:list','#','admin','2021-06-12 21:34:51','',NULL,''),
(1091,'补丁包管理新增',1089,2,'#','','F','0','1','upgrade:patch:add','#','admin','2021-06-12 21:34:51','',NULL,''),
(1092,'补丁包管理修改',1089,3,'#','','F','0','1','upgrade:patch:edit','#','admin','2021-06-12 21:34:51','',NULL,''),
(1093,'补丁包管理删除',1089,4,'#','','F','0','1','upgrade:patch:remove','#','admin','2021-06-12 21:34:51','',NULL,''),
(1094,'补丁包管理导出',1089,5,'#','','F','0','1','upgrade:patch:export','#','admin','2021-06-12 21:34:51','',NULL,''),
(1095,'升级管理',1062,1,'/upgrade/uprecord','menuItem','C','0','1','upgrade:uprecord:view','fa fa-paper-plane-o','admin','2021-06-13 21:52:31','admin','2021-06-13 21:59:24','升级管理菜单'),
(1096,'升级管理查询',1095,1,'#','','F','0','1','upgrade:uprecord:list','#','admin','2021-06-13 21:52:31','',NULL,''),
(1097,'升级管理新增',1095,2,'#','','F','0','1','upgrade:uprecord:add','#','admin','2021-06-13 21:52:31','',NULL,''),
(1098,'升级管理修改',1095,3,'#','','F','0','1','upgrade:uprecord:edit','#','admin','2021-06-13 21:52:31','',NULL,''),
(1099,'升级管理删除',1095,4,'#','','F','0','1','upgrade:uprecord:remove','#','admin','2021-06-13 21:52:31','',NULL,''),
(1100,'升级管理导出',1095,5,'#','','F','0','1','upgrade:uprecord:export','#','admin','2021-06-13 21:52:31','',NULL,'');

/*Table structure for table `sys_notice` */

DROP TABLE IF EXISTS `sys_notice`;

CREATE TABLE `sys_notice` (
  `notice_id` int(4) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) NOT NULL COMMENT '公告标题',
  `notice_type` char(1) NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` varchar(2000) DEFAULT NULL COMMENT '公告内容',
  `status` char(1) DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='通知公告表';

/*Data for the table `sys_notice` */

insert  into `sys_notice`(`notice_id`,`notice_title`,`notice_type`,`notice_content`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) values 
(1,'温馨提醒：2018-07-01 若依新版本发布啦','2','新版本内容','0','admin','2021-06-08 21:53:45','',NULL,'管理员'),
(2,'维护通知：2018-07-01 若依系统凌晨维护','1','维护内容','0','admin','2021-06-08 21:53:45','',NULL,'管理员');

/*Table structure for table `sys_oper_log` */

DROP TABLE IF EXISTS `sys_oper_log`;

CREATE TABLE `sys_oper_log` (
  `oper_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) DEFAULT '' COMMENT '模块标题',
  `business_type` int(2) DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(100) DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) DEFAULT '' COMMENT '请求方式',
  `operator_type` int(1) DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) DEFAULT '' COMMENT '返回参数',
  `status` int(1) DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`oper_id`)
) ENGINE=InnoDB AUTO_INCREMENT=257 DEFAULT CHARSET=utf8 COMMENT='操作日志记录';

/*Data for the table `sys_oper_log` */

insert  into `sys_oper_log`(`oper_id`,`title`,`business_type`,`method`,`request_method`,`operator_type`,`oper_name`,`dept_name`,`oper_url`,`oper_ip`,`oper_location`,`oper_param`,`json_result`,`status`,`error_msg`,`oper_time`) values 
(100,'菜单管理',3,'com.ruoyi.web.controller.system.SysMenuController.remove()','GET',1,'admin','研发部门','/system/menu/remove/4','127.0.0.1','内网IP','4','{\r\n  \"msg\" : \"菜单已分配,不允许删除\",\r\n  \"code\" : 301\r\n}',0,NULL,'2021-06-08 22:12:45'),
(101,'部门管理',2,'com.ruoyi.web.controller.system.SysDeptController.editSave()','POST',1,'admin','研发部门','/system/dept/edit','127.0.0.1','内网IP','{\"deptId\":[\"100\"],\"parentId\":[\"0\"],\"parentName\":[\"无\"],\"deptName\":[\"医疗云\"],\"orderNum\":[\"0\"],\"leader\":[\"医疗云\"],\"phone\":[\"15899999999\"],\"email\":[\"lijunqiang@kyee.com.cn\"],\"status\":[\"0\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-08 22:17:14'),
(102,'部门管理',2,'com.ruoyi.web.controller.system.SysDeptController.editSave()','POST',1,'admin','研发部门','/system/dept/edit','127.0.0.1','内网IP','{\"deptId\":[\"101\"],\"parentId\":[\"100\"],\"parentName\":[\"医疗云\"],\"deptName\":[\"云HIS研发\"],\"orderNum\":[\"1\"],\"leader\":[\"\"],\"phone\":[\"15888888888\"],\"email\":[\"ry@qq.com\"],\"status\":[\"0\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-08 22:17:50'),
(103,'部门管理',3,'com.ruoyi.web.controller.system.SysDeptController.remove()','GET',1,'admin','研发部门','/system/dept/remove/104','127.0.0.1','内网IP','104','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-08 22:17:58'),
(104,'部门管理',3,'com.ruoyi.web.controller.system.SysDeptController.remove()','GET',1,'admin','研发部门','/system/dept/remove/106','127.0.0.1','内网IP','106','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-08 22:18:03'),
(105,'部门管理',3,'com.ruoyi.web.controller.system.SysDeptController.remove()','GET',1,'admin','研发部门','/system/dept/remove/107','127.0.0.1','内网IP','107','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-08 22:18:09'),
(106,'部门管理',2,'com.ruoyi.web.controller.system.SysDeptController.editSave()','POST',1,'admin','研发部门','/system/dept/edit','127.0.0.1','内网IP','{\"deptId\":[\"102\"],\"parentId\":[\"100\"],\"parentName\":[\"医疗云\"],\"deptName\":[\"实施\"],\"orderNum\":[\"2\"],\"leader\":[\"\"],\"phone\":[\"15888888888\"],\"email\":[\"ry@qq.com\"],\"status\":[\"0\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-08 22:18:30'),
(107,'用户管理',2,'com.ruoyi.web.controller.system.SysUserController.editSave()','POST',1,'admin','研发部门','/system/user/edit','127.0.0.1','内网IP','{\"userId\":[\"1\"],\"deptId\":[\"103\"],\"userName\":[\"若依\"],\"dept.deptName\":[\"研发部门\"],\"phonenumber\":[\"15888888888\"],\"email\":[\"ry@163.com\"],\"loginName\":[\"admin\"],\"sex\":[\"1\"],\"role\":[\"1\"],\"remark\":[\"管理员\"],\"status\":[\"0\"],\"roleIds\":[\"1\"],\"postIds\":[\"1\"]}',NULL,1,'不允许操作超级管理员用户','2021-06-08 23:09:29'),
(108,'用户管理',2,'com.ruoyi.web.controller.system.SysUserController.editSave()','POST',1,'admin','研发部门','/system/user/edit','127.0.0.1','内网IP','{\"userId\":[\"2\"],\"deptId\":[\"105\"],\"userName\":[\"测试\"],\"dept.deptName\":[\"测试部门\"],\"phonenumber\":[\"15666666666\"],\"email\":[\"ry@qq.com\"],\"loginName\":[\"ry\"],\"sex\":[\"1\"],\"role\":[\"2\"],\"remark\":[\"测试员\"],\"status\":[\"0\"],\"roleIds\":[\"2\"],\"postIds\":[\"2\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-08 23:18:32'),
(109,'重置密码',2,'com.ruoyi.web.controller.system.SysUserController.resetPwdSave()','POST',1,'admin','研发部门','/system/user/resetPwd','127.0.0.1','内网IP','{\"userId\":[\"2\"],\"loginName\":[\"ceshi\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-08 23:20:17'),
(110,'菜单管理',3,'com.ruoyi.web.controller.system.SysMenuController.remove()','GET',1,'ceshi','测试部门','/system/menu/remove/4','127.0.0.1','内网IP','4','{\r\n  \"msg\" : \"菜单已分配,不允许删除\",\r\n  \"code\" : 301\r\n}',0,NULL,'2021-06-08 23:21:08'),
(111,'菜单管理',3,'com.ruoyi.web.controller.system.SysMenuController.remove()','GET',1,'ceshi','测试部门','/system/menu/remove/4','127.0.0.1','内网IP','4','{\r\n  \"msg\" : \"菜单已分配,不允许删除\",\r\n  \"code\" : 301\r\n}',0,NULL,'2021-06-08 23:22:09'),
(112,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.editSave()','POST',1,'admin','研发部门','/system/menu/edit','127.0.0.1','内网IP','{\"menuId\":[\"4\"],\"parentId\":[\"0\"],\"menuType\":[\"C\"],\"menuName\":[\"演示环境\"],\"url\":[\"https://demo.kyee.com.cn/auth_web/new_login\"],\"target\":[\"menuBlank\"],\"perms\":[\"\"],\"orderNum\":[\"4\"],\"icon\":[\"fa fa-location-arrow\"],\"visible\":[\"1\"],\"isRefresh\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-09 08:38:37'),
(113,'用户管理',1,'com.ruoyi.web.controller.system.SysUserController.addSave()','POST',1,'admin','研发部门','/system/user/add','127.0.0.1','内网IP','{\"deptId\":[\"\"],\"userName\":[\"实施\"],\"deptName\":[\"\"],\"phonenumber\":[\"18888888888\"],\"email\":[\"\"],\"loginName\":[\"shishi\"],\"sex\":[\"0\"],\"role\":[\"2\"],\"remark\":[\"\"],\"status\":[\"0\"],\"roleIds\":[\"2\"],\"postIds\":[\"2\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-09 08:40:01'),
(114,'角色管理',1,'com.ruoyi.web.controller.system.SysRoleController.addSave()','POST',1,'admin','研发部门','/system/role/add','127.0.0.1','内网IP','{\"roleName\":[\"测试角色\"],\"roleKey\":[\"tester\"],\"roleSort\":[\"3\"],\"status\":[\"0\"],\"remark\":[\"测试人员\"],\"menuIds\":[\"1,100,1000,1001,1002,1003,1004,1005,1006,101,1007,1008,1009,1010,1011,102,1012,1013,1014,1015,103,1016,1017,1018,1019,104,1020,1021,1022,1023,1024,105,1025,1026,1027,1028,1029,106,1030,1031,1032,1033,1034,107,1035,1036,1037,1038,108,500,1039,1040,1041,1042,501,1043,1044,1045,1046,2,109,1047,1048,1049,110,1050,1051,1052,1053,1054,1055,1056,111,112,113,3,114,115,1057,1058,1059,1060,1061,116,4\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-09 08:41:32'),
(115,'角色管理',2,'com.ruoyi.web.controller.system.SysRoleController.authDataScopeSave()','POST',1,'admin','研发部门','/system/role/authDataScope','127.0.0.1','内网IP','{\"roleId\":[\"3\"],\"roleName\":[\"测试角色\"],\"roleKey\":[\"tester\"],\"dataScope\":[\"2\"],\"deptIds\":[\"100,101,103,105\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-09 08:43:22'),
(116,'岗位管理',2,'com.ruoyi.web.controller.system.SysPostController.editSave()','POST',1,'admin','研发部门','/system/post/edit','127.0.0.1','内网IP','{\"postId\":[\"1\"],\"postName\":[\"管理员\"],\"postCode\":[\"manager\"],\"postSort\":[\"1\"],\"status\":[\"0\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-09 08:44:00'),
(117,'岗位管理',2,'com.ruoyi.web.controller.system.SysPostController.editSave()','POST',1,'admin','研发部门','/system/post/edit','127.0.0.1','内网IP','{\"postId\":[\"1\"],\"postName\":[\"管理员\"],\"postCode\":[\"sysmanager\"],\"postSort\":[\"1\"],\"status\":[\"0\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-09 08:44:34'),
(118,'岗位管理',2,'com.ruoyi.web.controller.system.SysPostController.editSave()','POST',1,'admin','研发部门','/system/post/edit','127.0.0.1','内网IP','{\"postId\":[\"2\"],\"postName\":[\"实施项目经理\"],\"postCode\":[\"ssxmjl\"],\"postSort\":[\"2\"],\"status\":[\"0\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-09 08:47:11'),
(119,'岗位管理',2,'com.ruoyi.web.controller.system.SysPostController.editSave()','POST',1,'admin','研发部门','/system/post/edit','127.0.0.1','内网IP','{\"postId\":[\"1\"],\"postName\":[\"管理员\"],\"postCode\":[\"gly\"],\"postSort\":[\"1\"],\"status\":[\"0\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-09 08:47:23'),
(120,'岗位管理',2,'com.ruoyi.web.controller.system.SysPostController.editSave()','POST',1,'admin','研发部门','/system/post/edit','127.0.0.1','内网IP','{\"postId\":[\"3\"],\"postName\":[\"实施人员\"],\"postCode\":[\"ssry\"],\"postSort\":[\"3\"],\"status\":[\"0\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-09 08:47:41'),
(121,'岗位管理',2,'com.ruoyi.web.controller.system.SysPostController.editSave()','POST',1,'admin','研发部门','/system/post/edit','127.0.0.1','内网IP','{\"postId\":[\"4\"],\"postName\":[\"测试人员\"],\"postCode\":[\"csry\"],\"postSort\":[\"4\"],\"status\":[\"0\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-09 08:48:06'),
(122,'岗位管理',1,'com.ruoyi.web.controller.system.SysPostController.addSave()','POST',1,'admin','研发部门','/system/post/add','127.0.0.1','内网IP','{\"postName\":[\"开发人员\"],\"postCode\":[\"kfry\"],\"postSort\":[\"5\"],\"status\":[\"0\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-09 08:48:55'),
(123,'参数管理',2,'com.ruoyi.web.controller.system.SysConfigController.editSave()','POST',1,'admin','研发部门','/system/config/edit','127.0.0.1','内网IP','{\"configId\":[\"8\"],\"configName\":[\"主框架页-菜单导航显示风格\"],\"configKey\":[\"sys.index.menuStyle\"],\"configValue\":[\"topnav\"],\"configType\":[\"Y\"],\"remark\":[\"菜单导航显示风格（default为左侧导航菜单，topnav为顶部导航菜单）\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-09 21:35:41'),
(124,'角色管理',2,'com.ruoyi.web.controller.system.SysRoleController.editSave()','POST',1,'admin','研发部门','/system/role/edit','127.0.0.1','内网IP','{\"roleId\":[\"1\"],\"roleName\":[\"超级管理员\"],\"roleKey\":[\"admin\"],\"roleSort\":[\"1\"],\"status\":[\"0\"],\"remark\":[\"超级管理员\"],\"menuIds\":[\"1,100,1000,1001,1002,1003,1004,1005,1006,101,1007,1008,1009,1010,1011,102,1012,1013,1014,1015,103,1016,1017,1018,1019,104,1020,1021,1022,1023,1024,105,1025,1026,1027,1028,1029,106,1030,1031,1032,1033,1034,107,1035,1036,1037,1038,108,500,1039,1040,1041,1042,501,1043,1044,1045,1046,2,109,1047,1048,1049,110,1050,1051,1052,1053,1054,1055,1056,111,112,113,3,114,115,1057,1058,1059,1060,1061,116,4\"]}',NULL,1,'不允许操作超级管理员角色','2021-06-09 21:37:17'),
(125,'代码生成',6,'com.ruoyi.generator.controller.GenController.importTableSave()','POST',1,'admin','研发部门','/tool/gen/importTable','127.0.0.1','内网IP','{\"tables\":[\"up_product\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 00:04:52'),
(126,'菜单管理',1,'com.ruoyi.web.controller.system.SysMenuController.addSave()','POST',1,'admin','研发部门','/system/menu/add','127.0.0.1','内网IP','{\"parentId\":[\"0\"],\"menuType\":[\"M\"],\"menuName\":[\"补丁管理\"],\"url\":[\"\"],\"target\":[\"menuItem\"],\"perms\":[\"\"],\"orderNum\":[\"2\"],\"icon\":[\"fa fa-rocket\"],\"visible\":[\"0\"],\"isRefresh\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 00:08:21'),
(127,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.editSave()','POST',1,'admin','研发部门','/system/menu/edit','127.0.0.1','内网IP','{\"menuId\":[\"1\"],\"parentId\":[\"0\"],\"menuType\":[\"M\"],\"menuName\":[\"系统管理\"],\"url\":[\"#\"],\"target\":[\"menuItem\"],\"perms\":[\"\"],\"orderNum\":[\"10\"],\"icon\":[\"fa fa-gear\"],\"visible\":[\"0\"],\"isRefresh\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 00:08:44'),
(128,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.editSave()','POST',1,'admin','研发部门','/system/menu/edit','127.0.0.1','内网IP','{\"menuId\":[\"2\"],\"parentId\":[\"0\"],\"menuType\":[\"M\"],\"menuName\":[\"系统监控\"],\"url\":[\"#\"],\"target\":[\"menuItem\"],\"perms\":[\"\"],\"orderNum\":[\"90\"],\"icon\":[\"fa fa-video-camera\"],\"visible\":[\"0\"],\"isRefresh\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 00:08:54'),
(129,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.editSave()','POST',1,'admin','研发部门','/system/menu/edit','127.0.0.1','内网IP','{\"menuId\":[\"1062\"],\"parentId\":[\"0\"],\"menuType\":[\"M\"],\"menuName\":[\"补丁管理\"],\"url\":[\"#\"],\"target\":[\"menuItem\"],\"perms\":[\"\"],\"orderNum\":[\"20\"],\"icon\":[\"fa fa-rocket\"],\"visible\":[\"0\"],\"isRefresh\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 00:09:03'),
(130,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.editSave()','POST',1,'admin','研发部门','/system/menu/edit','127.0.0.1','内网IP','{\"menuId\":[\"3\"],\"parentId\":[\"0\"],\"menuType\":[\"M\"],\"menuName\":[\"系统工具\"],\"url\":[\"#\"],\"target\":[\"menuItem\"],\"perms\":[\"\"],\"orderNum\":[\"30\"],\"icon\":[\"fa fa-bars\"],\"visible\":[\"0\"],\"isRefresh\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 00:09:13'),
(131,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.editSave()','POST',1,'admin','研发部门','/system/menu/edit','127.0.0.1','内网IP','{\"menuId\":[\"4\"],\"parentId\":[\"0\"],\"menuType\":[\"C\"],\"menuName\":[\"演示环境\"],\"url\":[\"https://demo.kyee.com.cn/auth_web/new_login\"],\"target\":[\"menuBlank\"],\"perms\":[\"\"],\"orderNum\":[\"40\"],\"icon\":[\"fa fa-location-arrow\"],\"visible\":[\"1\"],\"isRefresh\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 00:09:23'),
(132,'菜单管理',1,'com.ruoyi.web.controller.system.SysMenuController.addSave()','POST',1,'admin','研发部门','/system/menu/add','127.0.0.1','内网IP','{\"parentId\":[\"0\"],\"menuType\":[\"C\"],\"menuName\":[\"产品管理\"],\"url\":[\"\"],\"target\":[\"menuItem\"],\"perms\":[\"\"],\"orderNum\":[\"1\"],\"icon\":[\"\"],\"visible\":[\"0\"],\"isRefresh\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 00:09:55'),
(133,'菜单管理',3,'com.ruoyi.web.controller.system.SysMenuController.remove()','GET',1,'admin','研发部门','/system/menu/remove/1063','127.0.0.1','内网IP','1063','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 00:10:31'),
(134,'菜单管理',1,'com.ruoyi.web.controller.system.SysMenuController.addSave()','POST',1,'admin','研发部门','/system/menu/add','127.0.0.1','内网IP','{\"parentId\":[\"1062\"],\"menuType\":[\"C\"],\"menuName\":[\"产品管理\"],\"url\":[\"\"],\"target\":[\"menuItem\"],\"perms\":[\"\"],\"orderNum\":[\"10\"],\"icon\":[\"\"],\"visible\":[\"0\"],\"isRefresh\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 00:11:02'),
(135,'菜单管理',3,'com.ruoyi.web.controller.system.SysMenuController.remove()','GET',1,'admin','研发部门','/system/menu/remove/1064','127.0.0.1','内网IP','1064','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 00:12:02'),
(136,'代码生成',2,'com.ruoyi.generator.controller.GenController.editSave()','POST',1,'admin','研发部门','/tool/gen/edit','127.0.0.1','内网IP','{\"tableId\":[\"1\"],\"tableName\":[\"up_product\"],\"tableComment\":[\"产品表\"],\"className\":[\"UpProduct\"],\"functionAuthor\":[\"lijunqiang\"],\"remark\":[\"\"],\"columns[0].columnId\":[\"1\"],\"columns[0].sort\":[\"1\"],\"columns[0].columnComment\":[\"产品ID\"],\"columns[0].javaType\":[\"Integer\"],\"columns[0].javaField\":[\"productId\"],\"columns[0].isInsert\":[\"1\"],\"columns[0].queryType\":[\"EQ\"],\"columns[0].htmlType\":[\"input\"],\"columns[0].dictType\":[\"\"],\"columns[1].columnId\":[\"2\"],\"columns[1].sort\":[\"2\"],\"columns[1].columnComment\":[\"产品名\"],\"columns[1].javaType\":[\"String\"],\"columns[1].javaField\":[\"productName\"],\"columns[1].isInsert\":[\"1\"],\"columns[1].isEdit\":[\"1\"],\"columns[1].isList\":[\"1\"],\"columns[1].isQuery\":[\"1\"],\"columns[1].queryType\":[\"LIKE\"],\"columns[1].isRequired\":[\"1\"],\"columns[1].htmlType\":[\"input\"],\"columns[1].dictType\":[\"\"],\"columns[2].columnId\":[\"3\"],\"columns[2].sort\":[\"3\"],\"columns[2].columnComment\":[\"产品备注\"],\"columns[2].javaType\":[\"String\"],\"columns[2].javaField\":[\"memo\"],\"columns[2].isInsert\":[\"1\"],\"columns[2].isEdit\":[\"1\"],\"columns[2].isList\":[\"1\"],\"columns[2].isQuery\":[\"1\"],\"columns[2].queryType\":[\"EQ\"],\"columns[2].htmlType\":[\"input\"],\"columns[2].dictType\":[\"\"],\"tplCategory\":[\"crud\"],\"packageName\":[\"com.kyee.upgrade\"],\"moduleName\":[\"upgrade\"],\"businessName\":[\"product\"],\"functionName\":[\"产品管理\"],\"params[parentMenuId]\":[\"1062\"],\"params[parentMenuName]\":[\"补丁管理\"],\"genType\":[\"0\"],\"genPath\":[\"/\"],\"subTableName\":[\"\"],\"params[treeCode]\":[\"\"],\"params[treeParentCode]\":[\"\"],\"params[treeName]\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 00:12:29'),
(137,'代码生成',8,'com.ruoyi.generator.controller.GenController.download()','GET',1,'admin','研发部门','/tool/gen/download/up_product','127.0.0.1','内网IP','\"up_product\"',NULL,0,NULL,'2021-06-10 00:12:44'),
(138,'产品',1,'com.kyee.upgrade.controller.UpProductController.addSave()','POST',1,'admin','研发部门','/upgrade/product/add','127.0.0.1','内网IP','{\"productName\":[\"云HIS\"],\"memo\":[\"云HIS系统-HIS\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 00:54:08'),
(139,'代码生成',6,'com.ruoyi.generator.controller.GenController.importTableSave()','POST',1,'admin','研发部门','/tool/gen/importTable','127.0.0.1','内网IP','{\"tables\":[\"up_project\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 00:54:44'),
(140,'代码生成',2,'com.ruoyi.generator.controller.GenController.editSave()','POST',1,'admin','研发部门','/tool/gen/edit','127.0.0.1','内网IP','{\"tableId\":[\"2\"],\"tableName\":[\"up_project\"],\"tableComment\":[\"项目表\"],\"className\":[\"UpProject\"],\"functionAuthor\":[\"lijunqiang\"],\"remark\":[\"\"],\"columns[0].columnId\":[\"4\"],\"columns[0].sort\":[\"1\"],\"columns[0].columnComment\":[\"项目ID\"],\"columns[0].javaType\":[\"Integer\"],\"columns[0].javaField\":[\"projectId\"],\"columns[0].isInsert\":[\"1\"],\"columns[0].queryType\":[\"EQ\"],\"columns[0].htmlType\":[\"input\"],\"columns[0].dictType\":[\"\"],\"columns[1].columnId\":[\"5\"],\"columns[1].sort\":[\"2\"],\"columns[1].columnComment\":[\"项目名\"],\"columns[1].javaType\":[\"String\"],\"columns[1].javaField\":[\"projectName\"],\"columns[1].isInsert\":[\"1\"],\"columns[1].isEdit\":[\"1\"],\"columns[1].isList\":[\"1\"],\"columns[1].isQuery\":[\"1\"],\"columns[1].queryType\":[\"LIKE\"],\"columns[1].isRequired\":[\"1\"],\"columns[1].htmlType\":[\"input\"],\"columns[1].dictType\":[\"\"],\"tplCategory\":[\"crud\"],\"packageName\":[\"com.kyee.upgrade\"],\"moduleName\":[\"upgrade\"],\"businessName\":[\"project\"],\"functionName\":[\"项目\"],\"params[parentMenuId]\":[\"1062\"],\"params[parentMenuName]\":[\"补丁管理\"],\"genType\":[\"0\"],\"genPath\":[\"/\"],\"subTableName\":[\"\"],\"params[treeCode]\":[\"\"],\"params[treeParentCode]\":[\"\"],\"params[treeName]\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 00:55:43'),
(141,'代码生成',8,'com.ruoyi.generator.controller.GenController.download()','GET',1,'admin','研发部门','/tool/gen/download/up_project','127.0.0.1','内网IP','\"up_project\"',NULL,0,NULL,'2021-06-10 00:55:52'),
(142,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.editSave()','POST',1,'admin','研发部门','/system/menu/edit','127.0.0.1','内网IP','{\"menuId\":[\"1066\"],\"parentId\":[\"1065\"],\"menuType\":[\"F\"],\"menuName\":[\"产品查询\"],\"url\":[\"#\"],\"target\":[\"menuItem\"],\"perms\":[\"upgrade:product:list\"],\"orderNum\":[\"1\"],\"icon\":[\"#\"],\"visible\":[\"0\"],\"isRefresh\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 00:58:21'),
(143,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.editSave()','POST',1,'admin','研发部门','/system/menu/edit','127.0.0.1','内网IP','{\"menuId\":[\"1067\"],\"parentId\":[\"1065\"],\"menuType\":[\"F\"],\"menuName\":[\"产品新增\"],\"url\":[\"#\"],\"target\":[\"menuItem\"],\"perms\":[\"upgrade:product:add\"],\"orderNum\":[\"2\"],\"icon\":[\"#\"],\"visible\":[\"0\"],\"isRefresh\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 00:58:35'),
(144,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.editSave()','POST',1,'admin','研发部门','/system/menu/edit','127.0.0.1','内网IP','{\"menuId\":[\"1068\"],\"parentId\":[\"1065\"],\"menuType\":[\"F\"],\"menuName\":[\"产品修改\"],\"url\":[\"#\"],\"target\":[\"menuItem\"],\"perms\":[\"upgrade:product:edit\"],\"orderNum\":[\"3\"],\"icon\":[\"#\"],\"visible\":[\"0\"],\"isRefresh\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 00:58:47'),
(145,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.editSave()','POST',1,'admin','研发部门','/system/menu/edit','127.0.0.1','内网IP','{\"menuId\":[\"1069\"],\"parentId\":[\"1065\"],\"menuType\":[\"F\"],\"menuName\":[\"产品删除\"],\"url\":[\"#\"],\"target\":[\"menuItem\"],\"perms\":[\"upgrade:product:remove\"],\"orderNum\":[\"4\"],\"icon\":[\"#\"],\"visible\":[\"0\"],\"isRefresh\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 00:58:56'),
(146,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.editSave()','POST',1,'admin','研发部门','/system/menu/edit','127.0.0.1','内网IP','{\"menuId\":[\"1070\"],\"parentId\":[\"1065\"],\"menuType\":[\"F\"],\"menuName\":[\"产品导出\"],\"url\":[\"#\"],\"target\":[\"menuItem\"],\"perms\":[\"upgrade:product:export\"],\"orderNum\":[\"5\"],\"icon\":[\"#\"],\"visible\":[\"0\"],\"isRefresh\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 00:59:05'),
(147,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.editSave()','POST',1,'admin','研发部门','/system/menu/edit','127.0.0.1','内网IP','{\"menuId\":[\"1071\"],\"parentId\":[\"1062\"],\"menuType\":[\"C\"],\"menuName\":[\"项目管理\"],\"url\":[\"/upgrade/project\"],\"target\":[\"menuItem\"],\"perms\":[\"upgrade:project:view\"],\"orderNum\":[\"1\"],\"icon\":[\"#\"],\"visible\":[\"0\"],\"isRefresh\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 00:59:16'),
(148,'项目',1,'com.kyee.upgrade.controller.UpProjectController.addSave()','POST',1,'admin','研发部门','/upgrade/project/add','127.0.0.1','内网IP','{\"projectName\":[\"阳西HIS\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 01:00:06'),
(149,'代码生成',6,'com.ruoyi.generator.controller.GenController.importTableSave()','POST',1,'admin','研发部门','/tool/gen/importTable','127.0.0.1','内网IP','{\"tables\":[\"up_project_product\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 01:01:43'),
(150,'代码生成',2,'com.ruoyi.generator.controller.GenController.editSave()','POST',1,'admin','研发部门','/tool/gen/edit','127.0.0.1','内网IP','{\"tableId\":[\"3\"],\"tableName\":[\"up_project_product\"],\"tableComment\":[\"项目产品表\"],\"className\":[\"UpProjectProduct\"],\"functionAuthor\":[\"lijunqiang\"],\"remark\":[\"\"],\"columns[0].columnId\":[\"6\"],\"columns[0].sort\":[\"1\"],\"columns[0].columnComment\":[\"项目产品ID\"],\"columns[0].javaType\":[\"Integer\"],\"columns[0].javaField\":[\"projectProductId\"],\"columns[0].isInsert\":[\"1\"],\"columns[0].queryType\":[\"EQ\"],\"columns[0].htmlType\":[\"input\"],\"columns[0].dictType\":[\"\"],\"columns[1].columnId\":[\"7\"],\"columns[1].sort\":[\"2\"],\"columns[1].columnComment\":[\"项目ID\"],\"columns[1].javaType\":[\"Integer\"],\"columns[1].javaField\":[\"projectId\"],\"columns[1].isInsert\":[\"1\"],\"columns[1].isEdit\":[\"1\"],\"columns[1].isList\":[\"1\"],\"columns[1].isQuery\":[\"1\"],\"columns[1].queryType\":[\"EQ\"],\"columns[1].isRequired\":[\"1\"],\"columns[1].htmlType\":[\"select\"],\"columns[1].dictType\":[\"\"],\"columns[2].columnId\":[\"8\"],\"columns[2].sort\":[\"3\"],\"columns[2].columnComment\":[\"产品ID\"],\"columns[2].javaType\":[\"Integer\"],\"columns[2].javaField\":[\"productId\"],\"columns[2].isInsert\":[\"1\"],\"columns[2].isEdit\":[\"1\"],\"columns[2].isList\":[\"1\"],\"columns[2].isQuery\":[\"1\"],\"columns[2].queryType\":[\"EQ\"],\"columns[2].isRequired\":[\"1\"],\"columns[2].htmlType\":[\"select\"],\"columns[2].dictType\":[\"\"],\"columns[3].columnId\":[\"9\"],\"columns[3].sort\":[\"4\"],\"columns[3].columnComment\":[\"项目代码分支\"],\"columns[3].javaType\":[\"String\"],\"columns[3].javaField\":[\"sourceBranchName\"],\"columns[3].isInsert\":[\"1\"],\"columns[3].isEdit\":[\"1\"],\"columns[3].isList\":[\"1\"],\"columns[3].isQuery\":[\"1\"],\"columns[3].queryType\":[\"LIKE\"],\"columns[3].isRequired\":[\"1\"],\"columns[3].htmlType\":[\"input\"],\"columns[3].dictType\":[\"\"],\"columns[4].columnId\":[\"10\"],\"columns[4].sort\":[\"5\"],\"columns[4].columnComment\":[\"数据库版本\"],\"columns[4].javaType\":[\"String\"],\"columns[4].javaField\":[\"dbVersion\"],\"columns[4].isEdit\":[\"1\"],\"columns[4].isList\":[\"1\"],\"columns[4].queryType\":[\"EQ\"],\"columns[4].htmlType\":[\"input\"],\"columns[4].dictType\":[\"\"],\"columns[5].columnId\":[\"11\"],\"columns[5].sort\":[\"6\"],\"columns[5].columnComment\":[\"最小服务版本\"],','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 01:05:31'),
(151,'代码生成',2,'com.ruoyi.generator.controller.GenController.editSave()','POST',1,'admin','研发部门','/tool/gen/edit','127.0.0.1','内网IP','{\"tableId\":[\"3\"],\"tableName\":[\"up_project_product\"],\"tableComment\":[\"项目产品表\"],\"className\":[\"UpProjectProduct\"],\"functionAuthor\":[\"lijunqiang\"],\"remark\":[\"\"],\"columns[0].columnId\":[\"6\"],\"columns[0].sort\":[\"1\"],\"columns[0].columnComment\":[\"项目产品ID\"],\"columns[0].javaType\":[\"Integer\"],\"columns[0].javaField\":[\"projectProductId\"],\"columns[0].isInsert\":[\"1\"],\"columns[0].queryType\":[\"EQ\"],\"columns[0].htmlType\":[\"input\"],\"columns[0].dictType\":[\"\"],\"columns[1].columnId\":[\"7\"],\"columns[1].sort\":[\"2\"],\"columns[1].columnComment\":[\"项目ID\"],\"columns[1].javaType\":[\"Integer\"],\"columns[1].javaField\":[\"projectId\"],\"columns[1].isInsert\":[\"1\"],\"columns[1].isEdit\":[\"1\"],\"columns[1].isList\":[\"1\"],\"columns[1].isQuery\":[\"1\"],\"columns[1].queryType\":[\"EQ\"],\"columns[1].isRequired\":[\"1\"],\"columns[1].htmlType\":[\"select\"],\"columns[1].dictType\":[\"\"],\"columns[2].columnId\":[\"8\"],\"columns[2].sort\":[\"3\"],\"columns[2].columnComment\":[\"产品ID\"],\"columns[2].javaType\":[\"Integer\"],\"columns[2].javaField\":[\"productId\"],\"columns[2].isInsert\":[\"1\"],\"columns[2].isEdit\":[\"1\"],\"columns[2].isList\":[\"1\"],\"columns[2].isQuery\":[\"1\"],\"columns[2].queryType\":[\"EQ\"],\"columns[2].isRequired\":[\"1\"],\"columns[2].htmlType\":[\"select\"],\"columns[2].dictType\":[\"\"],\"columns[3].columnId\":[\"9\"],\"columns[3].sort\":[\"4\"],\"columns[3].columnComment\":[\"项目代码分支\"],\"columns[3].javaType\":[\"String\"],\"columns[3].javaField\":[\"sourceBranchName\"],\"columns[3].isInsert\":[\"1\"],\"columns[3].isEdit\":[\"1\"],\"columns[3].isList\":[\"1\"],\"columns[3].isQuery\":[\"1\"],\"columns[3].queryType\":[\"LIKE\"],\"columns[3].isRequired\":[\"1\"],\"columns[3].htmlType\":[\"input\"],\"columns[3].dictType\":[\"\"],\"columns[4].columnId\":[\"10\"],\"columns[4].sort\":[\"5\"],\"columns[4].columnComment\":[\"数据库版本\"],\"columns[4].javaType\":[\"String\"],\"columns[4].javaField\":[\"dbVersion\"],\"columns[4].isEdit\":[\"1\"],\"columns[4].isList\":[\"1\"],\"columns[4].queryType\":[\"EQ\"],\"columns[4].htmlType\":[\"input\"],\"columns[4].dictType\":[\"\"],\"columns[5].columnId\":[\"11\"],\"columns[5].sort\":[\"6\"],\"columns[5].columnComment\":[\"最小服务版本\"],','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 01:09:13'),
(152,'代码生成',8,'com.ruoyi.generator.controller.GenController.batchGenCode()','GET',1,'admin','研发部门','/tool/gen/batchGenCode','127.0.0.1','内网IP','{\"tables\":[\"up_project_product\"]}',NULL,0,NULL,'2021-06-10 01:09:18'),
(153,'代码生成',2,'com.ruoyi.generator.controller.GenController.editSave()','POST',1,'admin','研发部门','/tool/gen/edit','127.0.0.1','内网IP','{\"tableId\":[\"3\"],\"tableName\":[\"up_project_product\"],\"tableComment\":[\"项目产品表\"],\"className\":[\"UpProjectProduct\"],\"functionAuthor\":[\"lijunqiang\"],\"remark\":[\"\"],\"columns[0].columnId\":[\"6\"],\"columns[0].sort\":[\"1\"],\"columns[0].columnComment\":[\"项目产品ID\"],\"columns[0].javaType\":[\"Integer\"],\"columns[0].javaField\":[\"projectProductId\"],\"columns[0].isInsert\":[\"1\"],\"columns[0].queryType\":[\"EQ\"],\"columns[0].htmlType\":[\"input\"],\"columns[0].dictType\":[\"\"],\"columns[1].columnId\":[\"7\"],\"columns[1].sort\":[\"2\"],\"columns[1].columnComment\":[\"项目ID\"],\"columns[1].javaType\":[\"Integer\"],\"columns[1].javaField\":[\"projectId\"],\"columns[1].isInsert\":[\"1\"],\"columns[1].isEdit\":[\"1\"],\"columns[1].isList\":[\"1\"],\"columns[1].isQuery\":[\"1\"],\"columns[1].queryType\":[\"EQ\"],\"columns[1].isRequired\":[\"1\"],\"columns[1].htmlType\":[\"select\"],\"columns[1].dictType\":[\"sys_oper_type\"],\"columns[2].columnId\":[\"8\"],\"columns[2].sort\":[\"3\"],\"columns[2].columnComment\":[\"产品ID\"],\"columns[2].javaType\":[\"Integer\"],\"columns[2].javaField\":[\"productId\"],\"columns[2].isInsert\":[\"1\"],\"columns[2].isEdit\":[\"1\"],\"columns[2].isList\":[\"1\"],\"columns[2].isQuery\":[\"1\"],\"columns[2].queryType\":[\"EQ\"],\"columns[2].isRequired\":[\"1\"],\"columns[2].htmlType\":[\"select\"],\"columns[2].dictType\":[\"sys_notice_type\"],\"columns[3].columnId\":[\"9\"],\"columns[3].sort\":[\"4\"],\"columns[3].columnComment\":[\"项目代码分支\"],\"columns[3].javaType\":[\"String\"],\"columns[3].javaField\":[\"sourceBranchName\"],\"columns[3].isInsert\":[\"1\"],\"columns[3].isEdit\":[\"1\"],\"columns[3].isList\":[\"1\"],\"columns[3].isQuery\":[\"1\"],\"columns[3].queryType\":[\"LIKE\"],\"columns[3].isRequired\":[\"1\"],\"columns[3].htmlType\":[\"input\"],\"columns[3].dictType\":[\"\"],\"columns[4].columnId\":[\"10\"],\"columns[4].sort\":[\"5\"],\"columns[4].columnComment\":[\"数据库版本\"],\"columns[4].javaType\":[\"String\"],\"columns[4].javaField\":[\"dbVersion\"],\"columns[4].isEdit\":[\"1\"],\"columns[4].isList\":[\"1\"],\"columns[4].queryType\":[\"EQ\"],\"columns[4].htmlType\":[\"input\"],\"columns[4].dictType\":[\"\"],\"columns[5].columnId\":[\"11\"],\"columns[5].sort\":[\"6\"],\"columns[5','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 12:41:48'),
(154,'代码生成',8,'com.ruoyi.generator.controller.GenController.genCode()','GET',1,'admin','研发部门','/tool/gen/genCode/up_project_product','127.0.0.1','内网IP','\"up_project_product\"','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 12:41:52'),
(155,'项目产品',2,'com.kyee.upgrade.controller.UpProjectProductController.editSave()','POST',1,'admin','研发部门','/upgrade/projectProduct/edit','127.0.0.1','内网IP','{\"projectProductId\":[\"1\"],\"projectId\":[\"1\"],\"productId\":[\"1\"],\"sourceBranchName\":[\"project/master/yangxi\"],\"dbVersion\":[\"\"],\"minServerVersion\":[\"\"],\"maxServerVersion\":[\"\"],\"lastUpgradeTime\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 13:52:10'),
(156,'项目产品',1,'com.kyee.upgrade.controller.UpProjectProductController.addSave()','POST',1,'admin','研发部门','/upgrade/projectProduct/add','127.0.0.1','内网IP','{\"projectId\":[\"1\"],\"productId\":[\"1\"],\"sourceBranchName\":[\"aaa\"]}',NULL,1,'\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'1-1\' for key \'UNIDX_Project_Product\'\r\n### The error may exist in file [E:\\git_repository\\LAWU\\RuoYi\\ruoyi-admin\\target\\classes\\mapper\\upgrade\\UpProjectProductMapper.xml]\r\n### The error may involve com.kyee.upgrade.mapper.UpProjectProductMapper.insertUpProjectProduct-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into up_project_product          ( project_id,             product_id,             source_branch_name )           values ( ?,             ?,             ? )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'1-1\' for key \'UNIDX_Project_Product\'\n; Duplicate entry \'1-1\' for key \'UNIDX_Project_Product\'; nested exception is java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'1-1\' for key \'UNIDX_Project_Product\'','2021-06-10 22:34:32'),
(157,'项目产品',1,'com.kyee.upgrade.controller.UpProjectProductController.addSave()','POST',1,'admin','研发部门','/upgrade/projectProduct/add','127.0.0.1','内网IP','{\"projectId\":[\"1\"],\"productId\":[\"1\"],\"sourceBranchName\":[\"aaa\"]}',NULL,1,'\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'1-1\' for key \'UNIDX_Project_Product\'\r\n### The error may exist in file [E:\\git_repository\\LAWU\\RuoYi\\ruoyi-admin\\target\\classes\\mapper\\upgrade\\UpProjectProductMapper.xml]\r\n### The error may involve com.kyee.upgrade.mapper.UpProjectProductMapper.insertUpProjectProduct-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into up_project_product          ( project_id,             product_id,             source_branch_name )           values ( ?,             ?,             ? )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'1-1\' for key \'UNIDX_Project_Product\'\n; Duplicate entry \'1-1\' for key \'UNIDX_Project_Product\'; nested exception is java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'1-1\' for key \'UNIDX_Project_Product\'','2021-06-10 22:34:38'),
(158,'代码生成',6,'com.ruoyi.generator.controller.GenController.importTableSave()','POST',1,'admin','研发部门','/tool/gen/importTable','127.0.0.1','内网IP','{\"tables\":[\"up_project_server\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 22:40:50'),
(159,'代码生成',2,'com.ruoyi.generator.controller.GenController.synchDb()','GET',1,'admin','研发部门','/tool/gen/synchDb/up_project_server','127.0.0.1','内网IP','\"up_project_server\"','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 22:43:51'),
(160,'代码生成',2,'com.ruoyi.generator.controller.GenController.synchDb()','GET',1,'admin','研发部门','/tool/gen/synchDb/up_project_server','127.0.0.1','内网IP','\"up_project_server\"','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 22:45:54'),
(161,'代码生成',3,'com.ruoyi.generator.controller.GenController.remove()','POST',1,'admin','研发部门','/tool/gen/remove','127.0.0.1','内网IP','{\"ids\":[\"4\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 22:46:02'),
(162,'代码生成',6,'com.ruoyi.generator.controller.GenController.importTableSave()','POST',1,'admin','研发部门','/tool/gen/importTable','127.0.0.1','内网IP','{\"tables\":[\"up_project_server\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 22:48:59'),
(163,'代码生成',2,'com.ruoyi.generator.controller.GenController.editSave()','POST',1,'admin','研发部门','/tool/gen/edit','127.0.0.1','内网IP','{\"tableId\":[\"5\"],\"tableName\":[\"up_project_server\"],\"tableComment\":[\"项目应用服务表\"],\"className\":[\"UpProjectServer\"],\"functionAuthor\":[\"lijunqiang\"],\"remark\":[\"\"],\"columns[0].columnId\":[\"20\"],\"columns[0].sort\":[\"1\"],\"columns[0].columnComment\":[\"服务ID\"],\"columns[0].javaType\":[\"Integer\"],\"columns[0].javaField\":[\"serverId\"],\"columns[0].isInsert\":[\"1\"],\"columns[0].isEdit\":[\"1\"],\"columns[0].isList\":[\"1\"],\"columns[0].queryType\":[\"EQ\"],\"columns[0].isRequired\":[\"1\"],\"columns[0].htmlType\":[\"input\"],\"columns[0].dictType\":[\"\"],\"columns[1].columnId\":[\"21\"],\"columns[1].sort\":[\"2\"],\"columns[1].columnComment\":[\"项目ID\"],\"columns[1].javaType\":[\"Integer\"],\"columns[1].javaField\":[\"projectId\"],\"columns[1].isInsert\":[\"1\"],\"columns[1].isEdit\":[\"1\"],\"columns[1].isList\":[\"1\"],\"columns[1].isQuery\":[\"1\"],\"columns[1].queryType\":[\"EQ\"],\"columns[1].isRequired\":[\"1\"],\"columns[1].htmlType\":[\"select\"],\"columns[1].dictType\":[\"\"],\"columns[2].columnId\":[\"22\"],\"columns[2].sort\":[\"3\"],\"columns[2].columnComment\":[\"产品ID\"],\"columns[2].javaType\":[\"Integer\"],\"columns[2].javaField\":[\"productId\"],\"columns[2].isInsert\":[\"1\"],\"columns[2].isEdit\":[\"1\"],\"columns[2].isList\":[\"1\"],\"columns[2].isQuery\":[\"1\"],\"columns[2].queryType\":[\"EQ\"],\"columns[2].isRequired\":[\"1\"],\"columns[2].htmlType\":[\"select\"],\"columns[2].dictType\":[\"\"],\"columns[3].columnId\":[\"23\"],\"columns[3].sort\":[\"4\"],\"columns[3].columnComment\":[\"服务IP\"],\"columns[3].javaType\":[\"String\"],\"columns[3].javaField\":[\"serverIp\"],\"columns[3].isInsert\":[\"1\"],\"columns[3].isEdit\":[\"1\"],\"columns[3].isList\":[\"1\"],\"columns[3].isQuery\":[\"1\"],\"columns[3].queryType\":[\"EQ\"],\"columns[3].isRequired\":[\"1\"],\"columns[3].htmlType\":[\"input\"],\"columns[3].dictType\":[\"\"],\"columns[4].columnId\":[\"24\"],\"columns[4].sort\":[\"5\"],\"columns[4].columnComment\":[\"服务端口号\"],\"columns[4].javaType\":[\"Integer\"],\"columns[4].javaField\":[\"serverPort\"],\"columns[4].isInsert\":[\"1\"],\"columns[4].isEdit\":[\"1\"],\"columns[4].isList\":[\"1\"],\"columns[4].queryType\":[\"EQ\"],\"columns[4].isRequired\":[\"1\"],\"columns[4].htmlType\":[\"in','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 22:52:06'),
(164,'代码生成',8,'com.ruoyi.generator.controller.GenController.genCode()','GET',1,'admin','研发部门','/tool/gen/genCode/up_project_server','127.0.0.1','内网IP','\"up_project_server\"','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 22:52:30'),
(165,'代码生成',2,'com.ruoyi.generator.controller.GenController.editSave()','POST',1,'admin','研发部门','/tool/gen/edit','127.0.0.1','内网IP','{\"tableId\":[\"5\"],\"tableName\":[\"up_project_server\"],\"tableComment\":[\"项目应用服务表\"],\"className\":[\"UpProjectServer\"],\"functionAuthor\":[\"lijunqiang\"],\"remark\":[\"\"],\"columns[0].columnId\":[\"20\"],\"columns[0].sort\":[\"1\"],\"columns[0].columnComment\":[\"服务ID\"],\"columns[0].javaType\":[\"Integer\"],\"columns[0].javaField\":[\"serverId\"],\"columns[0].isInsert\":[\"1\"],\"columns[0].isEdit\":[\"1\"],\"columns[0].isList\":[\"1\"],\"columns[0].queryType\":[\"EQ\"],\"columns[0].isRequired\":[\"1\"],\"columns[0].htmlType\":[\"input\"],\"columns[0].dictType\":[\"\"],\"columns[1].columnId\":[\"21\"],\"columns[1].sort\":[\"2\"],\"columns[1].columnComment\":[\"项目ID\"],\"columns[1].javaType\":[\"Integer\"],\"columns[1].javaField\":[\"projectId\"],\"columns[1].isInsert\":[\"1\"],\"columns[1].isEdit\":[\"1\"],\"columns[1].isList\":[\"1\"],\"columns[1].isQuery\":[\"1\"],\"columns[1].queryType\":[\"EQ\"],\"columns[1].isRequired\":[\"1\"],\"columns[1].htmlType\":[\"select\"],\"columns[1].dictType\":[\"\"],\"columns[2].columnId\":[\"22\"],\"columns[2].sort\":[\"3\"],\"columns[2].columnComment\":[\"产品ID\"],\"columns[2].javaType\":[\"Integer\"],\"columns[2].javaField\":[\"productId\"],\"columns[2].isInsert\":[\"1\"],\"columns[2].isEdit\":[\"1\"],\"columns[2].isList\":[\"1\"],\"columns[2].isQuery\":[\"1\"],\"columns[2].queryType\":[\"EQ\"],\"columns[2].isRequired\":[\"1\"],\"columns[2].htmlType\":[\"select\"],\"columns[2].dictType\":[\"\"],\"columns[3].columnId\":[\"23\"],\"columns[3].sort\":[\"4\"],\"columns[3].columnComment\":[\"服务IP\"],\"columns[3].javaType\":[\"String\"],\"columns[3].javaField\":[\"serverIp\"],\"columns[3].isInsert\":[\"1\"],\"columns[3].isEdit\":[\"1\"],\"columns[3].isList\":[\"1\"],\"columns[3].isQuery\":[\"1\"],\"columns[3].queryType\":[\"EQ\"],\"columns[3].isRequired\":[\"1\"],\"columns[3].htmlType\":[\"input\"],\"columns[3].dictType\":[\"\"],\"columns[4].columnId\":[\"24\"],\"columns[4].sort\":[\"5\"],\"columns[4].columnComment\":[\"服务端口号\"],\"columns[4].javaType\":[\"Integer\"],\"columns[4].javaField\":[\"serverPort\"],\"columns[4].isInsert\":[\"1\"],\"columns[4].isEdit\":[\"1\"],\"columns[4].isList\":[\"1\"],\"columns[4].queryType\":[\"EQ\"],\"columns[4].isRequired\":[\"1\"],\"columns[4].htmlType\":[\"in','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 22:54:36'),
(166,'代码生成',8,'com.ruoyi.generator.controller.GenController.genCode()','GET',1,'admin','研发部门','/tool/gen/genCode/up_project_server','127.0.0.1','内网IP','\"up_project_server\"','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 22:54:40'),
(167,'代码生成',2,'com.ruoyi.generator.controller.GenController.editSave()','POST',1,'admin','研发部门','/tool/gen/edit','127.0.0.1','内网IP','{\"tableId\":[\"5\"],\"tableName\":[\"up_project_server\"],\"tableComment\":[\"项目应用服务表\"],\"className\":[\"UpProjectServer\"],\"functionAuthor\":[\"lijunqiang\"],\"remark\":[\"\"],\"columns[0].columnId\":[\"20\"],\"columns[0].sort\":[\"1\"],\"columns[0].columnComment\":[\"服务ID\"],\"columns[0].javaType\":[\"Integer\"],\"columns[0].javaField\":[\"serverId\"],\"columns[0].isInsert\":[\"1\"],\"columns[0].isEdit\":[\"1\"],\"columns[0].isList\":[\"1\"],\"columns[0].queryType\":[\"EQ\"],\"columns[0].isRequired\":[\"1\"],\"columns[0].htmlType\":[\"input\"],\"columns[0].dictType\":[\"\"],\"columns[1].columnId\":[\"21\"],\"columns[1].sort\":[\"2\"],\"columns[1].columnComment\":[\"项目ID\"],\"columns[1].javaType\":[\"Integer\"],\"columns[1].javaField\":[\"projectId\"],\"columns[1].isInsert\":[\"1\"],\"columns[1].isEdit\":[\"1\"],\"columns[1].isList\":[\"1\"],\"columns[1].isQuery\":[\"1\"],\"columns[1].queryType\":[\"EQ\"],\"columns[1].isRequired\":[\"1\"],\"columns[1].htmlType\":[\"select\"],\"columns[1].dictType\":[\"\"],\"columns[2].columnId\":[\"22\"],\"columns[2].sort\":[\"3\"],\"columns[2].columnComment\":[\"产品ID\"],\"columns[2].javaType\":[\"Integer\"],\"columns[2].javaField\":[\"productId\"],\"columns[2].isInsert\":[\"1\"],\"columns[2].isEdit\":[\"1\"],\"columns[2].isList\":[\"1\"],\"columns[2].isQuery\":[\"1\"],\"columns[2].queryType\":[\"EQ\"],\"columns[2].isRequired\":[\"1\"],\"columns[2].htmlType\":[\"select\"],\"columns[2].dictType\":[\"\"],\"columns[3].columnId\":[\"23\"],\"columns[3].sort\":[\"4\"],\"columns[3].columnComment\":[\"服务IP\"],\"columns[3].javaType\":[\"String\"],\"columns[3].javaField\":[\"serverIp\"],\"columns[3].isInsert\":[\"1\"],\"columns[3].isEdit\":[\"1\"],\"columns[3].isList\":[\"1\"],\"columns[3].isQuery\":[\"1\"],\"columns[3].queryType\":[\"EQ\"],\"columns[3].isRequired\":[\"1\"],\"columns[3].htmlType\":[\"input\"],\"columns[3].dictType\":[\"\"],\"columns[4].columnId\":[\"24\"],\"columns[4].sort\":[\"5\"],\"columns[4].columnComment\":[\"服务端口号\"],\"columns[4].javaType\":[\"Integer\"],\"columns[4].javaField\":[\"serverPort\"],\"columns[4].isInsert\":[\"1\"],\"columns[4].isEdit\":[\"1\"],\"columns[4].isList\":[\"1\"],\"columns[4].queryType\":[\"EQ\"],\"columns[4].isRequired\":[\"1\"],\"columns[4].htmlType\":[\"in','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-10 23:03:58'),
(168,'代码生成',8,'com.ruoyi.generator.controller.GenController.download()','GET',1,'admin','研发部门','/tool/gen/download/up_project_server','127.0.0.1','内网IP','\"up_project_server\"',NULL,0,NULL,'2021-06-10 23:04:03'),
(169,'项目应用服务',1,'com.kyee.upgrade.controller.UpProjectServerController.addSave()','POST',1,'admin','研发部门','/upgrade/projectServer/add','127.0.0.1','内网IP','{\"serverId\":[\"1\"],\"projectId\":[\"1\"],\"productId\":[\"1\"],\"serverIp\":[\"192.168.22.31\"],\"serverPort\":[\"8080\"],\"serverKey\":[\"人民31服务\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 20:45:24'),
(170,'项目应用服务',1,'com.kyee.upgrade.controller.UpProjectServerController.addSave()','POST',1,'admin','研发部门','/upgrade/projectServer/add','127.0.0.1','内网IP','{\"serverKey\":[\"人民32\"],\"projectId\":[\"1\"],\"productId\":[\"1\"],\"serverIp\":[\"192.168.22.32\"],\"serverPort\":[\"8080\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:00:03'),
(171,'项目应用服务',1,'com.kyee.upgrade.controller.UpProjectServerController.addSave()','POST',1,'admin','研发部门','/upgrade/projectServer/add','127.0.0.1','内网IP','{\"serverKey\":[\"测试服务\"],\"projectId\":[\"1\"],\"productId\":[\"1\"],\"serverIp\":[\"192.168.22.100\"],\"serverPort\":[\"8080\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:00:30'),
(172,'项目应用服务',2,'com.kyee.upgrade.controller.UpProjectServerController.editSave()','POST',1,'admin','研发部门','/upgrade/projectServer/edit','127.0.0.1','内网IP','{\"serverId\":[\"2\"],\"projectId\":[\"1\"],\"productId\":[\"1\"],\"serverIp\":[\"192.168.22.32\"],\"serverPort\":[\"8080\"],\"serverKey\":[\"人民32服务\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:00:40'),
(173,'代码生成',6,'com.ruoyi.generator.controller.GenController.importTableSave()','POST',1,'admin','研发部门','/tool/gen/importTable','127.0.0.1','内网IP','{\"tables\":[\"up_patch\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:11:33'),
(174,'代码生成',3,'com.ruoyi.generator.controller.GenController.remove()','POST',1,'admin','研发部门','/tool/gen/remove','127.0.0.1','内网IP','{\"ids\":[\"6\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:15:00'),
(175,'代码生成',6,'com.ruoyi.generator.controller.GenController.importTableSave()','POST',1,'admin','研发部门','/tool/gen/importTable','127.0.0.1','内网IP','{\"tables\":[\"up_patch\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:15:03'),
(176,'字典类型',1,'com.ruoyi.web.controller.system.SysDictTypeController.addSave()','POST',1,'admin','研发部门','/system/dict/add','127.0.0.1','内网IP','{\"dictName\":[\"补丁包状态\"],\"dictType\":[\"up_patch_status\"],\"status\":[\"0\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:17:32'),
(177,'字典数据',1,'com.ruoyi.web.controller.system.SysDictDataController.addSave()','POST',1,'admin','研发部门','/system/dict/data/add','127.0.0.1','内网IP','{\"dictLabel\":[\"10\"],\"dictValue\":[\"已登记\"],\"dictType\":[\"up_patch_status\"],\"cssClass\":[\"\"],\"dictSort\":[\"10\"],\"listClass\":[\"default\"],\"isDefault\":[\"Y\"],\"status\":[\"0\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:19:20'),
(178,'字典数据',1,'com.ruoyi.web.controller.system.SysDictDataController.addSave()','POST',1,'admin','研发部门','/system/dict/data/add','127.0.0.1','内网IP','{\"dictLabel\":[\"已打包\"],\"dictValue\":[\"20\"],\"dictType\":[\"up_patch_status\"],\"cssClass\":[\"\"],\"dictSort\":[\"20\"],\"listClass\":[\"\"],\"isDefault\":[\"Y\"],\"status\":[\"0\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:19:42'),
(179,'字典数据',2,'com.ruoyi.web.controller.system.SysDictDataController.editSave()','POST',1,'admin','研发部门','/system/dict/data/edit','127.0.0.1','内网IP','{\"dictCode\":[\"31\"],\"dictLabel\":[\"20\"],\"dictValue\":[\"已打包\"],\"dictType\":[\"up_patch_status\"],\"cssClass\":[\"\"],\"dictSort\":[\"20\"],\"listClass\":[\"\"],\"isDefault\":[\"Y\"],\"status\":[\"0\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:19:50'),
(180,'字典数据',1,'com.ruoyi.web.controller.system.SysDictDataController.addSave()','POST',1,'admin','研发部门','/system/dict/data/add','127.0.0.1','内网IP','{\"dictLabel\":[\"部分升级\"],\"dictValue\":[\"30\"],\"dictType\":[\"up_patch_status\"],\"cssClass\":[\"\"],\"dictSort\":[\"30\"],\"listClass\":[\"default\"],\"isDefault\":[\"Y\"],\"status\":[\"0\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:21:30'),
(181,'字典数据',2,'com.ruoyi.web.controller.system.SysDictDataController.editSave()','POST',1,'admin','研发部门','/system/dict/data/edit','127.0.0.1','内网IP','{\"dictCode\":[\"30\"],\"dictLabel\":[\"已登记\"],\"dictValue\":[\"10\"],\"dictType\":[\"up_patch_status\"],\"cssClass\":[\"\"],\"dictSort\":[\"10\"],\"listClass\":[\"default\"],\"isDefault\":[\"Y\"],\"status\":[\"0\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:21:37'),
(182,'字典数据',2,'com.ruoyi.web.controller.system.SysDictDataController.editSave()','POST',1,'admin','研发部门','/system/dict/data/edit','127.0.0.1','内网IP','{\"dictCode\":[\"31\"],\"dictLabel\":[\"已打包\"],\"dictValue\":[\"20\"],\"dictType\":[\"up_patch_status\"],\"cssClass\":[\"\"],\"dictSort\":[\"20\"],\"listClass\":[\"\"],\"isDefault\":[\"Y\"],\"status\":[\"0\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:21:43'),
(183,'字典数据',1,'com.ruoyi.web.controller.system.SysDictDataController.addSave()','POST',1,'admin','研发部门','/system/dict/data/add','127.0.0.1','内网IP','{\"dictLabel\":[\"全部升级\"],\"dictValue\":[\"40\"],\"dictType\":[\"up_patch_status\"],\"cssClass\":[\"\"],\"dictSort\":[\"40\"],\"listClass\":[\"\"],\"isDefault\":[\"Y\"],\"status\":[\"0\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:25:36'),
(184,'字典数据',2,'com.ruoyi.web.controller.system.SysDictDataController.editSave()','POST',1,'admin','研发部门','/system/dict/data/edit','127.0.0.1','内网IP','{\"dictCode\":[\"30\"],\"dictLabel\":[\"登记\"],\"dictValue\":[\"10\"],\"dictType\":[\"up_patch_status\"],\"cssClass\":[\"\"],\"dictSort\":[\"10\"],\"listClass\":[\"default\"],\"isDefault\":[\"Y\"],\"status\":[\"0\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:25:40'),
(185,'字典数据',2,'com.ruoyi.web.controller.system.SysDictDataController.editSave()','POST',1,'admin','研发部门','/system/dict/data/edit','127.0.0.1','内网IP','{\"dictCode\":[\"31\"],\"dictLabel\":[\"打包\"],\"dictValue\":[\"20\"],\"dictType\":[\"up_patch_status\"],\"cssClass\":[\"\"],\"dictSort\":[\"20\"],\"listClass\":[\"\"],\"isDefault\":[\"Y\"],\"status\":[\"0\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:25:45'),
(186,'字典数据',1,'com.ruoyi.web.controller.system.SysDictDataController.addSave()','POST',1,'admin','研发部门','/system/dict/data/add','127.0.0.1','内网IP','{\"dictLabel\":[\"回退\"],\"dictValue\":[\"50\"],\"dictType\":[\"up_patch_status\"],\"cssClass\":[\"\"],\"dictSort\":[\"50\"],\"listClass\":[\"\"],\"isDefault\":[\"Y\"],\"status\":[\"0\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:26:03'),
(187,'字典数据',1,'com.ruoyi.web.controller.system.SysDictDataController.addSave()','POST',1,'admin','研发部门','/system/dict/data/add','127.0.0.1','内网IP','{\"dictLabel\":[\"作废\"],\"dictValue\":[\"60\"],\"dictType\":[\"up_patch_status\"],\"cssClass\":[\"\"],\"dictSort\":[\"60\"],\"listClass\":[\"\"],\"isDefault\":[\"Y\"],\"status\":[\"0\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:26:15'),
(188,'字典数据',1,'com.ruoyi.web.controller.system.SysDictDataController.addSave()','POST',1,'admin','研发部门','/system/dict/data/add','127.0.0.1','内网IP','{\"dictLabel\":[\"关闭\"],\"dictValue\":[\"70\"],\"dictType\":[\"up_patch_status\"],\"cssClass\":[\"\"],\"dictSort\":[\"70\"],\"listClass\":[\"\"],\"isDefault\":[\"Y\"],\"status\":[\"0\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:26:47'),
(189,'代码生成',2,'com.ruoyi.generator.controller.GenController.synchDb()','GET',1,'admin','研发部门','/tool/gen/synchDb/up_patch','127.0.0.1','内网IP','\"up_patch\"','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:28:08'),
(190,'代码生成',3,'com.ruoyi.generator.controller.GenController.remove()','POST',1,'admin','研发部门','/tool/gen/remove','127.0.0.1','内网IP','{\"ids\":[\"7\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:28:17'),
(191,'代码生成',6,'com.ruoyi.generator.controller.GenController.importTableSave()','POST',1,'admin','研发部门','/tool/gen/importTable','127.0.0.1','内网IP','{\"tables\":[\"up_patch\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:28:35'),
(192,'代码生成',2,'com.ruoyi.generator.controller.GenController.synchDb()','GET',1,'admin','研发部门','/tool/gen/synchDb/up_patch','127.0.0.1','内网IP','\"up_patch\"','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:31:02'),
(193,'代码生成',2,'com.ruoyi.generator.controller.GenController.synchDb()','GET',1,'admin','研发部门','/tool/gen/synchDb/up_patch','127.0.0.1','内网IP','\"up_patch\"','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:32:13'),
(194,'代码生成',3,'com.ruoyi.generator.controller.GenController.remove()','POST',1,'admin','研发部门','/tool/gen/remove','127.0.0.1','内网IP','{\"ids\":[\"8\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:32:46'),
(195,'代码生成',6,'com.ruoyi.generator.controller.GenController.importTableSave()','POST',1,'admin','研发部门','/tool/gen/importTable','127.0.0.1','内网IP','{\"tables\":[\"up_patch\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:32:50'),
(196,'代码生成',2,'com.ruoyi.generator.controller.GenController.editSave()','POST',1,'admin','研发部门','/tool/gen/edit','127.0.0.1','内网IP','{\"tableId\":[\"9\"],\"tableName\":[\"up_patch\"],\"tableComment\":[\"补丁表\"],\"className\":[\"UpPatch\"],\"functionAuthor\":[\"lijunqiang\"],\"remark\":[\"\"],\"columns[0].columnId\":[\"52\"],\"columns[0].sort\":[\"1\"],\"columns[0].columnComment\":[\"补丁ID\"],\"columns[0].javaType\":[\"Long\"],\"columns[0].javaField\":[\"patchId\"],\"columns[0].isInsert\":[\"1\"],\"columns[0].queryType\":[\"EQ\"],\"columns[0].htmlType\":[\"input\"],\"columns[0].dictType\":[\"\"],\"columns[1].columnId\":[\"53\"],\"columns[1].sort\":[\"2\"],\"columns[1].columnComment\":[\"产品ID\"],\"columns[1].javaType\":[\"Integer\"],\"columns[1].javaField\":[\"productId\"],\"columns[1].isInsert\":[\"1\"],\"columns[1].isEdit\":[\"1\"],\"columns[1].isList\":[\"1\"],\"columns[1].isQuery\":[\"1\"],\"columns[1].queryType\":[\"EQ\"],\"columns[1].isRequired\":[\"1\"],\"columns[1].htmlType\":[\"input\"],\"columns[1].dictType\":[\"\"],\"columns[2].columnId\":[\"54\"],\"columns[2].sort\":[\"3\"],\"columns[2].columnComment\":[\"项目ID\"],\"columns[2].javaType\":[\"Integer\"],\"columns[2].javaField\":[\"projectId\"],\"columns[2].isInsert\":[\"1\"],\"columns[2].isEdit\":[\"1\"],\"columns[2].isList\":[\"1\"],\"columns[2].isQuery\":[\"1\"],\"columns[2].queryType\":[\"EQ\"],\"columns[2].isRequired\":[\"1\"],\"columns[2].htmlType\":[\"input\"],\"columns[2].dictType\":[\"\"],\"columns[3].columnId\":[\"55\"],\"columns[3].sort\":[\"4\"],\"columns[3].columnComment\":[\"补丁文件名\"],\"columns[3].javaType\":[\"String\"],\"columns[3].javaField\":[\"patchFileName\"],\"columns[3].isInsert\":[\"1\"],\"columns[3].isEdit\":[\"1\"],\"columns[3].isList\":[\"1\"],\"columns[3].isQuery\":[\"1\"],\"columns[3].queryType\":[\"LIKE\"],\"columns[3].isRequired\":[\"1\"],\"columns[3].htmlType\":[\"input\"],\"columns[3].dictType\":[\"\"],\"columns[4].columnId\":[\"56\"],\"columns[4].sort\":[\"5\"],\"columns[4].columnComment\":[\"补丁文件URL\"],\"columns[4].javaType\":[\"String\"],\"columns[4].javaField\":[\"patchFileUrl\"],\"columns[4].isInsert\":[\"1\"],\"columns[4].isEdit\":[\"1\"],\"columns[4].isList\":[\"1\"],\"columns[4].queryType\":[\"EQ\"],\"columns[4].isRequired\":[\"1\"],\"columns[4].htmlType\":[\"textarea\"],\"columns[4].dictType\":[\"\"],\"columns[5].columnId\":[\"57\"],\"columns[5].sort\":[\"6\"],\"columns[','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:33:53'),
(197,'代码生成',8,'com.ruoyi.generator.controller.GenController.download()','GET',1,'admin','研发部门','/tool/gen/download/up_patch','127.0.0.1','内网IP','\"up_patch\"',NULL,0,NULL,'2021-06-12 21:34:06'),
(198,'代码生成',2,'com.ruoyi.generator.controller.GenController.editSave()','POST',1,'admin','研发部门','/tool/gen/edit','127.0.0.1','内网IP','{\"tableId\":[\"9\"],\"tableName\":[\"up_patch\"],\"tableComment\":[\"补丁表\"],\"className\":[\"UpPatch\"],\"functionAuthor\":[\"lijunqiang\"],\"remark\":[\"\"],\"columns[0].columnId\":[\"52\"],\"columns[0].sort\":[\"1\"],\"columns[0].columnComment\":[\"补丁ID\"],\"columns[0].javaType\":[\"Long\"],\"columns[0].javaField\":[\"patchId\"],\"columns[0].isInsert\":[\"1\"],\"columns[0].queryType\":[\"EQ\"],\"columns[0].htmlType\":[\"input\"],\"columns[0].dictType\":[\"\"],\"columns[1].columnId\":[\"53\"],\"columns[1].sort\":[\"2\"],\"columns[1].columnComment\":[\"产品ID\"],\"columns[1].javaType\":[\"Integer\"],\"columns[1].javaField\":[\"productId\"],\"columns[1].isInsert\":[\"1\"],\"columns[1].isEdit\":[\"1\"],\"columns[1].isList\":[\"1\"],\"columns[1].isQuery\":[\"1\"],\"columns[1].queryType\":[\"EQ\"],\"columns[1].isRequired\":[\"1\"],\"columns[1].htmlType\":[\"select\"],\"columns[1].dictType\":[\"\"],\"columns[2].columnId\":[\"54\"],\"columns[2].sort\":[\"3\"],\"columns[2].columnComment\":[\"项目ID\"],\"columns[2].javaType\":[\"Integer\"],\"columns[2].javaField\":[\"projectId\"],\"columns[2].isInsert\":[\"1\"],\"columns[2].isEdit\":[\"1\"],\"columns[2].isList\":[\"1\"],\"columns[2].isQuery\":[\"1\"],\"columns[2].queryType\":[\"EQ\"],\"columns[2].isRequired\":[\"1\"],\"columns[2].htmlType\":[\"select\"],\"columns[2].dictType\":[\"\"],\"columns[3].columnId\":[\"55\"],\"columns[3].sort\":[\"4\"],\"columns[3].columnComment\":[\"补丁文件名\"],\"columns[3].javaType\":[\"String\"],\"columns[3].javaField\":[\"patchFileName\"],\"columns[3].isInsert\":[\"1\"],\"columns[3].isEdit\":[\"1\"],\"columns[3].isList\":[\"1\"],\"columns[3].isQuery\":[\"1\"],\"columns[3].queryType\":[\"LIKE\"],\"columns[3].isRequired\":[\"1\"],\"columns[3].htmlType\":[\"input\"],\"columns[3].dictType\":[\"\"],\"columns[4].columnId\":[\"56\"],\"columns[4].sort\":[\"5\"],\"columns[4].columnComment\":[\"补丁文件URL\"],\"columns[4].javaType\":[\"String\"],\"columns[4].javaField\":[\"patchFileUrl\"],\"columns[4].isInsert\":[\"1\"],\"columns[4].isEdit\":[\"1\"],\"columns[4].isList\":[\"1\"],\"columns[4].queryType\":[\"EQ\"],\"columns[4].isRequired\":[\"1\"],\"columns[4].htmlType\":[\"textarea\"],\"columns[4].dictType\":[\"\"],\"columns[5].columnId\":[\"57\"],\"columns[5].sort\":[\"6\"],\"column','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:47:38'),
(199,'代码生成',8,'com.ruoyi.generator.controller.GenController.download()','GET',1,'admin','研发部门','/tool/gen/download/up_patch','127.0.0.1','内网IP','\"up_patch\"',NULL,0,NULL,'2021-06-12 21:48:24'),
(200,'代码生成',2,'com.ruoyi.generator.controller.GenController.editSave()','POST',1,'admin','研发部门','/tool/gen/edit','127.0.0.1','内网IP','{\"tableId\":[\"9\"],\"tableName\":[\"up_patch\"],\"tableComment\":[\"补丁表\"],\"className\":[\"UpPatch\"],\"functionAuthor\":[\"lijunqiang\"],\"remark\":[\"\"],\"columns[0].columnId\":[\"52\"],\"columns[0].sort\":[\"1\"],\"columns[0].columnComment\":[\"补丁ID\"],\"columns[0].javaType\":[\"Long\"],\"columns[0].javaField\":[\"patchId\"],\"columns[0].isInsert\":[\"1\"],\"columns[0].queryType\":[\"EQ\"],\"columns[0].htmlType\":[\"input\"],\"columns[0].dictType\":[\"\"],\"columns[1].columnId\":[\"53\"],\"columns[1].sort\":[\"2\"],\"columns[1].columnComment\":[\"产品ID\"],\"columns[1].javaType\":[\"Integer\"],\"columns[1].javaField\":[\"productId\"],\"columns[1].isInsert\":[\"1\"],\"columns[1].isEdit\":[\"1\"],\"columns[1].isList\":[\"1\"],\"columns[1].isQuery\":[\"1\"],\"columns[1].queryType\":[\"EQ\"],\"columns[1].isRequired\":[\"1\"],\"columns[1].htmlType\":[\"select\"],\"columns[1].dictType\":[\"\"],\"columns[2].columnId\":[\"54\"],\"columns[2].sort\":[\"3\"],\"columns[2].columnComment\":[\"项目ID\"],\"columns[2].javaType\":[\"Integer\"],\"columns[2].javaField\":[\"projectId\"],\"columns[2].isInsert\":[\"1\"],\"columns[2].isEdit\":[\"1\"],\"columns[2].isList\":[\"1\"],\"columns[2].isQuery\":[\"1\"],\"columns[2].queryType\":[\"EQ\"],\"columns[2].isRequired\":[\"1\"],\"columns[2].htmlType\":[\"select\"],\"columns[2].dictType\":[\"\"],\"columns[3].columnId\":[\"55\"],\"columns[3].sort\":[\"4\"],\"columns[3].columnComment\":[\"补丁文件名\"],\"columns[3].javaType\":[\"String\"],\"columns[3].javaField\":[\"patchFileName\"],\"columns[3].isInsert\":[\"1\"],\"columns[3].isEdit\":[\"1\"],\"columns[3].isList\":[\"1\"],\"columns[3].isQuery\":[\"1\"],\"columns[3].queryType\":[\"LIKE\"],\"columns[3].isRequired\":[\"1\"],\"columns[3].htmlType\":[\"input\"],\"columns[3].dictType\":[\"\"],\"columns[4].columnId\":[\"56\"],\"columns[4].sort\":[\"5\"],\"columns[4].columnComment\":[\"补丁文件URL\"],\"columns[4].javaType\":[\"String\"],\"columns[4].javaField\":[\"patchFileUrl\"],\"columns[4].isInsert\":[\"1\"],\"columns[4].isEdit\":[\"1\"],\"columns[4].isList\":[\"1\"],\"columns[4].queryType\":[\"EQ\"],\"columns[4].isRequired\":[\"1\"],\"columns[4].htmlType\":[\"textarea\"],\"columns[4].dictType\":[\"\"],\"columns[5].columnId\":[\"57\"],\"columns[5].sort\":[\"6\"],\"column','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 21:56:18'),
(201,'代码生成',8,'com.ruoyi.generator.controller.GenController.download()','GET',1,'admin','研发部门','/tool/gen/download/up_patch','127.0.0.1','内网IP','\"up_patch\"',NULL,0,NULL,'2021-06-12 21:56:21'),
(202,'代码生成',2,'com.ruoyi.generator.controller.GenController.synchDb()','GET',1,'admin','研发部门','/tool/gen/synchDb/up_patch','127.0.0.1','内网IP','\"up_patch\"','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 22:26:58'),
(203,'代码生成',2,'com.ruoyi.generator.controller.GenController.synchDb()','GET',1,'admin','研发部门','/tool/gen/synchDb/up_patch','127.0.0.1','内网IP','\"up_patch\"','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 22:50:33'),
(204,'代码生成',2,'com.ruoyi.generator.controller.GenController.editSave()','POST',1,'admin','研发部门','/tool/gen/edit','127.0.0.1','内网IP','{\"tableId\":[\"9\"],\"tableName\":[\"up_patch\"],\"tableComment\":[\"补丁表\"],\"className\":[\"UpPatch\"],\"functionAuthor\":[\"lijunqiang\"],\"remark\":[\"\"],\"columns[0].columnId\":[\"52\"],\"columns[0].sort\":[\"1\"],\"columns[0].columnComment\":[\"补丁ID\"],\"columns[0].javaType\":[\"Long\"],\"columns[0].javaField\":[\"patchId\"],\"columns[0].isInsert\":[\"1\"],\"columns[0].queryType\":[\"EQ\"],\"columns[0].htmlType\":[\"input\"],\"columns[0].dictType\":[\"\"],\"columns[1].columnId\":[\"53\"],\"columns[1].sort\":[\"2\"],\"columns[1].columnComment\":[\"产品ID\"],\"columns[1].javaType\":[\"Integer\"],\"columns[1].javaField\":[\"productId\"],\"columns[1].isInsert\":[\"1\"],\"columns[1].isEdit\":[\"1\"],\"columns[1].isList\":[\"1\"],\"columns[1].isQuery\":[\"1\"],\"columns[1].queryType\":[\"EQ\"],\"columns[1].isRequired\":[\"1\"],\"columns[1].htmlType\":[\"select\"],\"columns[1].dictType\":[\"\"],\"columns[2].columnId\":[\"54\"],\"columns[2].sort\":[\"3\"],\"columns[2].columnComment\":[\"项目ID\"],\"columns[2].javaType\":[\"Integer\"],\"columns[2].javaField\":[\"projectId\"],\"columns[2].isInsert\":[\"1\"],\"columns[2].isEdit\":[\"1\"],\"columns[2].isList\":[\"1\"],\"columns[2].isQuery\":[\"1\"],\"columns[2].queryType\":[\"EQ\"],\"columns[2].isRequired\":[\"1\"],\"columns[2].htmlType\":[\"select\"],\"columns[2].dictType\":[\"\"],\"columns[3].columnId\":[\"55\"],\"columns[3].sort\":[\"4\"],\"columns[3].columnComment\":[\"补丁文件名\"],\"columns[3].javaType\":[\"String\"],\"columns[3].javaField\":[\"patchFileName\"],\"columns[3].isInsert\":[\"1\"],\"columns[3].isEdit\":[\"1\"],\"columns[3].isList\":[\"1\"],\"columns[3].isQuery\":[\"1\"],\"columns[3].queryType\":[\"LIKE\"],\"columns[3].isRequired\":[\"1\"],\"columns[3].htmlType\":[\"input\"],\"columns[3].dictType\":[\"\"],\"columns[4].columnId\":[\"56\"],\"columns[4].sort\":[\"5\"],\"columns[4].columnComment\":[\"补丁文件URL\"],\"columns[4].javaType\":[\"String\"],\"columns[4].javaField\":[\"patchFileUrl\"],\"columns[4].isInsert\":[\"1\"],\"columns[4].isEdit\":[\"1\"],\"columns[4].isList\":[\"1\"],\"columns[4].queryType\":[\"EQ\"],\"columns[4].isRequired\":[\"1\"],\"columns[4].htmlType\":[\"textarea\"],\"columns[4].dictType\":[\"\"],\"columns[5].columnId\":[\"57\"],\"columns[5].sort\":[\"6\"],\"column','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 22:52:20'),
(205,'代码生成',8,'com.ruoyi.generator.controller.GenController.download()','GET',1,'admin','研发部门','/tool/gen/download/up_patch','127.0.0.1','内网IP','\"up_patch\"',NULL,0,NULL,'2021-06-12 22:52:42'),
(206,'补丁包管理',1,'com.kyee.upgrade.controller.UpPatchController.addSave()','POST',1,'admin','研发部门','/upgrade/patch/add','127.0.0.1','内网IP','{\"projectId\":[\"1\"],\"productId\":[\"1\"],\"patchFileName\":[\"PATCH_YUNHIS-18455_20210611172625.ZIP\"],\"patchFileUrl\":[\"PATCH_YUNHIS-18455_20210611172625.ZIP\"],\"sqlFlag\":[\"N\"],\"patchStatus\":[\"10\"],\"bugFlag\":[\"Y\"],\"taskList\":[\"     YUNHIS-18406【随州】上传文件添加/hosnum/nodecode，保存文件路径时保存/hosnum/nodecode/{filename},sql插入的数据不允许修改\\r\\n      YUNHIS-18116【随州市中医医院】药房药品请领功能可根据药品消耗量生成请领单\\r\\n      YUNHIS-18399【随州市中医院】医生站开立检验项目右上角加急框项目可控，与申请单格式调整\\r\\n      YUNHIS-18429【随州市中医医院】门诊中药处方单模版优化\\r\\n      YUNHIS-18115【随州市中医医院】住院医生站通过药房控制药品是否可供\\r\\n      YUNCOMMERCIALBUG-5221【随州市中医医院】医嘱单打印时，药品名称与用法频次在一行造成重叠\\r\\n      YUNHIS-18107【随州市中医医院】门诊医生站检索药品通过药房控制药品是否可供\\r\\n      YUNHIS-18404【随州市中医医院】收费员搜索患者办理住院操作优化\\r\\n      YUNHIS-18129【随州市中医医院】收费员自动日结，时间可设置\\r\\n      YUNHIS-18455【随州中医】当天已经挂号的患者，医生再进行建档时可以进行提示。适配入出院管理-住院登记\\r\\n\\r\\n      定时器：\\r\\n      YUNHIS-18129【随州市中医医院】收费员自动日结，时间可设置\"],\"bugfixFlag\":[\"N\"],\"bugfixPatch\":[\"\"],\"createUserId\":[\"1\"],\"memo\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-12 23:16:15'),
(207,'代码生成',2,'com.ruoyi.generator.controller.GenController.synchDb()','GET',1,'admin','研发部门','/tool/gen/synchDb/up_patch','127.0.0.1','内网IP','\"up_patch\"','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 10:13:22'),
(208,'代码生成',2,'com.ruoyi.generator.controller.GenController.synchDb()','GET',1,'admin','研发部门','/tool/gen/synchDb/up_project_server','127.0.0.1','内网IP','\"up_project_server\"','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 10:13:24'),
(209,'代码生成',2,'com.ruoyi.generator.controller.GenController.synchDb()','GET',1,'admin','研发部门','/tool/gen/synchDb/up_project_product','127.0.0.1','内网IP','\"up_project_product\"','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 10:13:27'),
(210,'代码生成',2,'com.ruoyi.generator.controller.GenController.synchDb()','GET',1,'admin','研发部门','/tool/gen/synchDb/up_project','127.0.0.1','内网IP','\"up_project\"','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 10:13:29'),
(211,'代码生成',2,'com.ruoyi.generator.controller.GenController.synchDb()','GET',1,'admin','研发部门','/tool/gen/synchDb/up_product','127.0.0.1','内网IP','\"up_product\"','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 10:13:32'),
(212,'代码生成',2,'com.ruoyi.generator.controller.GenController.editSave()','POST',1,'admin','研发部门','/tool/gen/edit','127.0.0.1','内网IP','{\"tableId\":[\"2\"],\"tableName\":[\"up_project\"],\"tableComment\":[\"项目表\"],\"className\":[\"UpProject\"],\"functionAuthor\":[\"lijunqiang\"],\"remark\":[\"\"],\"columns[0].columnId\":[\"4\"],\"columns[0].sort\":[\"1\"],\"columns[0].columnComment\":[\"项目ID\"],\"columns[0].javaType\":[\"Integer\"],\"columns[0].javaField\":[\"projectId\"],\"columns[0].isInsert\":[\"1\"],\"columns[0].queryType\":[\"EQ\"],\"columns[0].htmlType\":[\"input\"],\"columns[0].dictType\":[\"\"],\"columns[1].columnId\":[\"5\"],\"columns[1].sort\":[\"2\"],\"columns[1].columnComment\":[\"项目名\"],\"columns[1].javaType\":[\"String\"],\"columns[1].javaField\":[\"projectName\"],\"columns[1].isInsert\":[\"1\"],\"columns[1].isEdit\":[\"1\"],\"columns[1].isList\":[\"1\"],\"columns[1].isQuery\":[\"1\"],\"columns[1].queryType\":[\"LIKE\"],\"columns[1].isRequired\":[\"1\"],\"columns[1].htmlType\":[\"input\"],\"columns[1].dictType\":[\"\"],\"columns[2].columnId\":[\"83\"],\"columns[2].sort\":[\"3\"],\"columns[2].columnComment\":[\"创建者\"],\"columns[2].javaType\":[\"String\"],\"columns[2].javaField\":[\"createBy\"],\"columns[2].isInsert\":[\"1\"],\"columns[2].queryType\":[\"EQ\"],\"columns[2].htmlType\":[\"input\"],\"columns[2].dictType\":[\"\"],\"columns[3].columnId\":[\"84\"],\"columns[3].sort\":[\"4\"],\"columns[3].columnComment\":[\"创建时间\"],\"columns[3].javaType\":[\"Date\"],\"columns[3].javaField\":[\"createTime\"],\"columns[3].isInsert\":[\"1\"],\"columns[3].queryType\":[\"EQ\"],\"columns[3].htmlType\":[\"datetime\"],\"columns[3].dictType\":[\"\"],\"columns[4].columnId\":[\"85\"],\"columns[4].sort\":[\"5\"],\"columns[4].columnComment\":[\"更新者\"],\"columns[4].javaType\":[\"String\"],\"columns[4].javaField\":[\"updateBy\"],\"columns[4].isInsert\":[\"1\"],\"columns[4].isEdit\":[\"1\"],\"columns[4].queryType\":[\"EQ\"],\"columns[4].htmlType\":[\"input\"],\"columns[4].dictType\":[\"\"],\"columns[5].columnId\":[\"86\"],\"columns[5].sort\":[\"6\"],\"columns[5].columnComment\":[\"更新时间\"],\"columns[5].javaType\":[\"Date\"],\"columns[5].javaField\":[\"updateTime\"],\"columns[5].isInsert\":[\"1\"],\"columns[5].isEdit\":[\"1\"],\"columns[5].queryType\":[\"EQ\"],\"columns[5].htmlType\":[\"datetime\"],\"columns[5].dictType\":[\"\"],\"columns[6].columnId\":[\"87\"],\"columns[6].s','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 10:14:18'),
(213,'代码生成',2,'com.ruoyi.generator.controller.GenController.synchDb()','GET',1,'admin','研发部门','/tool/gen/synchDb/up_project','127.0.0.1','内网IP','\"up_project\"','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 10:23:27'),
(214,'代码生成',8,'com.ruoyi.generator.controller.GenController.download()','GET',1,'admin','研发部门','/tool/gen/download/up_project','127.0.0.1','内网IP','\"up_project\"',NULL,0,NULL,'2021-06-13 10:25:20'),
(215,'代码生成',8,'com.ruoyi.generator.controller.GenController.batchGenCode()','GET',1,'admin','研发部门','/tool/gen/batchGenCode','127.0.0.1','内网IP','{\"tables\":[\"up_patch,up_project_server,up_project_product,up_project,up_product\"]}',NULL,0,NULL,'2021-06-13 10:52:53'),
(216,'代码生成',2,'com.ruoyi.generator.controller.GenController.synchDb()','GET',1,'admin','研发部门','/tool/gen/synchDb/up_project_server','127.0.0.1','内网IP','\"up_project_server\"','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 11:18:25'),
(217,'代码生成',8,'com.ruoyi.generator.controller.GenController.download()','GET',1,'admin','研发部门','/tool/gen/download/up_project_server','127.0.0.1','内网IP','\"up_project_server\"',NULL,0,NULL,'2021-06-13 11:18:38'),
(218,'产品',3,'com.kyee.upgrade.controller.UpProductController.remove()','POST',1,'admin','研发部门','/upgrade/product/remove','127.0.0.1','内网IP','{\"ids\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 11:28:04'),
(219,'补丁包管理',2,'com.kyee.upgrade.controller.UpPatchController.editSave()','POST',1,'admin','研发部门','/upgrade/patch/edit','127.0.0.1','内网IP','{\"patchId\":[\"1\"],\"projectId\":[\"1\"],\"productId\":[\"1\"],\"patchFileName\":[\"PATCH_YUNHIS-18455_20210611172625.ZIP\"],\"patchFileUrl\":[\"PATCH_YUNHIS-18455_20210611172625.ZIP\"],\"sqlFlag\":[\"N\"],\"patchStatus\":[\"10\"],\"taskList\":[\"     YUNHIS-18406【随州】上传文件添加/hosnum/nodecode，保存文件路径时保存/hosnum/nodecode/{filename},sql插入的数据不允许修改\\r\\n      YUNHIS-18116【随州市中医医院】药房药品请领功能可根据药品消耗量生成请领单\\r\\n      YUNHIS-18399【随州市中医院】医生站开立检验项目右上角加急框项目可控，与申请单格式调整\\r\\n      YUNHIS-18429【随州市中医医院】门诊中药处方单模版优化\\r\\n      YUNHIS-18115【随州市中医医院】住院医生站通过药房控制药品是否可供\\r\\n      YUNCOMMERCIALBUG-5221【随州市中医医院】医嘱单打印时，药品名称与用法频次在一行造成重叠\\r\\n      YUNHIS-18107【随州市中医医院】门诊医生站检索药品通过药房控制药品是否可供\\r\\n      YUNHIS-18404【随州市中医医院】收费员搜索患者办理住院操作优化\\r\\n      YUNHIS-18129【随州市中医医院】收费员自动日结，时间可设置\\r\\n      YUNHIS-18455【随州中医】当天已经挂号的患者，医生再进行建档时可以进行提示。适配入出院管理-住院登记\\r\\n\\r\\n      定时器：\\r\\n      YUNHIS-18129【随州市中医医院】收费员自动日结，时间可设置\"],\"bugFlag\":[\"N\"],\"bugfixFlag\":[\"N\"],\"bugfixPatch\":[\"\"],\"createBy\":[\"朱燕\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 11:52:26'),
(220,'项目',3,'com.kyee.upgrade.controller.UpProjectController.remove()','POST',1,'admin','研发部门','/upgrade/project/remove','127.0.0.1','内网IP','{\"ids\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 11:55:54'),
(221,'项目',2,'com.kyee.upgrade.controller.UpProjectController.editSave()','POST',1,'admin','研发部门','/upgrade/project/edit','127.0.0.1','内网IP','{\"projectId\":[\"1\"],\"projectName\":[\"阳西\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 12:17:27'),
(222,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.editSave()','POST',1,'admin','研发部门','/system/menu/edit','127.0.0.1','内网IP','{\"menuId\":[\"1065\"],\"parentId\":[\"1062\"],\"menuType\":[\"C\"],\"menuName\":[\"产品管理\"],\"url\":[\"/upgrade/product\"],\"target\":[\"menuItem\"],\"perms\":[\"upgrade:product:view\"],\"orderNum\":[\"1\"],\"icon\":[\"fa fa-cart-plus\"],\"visible\":[\"0\"],\"isRefresh\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 17:30:44'),
(223,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.editSave()','POST',1,'admin','研发部门','/system/menu/edit','127.0.0.1','内网IP','{\"menuId\":[\"1071\"],\"parentId\":[\"1062\"],\"menuType\":[\"C\"],\"menuName\":[\"项目管理\"],\"url\":[\"/upgrade/project\"],\"target\":[\"menuItem\"],\"perms\":[\"upgrade:project:view\"],\"orderNum\":[\"1\"],\"icon\":[\"fa fa-handshake-o\"],\"visible\":[\"0\"],\"isRefresh\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 17:31:39'),
(224,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.editSave()','POST',1,'admin','研发部门','/system/menu/edit','127.0.0.1','内网IP','{\"menuId\":[\"1077\"],\"parentId\":[\"1062\"],\"menuType\":[\"C\"],\"menuName\":[\"项目产品管理\"],\"url\":[\"/upgrade/projectProduct\"],\"target\":[\"menuItem\"],\"perms\":[\"upgrade:projectProduct:view\"],\"orderNum\":[\"1\"],\"icon\":[\"fa fa-asl-interpreting\"],\"visible\":[\"0\"],\"isRefresh\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 17:32:13'),
(225,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.editSave()','POST',1,'admin','研发部门','/system/menu/edit','127.0.0.1','内网IP','{\"menuId\":[\"1083\"],\"parentId\":[\"1062\"],\"menuType\":[\"C\"],\"menuName\":[\"项目应用服务\"],\"url\":[\"/upgrade/projectServer\"],\"target\":[\"menuItem\"],\"perms\":[\"upgrade:projectServer:view\"],\"orderNum\":[\"1\"],\"icon\":[\"fa fa-server\"],\"visible\":[\"0\"],\"isRefresh\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 17:33:02'),
(226,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.editSave()','POST',1,'admin','研发部门','/system/menu/edit','127.0.0.1','内网IP','{\"menuId\":[\"1089\"],\"parentId\":[\"1062\"],\"menuType\":[\"C\"],\"menuName\":[\"补丁包管理\"],\"url\":[\"/upgrade/patch\"],\"target\":[\"menuItem\"],\"perms\":[\"upgrade:patch:view\"],\"orderNum\":[\"1\"],\"icon\":[\"fa fa-bug\"],\"visible\":[\"0\"],\"isRefresh\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 17:33:12'),
(227,'字典数据',1,'com.ruoyi.web.controller.system.SysDictDataController.addSave()','POST',1,'admin','研发部门','/system/dict/data/add','127.0.0.1','内网IP','{\"dictLabel\":[\"发布\"],\"dictValue\":[\"15\"],\"dictType\":[\"up_patch_status\"],\"cssClass\":[\"\"],\"dictSort\":[\"15\"],\"listClass\":[\"\"],\"isDefault\":[\"Y\"],\"status\":[\"0\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 17:41:00'),
(228,'参数管理',1,'com.ruoyi.web.controller.system.SysConfigController.addSave()','POST',1,'admin','研发部门','/system/config/add','127.0.0.1','内网IP','{\"configName\":[\"补丁管理端OR升级端\"],\"configKey\":[\"管理端\"],\"configValue\":[\"10\"],\"configType\":[\"Y\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 17:56:23'),
(229,'参数管理',2,'com.ruoyi.web.controller.system.SysConfigController.editSave()','POST',1,'admin','研发部门','/system/config/edit','127.0.0.1','内网IP','{\"configId\":[\"10\"],\"configName\":[\"补丁管理端OR升级端\"],\"configKey\":[\"up.systype.manager\"],\"configValue\":[\"10\"],\"configType\":[\"Y\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 18:00:55'),
(230,'参数管理',2,'com.ruoyi.web.controller.system.SysConfigController.editSave()','POST',1,'admin','研发部门','/system/config/edit','127.0.0.1','内网IP','{\"configId\":[\"10\"],\"configName\":[\"补丁管理端OR升级端\"],\"configKey\":[\"up.sys.usetype\"],\"configValue\":[\"10\"],\"configType\":[\"Y\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 18:01:34'),
(231,'参数管理',2,'com.ruoyi.web.controller.system.SysConfigController.editSave()','POST',1,'admin','研发部门','/system/config/edit','127.0.0.1','内网IP','{\"configId\":[\"10\"],\"configName\":[\"补丁管理端OR升级端\"],\"configKey\":[\"up.sys.usetype\"],\"configValue\":[\"管理端\"],\"configType\":[\"Y\"],\"remark\":[\"管理端：配置产品、项目、补丁包\\r\\n升级端：升级操作\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 18:02:31'),
(232,'参数管理',2,'com.ruoyi.web.controller.system.SysConfigController.editSave()','POST',1,'admin','研发部门','/system/config/edit','127.0.0.1','内网IP','{\"configId\":[\"10\"],\"configName\":[\"补丁管理端OR升级端\"],\"configKey\":[\"up.sys.usetype\"],\"configValue\":[\"0\"],\"configType\":[\"Y\"],\"remark\":[\"0-管理端：配置产品、项目、补丁包\\r\\n1-升级端：升级操作\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 18:02:55'),
(233,'字典类型',1,'com.ruoyi.web.controller.system.SysDictTypeController.addSave()','POST',1,'admin','研发部门','/system/dict/add','127.0.0.1','内网IP','{\"dictName\":[\"升级状态\"],\"dictType\":[\"up_upgrade_status\"],\"status\":[\"0\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 19:14:56'),
(234,'字典数据',1,'com.ruoyi.web.controller.system.SysDictDataController.addSave()','POST',1,'admin','研发部门','/system/dict/data/add','127.0.0.1','内网IP','{\"dictLabel\":[\"未升级\"],\"dictValue\":[\"10\"],\"dictType\":[\"up_upgrade_status\"],\"cssClass\":[\"\"],\"dictSort\":[\"10\"],\"listClass\":[\"\"],\"isDefault\":[\"Y\"],\"status\":[\"0\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 19:15:27'),
(235,'字典数据',1,'com.ruoyi.web.controller.system.SysDictDataController.addSave()','POST',1,'admin','研发部门','/system/dict/data/add','127.0.0.1','内网IP','{\"dictLabel\":[\"已升级\"],\"dictValue\":[\"20\"],\"dictType\":[\"up_upgrade_status\"],\"cssClass\":[\"\"],\"dictSort\":[\"20\"],\"listClass\":[\"\"],\"isDefault\":[\"Y\"],\"status\":[\"0\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 19:15:40'),
(236,'字典数据',1,'com.ruoyi.web.controller.system.SysDictDataController.addSave()','POST',1,'admin','研发部门','/system/dict/data/add','127.0.0.1','内网IP','{\"dictLabel\":[\"已回退\"],\"dictValue\":[\"80\"],\"dictType\":[\"up_upgrade_status\"],\"cssClass\":[\"\"],\"dictSort\":[\"80\"],\"listClass\":[\"\"],\"isDefault\":[\"Y\"],\"status\":[\"0\"],\"remark\":[\"\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 19:16:03'),
(237,'代码生成',6,'com.ruoyi.generator.controller.GenController.importTableSave()','POST',1,'admin','研发部门','/tool/gen/importTable','127.0.0.1','内网IP','{\"tables\":[\"up_upgrade_record\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 20:51:19'),
(238,'代码生成',2,'com.ruoyi.generator.controller.GenController.synchDb()','GET',1,'admin','研发部门','/tool/gen/synchDb/up_upgrade_record','127.0.0.1','内网IP','\"up_upgrade_record\"','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 20:54:43'),
(239,'代码生成',6,'com.ruoyi.generator.controller.GenController.importTableSave()','POST',1,'admin','研发部门','/tool/gen/importTable','127.0.0.1','内网IP','{\"tables\":[\"up_upgrade_log\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 20:54:47'),
(240,'代码生成',2,'com.ruoyi.generator.controller.GenController.synchDb()','GET',1,'admin','研发部门','/tool/gen/synchDb/up_upgrade_log','127.0.0.1','内网IP','\"up_upgrade_log\"','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 20:54:49'),
(241,'代码生成',2,'com.ruoyi.generator.controller.GenController.editSave()','POST',1,'admin','研发部门','/tool/gen/edit','127.0.0.1','内网IP','{\"tableId\":[\"10\"],\"tableName\":[\"up_upgrade_record\"],\"tableComment\":[\"升级记录表\"],\"className\":[\"UpUpgradeRecord\"],\"functionAuthor\":[\"lijunqiang\"],\"remark\":[\"\"],\"columns[0].columnId\":[\"96\"],\"columns[0].sort\":[\"1\"],\"columns[0].columnComment\":[\"记录ID\"],\"columns[0].javaType\":[\"Long\"],\"columns[0].javaField\":[\"upgradeId\"],\"columns[0].isInsert\":[\"1\"],\"columns[0].queryType\":[\"EQ\"],\"columns[0].htmlType\":[\"input\"],\"columns[0].dictType\":[\"\"],\"columns[1].columnId\":[\"97\"],\"columns[1].sort\":[\"2\"],\"columns[1].columnComment\":[\"服务ID\"],\"columns[1].javaType\":[\"Long\"],\"columns[1].javaField\":[\"serverId\"],\"columns[1].isInsert\":[\"1\"],\"columns[1].isEdit\":[\"1\"],\"columns[1].isList\":[\"1\"],\"columns[1].isQuery\":[\"1\"],\"columns[1].queryType\":[\"EQ\"],\"columns[1].isRequired\":[\"1\"],\"columns[1].htmlType\":[\"input\"],\"columns[1].dictType\":[\"\"],\"columns[2].columnId\":[\"98\"],\"columns[2].sort\":[\"3\"],\"columns[2].columnComment\":[\"补丁ID\"],\"columns[2].javaType\":[\"Long\"],\"columns[2].javaField\":[\"patchId\"],\"columns[2].isInsert\":[\"1\"],\"columns[2].isEdit\":[\"1\"],\"columns[2].isList\":[\"1\"],\"columns[2].isQuery\":[\"1\"],\"columns[2].queryType\":[\"EQ\"],\"columns[2].isRequired\":[\"1\"],\"columns[2].htmlType\":[\"input\"],\"columns[2].dictType\":[\"\"],\"columns[3].columnId\":[\"99\"],\"columns[3].sort\":[\"4\"],\"columns[3].columnComment\":[\"升级状态\"],\"columns[3].javaType\":[\"String\"],\"columns[3].javaField\":[\"upStatus\"],\"columns[3].isInsert\":[\"1\"],\"columns[3].isEdit\":[\"1\"],\"columns[3].isList\":[\"1\"],\"columns[3].isQuery\":[\"1\"],\"columns[3].queryType\":[\"EQ\"],\"columns[3].isRequired\":[\"1\"],\"columns[3].htmlType\":[\"radio\"],\"columns[3].dictType\":[\"\"],\"columns[4].columnId\":[\"100\"],\"columns[4].sort\":[\"5\"],\"columns[4].columnComment\":[\"升级次数\"],\"columns[4].javaType\":[\"Integer\"],\"columns[4].javaField\":[\"upTimes\"],\"columns[4].isInsert\":[\"1\"],\"columns[4].isEdit\":[\"1\"],\"columns[4].isList\":[\"1\"],\"columns[4].isQuery\":[\"1\"],\"columns[4].queryType\":[\"EQ\"],\"columns[4].isRequired\":[\"1\"],\"columns[4].htmlType\":[\"input\"],\"columns[4].dictType\":[\"\"],\"columns[5].columnId\":[\"101\"],\"columns[','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 20:56:51'),
(242,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.editSave()','POST',1,'admin','研发部门','/system/menu/edit','127.0.0.1','内网IP','{\"menuId\":[\"4\"],\"parentId\":[\"0\"],\"menuType\":[\"C\"],\"menuName\":[\"演示环境\"],\"url\":[\"https://demo.kyee.com.cn/auth_web/new_login\"],\"target\":[\"menuBlank\"],\"perms\":[\"\"],\"orderNum\":[\"40\"],\"icon\":[\"fa fa-location-arrow\"],\"visible\":[\"0\"],\"isRefresh\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 21:17:21'),
(243,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.editSave()','POST',1,'admin','研发部门','/system/menu/edit','127.0.0.1','内网IP','{\"menuId\":[\"4\"],\"parentId\":[\"0\"],\"menuType\":[\"C\"],\"menuName\":[\"演示环境\"],\"url\":[\"https://demo.kyee.com.cn/auth_web/new_login\"],\"target\":[\"menuBlank\"],\"perms\":[\"\"],\"orderNum\":[\"999\"],\"icon\":[\"fa fa-location-arrow\"],\"visible\":[\"0\"],\"isRefresh\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 21:18:18'),
(244,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.editSave()','POST',1,'admin','研发部门','/system/menu/edit','127.0.0.1','内网IP','{\"menuId\":[\"4\"],\"parentId\":[\"0\"],\"menuType\":[\"C\"],\"menuName\":[\"演示环境\"],\"url\":[\"https://demo.kyee.com.cn/auth_web/new_login\"],\"target\":[\"menuBlank\"],\"perms\":[\"\"],\"orderNum\":[\"999\"],\"icon\":[\"fa fa-location-arrow\"],\"visible\":[\"1\"],\"isRefresh\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 21:21:05'),
(245,'项目',2,'com.kyee.upgrade.controller.UpProjectController.editSave()','POST',1,'admin','研发部门','/upgrade/project/edit','127.0.0.1','内网IP','{\"projectId\":[\"1\"],\"projectName\":[\"阳西\"],\"remark\":[\"阳西县医共体\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 21:25:21'),
(246,'产品',1,'com.kyee.upgrade.controller.UpProductController.addSave()','POST',1,'admin','研发部门','/upgrade/product/add','127.0.0.1','内网IP','{\"productName\":[\"AuthInter\"],\"remark\":[\"HIS接口项目\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 21:25:55'),
(247,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.editSave()','POST',1,'admin','研发部门','/system/menu/edit','127.0.0.1','内网IP','{\"menuId\":[\"1089\"],\"parentId\":[\"1062\"],\"menuType\":[\"C\"],\"menuName\":[\"补丁包发布\"],\"url\":[\"/upgrade/patch\"],\"target\":[\"menuItem\"],\"perms\":[\"upgrade:patch:view\"],\"orderNum\":[\"1\"],\"icon\":[\"fa fa-bug\"],\"visible\":[\"0\"],\"isRefresh\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 21:31:45'),
(248,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.editSave()','POST',1,'admin','研发部门','/system/menu/edit','127.0.0.1','内网IP','{\"menuId\":[\"1089\"],\"parentId\":[\"1062\"],\"menuType\":[\"C\"],\"menuName\":[\"补丁包管理\"],\"url\":[\"/upgrade/patch\"],\"target\":[\"menuItem\"],\"perms\":[\"upgrade:patch:view\"],\"orderNum\":[\"1\"],\"icon\":[\"fa fa-bug\"],\"visible\":[\"0\"],\"isRefresh\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 21:32:53'),
(249,'代码生成',2,'com.ruoyi.generator.controller.GenController.synchDb()','GET',1,'admin','研发部门','/tool/gen/synchDb/up_upgrade_record','127.0.0.1','内网IP','\"up_upgrade_record\"','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 21:33:11'),
(250,'代码生成',2,'com.ruoyi.generator.controller.GenController.editSave()','POST',1,'admin','研发部门','/tool/gen/edit','127.0.0.1','内网IP','{\"tableId\":[\"10\"],\"tableName\":[\"up_upgrade_record\"],\"tableComment\":[\"升级记录表\"],\"className\":[\"UpUpgradeRecord\"],\"functionAuthor\":[\"lijunqiang\"],\"remark\":[\"\"],\"columns[0].columnId\":[\"96\"],\"columns[0].sort\":[\"1\"],\"columns[0].columnComment\":[\"记录ID\"],\"columns[0].javaType\":[\"Long\"],\"columns[0].javaField\":[\"upgradeId\"],\"columns[0].isInsert\":[\"1\"],\"columns[0].queryType\":[\"EQ\"],\"columns[0].htmlType\":[\"input\"],\"columns[0].dictType\":[\"\"],\"columns[1].columnId\":[\"97\"],\"columns[1].sort\":[\"2\"],\"columns[1].columnComment\":[\"服务ID\"],\"columns[1].javaType\":[\"Long\"],\"columns[1].javaField\":[\"serverId\"],\"columns[1].isInsert\":[\"1\"],\"columns[1].isEdit\":[\"1\"],\"columns[1].isList\":[\"1\"],\"columns[1].isQuery\":[\"1\"],\"columns[1].queryType\":[\"EQ\"],\"columns[1].isRequired\":[\"1\"],\"columns[1].htmlType\":[\"input\"],\"columns[1].dictType\":[\"\"],\"columns[2].columnId\":[\"98\"],\"columns[2].sort\":[\"3\"],\"columns[2].columnComment\":[\"补丁ID\"],\"columns[2].javaType\":[\"Long\"],\"columns[2].javaField\":[\"patchId\"],\"columns[2].isInsert\":[\"1\"],\"columns[2].isEdit\":[\"1\"],\"columns[2].isList\":[\"1\"],\"columns[2].isQuery\":[\"1\"],\"columns[2].queryType\":[\"EQ\"],\"columns[2].isRequired\":[\"1\"],\"columns[2].htmlType\":[\"input\"],\"columns[2].dictType\":[\"\"],\"columns[3].columnId\":[\"99\"],\"columns[3].sort\":[\"4\"],\"columns[3].columnComment\":[\"升级状态\"],\"columns[3].javaType\":[\"String\"],\"columns[3].javaField\":[\"upStatus\"],\"columns[3].isInsert\":[\"1\"],\"columns[3].isEdit\":[\"1\"],\"columns[3].isList\":[\"1\"],\"columns[3].isQuery\":[\"1\"],\"columns[3].queryType\":[\"EQ\"],\"columns[3].isRequired\":[\"1\"],\"columns[3].htmlType\":[\"radio\"],\"columns[3].dictType\":[\"\"],\"columns[4].columnId\":[\"100\"],\"columns[4].sort\":[\"5\"],\"columns[4].columnComment\":[\"升级次数\"],\"columns[4].javaType\":[\"Integer\"],\"columns[4].javaField\":[\"upTimes\"],\"columns[4].isInsert\":[\"1\"],\"columns[4].isEdit\":[\"1\"],\"columns[4].isList\":[\"1\"],\"columns[4].isQuery\":[\"1\"],\"columns[4].queryType\":[\"EQ\"],\"columns[4].isRequired\":[\"1\"],\"columns[4].htmlType\":[\"input\"],\"columns[4].dictType\":[\"\"],\"columns[5].columnId\":[\"101\"],\"columns[','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 21:39:49'),
(251,'代码生成',2,'com.ruoyi.generator.controller.GenController.editSave()','POST',1,'admin','研发部门','/tool/gen/edit','127.0.0.1','内网IP','{\"tableId\":[\"10\"],\"tableName\":[\"up_upgrade_record\"],\"tableComment\":[\"升级记录表\"],\"className\":[\"UpUpgradeRecord\"],\"functionAuthor\":[\"lijunqiang\"],\"remark\":[\"\"],\"columns[0].columnId\":[\"96\"],\"columns[0].sort\":[\"1\"],\"columns[0].columnComment\":[\"记录ID\"],\"columns[0].javaType\":[\"Long\"],\"columns[0].javaField\":[\"upgradeId\"],\"columns[0].isInsert\":[\"1\"],\"columns[0].queryType\":[\"EQ\"],\"columns[0].htmlType\":[\"input\"],\"columns[0].dictType\":[\"\"],\"columns[1].columnId\":[\"97\"],\"columns[1].sort\":[\"2\"],\"columns[1].columnComment\":[\"服务ID\"],\"columns[1].javaType\":[\"Long\"],\"columns[1].javaField\":[\"serverId\"],\"columns[1].isInsert\":[\"1\"],\"columns[1].isEdit\":[\"1\"],\"columns[1].isList\":[\"1\"],\"columns[1].isQuery\":[\"1\"],\"columns[1].queryType\":[\"EQ\"],\"columns[1].isRequired\":[\"1\"],\"columns[1].htmlType\":[\"checkbox\"],\"columns[1].dictType\":[\"\"],\"columns[2].columnId\":[\"98\"],\"columns[2].sort\":[\"3\"],\"columns[2].columnComment\":[\"补丁ID\"],\"columns[2].javaType\":[\"Long\"],\"columns[2].javaField\":[\"patchId\"],\"columns[2].isInsert\":[\"1\"],\"columns[2].isEdit\":[\"1\"],\"columns[2].isList\":[\"1\"],\"columns[2].isQuery\":[\"1\"],\"columns[2].queryType\":[\"EQ\"],\"columns[2].isRequired\":[\"1\"],\"columns[2].htmlType\":[\"checkbox\"],\"columns[2].dictType\":[\"\"],\"columns[3].columnId\":[\"99\"],\"columns[3].sort\":[\"4\"],\"columns[3].columnComment\":[\"升级状态\"],\"columns[3].javaType\":[\"String\"],\"columns[3].javaField\":[\"upStatus\"],\"columns[3].isInsert\":[\"1\"],\"columns[3].isEdit\":[\"1\"],\"columns[3].isList\":[\"1\"],\"columns[3].queryType\":[\"EQ\"],\"columns[3].htmlType\":[\"radio\"],\"columns[3].dictType\":[\"\"],\"columns[4].columnId\":[\"100\"],\"columns[4].sort\":[\"5\"],\"columns[4].columnComment\":[\"升级次数\"],\"columns[4].javaType\":[\"Integer\"],\"columns[4].javaField\":[\"upTimes\"],\"columns[4].isInsert\":[\"1\"],\"columns[4].isEdit\":[\"1\"],\"columns[4].isList\":[\"1\"],\"columns[4].queryType\":[\"EQ\"],\"columns[4].htmlType\":[\"input\"],\"columns[4].dictType\":[\"\"],\"columns[5].columnId\":[\"101\"],\"columns[5].sort\":[\"6\"],\"columns[5].columnComment\":[\"最后升级人员\"],\"columns[5].javaType\":[\"String\"],\"columns[5].javaField\"','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 21:51:15'),
(252,'代码生成',8,'com.ruoyi.generator.controller.GenController.download()','GET',1,'admin','研发部门','/tool/gen/download/up_upgrade_record','127.0.0.1','内网IP','\"up_upgrade_record\"',NULL,0,NULL,'2021-06-13 21:51:30'),
(253,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.editSave()','POST',1,'admin','研发部门','/system/menu/edit','127.0.0.1','内网IP','{\"menuId\":[\"1095\"],\"parentId\":[\"1062\"],\"menuType\":[\"C\"],\"menuName\":[\"升级管理\"],\"url\":[\"/upgrade/uprecord\"],\"target\":[\"menuItem\"],\"perms\":[\"upgrade:uprecord:view\"],\"orderNum\":[\"1\"],\"icon\":[\"fa fa-rocket\"],\"visible\":[\"0\"],\"isRefresh\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 21:58:52'),
(254,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.editSave()','POST',1,'admin','研发部门','/system/menu/edit','127.0.0.1','内网IP','{\"menuId\":[\"1095\"],\"parentId\":[\"1062\"],\"menuType\":[\"C\"],\"menuName\":[\"升级管理\"],\"url\":[\"/upgrade/uprecord\"],\"target\":[\"menuItem\"],\"perms\":[\"upgrade:uprecord:view\"],\"orderNum\":[\"1\"],\"icon\":[\"fa fa-paper-plane-o\"],\"visible\":[\"0\"],\"isRefresh\":[\"1\"]}','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 21:59:24'),
(255,'代码生成',2,'com.ruoyi.generator.controller.GenController.editSave()','POST',1,'admin','研发部门','/tool/gen/edit','127.0.0.1','内网IP','{\"tableId\":[\"10\"],\"tableName\":[\"up_upgrade_record\"],\"tableComment\":[\"升级记录表\"],\"className\":[\"UpUpgradeRecord\"],\"functionAuthor\":[\"lijunqiang\"],\"remark\":[\"\"],\"columns[0].columnId\":[\"96\"],\"columns[0].sort\":[\"1\"],\"columns[0].columnComment\":[\"记录ID\"],\"columns[0].javaType\":[\"Long\"],\"columns[0].javaField\":[\"upgradeId\"],\"columns[0].isInsert\":[\"1\"],\"columns[0].queryType\":[\"EQ\"],\"columns[0].htmlType\":[\"input\"],\"columns[0].dictType\":[\"\"],\"columns[1].columnId\":[\"97\"],\"columns[1].sort\":[\"2\"],\"columns[1].columnComment\":[\"服务ID\"],\"columns[1].javaType\":[\"Long\"],\"columns[1].javaField\":[\"serverId\"],\"columns[1].isInsert\":[\"1\"],\"columns[1].isEdit\":[\"1\"],\"columns[1].isList\":[\"1\"],\"columns[1].isQuery\":[\"1\"],\"columns[1].queryType\":[\"EQ\"],\"columns[1].isRequired\":[\"1\"],\"columns[1].htmlType\":[\"select\"],\"columns[1].dictType\":[\"\"],\"columns[2].columnId\":[\"98\"],\"columns[2].sort\":[\"3\"],\"columns[2].columnComment\":[\"补丁ID\"],\"columns[2].javaType\":[\"Long\"],\"columns[2].javaField\":[\"patchId\"],\"columns[2].isInsert\":[\"1\"],\"columns[2].isEdit\":[\"1\"],\"columns[2].isList\":[\"1\"],\"columns[2].isQuery\":[\"1\"],\"columns[2].queryType\":[\"EQ\"],\"columns[2].isRequired\":[\"1\"],\"columns[2].htmlType\":[\"select\"],\"columns[2].dictType\":[\"\"],\"columns[3].columnId\":[\"99\"],\"columns[3].sort\":[\"4\"],\"columns[3].columnComment\":[\"升级状态\"],\"columns[3].javaType\":[\"String\"],\"columns[3].javaField\":[\"upStatus\"],\"columns[3].isInsert\":[\"1\"],\"columns[3].isEdit\":[\"1\"],\"columns[3].isList\":[\"1\"],\"columns[3].isQuery\":[\"1\"],\"columns[3].queryType\":[\"EQ\"],\"columns[3].htmlType\":[\"select\"],\"columns[3].dictType\":[\"up_upgrade_status\"],\"columns[4].columnId\":[\"100\"],\"columns[4].sort\":[\"5\"],\"columns[4].columnComment\":[\"升级次数\"],\"columns[4].javaType\":[\"Integer\"],\"columns[4].javaField\":[\"upTimes\"],\"columns[4].isInsert\":[\"1\"],\"columns[4].isEdit\":[\"1\"],\"columns[4].isList\":[\"1\"],\"columns[4].queryType\":[\"EQ\"],\"columns[4].htmlType\":[\"input\"],\"columns[4].dictType\":[\"\"],\"columns[5].columnId\":[\"101\"],\"columns[5].sort\":[\"6\"],\"columns[5].columnComment\":[\"最后升级人员\"],\"columns[5].ja','{\r\n  \"msg\" : \"操作成功\",\r\n  \"code\" : 0\r\n}',0,NULL,'2021-06-13 22:03:07'),
(256,'代码生成',8,'com.ruoyi.generator.controller.GenController.download()','GET',1,'admin','研发部门','/tool/gen/download/up_upgrade_record','127.0.0.1','内网IP','\"up_upgrade_record\"',NULL,0,NULL,'2021-06-13 22:03:11');

/*Table structure for table `sys_post` */

DROP TABLE IF EXISTS `sys_post`;

CREATE TABLE `sys_post` (
  `post_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) NOT NULL COMMENT '岗位名称',
  `post_sort` int(4) NOT NULL COMMENT '显示顺序',
  `status` char(1) NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='岗位信息表';

/*Data for the table `sys_post` */

insert  into `sys_post`(`post_id`,`post_code`,`post_name`,`post_sort`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) values 
(1,'gly','管理员',1,'0','admin','2021-06-08 21:53:28','admin','2021-06-09 08:47:23',''),
(2,'ssxmjl','实施项目经理',2,'0','admin','2021-06-08 21:53:28','admin','2021-06-09 08:47:11',''),
(3,'ssry','实施人员',3,'0','admin','2021-06-08 21:53:28','admin','2021-06-09 08:47:41',''),
(4,'csry','测试人员',4,'0','admin','2021-06-08 21:53:28','admin','2021-06-09 08:48:05',''),
(5,'kfry','开发人员',5,'0','admin','2021-06-09 08:48:55','',NULL,NULL);

/*Table structure for table `sys_role` */

DROP TABLE IF EXISTS `sys_role`;

CREATE TABLE `sys_role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) NOT NULL COMMENT '角色权限字符串',
  `role_sort` int(4) NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `status` char(1) NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='角色信息表';

/*Data for the table `sys_role` */

insert  into `sys_role`(`role_id`,`role_name`,`role_key`,`role_sort`,`data_scope`,`status`,`del_flag`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) values 
(1,'超级管理员','admin',1,'1','0','0','admin','2021-06-08 21:53:28','',NULL,'超级管理员'),
(2,'普通角色','common',2,'2','0','0','admin','2021-06-08 21:53:28','',NULL,'普通角色'),
(3,'测试角色','tester',3,'2','0','0','admin','2021-06-09 08:41:32','admin','2021-06-09 08:43:22','测试人员');

/*Table structure for table `sys_role_dept` */

DROP TABLE IF EXISTS `sys_role_dept`;

CREATE TABLE `sys_role_dept` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`,`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色和部门关联表';

/*Data for the table `sys_role_dept` */

insert  into `sys_role_dept`(`role_id`,`dept_id`) values 
(2,100),
(2,101),
(2,105),
(3,100),
(3,101),
(3,103),
(3,105);

/*Table structure for table `sys_role_menu` */

DROP TABLE IF EXISTS `sys_role_menu`;

CREATE TABLE `sys_role_menu` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色和菜单关联表';

/*Data for the table `sys_role_menu` */

insert  into `sys_role_menu`(`role_id`,`menu_id`) values 
(2,1),
(2,2),
(2,3),
(2,4),
(2,100),
(2,101),
(2,102),
(2,103),
(2,104),
(2,105),
(2,106),
(2,107),
(2,108),
(2,109),
(2,110),
(2,111),
(2,112),
(2,113),
(2,114),
(2,115),
(2,116),
(2,500),
(2,501),
(2,1000),
(2,1001),
(2,1002),
(2,1003),
(2,1004),
(2,1005),
(2,1006),
(2,1007),
(2,1008),
(2,1009),
(2,1010),
(2,1011),
(2,1012),
(2,1013),
(2,1014),
(2,1015),
(2,1016),
(2,1017),
(2,1018),
(2,1019),
(2,1020),
(2,1021),
(2,1022),
(2,1023),
(2,1024),
(2,1025),
(2,1026),
(2,1027),
(2,1028),
(2,1029),
(2,1030),
(2,1031),
(2,1032),
(2,1033),
(2,1034),
(2,1035),
(2,1036),
(2,1037),
(2,1038),
(2,1039),
(2,1040),
(2,1041),
(2,1042),
(2,1043),
(2,1044),
(2,1045),
(2,1046),
(2,1047),
(2,1048),
(2,1049),
(2,1050),
(2,1051),
(2,1052),
(2,1053),
(2,1054),
(2,1055),
(2,1056),
(2,1057),
(2,1058),
(2,1059),
(2,1060),
(2,1061),
(3,1),
(3,2),
(3,3),
(3,4),
(3,100),
(3,101),
(3,102),
(3,103),
(3,104),
(3,105),
(3,106),
(3,107),
(3,108),
(3,109),
(3,110),
(3,111),
(3,112),
(3,113),
(3,114),
(3,115),
(3,116),
(3,500),
(3,501),
(3,1000),
(3,1001),
(3,1002),
(3,1003),
(3,1004),
(3,1005),
(3,1006),
(3,1007),
(3,1008),
(3,1009),
(3,1010),
(3,1011),
(3,1012),
(3,1013),
(3,1014),
(3,1015),
(3,1016),
(3,1017),
(3,1018),
(3,1019),
(3,1020),
(3,1021),
(3,1022),
(3,1023),
(3,1024),
(3,1025),
(3,1026),
(3,1027),
(3,1028),
(3,1029),
(3,1030),
(3,1031),
(3,1032),
(3,1033),
(3,1034),
(3,1035),
(3,1036),
(3,1037),
(3,1038),
(3,1039),
(3,1040),
(3,1041),
(3,1042),
(3,1043),
(3,1044),
(3,1045),
(3,1046),
(3,1047),
(3,1048),
(3,1049),
(3,1050),
(3,1051),
(3,1052),
(3,1053),
(3,1054),
(3,1055),
(3,1056),
(3,1057),
(3,1058),
(3,1059),
(3,1060),
(3,1061);

/*Table structure for table `sys_user` */

DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `login_name` varchar(30) NOT NULL COMMENT '登录账号',
  `user_name` varchar(30) DEFAULT '' COMMENT '用户昵称',
  `user_type` varchar(2) DEFAULT '00' COMMENT '用户类型（00系统用户 01注册用户）',
  `email` varchar(50) DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) DEFAULT '' COMMENT '手机号码',
  `sex` char(1) DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) DEFAULT '' COMMENT '头像路径',
  `password` varchar(50) DEFAULT '' COMMENT '密码',
  `salt` varchar(20) DEFAULT '' COMMENT '盐加密',
  `status` char(1) DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `pwd_update_date` datetime DEFAULT NULL COMMENT '密码最后更新时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='用户信息表';

/*Data for the table `sys_user` */

insert  into `sys_user`(`user_id`,`dept_id`,`login_name`,`user_name`,`user_type`,`email`,`phonenumber`,`sex`,`avatar`,`password`,`salt`,`status`,`del_flag`,`login_ip`,`login_date`,`pwd_update_date`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) values 
(1,103,'admin','管理员','00','ry@163.com','15888888888','1','','29c67a30398638269fe600f73a054934','111111','0','0','127.0.0.1','2021-06-15 11:41:16','2021-06-08 21:53:28','admin','2021-06-08 21:53:28','','2021-06-15 11:41:15','管理员'),
(2,105,'ceshi','测试','00','ry@qq.com','15666666666','1','','855ee8e4371fab33dcf9c783c1e1d697','2155f4','0','0','127.0.0.1','2021-06-08 23:20:34','2021-06-08 21:53:28','admin','2021-06-08 21:53:28','admin','2021-06-08 23:20:34','测试员'),
(3,NULL,'shishi','实施','00','','18888888888','0','','2d23cef472ea14327ee1f8aec0f68199','8a4f41','0','0','',NULL,NULL,'admin','2021-06-09 08:40:01','',NULL,NULL);

/*Table structure for table `sys_user_online` */

DROP TABLE IF EXISTS `sys_user_online`;

CREATE TABLE `sys_user_online` (
  `sessionId` varchar(50) NOT NULL DEFAULT '' COMMENT '用户会话id',
  `login_name` varchar(50) DEFAULT '' COMMENT '登录账号',
  `dept_name` varchar(50) DEFAULT '' COMMENT '部门名称',
  `ipaddr` varchar(128) DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) DEFAULT '' COMMENT '操作系统',
  `status` varchar(10) DEFAULT '' COMMENT '在线状态on_line在线off_line离线',
  `start_timestamp` datetime DEFAULT NULL COMMENT 'session创建时间',
  `last_access_time` datetime DEFAULT NULL COMMENT 'session最后访问时间',
  `expire_time` int(5) DEFAULT '0' COMMENT '超时时间，单位为分钟',
  PRIMARY KEY (`sessionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='在线用户记录';

/*Data for the table `sys_user_online` */

/*Table structure for table `sys_user_post` */

DROP TABLE IF EXISTS `sys_user_post`;

CREATE TABLE `sys_user_post` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `post_id` bigint(20) NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`,`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户与岗位关联表';

/*Data for the table `sys_user_post` */

insert  into `sys_user_post`(`user_id`,`post_id`) values 
(1,1),
(2,2),
(3,2);

/*Table structure for table `sys_user_role` */

DROP TABLE IF EXISTS `sys_user_role`;

CREATE TABLE `sys_user_role` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户和角色关联表';

/*Data for the table `sys_user_role` */

insert  into `sys_user_role`(`user_id`,`role_id`) values 
(1,1),
(2,2),
(3,2);

/*Table structure for table `up_product` */

DROP TABLE IF EXISTS `up_product`;

CREATE TABLE `up_product` (
  `product_id` bigint(5) unsigned NOT NULL AUTO_INCREMENT COMMENT '产品ID',
  `product_name` varchar(30) NOT NULL COMMENT '产品名',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) DEFAULT 'N' COMMENT '删除标识',
  `remark` varchar(200) DEFAULT NULL COMMENT '产品备注',
  PRIMARY KEY (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='产品表';

/*Data for the table `up_product` */

insert  into `up_product`(`product_id`,`product_name`,`create_by`,`create_time`,`update_by`,`update_time`,`del_flag`,`remark`) values 
(1,'云HIS','',NULL,'','2021-06-13 11:28:04','N','云HIS系统-HIS'),
(2,'AuthInter','','2021-06-13 21:25:55','',NULL,'N','HIS接口项目');

/*Table structure for table `up_project` */

DROP TABLE IF EXISTS `up_project`;

CREATE TABLE `up_project` (
  `project_id` bigint(5) unsigned NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `project_name` varchar(100) NOT NULL COMMENT '项目名',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) DEFAULT 'N' COMMENT '删除标识',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='项目表';

/*Data for the table `up_project` */

insert  into `up_project`(`project_id`,`project_name`,`create_by`,`create_time`,`update_by`,`update_time`,`del_flag`,`remark`) values 
(1,'阳西','',NULL,'','2021-06-13 21:25:22','N','阳西县医共体');

/*Table structure for table `up_project_person` */

DROP TABLE IF EXISTS `up_project_person`;

CREATE TABLE `up_project_person` (
  `project_id` bigint(5) NOT NULL COMMENT '项目ID',
  `worker_id` bigint(10) NOT NULL COMMENT '人员工号',
  `worker_name` varchar(30) DEFAULT NULL COMMENT '人员姓名',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) DEFAULT 'N' COMMENT '删除标识',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`project_id`,`worker_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目人员表';

/*Data for the table `up_project_person` */

/*Table structure for table `up_project_product` */

DROP TABLE IF EXISTS `up_project_product`;

CREATE TABLE `up_project_product` (
  `project_product_id` bigint(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '项目产品ID',
  `project_id` bigint(5) NOT NULL COMMENT '项目ID',
  `product_id` bigint(5) NOT NULL COMMENT '产品ID',
  `source_branch_name` varchar(100) NOT NULL COMMENT '项目代码分支',
  `db_version` varchar(20) DEFAULT NULL COMMENT '数据库版本',
  `min_server_version` varchar(20) DEFAULT NULL COMMENT '最小服务版本',
  `max_server_version` varchar(20) DEFAULT NULL COMMENT '最大服务版本',
  `last_upgrade_time` datetime DEFAULT NULL COMMENT '最后升级时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) DEFAULT 'N' COMMENT '删除标识',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`project_product_id`),
  UNIQUE KEY `UNIDX_Project_Product` (`project_id`,`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='项目产品表';

/*Data for the table `up_project_product` */

insert  into `up_project_product`(`project_product_id`,`project_id`,`product_id`,`source_branch_name`,`db_version`,`min_server_version`,`max_server_version`,`last_upgrade_time`,`create_by`,`create_time`,`update_by`,`update_time`,`del_flag`,`remark`) values 
(1,1,1,'project/master/yangxi','','','',NULL,'',NULL,'',NULL,'N',NULL);

/*Table structure for table `up_project_server` */

DROP TABLE IF EXISTS `up_project_server`;

CREATE TABLE `up_project_server` (
  `server_id` bigint(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '服务ID',
  `server_name` varchar(100) NOT NULL COMMENT '服务名称',
  `project_id` bigint(5) NOT NULL COMMENT '项目ID',
  `product_id` bigint(5) NOT NULL COMMENT '产品ID',
  `server_ip` varchar(50) NOT NULL COMMENT '服务IP',
  `server_port` int(5) NOT NULL COMMENT '服务端口号',
  `server_path` varchar(200) DEFAULT NULL COMMENT '服务文件夹路径',
  `os_type` varchar(10) DEFAULT 'LINUX' COMMENT 'OS类型',
  `ssh_port` int(5) DEFAULT NULL COMMENT 'ssh端口',
  `ssh_user` varchar(30) DEFAULT NULL COMMENT 'ssh用户名',
  `ssh_password` varchar(100) DEFAULT NULL COMMENT 'ssh密码',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) DEFAULT 'N' COMMENT '删除标识',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  KEY `server_id` (`server_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='项目应用服务表';

/*Data for the table `up_project_server` */

insert  into `up_project_server`(`server_id`,`server_name`,`project_id`,`product_id`,`server_ip`,`server_port`,`server_path`,`os_type`,`ssh_port`,`ssh_user`,`ssh_password`,`create_by`,`create_time`,`update_by`,`update_time`,`del_flag`,`remark`) values 
(1,'人民31服务',1,1,'192.168.22.31',8080,NULL,'LINUX',NULL,NULL,NULL,'',NULL,'',NULL,'N',NULL),
(2,'人民32服务',1,1,'192.168.22.32',8080,NULL,'LINUX',NULL,NULL,NULL,'',NULL,'',NULL,'N',NULL),
(3,'测试服务',1,1,'192.168.22.100',8080,NULL,'LINUX',NULL,NULL,NULL,'',NULL,'',NULL,'N',NULL);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
