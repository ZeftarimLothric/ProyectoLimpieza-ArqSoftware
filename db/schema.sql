-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: gestion_limpieza
-- ------------------------------------------------------
-- Server version	8.0.46

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

CREATE DATABASE IF NOT EXISTS gestion_limpieza CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gestion_limpieza;

--
-- Table structure for table `actividad_colonia`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
DROP TABLE IF EXISTS `actividad_colonia`;
CREATE TABLE `actividad_colonia` (
  `ID_ACTIVIDAD_COLONIA` int NOT NULL AUTO_INCREMENT,
  `ID_ACTIVIDAD` int NOT NULL,
  `ID_COLONIA` int NOT NULL,
  PRIMARY KEY (`ID_ACTIVIDAD_COLONIA`),
  UNIQUE KEY `UQ_ACTIVIDAD_COLONIA` (`ID_ACTIVIDAD`, `ID_COLONIA`),
  KEY `FK_ATIENDE_COLONIAS` (`ID_ACTIVIDAD`),
  KEY `FK_REGISTRA_COLONIA_ATENDIDA` (`ID_COLONIA`),
  CONSTRAINT `FK_ATIENDE_COLONIAS` FOREIGN KEY (`ID_ACTIVIDAD`) REFERENCES `actividades` (`ID_ACTIVIDAD`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `FK_REGISTRA_COLONIA_ATENDIDA` FOREIGN KEY (`ID_COLONIA`) REFERENCES `colonias` (`ID_COLONIA`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `actividades`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
DROP TABLE IF EXISTS `actividades`;
CREATE TABLE `actividades` (
  `ID_ACTIVIDAD` int NOT NULL AUTO_INCREMENT,
  `ID_TIPO_ACTIVIDAD` int NOT NULL,
  `ID_ESTADO_ACTIVIDAD` int NOT NULL,
  `ID_CUADRILLA` int NOT NULL,
  `TITULO` varchar(100) DEFAULT NULL,
  `DESCRIPCION` varchar(200) DEFAULT NULL,
  `FECHA_ACTIVIDAD` date DEFAULT NULL,
  `HORA_INICIO` time DEFAULT NULL,
  `HORA_FIN` time DEFAULT NULL,
  `FECHA_PROGRAMADA` datetime DEFAULT NULL,
  `FECHA_REALIZACION` datetime DEFAULT NULL,
  `LUGAR_ESPECIFICO` varchar(200) NOT NULL,
  `OBSERVACIONES` longtext,
  PRIMARY KEY (`ID_ACTIVIDAD`),
  KEY `FK_CLASIFICA_ACTIVIDADES` (`ID_TIPO_ACTIVIDAD`),
  KEY `FK_REALIZA_ACTIVIDADES` (`ID_CUADRILLA`),
  KEY `FK_TIENE_ESTADOS` (`ID_ESTADO_ACTIVIDAD`),
  CONSTRAINT `FK_CLASIFICA_ACTIVIDADES` FOREIGN KEY (`ID_TIPO_ACTIVIDAD`) REFERENCES `tipo_actividad` (`ID_TIPO_ACTIVIDAD`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_REALIZA_ACTIVIDADES` FOREIGN KEY (`ID_CUADRILLA`) REFERENCES `cuadrillas` (`ID_CUADRILLA`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_TIENE_ESTADOS` FOREIGN KEY (`ID_ESTADO_ACTIVIDAD`) REFERENCES `estados_actividad` (`ID_ESTADO_ACTIVIDAD`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `colonias`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
DROP TABLE IF EXISTS `colonias`;
CREATE TABLE `colonias` (
  `ID_COLONIA` int NOT NULL AUTO_INCREMENT,
  `NOMBRE` varchar(100) NOT NULL,
  `CODIGO_POSTAL` varchar(5) NOT NULL,
  `MUNICIPIO` varchar(100) NOT NULL,
  `ESTADO` varchar(100) NOT NULL,
  PRIMARY KEY (`ID_COLONIA`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cuadrilla_empleado`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
DROP TABLE IF EXISTS `cuadrilla_empleado`;
CREATE TABLE `cuadrilla_empleado` (
  `ID_CUADRILLA_EMPLEADO` int NOT NULL AUTO_INCREMENT,
  `ID_CUADRILLA` int NOT NULL,
  `ID_EMPLEADO` int NOT NULL,
  `FECHA_ASIGNACION` date NOT NULL,
  `ES_JEFE` smallint NOT NULL,
  PRIMARY KEY (`ID_CUADRILLA_EMPLEADO`),
  UNIQUE KEY `UQ_CUADRILLA_EMPLEADO` (`ID_CUADRILLA`, `ID_EMPLEADO`),
  KEY `FK_TIENE_ASIGNACIONES` (`ID_EMPLEADO`),
  KEY `FK_TIENE_INTEGRANTES` (`ID_CUADRILLA`),
  CONSTRAINT `FK_TIENE_ASIGNACIONES` FOREIGN KEY (`ID_EMPLEADO`) REFERENCES `empleados` (`ID_EMPLEADO`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_TIENE_INTEGRANTES` FOREIGN KEY (`ID_CUADRILLA`) REFERENCES `cuadrillas` (`ID_CUADRILLA`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cuadrillas`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
DROP TABLE IF EXISTS `cuadrillas`;
CREATE TABLE `cuadrillas` (
  `ID_CUADRILLA` int NOT NULL AUTO_INCREMENT,
  `NOMBRE` varchar(100) NOT NULL,
  `ACTIVO` smallint NOT NULL,
  PRIMARY KEY (`ID_CUADRILLA`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `empleados`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
DROP TABLE IF EXISTS `empleados`;
CREATE TABLE `empleados` (
  `ID_EMPLEADO` int NOT NULL AUTO_INCREMENT,
  `NOMBRE` varchar(100) NOT NULL,
  `APELLIDO_PATERNO` varchar(100) NOT NULL,
  `APELLIDO_MATERNO` varchar(100) DEFAULT NULL,
  `TELEFONO` varchar(15) DEFAULT NULL,
  `PUESTO` varchar(50) NOT NULL,
  `ACTIVO` smallint NOT NULL,
  PRIMARY KEY (`ID_EMPLEADO`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `estados_actividad`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
DROP TABLE IF EXISTS `estados_actividad`;
CREATE TABLE `estados_actividad` (
  `ID_ESTADO_ACTIVIDAD` int NOT NULL AUTO_INCREMENT,
  `NOMBRE_ESTADO` varchar(50) NOT NULL,
  PRIMARY KEY (`ID_ESTADO_ACTIVIDAD`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

INSERT INTO `estados_actividad` (`ID_ESTADO_ACTIVIDAD`, `NOMBRE_ESTADO`) VALUES
  (1, 'PROGRAMADA'),
  (2, 'EN PROCESO'),
  (3, 'COMPLETADA');

--
-- Table structure for table `evidencia`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
DROP TABLE IF EXISTS `evidencia`;
CREATE TABLE `evidencia` (
  `ID_EVIDENCIA` int NOT NULL AUTO_INCREMENT,
  `ID_ACTIVIDAD` int NOT NULL,
  `RUTA_IMAGEN` varchar(255) NOT NULL,
  `DESCRIPCION` varchar(200) DEFAULT NULL,
  `FECHA_CARGA` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID_EVIDENCIA`),
  KEY `FK_TIENE_EVIDENCIAS` (`ID_ACTIVIDAD`),
  CONSTRAINT `FK_TIENE_EVIDENCIAS` FOREIGN KEY (`ID_ACTIVIDAD`) REFERENCES `actividades` (`ID_ACTIVIDAD`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tipo_actividad`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
DROP TABLE IF EXISTS `tipo_actividad`;
CREATE TABLE `tipo_actividad` (
  `ID_TIPO_ACTIVIDAD` int NOT NULL AUTO_INCREMENT,
  `NOMBRE` varchar(100) NOT NULL,
  `DESCRIPCION` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`ID_TIPO_ACTIVIDAD`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usuarios`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
DROP TABLE IF EXISTS `usuarios`;
CREATE TABLE `usuarios` (
  `ID_USUARIO` int NOT NULL AUTO_INCREMENT,
  `USERNAME` varchar(50) NOT NULL,
  `CONTRASENA` char(64) NOT NULL,
  `NOMBRE` varchar(100) NOT NULL,
  `ROL` varchar(50) NOT NULL DEFAULT 'ADMINISTRADOR',
  `ACTIVO` smallint NOT NULL,
  PRIMARY KEY (`ID_USUARIO`),
  UNIQUE KEY `UQ_USERNAME` (`USERNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-08 23:55:09
