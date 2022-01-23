package pl.dietadvisor.common.recipe.service.sampleData;

import pl.dietadvisor.common.recipe.model.CreateRecipeResponse;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;

public class ExpectedCreateRecipeResponseSampleData {
    public static List<CreateRecipeResponse> oneDayOneRecipe() {
        return List.of(getOneRecipeFirstRecipe(1));
    }

    public static List<CreateRecipeResponse> oneDayFourRecipes() {
        return List.of(
                getOneRecipeFirstRecipe(1),
                getOneRecipeSecondRecipe(2),
                getOneRecipeFirstRecipe(3),
                getOneRecipeFirstRecipe(5));
    }

    public static List<CreateRecipeResponse> twoDaysEightRecipes() {
        return List.of(
                getOneRecipeFirstRecipe(1),
                getOneRecipeFirstRecipe(2),
                getOneRecipeFirstRecipe(3),
                getOneRecipeFirstRecipe(5),
                getOneRecipeFirstRecipe(1),
                getOneRecipeFirstRecipe(2),
                getOneRecipeFirstRecipe(3),
                getOneRecipeFirstRecipe(5));
    }

    private static CreateRecipeResponse getOneRecipeFirstRecipe(Integer mealNumber) {
        return CreateRecipeResponse.builder()
                .mealNumbers(List.of(mealNumber))
                .name("Czekoladowy budyń jaglany z borówkami")
                .productsNamesToQuantities(ofEntries(
                        entry("Kasza jaglana", new BigDecimal("50")),
                        entry("Mleko 2%", new BigDecimal("161")),
                        entry("Kakao (proszek bez dodatku cukru)", new BigDecimal("5.1")),
                        entry("Miód", new BigDecimal("16")),
                        entry("Borówka amerykańska", new BigDecimal("100")),
                        entry("Skyr borówkowy", new BigDecimal("150")),
                        entry("Orzechy włoskie", new BigDecimal("30"))))
                .recipe(new StringBuilder()
                        .append("Kaszę opłukać, przelać wrzątkiem, by straciła charakterystyczną gorycz. Wlać mleko do kaszy, gotować na ")
                        .append("niewielkim ogniu, aż kasza będzie miękka (w razie potrzeby podlać kaszę wodą). Ugotowaną kaszę wymieszać z ")
                        .append("kakao i miodem, zmiksować na gładki budyń. Borówkę umyć, zblendować na mus. Budyń przelać do miseczki, w ")
                        .append("jego środek włożyć mus borówkowy i skyr. (lub całe jagody- jeśli nie chcemy ich blendować). Posypać ")
                        .append("orzechami. ")
                        .toString())
                .build();
    }

    private static CreateRecipeResponse getOneRecipeSecondRecipe(Integer mealNumber) {
        return CreateRecipeResponse.builder()
                .mealNumbers(List.of(mealNumber))
                .productsNamesToQuantities(ofEntries(
                        entry("Koktajl z białkiem serwatkowym Piątnica", new BigDecimal("256")),
                        entry("Winogrona", new BigDecimal("210")),
                        entry("Orzechy włoskie", new BigDecimal("30"))))
                .build();
    }
}
