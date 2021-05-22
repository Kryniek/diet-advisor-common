package pl.dietadvisor.common.api.config;

import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import static org.springframework.context.annotation.FilterType.ASPECTJ;

@Configuration
@ComponentScan(basePackages = {
        "pl.dietadvisor.common.api",
        "pl.dietadvisor.common.productScraper.enums",
        "pl.dietadvisor.common.productScraper.model",
        "pl.dietadvisor.common.productScraper.service",
        "pl.dietadvisor.common.productScraper.util",
        "pl.dietadvisor.common.recipeScraper.enums",
        "pl.dietadvisor.common.recipeScraper.model",
        "pl.dietadvisor.common.recipeScraper.service",
        "pl.dietadvisor.common.recipeScraper.util"
}, excludeFilters = {
        @Filter(type = ASPECTJ, pattern = {
                "pl.dietadvisor.common.productScraper.service.scrape",
                "pl.dietadvisor.common.recipeScraper.service.scrape"
        })
})
@EnableDynamoDBRepositories(basePackages = {
        "pl.dietadvisor.common.productScraper.repository.dynamodb",
        "pl.dietadvisor.common.recipeScraper.repository.dynamodb"
})
@EnableRedisRepositories(basePackages = {
        "pl.dietadvisor.common.productScraper.repository.redis",
        "pl.dietadvisor.common.recipeScraper.repository.redis"
})
public class ApiConfig {
}
