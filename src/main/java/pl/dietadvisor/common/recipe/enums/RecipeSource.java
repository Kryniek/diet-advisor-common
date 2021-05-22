package pl.dietadvisor.common.recipe.enums;

import static java.lang.String.format;
import static java.util.Objects.isNull;

public enum RecipeSource {
    STRENGTH_FACTORY, USER;

    public static RecipeSource parse(String rawState) {
        if (isNull(rawState)) {
            throw new RuntimeException("Can't parse field to enum. Field is empty.");
        }

        for (RecipeSource state : RecipeSource.values()) {
            if (state.name().equals(rawState)) {
                return state;
            }
        }

        throw new RuntimeException(format("Can't parse %s to enum.", rawState));
    }
}
