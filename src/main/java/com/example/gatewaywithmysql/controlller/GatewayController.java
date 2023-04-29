package com.example.gatewaywithmysql.controlller;

import com.example.gatewaywithmysql.service.JdbcRouteDefinitionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : yong
 * create at: 2023/4/18 23:11
 * @Description :
 **/

@RestController
public class GatewayController {

    private final JdbcRouteDefinitionRepository routeDefinitionRepository;


    public GatewayController(JdbcRouteDefinitionRepository routeDefinitionRepository) {
        this.routeDefinitionRepository = routeDefinitionRepository;
    }

    /**
     * 在mysql的gateway_routes表中修改好路由信息后
     * 浏览器调用http://localhost:9420/gateway/refresh-routes即可全量更新路由信息
     * @return success
     */
    @GetMapping("/gateway/refresh-routes")
    public String refreshRoutes() {
        return routeDefinitionRepository.refreshAll();
    }

    /**
     * 在mysql的gateway_routes表中增加一个新的路由信息后
     * 浏览器调用http://localhost:9420/gateway/add-route?routeId=新配置的路由的routeId
     * @return success
     */
    @GetMapping("/gateway/add-route")
    public String addRoute(@RequestParam("routeId") String routeId) {
        return routeDefinitionRepository.addOneRoute(routeId);
    }

    /**
     * 在mysql的gateway_routes表中增加一个新的路由信息后
     * 浏览器调用http://localhost:9420/gateway/del-route?routeId=要删除的路由的routeId
     * @return success
     */
    @GetMapping("/gateway/del-route")
    public String deleteRoute(@RequestParam("routeId") String routeId) {
        return routeDefinitionRepository.deleteOneRoute(routeId);
    }
}
