package pl.dietadvisor.common.recipe.util.converter.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import pl.dietadvisor.common.recipe.enums.RecipeScrapeJobState;

public class RecipeScrapeJobStateConverter implements DynamoDBTypeConverter<String, RecipeScrapeJobState> {
    @Override
    public String convert(RecipeScrapeJobState state) {
        return state.name();
    }

    @Override
    public RecipeScrapeJobState unconvert(String rawState) {
        return RecipeScrapeJobState.parse(rawState);
    }
}
