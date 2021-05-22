package pl.dietadvisor.common.recipe.util.converter.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import pl.dietadvisor.common.recipe.enums.RecipeSource;

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
