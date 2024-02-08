package org.example.springcloudapigatewaytokenbucket;

import org.springframework.cloud.gateway.support.Configurable;

public class RateLimiterConfig implements Configurable<String> {

    @Override
    public Class<String> getConfigClass() {
        return String.class;
    }

    @Override
    public String newConfig() {
        return "Authorization";
    }

}