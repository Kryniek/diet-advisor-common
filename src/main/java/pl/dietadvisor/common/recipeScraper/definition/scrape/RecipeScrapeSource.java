package pl.dietadvisor.common.recipeScraper.definition.scrape;

import pl.dietadvisor.common.recipe.enums.RecipeScrapeJobSource;
import pl.dietadvisor.common.recipe.model.dynamodb.RecipeScrapeJob;
import pl.dietadvisor.common.recipe.model.dynamodb.RecipeScrapeLog;

import java.util.List;

public interface RecipeScrapeSource {
    RecipeScrapeJobSource getSource();

    List<RecipeScrapeLog> scrape(RecipeScrapeJob recipeScrapeJob);
}
