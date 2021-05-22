package pl.dietadvisor.common.product.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.dietadvisor.common.product.model.dynamodb.Product;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductMigrationResult {
    private List<Product> migratedProducts;
    private List<Product> alreadyExistingProducts;
}
