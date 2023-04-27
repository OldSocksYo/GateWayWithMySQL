package com.example.gatewaywithmysql.dao;

import com.example.gatewaywithmysql.entity.GatewayRout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : yong
 * create at: 2023/4/15 14:08
 * @Description :
 **/

@Repository
public interface GatewayRouteRepository extends JpaRepository<GatewayRout, Long> {
    public List<GatewayRout> findGatewayRoutsByIsDelete(Integer isDelete);
}
