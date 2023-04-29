package com.example.gatewaywithmysql.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.cloud.gateway.route.RouteDefinition;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author : yong
 * create at: 2023/4/15 14:04
 * @Description : 路由配置实体类
 **/

@Entity
@Table(name = "gateway_routes")
@Data
@Accessors(chain = true)
public class GatewayRout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "route_id")
    private String routeId;

    @Column(name = "uri")
    private String uri;

    @Column(name = "predicates")
    private String predicates;

    @Column(name = "filters")
    private String filters;

    @Column(name = "meta_data")
    private String metadata;

    @Column(name = "`order`")
    private Integer order;

    @Column(name = "is_delete")
    private Integer isDelete;
}
