package pl.dietadvisor.common.dataInitializer.config;

import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "pl.dietadvisor.common.dataInitializer")
@EnableDynamoDBRepositories(basePackages = {
        "pl.dietadvisor.common.product.repository.dynamodb",
        "pl.dietadvisor.common.recipe.repository.dynamodb",
        "pl.dietadvisor.common.shoppingList.repository.dynamodb"
})
public class DataInitializerConfig {
}
