package pl.dietadvisor.common.product.model.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.dietadvisor.common.product.enums.ProductSource;
import pl.dietadvisor.common.shared.util.converter.dynamodb.LocalDateTimeConverter;
import pl.dietadvisor.common.product.util.converter.dynamodb.ProductSourceConverter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "products")
public class Product implements Serializable {
    @DynamoDBHashKey
    @DynamoDBAutoGeneratedKey
    private String id;
    @DynamoDBTypeConverted(converter = ProductSourceConverter.class)
    private ProductSource source;
    private String name;
    private Integer kcal;
    private BigDecimal proteins;
    private BigDecimal carbohydrates;
    private BigDecimal fats;
    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    private LocalDateTime createdAt;
    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    private LocalDateTime updatedAt;
}