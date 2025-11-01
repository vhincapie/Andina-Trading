-- MySQL dump 10.13  Distrib 8.0.43, for Linux (x86_64)
--
-- Host: host.docker.internal    Database: andinatrading_contratos
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
-- Table structure for table `contratos`
--

DROP TABLE IF EXISTS `contratos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contratos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `inversionista_id` bigint NOT NULL,
  `comisionista_id` bigint NOT NULL,
  `estado` varchar(20) NOT NULL,
  `moneda` varchar(3) NOT NULL,
  `porcentaje_cobro_aplicado` decimal(5,2) NOT NULL,
  `terminos_texto` text,
  `observaciones` text,
  `fecha_inicio` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_fin` timestamp NULL DEFAULT NULL,
  `creado_en` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `actualizado_en` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contratos`
--

LOCK TABLES `contratos` WRITE;
/*!40000 ALTER TABLE `contratos` DISABLE KEYS */;
INSERT INTO `contratos` VALUES (1,1,1,'ACTIVO','COP',2.50,'ANDINA TRADING S.A.S. - TÉRMINOS Y CONDICIONES DEL CONTRATO DE COMISIONAMIENTO\n\n1. Partes.\nIntervienen: (i) ANDINA TRADING S.A.S. (?Andina Trading?), en calidad de comisionista, a través del comisionista seleccionado por el inversionista, y (ii) el Inversionista identificado en la orden de registro realizada en la plataforma.\n\n2. Objeto.\nEl Comisionista realizará, por cuenta y riesgo del Inversionista, actividades de intermediación y ejecución de órdenes de inversión conforme a las instrucciones del Inversionista y a las políticas internas de Andina Trading.\n\n3. Honorarios.\nEl Inversionista reconoce y acepta un honorario estándar equivalente al 2.50% sobre el valor administrado y/o resultados según aplique, cobrado con la periodicidad definida por Andina Trading. Este porcentaje es corporativo y aplica de manera uniforme para todos los inversionistas salvo comunicación posterior por escrito.\n\n4. Moneda y cargos.\nLa moneda del contrato será COP. El Inversionista asume costos, impuestos, comisiones bancarias, cambiarias y demás cargos aplicables por terceros.\n\n5. Vigencia.\nEl contrato inicia en la Fecha de Inicio indicada por el Inversionista y tendrá vigencia hasta la Fecha de Fin (si fue indicada) o hasta su terminación conforme a la Cláusula 12.\n\n6. Perfil y facultades.\nEl Comisionista actuará siguiendo las instrucciones del Inversionista y las normas aplicables. No garantiza resultados. El Inversionista declara comprender los riesgos del mercado y que los precios de los instrumentos financieros pueden fluctuar.\n\n7. Información y reportes.\nAndina Trading pondrá a disposición del Inversionista la información operativa razonable sobre sus posiciones y movimientos, a través de los medios tecnológicos habilitados.\n\n8. Confidencialidad.\nLa información de cada parte se mantendrá confidencial y solo se divulgará cuando sea exigido por ley o autoridad competente.\n\n9. Protección de datos.\nEl tratamiento de datos personales se realizará conforme a la política de privacidad de Andina Trading disponible en sus canales oficiales. El Inversionista autoriza dicho tratamiento para la ejecución de este contrato.\n\n10. Prevención de fraude y cumplimiento.\nAndina Trading podrá realizar verificaciones KYC/AML y suspender operaciones ante señales de riesgo o requerimientos regulatorios.\n\n11. Responsabilidad.\nAndina Trading y el Comisionista no serán responsables por pérdidas derivadas de fluctuaciones de mercado, fuerza mayor, fallas de terceros o decisiones del Inversionista.\n\n12. Terminación.\nCualquiera de las partes podrá terminar el contrato en cualquier momento con aviso por los canales oficiales. La terminación no afecta las obligaciones de pago devengadas ni los procesos de cierre en curso.\n\n13. Modificaciones.\nCualquier modificación de estos términos se comunicará por canales oficiales y regirá para contratos futuros. Los contratos ya celebrados conservan su texto y porcentaje aplicados al momento de su creación.\n\n14. Ley y jurisdicción.\nEste contrato se rige por las leyes del país de registro de Andina Trading. Las controversias se someterán a los jueces competentes del mismo domicilio, salvo pacto arbitral distinto.\n\n15. Aceptación.\nAl marcar ?Acepto los términos? y registrar la orden en la plataforma, el Inversionista declara haber leído, entendido y aceptado íntegramente este contrato.','','2025-10-31 20:47:49',NULL,'2025-10-31 20:47:49','2025-10-31 20:47:49');
/*!40000 ALTER TABLE `contratos` ENABLE KEYS */;
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
INSERT INTO `flyway_schema_history` VALUES (1,'1','create contratos','SQL','V1__create_contratos.sql',583424044,'root','2025-10-31 19:31:00',1647,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-01  3:34:54
