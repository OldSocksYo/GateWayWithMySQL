package com.example.gatewaywithmysql.controlller;

import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : yong
 * create at: 2023/4/18 23:11
 * @Description :
 **/

@RestController
public class GatewayController {

    private ApplicationEventPublisher publisher;

    public GatewayController(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * 在mysql的gateway_routes表中修改好路由信息后
     * 浏览器调用http://localhost:9420/gateway/refresh-routes即可全量更新路由信息
     * @return success
     */
    @GetMapping("/gateway/refresh-routes")
    public String refreshRoutes() {
        publisher.publishEvent(new RefreshRoutesEvent(this));
        return "success";
    }

    //@GetMapping("/static/baidu")
    //public String staticGateway() {
    //
    //}
    //
    //@GetMapping("/dynamic/baidu")
    //public String dynamicGateway() {
    //
    //}
}
