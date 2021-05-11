package pl.dietadvisor.common.shared.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import pl.dietadvisor.common.shared.config.properties.kafka.KafkaProperties;

import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {
    private final KafkaProperties kafkaProperties;

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Object>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(
                new DefaultKafkaConsumerFactory<>(
                        getDefaultConsumerConfigs()));
        factory.setMessageConverter(new StringJsonMessageConverter());

        return factory;
    }

    private Map<String, Object> getDefaultConsumerConfigs() {
        return Map.ofEntries(
                Map.entry(BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers()),
                Map.entry(GROUP_ID_CONFIG, kafkaProperties.getConsumer().getGroupId()),
                Map.entry(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class),
                Map.entry(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class),
                Map.entry(CLIENT_ID_CONFIG, kafkaProperties.getClient().getId())
        );
    }
}
