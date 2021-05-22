package pl.dietadvisor.common.recipeScraper.util.converter.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import pl.dietadvisor.common.recipeScraper.enums.RecipeSource;

public class RecipeSourceConverter implements DynamoDBTypeConverter<String, RecipeSource> {
    @Override
    public String convert(RecipeSource source) {
        return source.name();
    }

    @Override
    public RecipeSource unconvert(String rawSource) {
        return RecipeSource.parse(rawSource);
    }
}
