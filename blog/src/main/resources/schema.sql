DROP TABLE IF EXISTS `posts`;

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `password` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
);

LOCK TABLES `users` WRITE;

INSERT INTO `users` VALUES (1,'Himalaya','himalaya.saxena@gmail.com','12345'),(2,'Test User','test@gmail.com','12345'),(3,'qwerty','qwerty@gmail.com','12345');

UNLOCK TABLES;

CREATE TABLE `posts` (
  `post_id` int(11) NOT NULL AUTO_INCREMENT,
  `post_title` varchar(455) DEFAULT NULL,
  `post_body` longtext,
  `published_by` int(11) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `updated_on` datetime DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`post_id`),
  KEY `userdId_idx` (`published_by`),
  CONSTRAINT `userdId` FOREIGN KEY (`published_by`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
); 

LOCK TABLES `posts` WRITE;

INSERT INTO `posts` VALUES (1,'Post First Updated','Post body Updated',1,'2020-04-14 21:04:06','2020-04-17 15:04:46',0),(2,'This is be body of my second blog post','My Second Post',1,'2020-04-16 11:55:48','2020-04-16 11:55:48',1),(3,'titlee','bodydyyy',1,'2020-04-16 11:56:11','2020-04-17 15:31:56',0),(4,'Title 34','Thhis is my 4th Blog body',1,'2020-04-17 15:01:36','2020-04-17 15:01:36',1),(5,'This is blog title 5','This is blog body 5',1,'2020-04-17 15:29:53','2020-04-17 15:29:53',1),(6,'Thats it','I am updated',2,'2020-04-17 16:26:10','2020-04-17 16:26:54',1),(7,'title demo updated','demo body updated',1,'2020-04-17 17:27:05','2020-04-17 17:28:34',0);

UNLOCK TABLES;