-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: inventory
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `inventory`
--

DROP TABLE IF EXISTS `inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory` (
  `id` int NOT NULL AUTO_INCREMENT,
  `User_id` int NOT NULL,
  `Item_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `User_id` (`User_id`),
  KEY `Item_id` (`Item_id`),
  CONSTRAINT `inventory_ibfk_1` FOREIGN KEY (`User_id`) REFERENCES `users` (`id`),
  CONSTRAINT `inventory_ibfk_2` FOREIGN KEY (`Item_id`) REFERENCES `items` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory`
--

LOCK TABLES `inventory` WRITE;
/*!40000 ALTER TABLE `inventory` DISABLE KEYS */;
INSERT INTO `inventory` VALUES (1,8,1),(2,8,6),(4,9,1),(5,9,6),(6,9,5),(10,6,1),(11,8,1),(12,8,7),(13,8,5),(14,8,5),(15,8,6),(16,8,5),(17,5,1),(18,5,1),(19,5,6),(20,5,6),(21,5,5),(22,9,1),(23,8,5),(24,5,1),(25,9,6),(26,9,14),(27,9,1),(32,1,5),(33,5,1),(35,7,5),(36,7,8),(37,7,8),(38,7,6),(39,7,6),(40,7,5),(41,7,6),(42,7,6),(43,7,6),(44,7,12),(45,7,12),(46,9,9),(47,9,9),(48,1,13),(49,1,9),(50,10,1),(51,10,6),(52,10,5),(53,10,1),(54,10,1),(55,10,9),(56,10,14),(57,10,7),(58,8,1);
/*!40000 ALTER TABLE `inventory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `items`
--

DROP TABLE IF EXISTS `items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `weight` double NOT NULL,
  `rarity` enum('COMMON','UNCOMMON','RARE','EPIC','LEGENDARY') DEFAULT NULL,
  `type` enum('OFF_HAND','1_HAND','2_HAND') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `items`
--

LOCK TABLES `items` WRITE;
/*!40000 ALTER TABLE `items` DISABLE KEYS */;
INSERT INTO `items` VALUES (1,'Dragon sword',7.5,'COMMON','1_HAND'),(5,'Fire-Potion',2,'COMMON','1_HAND'),(6,'Wood-Chestplate',5,'COMMON','OFF_HAND'),(7,'DoomBringer',6,'UNCOMMON','1_HAND'),(8,'Nightcrackle',3,'UNCOMMON','1_HAND'),(9,'Wood-Helmet',3.5,'UNCOMMON','OFF_HAND'),(10,'DemonSlayer',5,'RARE','2_HAND'),(11,'Healing-Potion',2,'RARE','1_HAND'),(12,'Steel-Chest',6,'RARE','OFF_HAND'),(13,'Katana',4.5,'EPIC','1_HAND'),(14,'Poison-Ivy',3,'EPIC','1_HAND'),(15,'Gold-Armor',8.5,'EPIC','OFF_HAND'),(16,'GalaxyDestroyer',9,'LEGENDARY','1_HAND'),(17,'Eternal-Potion',5,'LEGENDARY','OFF_HAND'),(18,'Diamond-Armor',12,'LEGENDARY','OFF_HAND');
/*!40000 ALTER TABLE `items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `balance` double DEFAULT '50',
  PRIMARY KEY (`id`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'caspar','123',48),(2,'marcus','1234',50),(4,'ewklgjwek','weknkjwengjkwen',50),(5,'Jesper','12345',9293),(6,'hej','123',141),(7,'hejj','123',1628),(8,'hejjj','123',630),(9,'Ole','123',198),(10,'gris','123',148);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-12-18 12:15:42
