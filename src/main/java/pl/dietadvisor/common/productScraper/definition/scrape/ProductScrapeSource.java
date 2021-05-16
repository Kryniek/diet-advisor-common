package pl.dietadvisor.common.productScraper.definition.scrape;

import pl.dietadvisor.common.productScraper.enums.ProductScrapeJobSource;
import pl.dietadvisor.common.productScraper.model.dynamodb.ProductScrapeJob;
import pl.dietadvisor.common.productScraper.model.dynamodb.ProductScrapeLog;

import java.util.List;

public interface ProductScrapeSource {
    ProductScrapeJobSource getSource();

    List<ProductScrapeLog> scrape(ProductScrapeJob productScrapeJob);
}
