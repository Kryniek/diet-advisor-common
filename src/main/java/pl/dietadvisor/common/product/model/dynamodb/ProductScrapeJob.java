package pl.dietadvisor.common.product.model.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.dietadvisor.common.product.enums.ProductScrapeJobSource;
import pl.dietadvisor.common.product.enums.ProductScrapeJobState;
import pl.dietadvisor.common.product.util.converter.dynamodb.ProductScrapeJobSourceConverter;
import pl.dietadvisor.common.product.util.converter.dynamodb.ProductScrapeJobStateConverter;
import pl.dietadvisor.common.shared.util.converter.dynamodb.LocalDateTimeConverter;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "product-scrape-jobs")
public class ProductScrapeJob {
    @DynamoDBHashKey
    @DynamoDBAutoGeneratedKey
    private String id;
    @DynamoDBTypeConverted(converter = ProductScrapeJobStateConverter.class)
    private ProductScrapeJobState state;
    @DynamoDBTypeConverted(converter = ProductScrapeJobSourceConverter.class)
    private ProductScrapeJobSource source;
    private String errorMessage;
    private Integer scrapedProductsNumber;
    private Integer migratedProductsNumber;
    @DynamoDBTypeConvertedJson
    private Map<String, Object> additionalFields;
    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    private LocalDateTime createdAt;
    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    private LocalDateTime updatedAt;
}
