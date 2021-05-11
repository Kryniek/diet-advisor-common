package pl.dietadvisor.Common.productScraper.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.dietadvisor.Common.shared.util.converter.dynamodb.LocalDateTimeConverter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "product-scrape-logs")
public class ProductScrapeLog {
    @DynamoDBHashKey
    @DynamoDBAutoGeneratedKey
    private String id;
    private String jobId;
    private String name;
    private Integer kcal;
    private BigDecimal proteins;
    private BigDecimal carbohydrates;
    private BigDecimal fats;
    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    private LocalDateTime createdAt;
}
