package com.example.gatewaywithmysql.utils;

import com.example.gatewaywithmysql.entity.GatewayRout;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : yong
 * create at: 2023/4/29 20:23
 * @Description :
 **/
@Slf4j
public class ParseUtils {
    /**
     * 根据数据库中的gatewayRout信息封装RouteDefinition对象
     *
     * @param gatewayRout -
     * @return -
     */
    public static RouteDefinition parseGatewayRout2RouteDefinition(GatewayRout gatewayRout) {
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
                metadata = objectMapper.readValue(gatewayRout.getMetadata(), new TypeReference<Map<String, Object>>() {
                });
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
                throw new RuntimeException("could not parse metadata, e:" + e.getMessage());
            }
        }
        routeDefinition.setMetadata(metadata);
        return routeDefinition;
    }

    public static String parse2JsonStr(Object obj) {
        String json = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            json = objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException("parse obj to str failed, e=" + e.getMessage());
        }
        return json;
    }
}
