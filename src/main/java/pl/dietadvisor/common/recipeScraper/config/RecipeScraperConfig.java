package pl.dietadvisor.common.recipeScraper.config;

import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@ComponentScan(basePackages = {
        "pl.dietadvisor.common.recipeScraper",
        "pl.dietadvisor.common.shared.service.scrape"
})
@EnableRedisRepositories(basePackages = "pl.dietadvisor.common.recipeScraper.repository.redis")
@EnableDynamoDBRepositories(basePackages = {
        "pl.dietadvisor.common.product.repository.dynamodb",
        "pl.dietadvisor.common.recipe.repository.dynamodb"
})
public class RecipeScraperConfig {
}
