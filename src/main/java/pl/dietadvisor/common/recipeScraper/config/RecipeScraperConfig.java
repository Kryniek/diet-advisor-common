package pl.dietadvisor.common.recipeScraper.config;

import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import static org.springframework.context.annotation.FilterType.ASPECTJ;

@Configuration
@ComponentScan(basePackages = {
        "pl.dietadvisor.common.recipeScraper",
        "pl.dietadvisor.common.shared.service.scrape",
        "pl.dietadvisor.common.productScraper.enums",
        "pl.dietadvisor.common.productScraper.model",
        "pl.dietadvisor.common.productScraper.service",
        "pl.dietadvisor.common.productScraper.util",
}, excludeFilters = {
        @ComponentScan.Filter(type = ASPECTJ, pattern = {
                "pl.dietadvisor.common.productScraper.service.scrape"
        })
})
@EnableRedisRepositories(basePackages = {
        "pl.dietadvisor.common.recipeScraper.repository.redis",
        "pl.dietadvisor.common.productScraper.repository.redis"
})
@EnableDynamoDBRepositories(basePackages = {
        "pl.dietadvisor.common.recipeScraper.repository.dynamodb",
        "pl.dietadvisor.common.productScraper.repository.dynamodb"
})
public class RecipeScraperConfig {
}
