-- MySQL dump 10.13  Distrib 8.0.43, for Linux (x86_64)
--
-- Host: host.docker.internal    Database: andinatrading_cuentas
-- ------------------------------------------------------
-- Server version	8.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account_ach_relationship`
--

DROP TABLE IF EXISTS `account_ach_relationship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account_ach_relationship` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `ach_id` varchar(64) NOT NULL,
  `account_owner_name` varchar(120) DEFAULT NULL,
  `bank_account_type` varchar(30) DEFAULT NULL,
  `bank_account_number` varchar(34) DEFAULT NULL,
  `nickname` varchar(80) DEFAULT NULL,
  `alpaca_account_id` varchar(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ach_id` (`ach_id`),
  KEY `idx_ach_alpaca` (`alpaca_account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_ach_relationship`
--

LOCK TABLES `account_ach_relationship` WRITE;
/*!40000 ALTER TABLE `account_ach_relationship` DISABLE KEYS */;
INSERT INTO `account_ach_relationship` VALUES (1,'2025-10-31 20:48:22','b026f056-86e8-42e7-a9fd-a96c452bb327','Viviana Hincapie','SAVINGS','65489256','Davivienda','a066f330-75ca-4c66-844b-581e0c23036f');
/*!40000 ALTER TABLE `account_ach_relationship` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `description` varchar(200) COLLATE utf8mb4_general_ci NOT NULL,
  `type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
  `script` varchar(1000) COLLATE utf8mb4_general_ci NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'1','cuentas','SQL','V1__cuentas.sql',324855653,'root','2025-10-31 19:31:01',1076,1),(2,'2','add index transfer status','SQL','V2__add_index_transfer_status.sql',-1920248954,'root','2025-10-31 19:31:02',342,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transfer_log`
--

DROP TABLE IF EXISTS `transfer_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transfer_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `alpaca_account_id` varchar(64) NOT NULL,
  `external_id` varchar(100) DEFAULT NULL,
  `amount` decimal(19,2) NOT NULL,
  `status` varchar(40) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_transfer_log_acc_created` (`alpaca_account_id`,`created_at`),
  KEY `idx_transfer_log_status_created` (`status`,`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transfer_log`
--

LOCK TABLES `transfer_log` WRITE;
/*!40000 ALTER TABLE `transfer_log` DISABLE KEYS */;
INSERT INTO `transfer_log` VALUES (1,'a066f330-75ca-4c66-844b-581e0c23036f','dc7e6613-3678-4be6-ad2a-82cfa269e401',6000.00,'QUEUED','2025-10-31 21:48:35');
/*!40000 ALTER TABLE `transfer_log` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-31 22:56:10
