package pl.dietadvisor.common.recipeScraper.model.redis;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("RecipeScrapeJobCancel")
@Data
@Builder
public class RecipeScrapeJobCancel {
    private String id;
}
