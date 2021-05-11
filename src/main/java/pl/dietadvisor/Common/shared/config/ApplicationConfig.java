package pl.dietadvisor.Common.shared.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
@EnableCaching
@ComponentScan(basePackages = {
        "pl.dietadvisor.Common.shared"
})
public class ApplicationConfig {
}
