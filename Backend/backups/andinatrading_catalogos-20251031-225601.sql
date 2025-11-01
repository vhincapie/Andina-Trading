-- MySQL dump 10.13  Distrib 8.0.43, for Linux (x86_64)
--
-- Host: host.docker.internal    Database: andinatrading_catalogos
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
-- Table structure for table `ciudades`
--

DROP TABLE IF EXISTS `ciudades`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ciudades` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) NOT NULL,
  `pais_id` bigint NOT NULL,
  `estado` varchar(20) NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`),
  UNIQUE KEY `uk_ciudad_pais_nombre` (`pais_id`,`nombre`),
  CONSTRAINT `fk_ciudad_pais` FOREIGN KEY (`pais_id`) REFERENCES `paises` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ciudades`
--

LOCK TABLES `ciudades` WRITE;
/*!40000 ALTER TABLE `ciudades` DISABLE KEYS */;
INSERT INTO `ciudades` VALUES (1,'Quito',1,'ACTIVO'),(2,'Guayaquil',1,'ACTIVO'),(3,'Lima',2,'ACTIVO'),(4,'Arequipa',2,'ACTIVO'),(5,'Caracas',3,'ACTIVO'),(6,'Maracaibo',3,'ACTIVO'),(7,'Bogotá',4,'ACTIVO'),(8,'Medellín',4,'ACTIVO');
/*!40000 ALTER TABLE `ciudades` ENABLE KEYS */;
UNLOCK TABLES;

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
INSERT INTO `flyway_schema_history` VALUES (1,'1','init catalogos','SQL','V1__init_catalogos.sql',-1810272202,'root','2025-10-31 19:31:00',2005,1),(2,'2','seed catalogos','SQL','V2__seed_catalogos.sql',1613432103,'root','2025-10-31 19:31:02',1059,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `paises`
--

DROP TABLE IF EXISTS `paises`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `paises` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `codigo_iso3` varchar(3) NOT NULL,
  `nombre` varchar(120) NOT NULL,
  `estado` varchar(20) NOT NULL DEFAULT 'ACTIVO',
  `situacion_economica_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo_iso3` (`codigo_iso3`),
  UNIQUE KEY `nombre` (`nombre`),
  UNIQUE KEY `uk_pais_iso3` (`codigo_iso3`),
  UNIQUE KEY `uk_pais_nombre` (`nombre`),
  KEY `fk_pais_sit_eco` (`situacion_economica_id`),
  CONSTRAINT `fk_pais_sit_eco` FOREIGN KEY (`situacion_economica_id`) REFERENCES `situaciones_economicas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `paises`
--

LOCK TABLES `paises` WRITE;
/*!40000 ALTER TABLE `paises` DISABLE KEYS */;
INSERT INTO `paises` VALUES (1,'ECU','Ecuador','ACTIVO',1),(2,'PER','Perú','ACTIVO',2),(3,'VEN','Venezuela','ACTIVO',3),(4,'COL','Colombia','ACTIVO',4);
/*!40000 ALTER TABLE `paises` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `situaciones_economicas`
--

DROP TABLE IF EXISTS `situaciones_economicas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `situaciones_economicas` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) NOT NULL,
  `descripcion` varchar(400) DEFAULT NULL,
  `estado` varchar(20) NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `situaciones_economicas`
--

LOCK TABLES `situaciones_economicas` WRITE;
/*!40000 ALTER TABLE `situaciones_economicas` DISABLE KEYS */;
INSERT INTO `situaciones_economicas` VALUES (1,'Crecimiento Moderado','PIB creciendo de forma sostenida','ACTIVO'),(2,'Estabilidad','Indicadores macroeconómicos estables','ACTIVO'),(3,'Inflación Alta','Presión inflacionaria notable','ACTIVO'),(4,'Recuperación','Recuperación económica pospandemia','ACTIVO');
/*!40000 ALTER TABLE `situaciones_economicas` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-31 22:56:03
