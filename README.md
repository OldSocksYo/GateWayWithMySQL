# GateWayWithMySQL
gateway结合mysql实现动态路由

# 数据库表结构
```sql
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

```
其中断言predicates，过滤器filters的配置方式和在配置文件中一样；
多个断言规则和过滤规则之间用三个分号==;;;==进行分隔。

# 路由更新api
目前没有做到定时自动读取数据库更新，需要借助api调用
1. 全量刷新：http://localhost:9420/gateway/refresh-routes
2. 增加一个具体的route：http://localhost:9420/gateway/add-route?routeId=dynamic_route6 【这个调用前需要先在数据库中将新的route配置好，然后，将新的routeId当做参数传入】
3. 删除某一个具体的route：http://localhost:9420/gateway/del-route?routeId=dynamic_route6
