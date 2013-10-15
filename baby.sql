# Host: 127.0.0.1  (Version: 5.5.30-log)
# Date: 2013-04-09 16:30:41
# Generator: MySQL-Front 5.3  (Build 2.53)

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE */;
/*!40101 SET SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES */;
/*!40103 SET SQL_NOTES='ON' */;

DROP DATABASE IF EXISTS `baby`;
CREATE DATABASE `baby` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */;
USE `baby`;

#
# Source for table "brand"
#

DROP TABLE IF EXISTS `brand`;
CREATE TABLE `brand` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `brandname` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `createtime` bigint(1) NOT NULL DEFAULT '0',
  `lastmodifytime` bigint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `ix_brandname` (`brandname`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

#
# Source for table "buy"
#

DROP TABLE IF EXISTS `buy`;
CREATE TABLE `buy` (
  `id` bigint(1) NOT NULL DEFAULT '0',
  `objectid` bigint(1) NOT NULL DEFAULT '0',
  `operator` bigint(1) NOT NULL DEFAULT '0',
  `optime` bigint(1) NOT NULL DEFAULT '0',
  `comment` varchar(1024) NOT NULL DEFAULT '',
  `dataflag` int(11) NOT NULL DEFAULT '0',
  `calctotalprice` bigint(1) NOT NULL DEFAULT '0',
  `totalprice` bigint(1) NOT NULL DEFAULT '0',
  `actualmoney` bigint(1) NOT NULL DEFAULT '0',
  `paycredit` bigint(1) NOT NULL,
  `rewardcredit` bigint(1) NOT NULL,
  `originalid` bigint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `ix_objectid` (`objectid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Source for table "circulation"
#

DROP TABLE IF EXISTS `circulation`;
CREATE TABLE `circulation` (
  `transactionid` bigint(1) NOT NULL DEFAULT '0' COMMENT 'positive for sell, negative for buy',
  `goodsid` bigint(1) NOT NULL DEFAULT '0',
  `amount` bigint(1) NOT NULL DEFAULT '0',
  `unitprice` int(11) NOT NULL,
  KEY `ix_circulationid` (`transactionid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Source for table "customer"
#

DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer` (
  `id` bigint(1) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `sex` int(11) NOT NULL DEFAULT '0',
  `birthyear` int(11) NOT NULL DEFAULT '0',
  `birthmonth` int(1) NOT NULL DEFAULT '0',
  `birthdate` int(11) NOT NULL DEFAULT '0',
  `money` bigint(1) NOT NULL DEFAULT '0',
  `credit` bigint(1) NOT NULL DEFAULT '0',
  `mobile` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `rollid` bigint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `ix_mobile` (`mobile`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

#
# Source for table "customerchange"
#

DROP TABLE IF EXISTS `customerchange`;
CREATE TABLE `customerchange` (
  `id` bigint(11) NOT NULL DEFAULT '0',
  `objectid` bigint(1) NOT NULL DEFAULT '0',
  `money` int(1) NOT NULL DEFAULT '0',
  `credit` int(1) NOT NULL DEFAULT '0',
  `operator` bigint(1) NOT NULL DEFAULT '0',
  `cause` int(11) NOT NULL DEFAULT '0',
  `remark` varchar(255) NOT NULL DEFAULT '',
  `optime` bigint(1) NOT NULL DEFAULT '0',
  `dataflag` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `ix_objectid` (`objectid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Source for table "fund"
#

DROP TABLE IF EXISTS `fund`;
CREATE TABLE `fund` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `realamount` bigint(1) NOT NULL DEFAULT '0',
  `effectiveamount` bigint(1) NOT NULL DEFAULT '0',
  `customerdebt` bigint(1) NOT NULL DEFAULT '0',
  `supplierdebt` bigint(1) NOT NULL DEFAULT '0',
  `generaldebt` bigint(1) NOT NULL DEFAULT '0',
  `lastmodifytime` bigint(1) NOT NULL DEFAULT '0',
  `rollid` bigint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

#
# Source for table "fundchange"
#

DROP TABLE IF EXISTS `fundchange`;
CREATE TABLE `fundchange` (
  `id` bigint(11) NOT NULL DEFAULT '0',
  `objectid` bigint(1) NOT NULL DEFAULT '0',
  `money` int(1) NOT NULL DEFAULT '0',
  `credit` int(1) NOT NULL DEFAULT '0',
  `operator` bigint(1) NOT NULL DEFAULT '0',
  `cause` int(11) NOT NULL DEFAULT '0',
  `remark` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `optime` bigint(1) NOT NULL DEFAULT '0',
  `dataflag` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

#
# Source for table "goods"
#

DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods` (
  `id` bigint(1) NOT NULL AUTO_INCREMENT,
  `goodsname` varchar(255) NOT NULL DEFAULT '',
  `brand` bigint(1) NOT NULL DEFAULT '0',
  `amount` bigint(1) NOT NULL DEFAULT '0',
  `unitprice` bigint(1) NOT NULL DEFAULT '0',
  `rollid` bigint(1) NOT NULL DEFAULT '0',
  `createTime` bigint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

#
# Source for table "goodschange"
#

DROP TABLE IF EXISTS `goodschange`;
CREATE TABLE `goodschange` (
  `id` bigint(11) NOT NULL DEFAULT '0',
  `goodsid` bigint(1) NOT NULL DEFAULT '0',
  `amount` bigint(1) NOT NULL DEFAULT '0',
  `optime` bigint(1) NOT NULL DEFAULT '0',
  `unitprice` bigint(1) NOT NULL,
  `remark` varchar(255) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

#
# Source for table "notepad"
#

DROP TABLE IF EXISTS `notepad`;
CREATE TABLE `notepad` (
  `id` bigint(1) NOT NULL AUTO_INCREMENT,
  `content` varchar(512) COLLATE utf8_bin NOT NULL DEFAULT '',
  `createtime` bigint(1) NOT NULL DEFAULT '0',
  `operator` bigint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1114 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

#
# Source for table "op"
#

DROP TABLE IF EXISTS `op`;
CREATE TABLE `op` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `operator` bigint(1) NOT NULL DEFAULT '0',
  `optype` int(1) NOT NULL DEFAULT '0',
  `originalid` bigint(1) NOT NULL DEFAULT '0',
  `opflag` int(11) NOT NULL DEFAULT '0',
  `logtime` bigint(1) NOT NULL DEFAULT '0',
  `rolltime` bigint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

#
# Source for table "operator"
#

DROP TABLE IF EXISTS `operator`;
CREATE TABLE `operator` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

#
# Source for table "pricechange"
#

DROP TABLE IF EXISTS `pricechange`;
CREATE TABLE `pricechange` (
  `id` bigint(1) NOT NULL AUTO_INCREMENT,
  `goodsid` bigint(1) NOT NULL DEFAULT '0',
  `unitprice` bigint(1) NOT NULL DEFAULT '0',
  `opTime` bigint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Source for table "sell"
#

DROP TABLE IF EXISTS `sell`;
CREATE TABLE `sell` (
  `id` bigint(1) NOT NULL DEFAULT '0',
  `objectid` bigint(1) NOT NULL DEFAULT '0',
  `operator` bigint(1) NOT NULL DEFAULT '0',
  `optime` bigint(1) NOT NULL DEFAULT '0',
  `comment` varchar(1024) NOT NULL DEFAULT '',
  `dataflag` int(11) NOT NULL DEFAULT '0',
  `calctotalprice` bigint(1) NOT NULL DEFAULT '0',
  `totalprice` bigint(1) NOT NULL DEFAULT '0',
  `actualmoney` bigint(1) NOT NULL DEFAULT '0',
  `paycredit` bigint(1) NOT NULL DEFAULT '0',
  `rewardcredit` bigint(1) NOT NULL DEFAULT '0',
  `originalid` bigint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Source for table "supplier"
#

DROP TABLE IF EXISTS `supplier`;
CREATE TABLE `supplier` (
  `id` bigint(1) NOT NULL AUTO_INCREMENT,
  `suppliername` varchar(255) NOT NULL DEFAULT '',
  `credit` bigint(1) NOT NULL DEFAULT '0',
  `money` bigint(1) NOT NULL DEFAULT '0',
  `linkman` varchar(255) NOT NULL DEFAULT '',
  `contact` varchar(255) NOT NULL DEFAULT '',
  `createtime` bigint(1) NOT NULL,
  `rollid` bigint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

#
# Source for table "supplierchange"
#

DROP TABLE IF EXISTS `supplierchange`;
CREATE TABLE `supplierchange` (
  `id` bigint(11) NOT NULL DEFAULT '0',
  `objectid` bigint(1) NOT NULL DEFAULT '0',
  `money` int(1) NOT NULL DEFAULT '0',
  `credit` int(1) NOT NULL DEFAULT '0',
  `operator` bigint(1) NOT NULL DEFAULT '0',
  `cause` int(11) NOT NULL DEFAULT '0',
  `remark` varchar(255) NOT NULL DEFAULT '',
  `optime` bigint(1) NOT NULL DEFAULT '0',
  `dataflag` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `ix_objectid` (`objectid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
