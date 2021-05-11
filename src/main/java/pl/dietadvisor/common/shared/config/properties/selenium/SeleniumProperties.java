package pl.dietadvisor.common.shared.config.properties.selenium;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("diet-advisor.selenium")
@Data
public class SeleniumProperties {
    private String host;
}
