package pl.dietadvisor.common.product.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.dietadvisor.common.product.model.dynamodb.ProductScrapeJob;
import pl.dietadvisor.common.product.model.dynamodb.ProductScrapeLog;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductMigration {
    private ProductScrapeJob job;
    private List<ProductScrapeLog> existingLogs;
    private List<ProductScrapeLog> nonExistingLogs;
    private List<String> migrationLogsIds;
    private boolean saveAll;
}
