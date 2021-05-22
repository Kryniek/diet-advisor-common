package pl.dietadvisor.common.productScraper.service.scrape;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.dietadvisor.common.productScraper.definition.scrape.ProductScrapeSource;
import pl.dietadvisor.common.product.enums.ProductScrapeJobSource;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductScrapeSourceFactory {
    private final List<ProductScrapeSource> scrapeSources;

    public ProductScrapeSource getSource(ProductScrapeJobSource productScrapeJobSource) {
        return scrapeSources.stream()
                .filter(scrapeSource -> scrapeSource.getSource().equals(productScrapeJobSource))
                .findFirst()
                .orElseThrow();
    }
}
