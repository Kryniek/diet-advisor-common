package pl.dietadvisor.common.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dietadvisor.common.product.enums.ProductSource;
import pl.dietadvisor.common.product.model.ProductMigration;
import pl.dietadvisor.common.product.model.ProductMigrationResult;
import pl.dietadvisor.common.product.model.dynamodb.Product;
import pl.dietadvisor.common.product.model.dynamodb.ProductScrapeJob;
import pl.dietadvisor.common.product.model.dynamodb.ProductScrapeLog;
import pl.dietadvisor.common.shared.exception.custom.BadRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.groupingBy;
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
        List<ProductScrapeLog> logsWithDuplicates = productScrapeLogService.getByJobId(jobId);
        List<ProductScrapeLog> logs = getLogsWithoutDuplicates(logsWithDuplicates);
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

    public ProductMigrationResult migrate(ProductMigration productMigration) {
        ProductMigrationResult migrationResult = new ProductMigrationResult();
        ProductScrapeJob job = productScrapeJobService.getById(productMigration.getJob().getId());
        validateIfJobCanBeMigrated(job);

        List<ProductScrapeLog> logs = getLogsWithoutDuplicates(getMigrationLogs(productMigration));
        validateIfProductsIntendedToMigrationNotExists(migrationResult, logs);

        migrationResult.setMigratedProducts(
                productService.create(
                        logs.stream()
                                .map(log -> Product.builder()
                                        .source(ProductSource.parse(job.getSource().name()))
                                        .name(log.getName())
                                        .kcal(log.getKcal())
                                        .proteins(log.getProteins())
                                        .carbohydrates(log.getCarbohydrates())
                                        .fats(log.getFats())
                                        .build())
                                .collect(toList())));

        job.setState(MIGRATED);
        job.setMigratedProductsNumber(migrationResult.getMigratedProducts().size());
        productScrapeJobService.update(job);

        return migrationResult;
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

    private List<ProductScrapeLog> getLogsWithoutDuplicates(List<ProductScrapeLog> productScrapeLogs) {
        Map<String, List<ProductScrapeLog>> namesToCollectionsOfLogs = productScrapeLogs.stream()
                .collect(groupingBy(ProductScrapeLog::getName));

        List<String> duplicatedIds = new ArrayList<>();
        namesToCollectionsOfLogs.forEach((name, logs) -> {
            if (logs.size() > 1) {
                IntStream.range(1, logs.size())
                        .forEach(index ->
                                duplicatedIds.add(logs.get(index).getId()));
            }
        });

        return productScrapeLogs.stream()
                .filter(log -> !duplicatedIds.contains(log.getId()))
                .collect(toList());
    }

    private void validateIfProductsIntendedToMigrationNotExists(ProductMigrationResult migrationResult, List<ProductScrapeLog> logs) {
        List<Product> existingProducts = productService.getByNames(
                logs.stream()
                        .map(ProductScrapeLog::getName)
                        .collect(toList()));
        if (!isEmpty(existingProducts)) {
            migrationResult.setAlreadyExistingProducts(existingProducts);
            logs.removeIf(log ->
                    existingProducts.stream()
                            .anyMatch(product -> product.getName().equals(log.getName())));
        }
    }
}
