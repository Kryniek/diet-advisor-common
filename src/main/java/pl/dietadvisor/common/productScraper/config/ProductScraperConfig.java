package pl.dietadvisor.common.productScraper.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "pl.dietadvisor.common.productScraper"
})
public class ProductScraperConfig {
}
