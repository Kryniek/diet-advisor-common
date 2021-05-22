package pl.dietadvisor.common.recipeScraper.service.scrape;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.dietadvisor.common.recipeScraper.definition.scrape.RecipeScrapeSource;
import pl.dietadvisor.common.recipe.enums.RecipeScrapeJobSource;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecipeScrapeSourceFactory {
    private final List<RecipeScrapeSource> scrapeSources;

    public RecipeScrapeSource getSource(RecipeScrapeJobSource recipeScrapeJobSource) {
        return scrapeSources.stream()
                .filter(scrapeSource -> scrapeSource.getSource().equals(recipeScrapeJobSource))
                .findFirst()
                .orElseThrow();
    }
}
