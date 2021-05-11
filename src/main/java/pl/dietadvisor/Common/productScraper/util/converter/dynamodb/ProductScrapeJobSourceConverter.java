package pl.dietadvisor.Common.productScraper.util.converter.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import lombok.NonNull;
import pl.dietadvisor.Common.productScraper.enums.ProductScrapeJobSource;

import javax.validation.constraints.NotBlank;

public class ProductScrapeJobSourceConverter implements DynamoDBTypeConverter<String, ProductScrapeJobSource> {
    @Override
    public String convert(@NonNull ProductScrapeJobSource source) {
        return source.name();
    }

    @Override
    public ProductScrapeJobSource unconvert(@NotBlank String rawSource) {
        return ProductScrapeJobSource.parse(rawSource);
    }
}
