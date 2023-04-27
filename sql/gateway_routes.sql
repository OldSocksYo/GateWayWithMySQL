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

-- 路由举例
INSERT INTO `dynamic_gateway`.`gateway_routes`(`id`, `route_id`, `uri`, `predicates`, `filters`, `meta_data`, `order`, `is_delete`) VALUES (1, 'dynamic_route1', 'https://www.bilibili.com/', 'Path=/v/dance;;;Method=GET', 'AddRequestParameter=spm_id_from,333.1007.0.0', NULL, 10, 0);
INSERT INTO `dynamic_gateway`.`gateway_routes`(`id`, `route_id`, `uri`, `predicates`, `filters`, `meta_data`, `order`, `is_delete`) VALUES (2, 'dynamic_route2', 'https://www.nowcoder.com/', 'Path=/exam/company', NULL, NULL, 9, 0);
INSERT INTO `dynamic_gateway`.`gateway_routes`(`id`, `route_id`, `uri`, `predicates`, `filters`, `meta_data`, `order`, `is_delete`) VALUES (3, 'dynamic_route3', 'https://baike.baidu.com/', 'Path=/wikicategory/view', 'AddRequestParameter=categoryName,%E6%81%90%E9%BE%99%E5%A4%A7%E5%85%A8', NULL, 11, 0);
INSERT INTO `dynamic_gateway`.`gateway_routes`(`id`, `route_id`, `uri`, `predicates`, `filters`, `meta_data`, `order`, `is_delete`) VALUES (4, 'dynamic_route4', 'https://baike.baidu.com/', 'Path=/art', NULL, NULL, 12, 1);
