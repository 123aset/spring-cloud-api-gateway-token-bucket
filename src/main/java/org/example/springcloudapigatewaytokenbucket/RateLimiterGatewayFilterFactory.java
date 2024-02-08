package org.example.springcloudapigatewaytokenbucket;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimiterGatewayFilterFactory extends AbstractGatewayFilterFactory<RateLimiterConfig> {
    ConcurrentHashMap<String, Integer> concurrentHashMap;
    int limit;
    private int period;

    public RateLimiterGatewayFilterFactory() {

    }

    public RateLimiterGatewayFilterFactory(int limit, int period) {
        concurrentHashMap = new ConcurrentHashMap<>();
        this.limit = limit;
        this.period = period;
        new Thread(() -> {
            while (true) {
                for (String s : concurrentHashMap.keySet()) {
                    concurrentHashMap.put(s, limit);
                }
                try {
                    Thread.sleep(period * 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    @Override
    public GatewayFilter apply(RateLimiterConfig config) {
        return (exchange, chain) -> {
            String hostAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            if (concurrentHashMap.get(hostAddress) != null) {
                Integer integer = concurrentHashMap.get(hostAddress);
                if (integer == 0) {
                    exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                    return exchange.getResponse().setComplete();
                }
                concurrentHashMap.put(hostAddress, integer - 1);
            } else {
                concurrentHashMap.put(hostAddress, limit - 1);
            }
            return chain.filter(exchange);
        };
    }
}