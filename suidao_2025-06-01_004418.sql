-- MySQL dump 10.13  Distrib 9.3.0, for macos13.7 (arm64)
--
-- Host: 127.0.0.1    Database: suidao
-- ------------------------------------------------------
-- Server version	9.3.0

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
-- Table structure for table `camera`
--

DROP TABLE IF EXISTS `camera`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `camera` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `is_online` bit(1) DEFAULT NULL,
  `last_online_time` datetime(6) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `rtsp_url` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `tunnel_id` bigint DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKq5wrjd781p5111fap0tq6mxku` (`tunnel_id`),
  CONSTRAINT `FKq5wrjd781p5111fap0tq6mxku` FOREIGN KEY (`tunnel_id`) REFERENCES `tunnel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `camera`
--

/*!40000 ALTER TABLE `camera` DISABLE KEYS */;
/*!40000 ALTER TABLE `camera` ENABLE KEYS */;

--
-- Table structure for table `device`
--

DROP TABLE IF EXISTS `device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `device` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `is_online` bit(1) DEFAULT NULL,
  `last_maintenance_time` datetime(6) DEFAULT NULL,
  `last_online_time` datetime(6) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `tunnel_id` bigint DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK7yl7o1htvlgridu3ec6vbmour` (`tunnel_id`),
  CONSTRAINT `FK7yl7o1htvlgridu3ec6vbmour` FOREIGN KEY (`tunnel_id`) REFERENCES `tunnel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device`
--

/*!40000 ALTER TABLE `device` DISABLE KEYS */;
/*!40000 ALTER TABLE `device` ENABLE KEYS */;

--
-- Table structure for table `environment`
--

DROP TABLE IF EXISTS `environment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `environment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `co2` double DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `humidity` double DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `pm25` double DEFAULT NULL,
  `temperature` double DEFAULT NULL,
  `tunnel_id` bigint DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8j3omf1s9ssbmuvfy6c7mv1f7` (`tunnel_id`),
  CONSTRAINT `FK8j3omf1s9ssbmuvfy6c7mv1f7` FOREIGN KEY (`tunnel_id`) REFERENCES `tunnel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `environment`
--

/*!40000 ALTER TABLE `environment` DISABLE KEYS */;
/*!40000 ALTER TABLE `environment` ENABLE KEYS */;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;

--
-- Table structure for table `tunnel`
--

DROP TABLE IF EXISTS `tunnel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tunnel` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `construction_time` datetime(6) DEFAULT NULL,
  `contact` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `height` double DEFAULT NULL,
  `length` double DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `manager` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `open_time` datetime(6) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `width` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tunnel`
--

/*!40000 ALTER TABLE `tunnel` DISABLE KEYS */;
/*!40000 ALTER TABLE `tunnel` ENABLE KEYS */;

--
-- Table structure for table `warning`
--

DROP TABLE IF EXISTS `warning`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `warning` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `handle_method` varchar(255) DEFAULT NULL,
  `handle_time` datetime(6) DEFAULT NULL,
  `handler` varchar(255) DEFAULT NULL,
  `level` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `tunnel_id` bigint DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKsg1omdbo6n38yvwxey4m8bj6j` (`tunnel_id`),
  CONSTRAINT `FKsg1omdbo6n38yvwxey4m8bj6j` FOREIGN KEY (`tunnel_id`) REFERENCES `tunnel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `warning`
--

/*!40000 ALTER TABLE `warning` DISABLE KEYS */;
/*!40000 ALTER TABLE `warning` ENABLE KEYS */;

--
-- Dumping routines for database 'suidao'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-01  0:44:21
