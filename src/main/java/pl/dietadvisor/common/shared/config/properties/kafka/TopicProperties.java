package pl.dietadvisor.common.shared.config.properties.kafka;

import lombok.Data;

@Data
public class TopicProperties {
    private String productsScrapeJob;
    private String recipesScrapeJob;
}
