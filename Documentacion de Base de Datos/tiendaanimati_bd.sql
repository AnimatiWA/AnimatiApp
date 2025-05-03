-- MySQL dump 10.13  Distrib 8.0.39, for Win64 (x86_64)
--
-- Host: localhost    Database: tiendaanimati_bd
-- ------------------------------------------------------
-- Server version	5.5.5-10.5.26-MariaDB

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
-- Table structure for table `animatiapp_carrito`
--

DROP TABLE IF EXISTS `animatiapp_carrito`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `animatiapp_carrito` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `Creado` datetime(6) NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `Usuario_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `AnimatiApp_carrito_Usuario_id_03051ced_fk_AnimatiApp_user_id` (`Usuario_id`),
  CONSTRAINT `AnimatiApp_carrito_Usuario_id_03051ced_fk_AnimatiApp_user_id` FOREIGN KEY (`Usuario_id`) REFERENCES `animatiapp_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `animatiapp_carrito`
--

LOCK TABLES `animatiapp_carrito` WRITE;
/*!40000 ALTER TABLE `animatiapp_carrito` DISABLE KEYS */;
/*!40000 ALTER TABLE `animatiapp_carrito` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `animatiapp_correocontacto`
--

DROP TABLE IF EXISTS `animatiapp_correocontacto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `animatiapp_correocontacto` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `email` varchar(254) NOT NULL,
  `mensaje` longtext NOT NULL,
  `creado` datetime(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `animatiapp_correocontacto`
--

LOCK TABLES `animatiapp_correocontacto` WRITE;
/*!40000 ALTER TABLE `animatiapp_correocontacto` DISABLE KEYS */;
/*!40000 ALTER TABLE `animatiapp_correocontacto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `animatiapp_passwordresettoken`
--

DROP TABLE IF EXISTS `animatiapp_passwordresettoken`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `animatiapp_passwordresettoken` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `token` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `expires_at` datetime(6) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `token` (`token`),
  KEY `AnimatiApp_passwordr_user_id_12aa4002_fk_AnimatiAp` (`user_id`),
  CONSTRAINT `AnimatiApp_passwordr_user_id_12aa4002_fk_AnimatiAp` FOREIGN KEY (`user_id`) REFERENCES `animatiapp_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `animatiapp_passwordresettoken`
--

LOCK TABLES `animatiapp_passwordresettoken` WRITE;
/*!40000 ALTER TABLE `animatiapp_passwordresettoken` DISABLE KEYS */;
/*!40000 ALTER TABLE `animatiapp_passwordresettoken` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `animatiapp_productocarrito`
--

DROP TABLE IF EXISTS `animatiapp_productocarrito`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `animatiapp_productocarrito` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `Cantidad` int(11) NOT NULL,
  `Precio` double NOT NULL,
  `Carrito_id` bigint(20) NOT NULL,
  `Codigo_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `AnimatiApp_productoc_Carrito_id_5b975cf7_fk_AnimatiAp` (`Carrito_id`),
  KEY `AnimatiApp_productoc_Codigo_id_6c0396c7_fk_producto_` (`Codigo_id`),
  CONSTRAINT `AnimatiApp_productoc_Carrito_id_5b975cf7_fk_AnimatiAp` FOREIGN KEY (`Carrito_id`) REFERENCES `animatiapp_carrito` (`id`),
  CONSTRAINT `AnimatiApp_productoc_Codigo_id_6c0396c7_fk_producto_` FOREIGN KEY (`Codigo_id`) REFERENCES `producto` (`Codigo_Producto`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `animatiapp_productocarrito`
--

LOCK TABLES `animatiapp_productocarrito` WRITE;
/*!40000 ALTER TABLE `animatiapp_productocarrito` DISABLE KEYS */;
/*!40000 ALTER TABLE `animatiapp_productocarrito` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `animatiapp_user`
--

DROP TABLE IF EXISTS `animatiapp_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `animatiapp_user` (
  `password` varchar(128) NOT NULL,
  `last_login` datetime(6) DEFAULT NULL,
  `is_superuser` tinyint(1) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `is_staff` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `animatiapp_user`
--

LOCK TABLES `animatiapp_user` WRITE;
/*!40000 ALTER TABLE `animatiapp_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `animatiapp_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `animatiapp_user_groups`
--

DROP TABLE IF EXISTS `animatiapp_user_groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `animatiapp_user_groups` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `group_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `AnimatiApp_user_groups_user_id_group_id_5a4fd50f_uniq` (`user_id`,`group_id`),
  KEY `AnimatiApp_user_groups_group_id_e750693c_fk_auth_group_id` (`group_id`),
  CONSTRAINT `AnimatiApp_user_groups_group_id_e750693c_fk_auth_group_id` FOREIGN KEY (`group_id`) REFERENCES `auth_group` (`id`),
  CONSTRAINT `AnimatiApp_user_groups_user_id_2f6d36ce_fk_AnimatiApp_user_id` FOREIGN KEY (`user_id`) REFERENCES `animatiapp_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `animatiapp_user_groups`
--

LOCK TABLES `animatiapp_user_groups` WRITE;
/*!40000 ALTER TABLE `animatiapp_user_groups` DISABLE KEYS */;
/*!40000 ALTER TABLE `animatiapp_user_groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `animatiapp_user_user_permissions`
--

DROP TABLE IF EXISTS `animatiapp_user_user_permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `animatiapp_user_user_permissions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `permission_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `AnimatiApp_user_user_per_user_id_permission_id_976f6101_uniq` (`user_id`,`permission_id`),
  KEY `AnimatiApp_user_user_permission_id_553d1027_fk_auth_perm` (`permission_id`),
  CONSTRAINT `AnimatiApp_user_user_permission_id_553d1027_fk_auth_perm` FOREIGN KEY (`permission_id`) REFERENCES `auth_permission` (`id`),
  CONSTRAINT `AnimatiApp_user_user_user_id_9c298f14_fk_AnimatiAp` FOREIGN KEY (`user_id`) REFERENCES `animatiapp_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `animatiapp_user_user_permissions`
--

LOCK TABLES `animatiapp_user_user_permissions` WRITE;
/*!40000 ALTER TABLE `animatiapp_user_user_permissions` DISABLE KEYS */;
/*!40000 ALTER TABLE `animatiapp_user_user_permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `auth_group`
--

DROP TABLE IF EXISTS `auth_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auth_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(150) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auth_group`
--

LOCK TABLES `auth_group` WRITE;
/*!40000 ALTER TABLE `auth_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `auth_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `auth_group_permissions`
--

DROP TABLE IF EXISTS `auth_group_permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auth_group_permissions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `auth_group_permissions_group_id_permission_id_0cd325b0_uniq` (`group_id`,`permission_id`),
  KEY `auth_group_permissio_permission_id_84c5c92e_fk_auth_perm` (`permission_id`),
  CONSTRAINT `auth_group_permissio_permission_id_84c5c92e_fk_auth_perm` FOREIGN KEY (`permission_id`) REFERENCES `auth_permission` (`id`),
  CONSTRAINT `auth_group_permissions_group_id_b120cbf9_fk_auth_group_id` FOREIGN KEY (`group_id`) REFERENCES `auth_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auth_group_permissions`
--

LOCK TABLES `auth_group_permissions` WRITE;
/*!40000 ALTER TABLE `auth_group_permissions` DISABLE KEYS */;
/*!40000 ALTER TABLE `auth_group_permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `auth_permission`
--

DROP TABLE IF EXISTS `auth_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auth_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `content_type_id` int(11) NOT NULL,
  `codename` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `auth_permission_content_type_id_codename_01ab375a_uniq` (`content_type_id`,`codename`),
  CONSTRAINT `auth_permission_content_type_id_2f476e4b_fk_django_co` FOREIGN KEY (`content_type_id`) REFERENCES `django_content_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auth_permission`
--

LOCK TABLES `auth_permission` WRITE;
/*!40000 ALTER TABLE `auth_permission` DISABLE KEYS */;
INSERT INTO `auth_permission` VALUES (1,'Can add log entry',1,'add_logentry'),(2,'Can change log entry',1,'change_logentry'),(3,'Can delete log entry',1,'delete_logentry'),(4,'Can view log entry',1,'view_logentry'),(5,'Can add permission',2,'add_permission'),(6,'Can change permission',2,'change_permission'),(7,'Can delete permission',2,'delete_permission'),(8,'Can view permission',2,'view_permission'),(9,'Can add group',3,'add_group'),(10,'Can change group',3,'change_group'),(11,'Can delete group',3,'delete_group'),(12,'Can view group',3,'view_group'),(13,'Can add content type',4,'add_contenttype'),(14,'Can change content type',4,'change_contenttype'),(15,'Can delete content type',4,'delete_contenttype'),(16,'Can view content type',4,'view_contenttype'),(17,'Can add session',5,'add_session'),(18,'Can change session',5,'change_session'),(19,'Can delete session',5,'delete_session'),(20,'Can view session',5,'view_session'),(21,'Can add carrito',6,'add_carrito'),(22,'Can change carrito',6,'change_carrito'),(23,'Can delete carrito',6,'delete_carrito'),(24,'Can view carrito',6,'view_carrito'),(25,'Can add Categoria',7,'add_categoria'),(26,'Can change Categoria',7,'change_categoria'),(27,'Can delete Categoria',7,'delete_categoria'),(28,'Can view Categoria',7,'view_categoria'),(29,'Can add correo contacto',8,'add_correocontacto'),(30,'Can change correo contacto',8,'change_correocontacto'),(31,'Can delete correo contacto',8,'delete_correocontacto'),(32,'Can view correo contacto',8,'view_correocontacto'),(33,'Can add Producto',9,'add_producto'),(34,'Can change Producto',9,'change_producto'),(35,'Can delete Producto',9,'delete_producto'),(36,'Can view Producto',9,'view_producto'),(37,'Can add producto carrito',10,'add_productocarrito'),(38,'Can change producto carrito',10,'change_productocarrito'),(39,'Can delete producto carrito',10,'delete_productocarrito'),(40,'Can view producto carrito',10,'view_productocarrito'),(41,'Can add Usuario',11,'add_user'),(42,'Can change Usuario',11,'change_user'),(43,'Can delete Usuario',11,'delete_user'),(44,'Can view Usuario',11,'view_user'),(45,'Can add password reset token',12,'add_passwordresettoken'),(46,'Can change password reset token',12,'change_passwordresettoken'),(47,'Can delete password reset token',12,'delete_passwordresettoken'),(48,'Can view password reset token',12,'view_passwordresettoken'),(49,'Can add Cliente',13,'add_cliente'),(50,'Can change Cliente',13,'change_cliente'),(51,'Can delete Cliente',13,'delete_cliente'),(52,'Can view Cliente',13,'view_cliente'),(53,'Can add blacklisted token',14,'add_blacklistedtoken'),(54,'Can change blacklisted token',14,'change_blacklistedtoken'),(55,'Can delete blacklisted token',14,'delete_blacklistedtoken'),(56,'Can view blacklisted token',14,'view_blacklistedtoken'),(57,'Can add outstanding token',15,'add_outstandingtoken'),(58,'Can change outstanding token',15,'change_outstandingtoken'),(59,'Can delete outstanding token',15,'delete_outstandingtoken'),(60,'Can view outstanding token',15,'view_outstandingtoken');
/*!40000 ALTER TABLE `auth_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categoria`
--

DROP TABLE IF EXISTS `categoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categoria` (
  `Id_Categoria` int(11) NOT NULL AUTO_INCREMENT,
  `Nombre_Categoria` varchar(100) NOT NULL,
  `Descripcion_Categoria` varchar(100) NOT NULL,
  PRIMARY KEY (`Id_Categoria`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categoria`
--

LOCK TABLES `categoria` WRITE;
/*!40000 ALTER TABLE `categoria` DISABLE KEYS */;
/*!40000 ALTER TABLE `categoria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cliente`
--

DROP TABLE IF EXISTS `cliente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente` (
  `DNI` int(11) NOT NULL,
  `Nombre` varchar(100) NOT NULL,
  `Apellido` varchar(100) NOT NULL,
  `Correo_Electronico` varchar(130) NOT NULL,
  `Direccion` varchar(120) NOT NULL,
  `Telefono` int(11) NOT NULL,
  `Id_usuario_id` bigint(20) NOT NULL,
  PRIMARY KEY (`DNI`),
  KEY `cliente_Id_usuario_id_6ee9ed64_fk_AnimatiApp_user_id` (`Id_usuario_id`),
  CONSTRAINT `cliente_Id_usuario_id_6ee9ed64_fk_AnimatiApp_user_id` FOREIGN KEY (`Id_usuario_id`) REFERENCES `animatiapp_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cliente`
--

LOCK TABLES `cliente` WRITE;
/*!40000 ALTER TABLE `cliente` DISABLE KEYS */;
/*!40000 ALTER TABLE `cliente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `django_admin_log`
--

DROP TABLE IF EXISTS `django_admin_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `django_admin_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `action_time` datetime(6) NOT NULL,
  `object_id` longtext DEFAULT NULL,
  `object_repr` varchar(200) NOT NULL,
  `action_flag` smallint(5) unsigned NOT NULL CHECK (`action_flag` >= 0),
  `change_message` longtext NOT NULL,
  `content_type_id` int(11) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `django_admin_log_content_type_id_c4bce8eb_fk_django_co` (`content_type_id`),
  KEY `django_admin_log_user_id_c564eba6_fk_AnimatiApp_user_id` (`user_id`),
  CONSTRAINT `django_admin_log_content_type_id_c4bce8eb_fk_django_co` FOREIGN KEY (`content_type_id`) REFERENCES `django_content_type` (`id`),
  CONSTRAINT `django_admin_log_user_id_c564eba6_fk_AnimatiApp_user_id` FOREIGN KEY (`user_id`) REFERENCES `animatiapp_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `django_admin_log`
--

LOCK TABLES `django_admin_log` WRITE;
/*!40000 ALTER TABLE `django_admin_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `django_admin_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `django_content_type`
--

DROP TABLE IF EXISTS `django_content_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `django_content_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app_label` varchar(100) NOT NULL,
  `model` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `django_content_type_app_label_model_76bd3d3b_uniq` (`app_label`,`model`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `django_content_type`
--

LOCK TABLES `django_content_type` WRITE;
/*!40000 ALTER TABLE `django_content_type` DISABLE KEYS */;
INSERT INTO `django_content_type` VALUES (1,'admin','logentry'),(6,'AnimatiApp','carrito'),(7,'AnimatiApp','categoria'),(13,'AnimatiApp','cliente'),(8,'AnimatiApp','correocontacto'),(12,'AnimatiApp','passwordresettoken'),(9,'AnimatiApp','producto'),(10,'AnimatiApp','productocarrito'),(11,'AnimatiApp','user'),(3,'auth','group'),(2,'auth','permission'),(4,'contenttypes','contenttype'),(5,'sessions','session'),(14,'token_blacklist','blacklistedtoken'),(15,'token_blacklist','outstandingtoken');
/*!40000 ALTER TABLE `django_content_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `django_migrations`
--

DROP TABLE IF EXISTS `django_migrations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `django_migrations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `app` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `applied` datetime(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `django_migrations`
--

LOCK TABLES `django_migrations` WRITE;
/*!40000 ALTER TABLE `django_migrations` DISABLE KEYS */;
INSERT INTO `django_migrations` VALUES (1,'contenttypes','0001_initial','2024-12-17 22:50:42.566955'),(2,'contenttypes','0002_remove_content_type_name','2024-12-17 22:50:42.594453'),(3,'auth','0001_initial','2024-12-17 22:50:42.727075'),(4,'auth','0002_alter_permission_name_max_length','2024-12-17 22:50:42.733603'),(5,'auth','0003_alter_user_email_max_length','2024-12-17 22:50:42.736119'),(6,'auth','0004_alter_user_username_opts','2024-12-17 22:50:42.738588'),(7,'auth','0005_alter_user_last_login_null','2024-12-17 22:50:42.741784'),(8,'auth','0006_require_contenttypes_0002','2024-12-17 22:50:42.742882'),(9,'auth','0007_alter_validators_add_error_messages','2024-12-17 22:50:42.745417'),(10,'auth','0008_alter_user_username_max_length','2024-12-17 22:50:42.747926'),(11,'auth','0009_alter_user_last_name_max_length','2024-12-17 22:50:42.751257'),(12,'auth','0010_alter_group_name_max_length','2024-12-17 22:50:42.759013'),(13,'auth','0011_update_proxy_permissions','2024-12-17 22:50:42.762112'),(14,'auth','0012_alter_user_first_name_max_length','2024-12-17 22:50:42.765028'),(15,'AnimatiApp','0001_initial','2024-12-17 22:50:43.152575'),(16,'admin','0001_initial','2024-12-17 22:50:43.223483'),(17,'admin','0002_logentry_remove_auto_add','2024-12-17 22:50:43.227002'),(18,'admin','0003_logentry_add_action_flag_choices','2024-12-17 22:50:43.231185'),(19,'sessions','0001_initial','2024-12-17 22:50:43.250190'),(20,'token_blacklist','0001_initial','2024-12-17 22:50:43.398367'),(21,'token_blacklist','0002_outstandingtoken_jti_hex','2024-12-17 22:50:43.406908'),(22,'token_blacklist','0003_auto_20171017_2007','2024-12-17 22:50:43.413537'),(23,'token_blacklist','0004_auto_20171017_2013','2024-12-17 22:50:43.451014'),(24,'token_blacklist','0005_remove_outstandingtoken_jti','2024-12-17 22:50:43.462684'),(25,'token_blacklist','0006_auto_20171017_2113','2024-12-17 22:50:43.473370'),(26,'token_blacklist','0007_auto_20171017_2214','2024-12-17 22:50:43.696943'),(27,'token_blacklist','0008_migrate_to_bigautofield','2024-12-17 22:50:43.903335'),(28,'token_blacklist','0010_fix_migrate_to_bigautofield','2024-12-17 22:50:43.909910'),(29,'token_blacklist','0011_linearizes_history','2024-12-17 22:50:43.910913'),(30,'token_blacklist','0012_alter_outstandingtoken_user','2024-12-17 22:50:43.917149');
/*!40000 ALTER TABLE `django_migrations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `django_session`
--

DROP TABLE IF EXISTS `django_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `django_session` (
  `session_key` varchar(40) NOT NULL,
  `session_data` longtext NOT NULL,
  `expire_date` datetime(6) NOT NULL,
  PRIMARY KEY (`session_key`),
  KEY `django_session_expire_date_a5c62663` (`expire_date`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `django_session`
--

LOCK TABLES `django_session` WRITE;
/*!40000 ALTER TABLE `django_session` DISABLE KEYS */;
/*!40000 ALTER TABLE `django_session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `producto`
--

DROP TABLE IF EXISTS `producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto` (
  `Codigo_Producto` int(11) NOT NULL AUTO_INCREMENT,
  `Nombre_Producto` varchar(70) NOT NULL,
  `Imagen` varchar(250) NOT NULL,
  `Precio` decimal(10,2) NOT NULL,
  `Stock` int(10) unsigned NOT NULL CHECK (`Stock` >= 0),
  `Id_Categoria_id` int(11) NOT NULL,
  PRIMARY KEY (`Codigo_Producto`),
  KEY `producto_Id_Categoria_id_66d644ef_fk_categoria_Id_Categoria` (`Id_Categoria_id`),
  CONSTRAINT `producto_Id_Categoria_id_66d644ef_fk_categoria_Id_Categoria` FOREIGN KEY (`Id_Categoria_id`) REFERENCES `categoria` (`Id_Categoria`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producto`
--

LOCK TABLES `producto` WRITE;
/*!40000 ALTER TABLE `producto` DISABLE KEYS */;
/*!40000 ALTER TABLE `producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `token_blacklist_blacklistedtoken`
--

DROP TABLE IF EXISTS `token_blacklist_blacklistedtoken`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `token_blacklist_blacklistedtoken` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `blacklisted_at` datetime(6) NOT NULL,
  `token_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `token_id` (`token_id`),
  CONSTRAINT `token_blacklist_blacklistedtoken_token_id_3cc7fe56_fk` FOREIGN KEY (`token_id`) REFERENCES `token_blacklist_outstandingtoken` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `token_blacklist_blacklistedtoken`
--

LOCK TABLES `token_blacklist_blacklistedtoken` WRITE;
/*!40000 ALTER TABLE `token_blacklist_blacklistedtoken` DISABLE KEYS */;
/*!40000 ALTER TABLE `token_blacklist_blacklistedtoken` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `token_blacklist_outstandingtoken`
--

DROP TABLE IF EXISTS `token_blacklist_outstandingtoken`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `token_blacklist_outstandingtoken` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `token` longtext NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `expires_at` datetime(6) NOT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `jti` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `token_blacklist_outstandingtoken_jti_hex_d9bdf6f7_uniq` (`jti`),
  KEY `token_blacklist_outs_user_id_83bc629a_fk_AnimatiAp` (`user_id`),
  CONSTRAINT `token_blacklist_outs_user_id_83bc629a_fk_AnimatiAp` FOREIGN KEY (`user_id`) REFERENCES `animatiapp_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `token_blacklist_outstandingtoken`
--

LOCK TABLES `token_blacklist_outstandingtoken` WRITE;
/*!40000 ALTER TABLE `token_blacklist_outstandingtoken` DISABLE KEYS */;
/*!40000 ALTER TABLE `token_blacklist_outstandingtoken` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-12-17 19:52:33
