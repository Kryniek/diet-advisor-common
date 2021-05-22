package pl.dietadvisor.common.product.util.converter.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import pl.dietadvisor.common.product.enums.ProductSource;

public class ProductSourceConverter implements DynamoDBTypeConverter<String, ProductSource> {
    @Override
    public String convert(ProductSource source) {
        return source.name();
    }

    @Override
    public ProductSource unconvert(String rawSource) {
        return ProductSource.parse(rawSource);
    }
}
