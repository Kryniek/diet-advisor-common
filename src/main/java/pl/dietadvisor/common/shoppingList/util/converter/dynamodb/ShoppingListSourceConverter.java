package pl.dietadvisor.common.shoppingList.util.converter.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import pl.dietadvisor.common.shoppingList.enums.ShoppingListSource;

public class ShoppingListSourceConverter implements DynamoDBTypeConverter<String, ShoppingListSource> {
    @Override
    public String convert(ShoppingListSource source) {
        return source.name();
    }

    @Override
    public ShoppingListSource unconvert(String rawSource) {
        return ShoppingListSource.parse(rawSource);
    }
}
