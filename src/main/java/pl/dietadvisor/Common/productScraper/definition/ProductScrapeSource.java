package pl.dietadvisor.Common.productScraper.definition;

import pl.dietadvisor.Common.productScraper.enums.ProductScrapeJobSource;
import pl.dietadvisor.Common.productScraper.model.ProductScrapeJob;
import pl.dietadvisor.Common.productScraper.model.ProductScrapeLog;

import java.util.List;

public interface ProductScrapeSource {
    ProductScrapeJobSource getSource();

    List<ProductScrapeLog> scrape(ProductScrapeJob productScrapeJob);
}
