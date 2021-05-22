package pl.dietadvisor.common.product.enums;

import static java.lang.String.format;
import static java.util.Objects.isNull;

public enum ProductSource {
    CALORIES_CALCULATOR, USER;

    public static ProductSource parse(String rawState) {
        if (isNull(rawState)) {
            throw new RuntimeException("Can't parse field to enum. Field is empty.");
        }

        for (ProductSource state : ProductSource.values()) {
            if (state.name().equals(rawState)) {
                return state;
            }
        }

        throw new RuntimeException(format("Can't parse %s to enum.", rawState));
    }
}
