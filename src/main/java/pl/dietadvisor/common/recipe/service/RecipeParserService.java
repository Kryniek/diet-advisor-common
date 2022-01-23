package pl.dietadvisor.common.recipe.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import pl.dietadvisor.common.recipe.enums.RecipeMealType;
import pl.dietadvisor.common.recipe.model.CreateRecipeResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import static java.lang.String.format;
import static java.util.Map.entry;
import static java.util.Objects.isNull;
import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
public class RecipeParserService {
    public List<CreateRecipeResponse> parse(String rawRecipes) {
        List<CreateRecipeResponse> createRecipeResponses = new ArrayList<>();
        List<String> rawRecipesLines = List.of(rawRecipes.split("\\n"));

        Wrapper<CreateRecipeResponse> wrappedCreateRecipeResponse = new Wrapper<>(new CreateRecipeResponse());
        Wrapper<ParserDto> wrappedParserDto = new Wrapper<>(new ParserDto());
        rawRecipesLines.forEach(rawRecipesLine -> {
            if (lineShouldBeSkipped(rawRecipesLine)) {
                return;
            }
            if (isProteinShake(rawRecipesLine)) {
                createRecipeResponses.add(wrappedCreateRecipeResponse.getValue());
                wrappedCreateRecipeResponse.resetValue(new CreateRecipeResponse());
                wrappedParserDto.resetValue(new ParserDto());
            }

            CreateRecipeResponse createRecipeResponse = wrappedCreateRecipeResponse.getValue();
            ParserDto parserDto = wrappedParserDto.getValue();
            if (parserDto.isShouldFindRecipeFirstLine() && isFirstLineOfRecipe(rawRecipesLine)) {
                if (parserDto.isShouldGetRecipe() || parserDto.isShouldGetProducts()) {
                    createRecipeResponses.add(createRecipeResponse);
                    wrappedCreateRecipeResponse.resetValue(new CreateRecipeResponse());
                    wrappedParserDto.resetValue(new ParserDto());

                    createRecipeResponse = wrappedCreateRecipeResponse.getValue();
                    parserDto = wrappedParserDto.getValue();
                }
                parserDto.setShouldFindRecipeFirstLine(false);
                parserDto.setShouldFindRecipeName(true);
                parserDto.setShouldFindLineBeforeName(true);
                createRecipeResponse.setMealNumbers(List.of(RecipeMealType.parse(rawRecipesLine).getMealNumber()));
                return;
            }
            if (parserDto.isShouldFindRecipeName()) {
                if (parserDto.isShouldFindLineBeforeName()) {
                    if (isLineBeforeName(rawRecipesLine)) {
                        parserDto.setShouldFindLineBeforeName(false);
                    } else if (isRecipeWithoutName(rawRecipesLine)) {
                        parserDto.setShouldFindRecipeName(false);
                        parserDto.setShouldGetProducts(true);
                        parserDto.setShouldFindRecipeFirstLine(true);
                    }
                } else {
                    createRecipeResponse.setName(rawRecipesLine.trim());
                    parserDto.setShouldFindRecipeName(false);
                    parserDto.setShouldFindProducts(true);
                }
                return;
            }
            if (parserDto.isShouldFindProducts() && isLineBeforeProducts(rawRecipesLine)) {
                parserDto.setShouldFindProducts(false);
                parserDto.setShouldGetProducts(true);
                return;
            }
            if (parserDto.isShouldGetProducts()) {
                if (isLineBeforeRecipe(rawRecipesLine)) {
                    parserDto.setShouldGetProducts(false);
                    parserDto.setShouldGetRecipe(true);
                    parserDto.setShouldFindRecipeFirstLine(true);
                    return;
                }

                Entry<String, BigDecimal> productNameToQuantity = getProductNameToQuantity(rawRecipesLine);
                createRecipeResponse.getProductsNamesToQuantities()
                        .put(productNameToQuantity.getKey(), productNameToQuantity.getValue());
            }
            if (parserDto.isShouldGetRecipe()) {
                if (isAdditionalProductsList(rawRecipesLine)) {
                    parserDto.setFoundAdditionalProducts(true);
                }
                if (parserDto.isFoundAdditionalProducts()) {
                    return;
                }

                createRecipeResponse.setRecipe(
                        isNull(createRecipeResponse.getRecipe()) ?
                                getPartOfRecipe(rawRecipesLine)
                                : createRecipeResponse.getRecipe() + getPartOfRecipe(rawRecipesLine));
            }
        });
        createRecipeResponses.add(wrappedCreateRecipeResponse.getValue());

        return createRecipeResponses;
    }

    private boolean lineShouldBeSkipped(String rawRecipesLine) {
        return isBlank(rawRecipesLine)
                || rawRecipesLine.startsWith("Strona")
                || rawRecipesLine.startsWith("Dzień")
                || rawRecipesLine.startsWith("Kcal");
    }

    private boolean isFirstLineOfRecipe(String rawRecipesLine) {
        return RecipeMealType.DESCRIPTIONS
                .stream()
                .anyMatch(rawRecipesLine::startsWith);
    }

    private boolean isLineBeforeName(String rawRecipesLine) {
        return rawRecipesLine.contains("Dania");
    }

    private boolean isRecipeWithoutName(String rawRecipesLine) {
        return rawRecipesLine.contains("Produkty");
    }

    private boolean isLineBeforeProducts(String rawRecipesLine) {
        return rawRecipesLine.contains("Składniki");
    }

    private boolean isLineBeforeRecipe(String rawRecipesLine) {
        return rawRecipesLine.contains("Przygotowanie");
    }

    private Entry<String, BigDecimal> getProductNameToQuantity(String rawRecipesLine) {
        String[] splitNameToQuantity = rawRecipesLine.split(" x ");
        return entry(getProductName(splitNameToQuantity[0]), getProductQuantity(splitNameToQuantity[1]));
    }

    private String getProductName(String rawName) {
        int wordLastCharacterIndex = 0;
        for (int characterIndex = rawName.length() - 1; characterIndex > 0; characterIndex--) {
            char character = rawName.charAt(characterIndex);
            boolean isANumber = Character.isDigit(character);
            boolean isADot = character == '.';
            if (!isANumber && !isADot) {
                wordLastCharacterIndex = characterIndex;
                break;
            }
        }

        if (wordLastCharacterIndex == 0) {
            throw new RuntimeException(format("Cannot parse product name: %s", rawName));
        }

        String rawNameWithoutSuffix = rawName.substring(0, wordLastCharacterIndex);
        String name = rawNameWithoutSuffix.charAt(0) == '+' ? rawNameWithoutSuffix.substring(1) : rawNameWithoutSuffix;

        return name.trim();
    }

    private BigDecimal getProductQuantity(String rawQuantity) {
        StringBuilder rawNumber = new StringBuilder();

        boolean foundANumber = false;
        for (int characterIndex = 0; characterIndex < rawQuantity.length(); characterIndex++) {
            char character = rawQuantity.charAt(characterIndex);
            boolean isANumber = Character.isDigit(character);
            boolean isADot = character == '.';
            if (isANumber || isADot) {
                foundANumber = true;
                rawNumber.append(character);
            } else if (foundANumber) {
                break;
            }
        }

        if (isBlank(rawNumber.toString())) {
            throw new RuntimeException(format("Cannot parse product quantity: %s", rawQuantity));
        }

        return new BigDecimal(rawNumber.toString());
    }

    private String getPartOfRecipe(String rawRecipesLine) {
        return rawRecipesLine.trim() + " ";
    }

    private boolean isProteinShake(String rawRecipesLine) {
        return rawRecipesLine.contains("Shake białkowy");
    }

    private boolean isAdditionalProductsList(String rawRecipesLine) {
        return rawRecipesLine.contains("Produkty");
    }

    @Getter
    @Setter
    private static class ParserDto {
        private boolean shouldFindRecipeFirstLine = true;
        private boolean shouldFindRecipeName;
        private boolean shouldFindLineBeforeName;
        private boolean shouldFindProducts;
        private boolean shouldGetProducts;
        private boolean shouldGetRecipe;
        private boolean foundAdditionalProducts;
    }

    @AllArgsConstructor
    @Getter
    private static class Wrapper<T> {
        private T value;

        public void resetValue(T value) {
            this.value = value;
        }
    }
}