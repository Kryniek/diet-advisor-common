package pl.dietadvisor.common.recipeScraper.util.converter.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import pl.dietadvisor.common.recipeScraper.enums.RecipeScrapeJobSource;

public class RecipeScrapeJobSourceConverter implements DynamoDBTypeConverter<String, RecipeScrapeJobSource> {
    @Override
    public String convert(RecipeScrapeJobSource source) {
        return source.name();
    }

    @Override
    public RecipeScrapeJobSource unconvert(String rawSource) {
        return RecipeScrapeJobSource.parse(rawSource);
    }
}
