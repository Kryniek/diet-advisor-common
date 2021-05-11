package pl.dietadvisor.Common.productScraper.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "pl.dietadvisor.Common.productScraper"
})
public class ProductScraperConfig {
}
