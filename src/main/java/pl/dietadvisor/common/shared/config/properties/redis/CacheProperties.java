package pl.dietadvisor.common.shared.config.properties.redis;

import lombok.Data;

@Data
public class CacheProperties {
    private String ttlSeconds;
}
