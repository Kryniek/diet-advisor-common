package pl.dietadvisor.Common.shared.config.properties.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("diet-advisor.redis")
@Data
public class RedisProperties {
    private CacheProperties cache;
}
