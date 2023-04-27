package com.example.gatewaywithmysql.service;

import com.example.gatewaywithmysql.dao.GatewayRouteRepository;
import com.example.gatewaywithmysql.entity.GatewayRout;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : yong
 * create at: 2023/4/15 14:14
 * @Description :
 **/

@Component
@Slf4j
public class JdbcRouteDefinitionRepository implements RouteDefinitionRepository {

    private final GatewayRouteRepository gatewayRouteRepository;

    public JdbcRouteDefinitionRepository(GatewayRouteRepository gatewayRouteRepository) {
        this.gatewayRouteRepository = gatewayRouteRepository;
    }

    /**
     * 一次性从数据库中获取全量route定义信息，全量更新
     */
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        List<GatewayRout> gatewayRouts = gatewayRouteRepository.findGatewayRoutsByIsDelete(0);

        List<RouteDefinition> routeDefinitions = gatewayRouts
                .stream()
                .map(this::parseGatewayRout2RouteDefinition)
                .collect(Collectors.toList());

        return Flux.fromIterable(routeDefinitions);
    }


    /**
     * 根据数据库中的gatewayRout信息封装RouteDefinition对象
     * @param gatewayRout -
     * @return -
     */
    private RouteDefinition parseGatewayRout2RouteDefinition(GatewayRout gatewayRout) {
        RouteDefinition routeDefinition = new RouteDefinition();
        routeDefinition.setId(gatewayRout.getRouteId());
        routeDefinition.setUri(URI.create(gatewayRout.getUri()));
        routeDefinition.setOrder(gatewayRout.getOrder());

        ObjectMapper objectMapper = new ObjectMapper();
        List<PredicateDefinition> predicateDefinitions = new ArrayList<>();
        if (StringUtils.hasText(gatewayRout.getPredicates())) {
            try {
                String[] predicates = gatewayRout.getPredicates().split(";;;");
                for (String predicate : predicates) {
                    predicateDefinitions.add(new PredicateDefinition(predicate));
                }
                //predicateDefinitions = objectMapper.readValue(gatewayRout.getPredicates(), new TypeReference<List<PredicateDefinition>>() {});
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new RuntimeException("could not handle predicate, e:" + e.getMessage());
            }
        }
        routeDefinition.setPredicates(predicateDefinitions);

        List<FilterDefinition> filterDefinitions = new ArrayList<>();
        if (StringUtils.hasText(gatewayRout.getFilters())) {
            try {
                String[] filters = gatewayRout.getFilters().split(";;;");
                for (String filter : filters) {
                    filterDefinitions.add(new FilterDefinition(filter));
                }
                //filterDefinitions = objectMapper.readValue(gatewayRout.getFilters(), new TypeReference<List<FilterDefinition>>() {});
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new RuntimeException("could not handle filter, e:" + e.getMessage());
            }
        }
        routeDefinition.setFilters(filterDefinitions);

        Map<String, Object> metadata = new HashMap<>();
        if (StringUtils.hasText(gatewayRout.getMetadata())) {
            try {
                metadata = objectMapper.readValue(gatewayRout.getMetadata(), new TypeReference<Map<String, Object>>() {});
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
                throw new RuntimeException("could not parse metadata, e:" + e.getMessage());
            }
        }
        routeDefinition.setMetadata(metadata);
        return routeDefinition;
    }

    @EventListener
    public void refreshRoutesEvent(RefreshRoutesEvent event) {
        log.info("RefreshRoutesEvent 事件执行了，source为：" + event.getSource());

    }

    /**
     * 这里的实现方案是通过getRouteDefinitions一次性从数
     * 据库中获取全量route定义信息，全量更新，所以save不实现
     */
    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        /*return route.flatMap(r -> {
            GatewayRout gatewayRout = new GatewayRout();
            gatewayRout.setRouteId(r.getId())
                    .setUri(r.getUri().toString())
                    .setOrder(r.getOrder())
                    .setPredicates(parse2JsonStr(r.getPredicates()))
                    .setFilters(parse2JsonStr(r.getFilters()))
                    .setMetadata(parse2JsonStr(r.getMetadata()));
            return Mono.fromCallable(() -> gatewayRouteRepository.save(gatewayRout)).then();
        });*/
        return Mono.defer(() -> Mono.error(new NotFoundException("Unsupported operation")));
    }

    /**
     * 这里的实现方案是通过getRouteDefinitions一次性从数
     * 据库中获取全量route定义信息，全量更新，所以delete不实现
     */
    @Override
    public Mono<Void> delete(Mono<String> routeId) {
       /*return routeId.flatMap(rd -> Mono.fromRunnable(() -> {
           gatewayRouteRepository.delete(new GatewayRout().setRouteId(rd));
       }).then());*/
        return Mono.defer(() -> Mono.error(new NotFoundException("Unsupported operation")));
    }

    /*private String parse2JsonStr(Object obj) {
        String json = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            json = objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException("parse obj to str failed, e=" + e.getMessage());
        }
        return json;
    }*/
}
