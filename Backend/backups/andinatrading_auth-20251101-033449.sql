-- MySQL dump 10.13  Distrib 8.0.43, for Linux (x86_64)
--
-- Host: host.docker.internal    Database: andinatrading_auth
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
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `script` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'1','init','SQL','V1__init.sql',2005417304,'root','2025-10-31 19:30:42',1362,1),(2,'2','tokens','SQL','V2__tokens.sql',63963418,'root','2025-10-31 19:30:45',2384,1),(3,'3','admin seed','SQL','V3__admin_seed.sql',2146949451,'root','2025-10-31 19:30:48',725,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `password_reset_tokens`
--

DROP TABLE IF EXISTS `password_reset_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `password_reset_tokens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `usuario_id` bigint NOT NULL,
  `token` varchar(256) NOT NULL,
  `expira_en` timestamp NOT NULL,
  `usado` tinyint(1) NOT NULL DEFAULT '0',
  `creado_en` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `token` (`token`),
  KEY `idx_pr_usuario` (`usuario_id`),
  KEY `idx_pr_expira` (`expira_en`),
  CONSTRAINT `fk_pr_user` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `password_reset_tokens`
--

LOCK TABLES `password_reset_tokens` WRITE;
/*!40000 ALTER TABLE `password_reset_tokens` DISABLE KEYS */;
/*!40000 ALTER TABLE `password_reset_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refresh_tokens`
--

DROP TABLE IF EXISTS `refresh_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refresh_tokens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `usuario_id` bigint NOT NULL,
  `token` varchar(512) NOT NULL,
  `expira_en` timestamp NOT NULL,
  `revocado` tinyint(1) NOT NULL DEFAULT '0',
  `creado_en` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `token` (`token`),
  KEY `idx_rt_usuario` (`usuario_id`),
  KEY `idx_rt_expira` (`expira_en`),
  CONSTRAINT `fk_rt_user` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refresh_tokens`
--

LOCK TABLES `refresh_tokens` WRITE;
/*!40000 ALTER TABLE `refresh_tokens` DISABLE KEYS */;
INSERT INTO `refresh_tokens` VALUES (1,1,'6b07b342-4d18-4840-b6cc-6343dc67b5bb','2025-11-08 00:39:26',0,'2025-11-01 00:39:26'),(2,1,'e4dcdf1e-04c2-4a35-884d-3875f987b873','2025-11-08 00:51:41',1,'2025-11-01 00:51:41'),(3,1,'140f3a6d-b47d-44c7-b3af-11f299ac5af0','2025-11-08 01:40:42',1,'2025-11-01 01:40:42'),(4,6,'a7148f35-d44a-4b7a-8736-3f95b298ed23','2025-11-08 01:46:31',0,'2025-11-01 01:46:31'),(5,1,'07d2547e-e5b9-47d3-992b-e20ebfb7959c','2025-11-08 02:46:05',0,'2025-11-01 02:46:05'),(6,1,'b85ddd4e-d96a-4f0d-9eec-f1f00f3c5de6','2025-11-08 03:59:17',0,'2025-11-01 03:59:17'),(7,1,'cf6c7ebd-4b97-43d8-a53b-ce1dd2b9e5e7','2025-11-08 04:51:54',0,'2025-11-01 04:51:54'),(8,1,'db64b2f1-5ed1-4de0-9eef-0b0116f25ad7','2025-11-08 07:04:20',0,'2025-11-01 07:04:20'),(9,1,'0a507590-517f-437c-8ce9-8e9c92db6943','2025-11-08 07:23:20',0,'2025-11-01 07:23:20'),(10,1,'f6e48c01-e1f7-48bb-834b-f2bfcf51ee0a','2025-11-08 07:57:31',0,'2025-11-01 07:57:31');
/*!40000 ALTER TABLE `refresh_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'ADMIN'),(2,'COMISIONISTA'),(3,'INVERSIONISTA');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `correo` varchar(180) COLLATE utf8mb4_unicode_ci NOT NULL,
  `contrasena_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `estado` enum('ACTIVO','INACTIVO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVO',
  `intentos_fallidos` int NOT NULL DEFAULT '0',
  `ultimo_login` timestamp NULL DEFAULT NULL,
  `rol_id` bigint NOT NULL,
  `creado_en` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `actualizado_en` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `correo` (`correo`),
  KEY `fk_usuarios_rol` (`rol_id`),
  CONSTRAINT `fk_usuarios_rol` FOREIGN KEY (`rol_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'admin@gmail.com','$2a$12$VkQzHQJUYvc5SnMdbaaf6u9U8V5x.Chd0TPNds5d.K1EM4inzwmgm','ACTIVO',0,'2025-11-01 07:57:31',1,'2025-10-31 19:30:47','2025-11-01 02:57:30'),(2,'juanrivera@mail.com','$2a$11$WLPU89KjVUdhPiIIaC2VtOcBTKhiwP/Nk9NxzTz73q5UgyajpM9lm','ACTIVO',0,NULL,2,'2025-10-31 20:42:21','2025-10-31 20:42:21'),(3,'manuelgonzalez@mail.com','$2a$11$TMqv6VK3loU2U2yLpPSUL.6si1FnniDMMZ2hu8oCwHTHNWa9ZYJ26','ACTIVO',0,NULL,2,'2025-10-31 20:43:10','2025-10-31 20:43:10'),(4,'davidlopez@mail.com','$2a$11$HhjfpwJ.buM5PQ2Ata7Dqe/E.ETao7/.wNOJsG7TfhJM8aFdcNn3q','ACTIVO',0,NULL,2,'2025-10-31 20:44:02','2025-10-31 20:44:02'),(5,'mariagarcia@mail.com','$2a$11$7kCAytRmCWbUj0PlRub/i.6LrYX6iMdIKwfIW5p0.vNL9uVHCUfxe','ACTIVO',0,NULL,2,'2025-10-31 20:44:54','2025-10-31 20:44:54'),(6,'vivianahincapie@mail.com','$2a$11$bVazd6lKynJasTSHRlF/fOHf5nir15NQSNtfMjOGLAAkIyfzf4HpK','ACTIVO',0,'2025-11-01 01:46:31',3,'2025-10-31 20:46:10','2025-10-31 20:46:31');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-01  3:34:51
