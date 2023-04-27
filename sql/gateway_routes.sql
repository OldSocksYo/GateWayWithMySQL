CREATE TABLE `gateway_routes` (
    `id` int unsigned NOT NULL AUTO_INCREMENT,
    `route_id` varchar(255) DEFAULT NULL,
    `uri` varchar(255) DEFAULT NULL,
    `predicates` varchar(2048) DEFAULT NULL,
    `filters` varchar(2048) DEFAULT NULL,
    `meta_data` varchar(2048) DEFAULT NULL,
    `order` int DEFAULT NULL,
    `is_delete` tinyint DEFAULT '0' COMMENT '0:未删除，1:已删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci