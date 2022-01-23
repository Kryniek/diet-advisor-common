package pl.dietadvisor.common.recipe.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

@AllArgsConstructor
@Getter
public enum RecipeMealType {
    BREAKFAST(1, "Śniadanie"),
    SECOND_BREAKFAST(2, "II Śniadanie"),
    LUNCH(3, "Obiad"),
    DINNER(5, "Kolacja");

    public static final List<String> DESCRIPTIONS = getEnumDescriptions();

    private Integer mealNumber;
    private String description;

    public static RecipeMealType parse(String rawType) {
        if (isNull(rawType)) {
            throw new RuntimeException("Can't parse field to enum. Field is empty.");
        }

        for (RecipeMealType type : RecipeMealType.values()) {
            if (rawType.trim().startsWith(type.getDescription())) {
                return type;
            }
        }

        throw new RuntimeException(format("Can't parse %s to enum.", rawType));
    }

    private static List<String> getEnumDescriptions() {
        return Stream.of(RecipeMealType.values())
                .map(RecipeMealType::getDescription)
                .collect(toList());
    }
}
