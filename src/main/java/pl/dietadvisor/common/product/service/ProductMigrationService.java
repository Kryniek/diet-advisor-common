package pl.dietadvisor.common.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dietadvisor.common.product.enums.ProductSource;
import pl.dietadvisor.common.product.model.dynamodb.Product;
import pl.dietadvisor.common.product.model.dynamodb.ProductMigration;
import pl.dietadvisor.common.product.model.dynamodb.ProductScrapeJob;
import pl.dietadvisor.common.product.model.dynamodb.ProductScrapeLog;
import pl.dietadvisor.common.shared.exception.custom.BadRequestException;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;
import static pl.dietadvisor.common.product.enums.ProductScrapeJobState.FINISHED;
import static pl.dietadvisor.common.product.enums.ProductScrapeJobState.MIGRATED;

@Service
@RequiredArgsConstructor
public class ProductMigrationService {
    private final ProductScrapeJobService productScrapeJobService;
    private final ProductScrapeLogService productScrapeLogService;
    private final ProductService productService;

    public ProductMigration getById(String jobId) {
        List<ProductScrapeLog> logs = productScrapeLogService.getByJobId(jobId);
        List<Product> existingProducts = productService.getByNames(logs.stream()
                .map(ProductScrapeLog::getName)
                .collect(toList()));
        List<String> existingProductsNames = existingProducts.stream()
                .map(Product::getName)
                .collect(toList());
        List<ProductScrapeLog> existingLogs = new ArrayList<>();
        List<ProductScrapeLog> nonExistingLogs = new ArrayList<>();

        logs.forEach(log -> {
            if (existingProductsNames.contains(log.getName())) {
                existingLogs.add(log);
            } else {
                nonExistingLogs.add(log);
            }
        });

        return ProductMigration.builder()
                .job(productScrapeJobService.getById(jobId))
                .existingLogs(existingLogs)
                .nonExistingLogs(nonExistingLogs)
                .build();
    }

    public List<Product> migrate(ProductMigration productMigration) {
        ProductScrapeJob job = productScrapeJobService.getById(productMigration.getJob().getId());
        validateIfJobCanBeMigrated(job);

        List<ProductScrapeLog> logs = getMigrationLogs(productMigration);
        validateIfProductsIntendedToMigrationNotExists(logs);

        List<Product> createdProducts = productService.create(logs.stream()
                .map(log -> Product.builder()
                        .source(ProductSource.parse(job.getSource().name()))
                        .name(log.getName())
                        .kcal(log.getKcal())
                        .proteins(log.getProteins())
                        .carbohydrates(log.getCarbohydrates())
                        .fats(log.getFats())
                        .build())
                .collect(toList()));

        job.setState(MIGRATED);
        productScrapeJobService.update(job);

        return createdProducts;
    }

    private void validateIfJobCanBeMigrated(ProductScrapeJob job) {
        if (!FINISHED.equals(job.getState())) {
            throw new BadRequestException("Job: %s has illegal state: %s. Migration is only available for jobs with state: %s",
                    job.getId(),
                    job.getState().name(),
                    FINISHED.name());
        }
    }

    private List<ProductScrapeLog> getMigrationLogs(ProductMigration productMigration) {
        if (productMigration.isSaveAll()) {
            return productScrapeLogService.getByJobId(productMigration.getJob().getId());
        }

        return productScrapeLogService.getByIds(productMigration.getMigrationLogsIds());
    }

    private void validateIfProductsIntendedToMigrationNotExists(List<ProductScrapeLog> logs) {
        List<Product> existingProducts = productService.getByNames(logs.stream().map(ProductScrapeLog::getName).collect(toList()));
        if (!isEmpty(existingProducts)) {
            throw new BadRequestException("Products: %s already exists.", existingProducts.stream()
                    .map(Product::getName)
                    .collect(toList()));
        }
    }
}
