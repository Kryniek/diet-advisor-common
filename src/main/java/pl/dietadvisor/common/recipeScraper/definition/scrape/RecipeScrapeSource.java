package pl.dietadvisor.common.recipeScraper.definition.scrape;

import pl.dietadvisor.common.recipeScraper.enums.RecipeScrapeJobSource;
import pl.dietadvisor.common.recipeScraper.model.dynamodb.RecipeScrapeJob;
import pl.dietadvisor.common.recipeScraper.model.dynamodb.RecipeScrapeLog;

import java.util.List;

public interface RecipeScrapeSource {
    RecipeScrapeJobSource getSource();

    List<RecipeScrapeLog> scrape(RecipeScrapeJob recipeScrapeJob);
}
