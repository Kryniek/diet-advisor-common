package pl.dietadvisor.Common.shared.config.kafka.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.ProducerListener;

@Slf4j
public class KafkaProducerListener<Type> implements ProducerListener<String, Type> {
    @Override
    public void onSuccess(ProducerRecord<String, Type> producerRecord, RecordMetadata recordMetadata) {
        log.info("Sent message. Topic: {}, message: {}.", producerRecord.topic(), producerRecord.value());
    }

    @Override
    public void onError(ProducerRecord<String, Type> producerRecord, RecordMetadata recordMetadata, Exception exception) {
        log.info("Failed to sent message. Topic: {}, message: {}.", producerRecord.topic(), producerRecord.value());
    }
}