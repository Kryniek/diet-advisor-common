package pl.dietadvisor.common.recipeScraper.enums;

import static java.lang.String.format;
import static java.util.Objects.isNull;

public enum RecipeScrapeJobSource {
    STRENGTH_FACTORY;

    public static RecipeScrapeJobSource parse(String rawState) {
        if (isNull(rawState)) {
            throw new RuntimeException("Can't parse field to enum. Field is empty.");
        }

        for (RecipeScrapeJobSource state : RecipeScrapeJobSource.values()) {
            if (state.name().equals(rawState)) {
                return state;
            }
        }

        throw new RuntimeException(format("Can't parse %s to enum.", rawState));
    }
}
