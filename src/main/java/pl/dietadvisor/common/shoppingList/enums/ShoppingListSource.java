package pl.dietadvisor.common.shoppingList.enums;

import static java.lang.String.format;
import static java.util.Objects.isNull;

public enum ShoppingListSource {
    GENERATED, USER;

    public static ShoppingListSource parse(String rawState) {
        if (isNull(rawState)) {
            throw new RuntimeException("Can't parse field to enum. Field is empty.");
        }

        for (ShoppingListSource state : ShoppingListSource.values()) {
            if (state.name().equals(rawState)) {
                return state;
            }
        }

        throw new RuntimeException(format("Can't parse %s to enum.", rawState));
    }
}
