package pl.dietadvisor.common.shared.config.properties.kafka;

import lombok.Data;

@Data
public class ConsumerProperties {
    private String groupId;
    private String autoOffsetReset;
}
