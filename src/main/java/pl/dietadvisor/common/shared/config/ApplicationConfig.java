package pl.dietadvisor.common.shared.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

import static org.springframework.context.annotation.FilterType.ASPECTJ;

@Configuration
@EnableKafka
@EnableCaching
@ComponentScan(basePackages = {
        "pl.dietadvisor.common.shared"
}, excludeFilters = {
        @Filter(type = ASPECTJ, pattern = "pl.dietadvisor.common.shared.service.scrape")
})
public class ApplicationConfig {
}
