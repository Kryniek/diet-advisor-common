package pl.dietadvisor.common.productScraper.enums;

import static java.lang.String.format;
import static java.util.Objects.isNull;

public enum ProductScrapeJobState {
    CREATED, IN_PROGRESS, FINISHED, FINISHED_WITH_ERRORS, FAILED, CANCELLED, MIGRATED;

    public static ProductScrapeJobState parse(String rawState) {
        if (isNull(rawState)) {
            throw new RuntimeException("Can't parse field to enum. Field is empty.");
        }

        for (ProductScrapeJobState state : ProductScrapeJobState.values()) {
            if (state.name().equals(rawState)) {
                return state;
            }
        }

        throw new RuntimeException(format("Can't parse %s to enum.", rawState));
    }
}
