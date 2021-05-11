package pl.dietadvisor.Common.shared.config.properties.aws;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("diet-advisor.aws")
@Data
public class AwsProperties {
    private UserCredentialsProperties userCredentials;
    private String region;
    private String url;
}
