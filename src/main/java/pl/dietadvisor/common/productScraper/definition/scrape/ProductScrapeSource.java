package pl.dietadvisor.common.productScraper.definition.scrape;

import pl.dietadvisor.common.product.enums.ProductScrapeJobSource;
import pl.dietadvisor.common.product.model.dynamodb.ProductScrapeJob;
import pl.dietadvisor.common.product.model.dynamodb.ProductScrapeLog;

import java.util.List;

public interface ProductScrapeSource {
    ProductScrapeJobSource getSource();

    List<ProductScrapeLog> scrape(ProductScrapeJob productScrapeJob);
}
