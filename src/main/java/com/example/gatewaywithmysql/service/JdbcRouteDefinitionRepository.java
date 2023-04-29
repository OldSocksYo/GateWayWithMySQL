package com.example.gatewaywithmysql.service;

import com.example.gatewaywithmysql.dao.GatewayRouteRepository;
import com.example.gatewaywithmysql.entity.GatewayRout;
import com.example.gatewaywithmysql.utils.ParseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Collections.synchronizedMap;

/**
 * @author : yong
 * create at: 2023/4/15 14:14
 * @Description :
 **/

@Component
@Slf4j
public class JdbcRouteDefinitionRepository implements RouteDefinitionLocator {

    private final Map<String, RouteDefinition> routes = synchronizedMap(new LinkedHashMap<String, RouteDefinition>());
    private final GatewayRouteRepository gatewayRouteRepository;
    private final ApplicationEventPublisher publisher;

    private boolean firstStartFlag;

    public JdbcRouteDefinitionRepository(GatewayRouteRepository gatewayRouteRepository,
                                         ApplicationEventPublisher publisher) {
        this.gatewayRouteRepository = gatewayRouteRepository;
        this.publisher = publisher;
    }


    /**
     * 发布RefreshRouteEvent会自动调用此方法
     * @return 更新后的全量route信息
     */
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        // 第一次启动时需要先全量查一次数据库，后续操作此if中的逻辑不再执行
        if (!firstStartFlag) {
            List<GatewayRout> gatewayRouts = gatewayRouteRepository.findGatewayRoutsByIsDelete(0);
            gatewayRouts.forEach(gatewayRout -> routes.put(gatewayRout.getRouteId()
                    , ParseUtils.parseGatewayRout2RouteDefinition(gatewayRout)));
            firstStartFlag = true;
        }
        return Flux.fromIterable(routes.values());
    }


    /**
     * 一次性从数据库中获取全量route定义信息，全量更新
     */
    public String refreshAll() {
        List<GatewayRout> gatewayRouts = gatewayRouteRepository.findGatewayRoutsByIsDelete(0);

        gatewayRouts.forEach(gatewayRout -> routes.put(gatewayRout.getRouteId()
                , ParseUtils.parseGatewayRout2RouteDefinition(gatewayRout)));

        // 发布RefreshRoute事件
        publisher.publishEvent(new RefreshRoutesEvent(this));
        return "success";
    }

    /**
     * 单独增加一个Route
     * @param routeId 数据库中配置好的route的routeId（调用前，先将route信息在数据库中配置好）
     * @return success
     */
    public String addOneRoute(String routeId) {
        GatewayRout gatewayRout = gatewayRouteRepository.findByRouteId(routeId);
        if (Objects.isNull(gatewayRout)) {
            throw new NotFoundException("未在数据库中找到对应的route配置");
        }
        gatewayRout.setIsDelete(0);
        gatewayRouteRepository.save(gatewayRout);

        RouteDefinition routeDefinition = ParseUtils.parseGatewayRout2RouteDefinition(gatewayRout);
        routes.put(gatewayRout.getRouteId(), routeDefinition);

        publisher.publishEvent(new RefreshRoutesEvent(this));
        return "success";
    }

    /**
     * 根据routeId删除一个route
     * @param routeId 数据库中配置的routeId
     * @return success
     */
    public String deleteOneRoute(String routeId) {
        GatewayRout gatewayRout = gatewayRouteRepository.findByRouteId(routeId);

        if (!Objects.isNull(gatewayRout)) {
            gatewayRout.setIsDelete(1);
            gatewayRouteRepository.save(gatewayRout);
        }
        routes.remove(gatewayRout.getRouteId());

        publisher.publishEvent(new RefreshRoutesEvent(this));
        return "success";
    }



    @EventListener
    public void refreshRoutesEvent(RefreshRoutesEvent event) {
        log.info("RefreshRoutesEvent 事件执行了，source为：" + event.getSource());
    }
}
