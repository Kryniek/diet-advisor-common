package pl.dietadvisor.Common.shared.config.properties.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("diet-advisor.kafka")
@Data
public class KafkaProperties {
    private String bootstrapServers;
    private ClientProperties client;
    private ConsumerProperties consumer;
    private TopicProperties topic;
}
