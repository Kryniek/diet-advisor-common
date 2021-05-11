package pl.dietadvisor.common.shared.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import pl.dietadvisor.common.shared.config.kafka.util.KafkaProducerListener;
import pl.dietadvisor.common.shared.config.properties.kafka.KafkaProperties;

import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {
    private final KafkaProperties kafkaProperties;

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        KafkaTemplate<String, Object> kafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(getProducerConfigs()));
        kafkaTemplate.setProducerListener(new KafkaProducerListener<>());

        return kafkaTemplate;
    }

    private Map<String, Object> getProducerConfigs() {
        return Map.ofEntries(
                Map.entry(BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers()),
                Map.entry(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class),
                Map.entry(CLIENT_ID_CONFIG, kafkaProperties.getClient().getId()),
                Map.entry(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class)
        );
    }
}
