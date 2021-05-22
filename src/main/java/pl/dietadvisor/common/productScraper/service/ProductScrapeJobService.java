package pl.dietadvisor.common.productScraper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dietadvisor.common.productScraper.model.dynamodb.ProductScrapeJob;
import pl.dietadvisor.common.productScraper.repository.dynamodb.ProductScrapeJobRepository;

import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.Objects.nonNull;
import static org.apache.logging.log4j.util.Strings.isNotBlank;
import static pl.dietadvisor.common.productScraper.enums.ProductScrapeJobState.CREATED;

@Service
@RequiredArgsConstructor
public class ProductScrapeJobService {
    private final ProductScrapeJobRepository repository;

    public List<ProductScrapeJob> get() {
        return (List<ProductScrapeJob>) repository.findAll();
    }

    public ProductScrapeJob getById(String id) {
        return repository.findById(id).orElseThrow();
    }

    public ProductScrapeJob create(ProductScrapeJob productScrapeJob) {
        return repository.save(ProductScrapeJob.builder()
                .state(CREATED)
                .source(productScrapeJob.getSource())
                .createdAt(now())
                .build());
    }

    public ProductScrapeJob update(ProductScrapeJob productScrapeJob) {
        ProductScrapeJob existingProductScrapeJob = getById(productScrapeJob.getId());
        if (nonNull(productScrapeJob.getState())) {
            existingProductScrapeJob.setState(productScrapeJob.getState());
        }
        if (isNotBlank(productScrapeJob.getErrorMessage())) {
            existingProductScrapeJob.setErrorMessage(productScrapeJob.getErrorMessage());
        }
        if (nonNull(productScrapeJob.getScrapedProductsNumber())) {
            existingProductScrapeJob.setScrapedProductsNumber(productScrapeJob.getScrapedProductsNumber());
        }

        existingProductScrapeJob.setUpdatedAt(now());

        return repository.save(existingProductScrapeJob);
    }
}
