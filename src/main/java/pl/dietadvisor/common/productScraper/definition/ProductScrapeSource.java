package pl.dietadvisor.common.productScraper.definition;

import pl.dietadvisor.common.productScraper.enums.ProductScrapeJobSource;
import pl.dietadvisor.common.productScraper.model.ProductScrapeJob;
import pl.dietadvisor.common.productScraper.model.ProductScrapeLog;

import java.util.List;

public interface ProductScrapeSource {
    ProductScrapeJobSource getSource();

    List<ProductScrapeLog> scrape(ProductScrapeJob productScrapeJob);
}
