package pl.dietadvisor.common.recipeScraper.enums;

import static java.lang.String.format;
import static java.util.Objects.isNull;

public enum RecipeScrapeJobState {
    CREATED, IN_PROGRESS, FINISHED, FINISHED_WITH_ERRORS, FAILED, CANCELLED, MIGRATED;

    public static RecipeScrapeJobState parse(String rawState) {
        if (isNull(rawState)) {
            throw new RuntimeException("Can't parse field to enum. Field is empty.");
        }

        for (RecipeScrapeJobState state : RecipeScrapeJobState.values()) {
            if (state.name().equals(rawState)) {
                return state;
            }
        }

        throw new RuntimeException(format("Can't parse %s to enum.", rawState));
    }
}
