package pl.dietadvisor.common.dataInitializer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import pl.dietadvisor.common.productScraper.model.dynamodb.Product;
import pl.dietadvisor.common.productScraper.model.dynamodb.ProductScrapeJob;
import pl.dietadvisor.common.productScraper.model.dynamodb.ProductScrapeLog;
import pl.dietadvisor.common.productScraper.repository.dynamodb.ProductRepository;
import pl.dietadvisor.common.productScraper.repository.dynamodb.ProductScrapeJobRepository;
import pl.dietadvisor.common.productScraper.repository.dynamodb.ProductScrapeLogRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static java.lang.String.format;
import static org.springframework.util.CollectionUtils.isEmpty;

@Configuration
@RequiredArgsConstructor
@Profile("dev")
@Log4j2
public class InitializeDataRunner implements ApplicationRunner {
    private final ProductScrapeJobRepository productScrapeJobRepository;
    private final ProductScrapeLogRepository productScrapeLogRepository;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        insertProductScrapeJobs();
        insertProductScrapeLogs();
        insertProducts();
    }

    private void insertProductScrapeJobs() throws IOException {
        if (isEmpty((List<ProductScrapeJob>) productScrapeJobRepository.findAll())) {
            String rawProductScrapeJobs = getFileContent("product-scrape-jobs");
            List<ProductScrapeJob> productScrapeJobs = objectMapper.readValue(rawProductScrapeJobs,
                    objectMapper
                            .getTypeFactory()
                            .constructCollectionType(List.class, ProductScrapeJob.class));

            Integer insertedProductScrapeJobsSize = ((List<ProductScrapeJob>) productScrapeJobRepository.saveAll(productScrapeJobs)).size();
            log.info("Inserted test product scrape jobs: {}", insertedProductScrapeJobsSize);
        }
    }

    private void insertProductScrapeLogs() throws IOException {
        if (isEmpty((List<ProductScrapeLog>) productScrapeLogRepository.findAll())) {
            String rawProductScrapeLogs = getFileContent("product-scrape-logs");
            List<ProductScrapeLog> productScrapeLogs = objectMapper.readValue(rawProductScrapeLogs,
                    objectMapper
                            .getTypeFactory()
                            .constructCollectionType(List.class, ProductScrapeLog.class));

            Integer insertedProductScrapeLogsSize = ((List<ProductScrapeLog>) productScrapeLogRepository.saveAll(productScrapeLogs)).size();
            log.info("Inserted test product scrape logs: {}", insertedProductScrapeLogsSize);
        }
    }

    private void insertProducts() throws IOException {
        if (isEmpty((List<Product>) productRepository.findAll())) {
            String rawProducts = getFileContent("products");
            List<Product> products = objectMapper.readValue(rawProducts,
                    objectMapper
                            .getTypeFactory()
                            .constructCollectionType(List.class, Product.class));

            Integer insertedProductsSize = ((List<Product>) productRepository.saveAll(products)).size();
            log.info("Inserted test products: {}", insertedProductsSize);
        }
    }

    private String getFileContent(String fileName) throws IOException {
        File file = new ClassPathResource(format("data/%s.json", fileName)).getFile();
        return Files.readString(file.toPath());
    }
}
