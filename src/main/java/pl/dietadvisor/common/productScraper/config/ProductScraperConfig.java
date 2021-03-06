package pl.dietadvisor.common.productScraper.config;

import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@ComponentScan(basePackages = {
        "pl.dietadvisor.common.productScraper",
        "pl.dietadvisor.common.shared.service.scrape"
})
@EnableRedisRepositories(basePackages = "pl.dietadvisor.common.productScraper.repository.redis")
@EnableDynamoDBRepositories(basePackages = "pl.dietadvisor.common.product.repository.dynamodb")
public class ProductScraperConfig {
}
