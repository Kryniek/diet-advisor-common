package pl.dietadvisor.common.recipe.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static pl.dietadvisor.common.recipe.service.sampleData.ExpectedCreateRecipeResponseSampleData.*;

class RecipeParserServiceTest {
    private RecipeParserService service = new RecipeParserService();

    @Test
    @DisplayName("Parse 1 Day 1 Recipe")
    void shouldParse1Day1Recipe() throws IOException {
        assertThat(service.parse(getFileContent("1Day1Recipe")))
                .isEqualTo(oneDayOneRecipe());
    }

    @Test
    @DisplayName("Parse 1 Day 4 Recipes")
    void shouldParse1Day4Recipes() throws IOException {
        assertThat(service.parse(getFileContent("1Day4Recipes")))
                .isEqualTo(oneDayFourRecipes());
    }

    @Test
    @DisplayName("Parse 2 Days 8 Recipes")
    void shouldParse2Days8Recipes() throws IOException {
        assertThat(service.parse(getFileContent("2Days8Recipes")))
                .isEqualTo(twoDaysEightRecipes());
    }

    private String getFileContent(String fileName) throws IOException {
        File file = new ClassPathResource(format("pl/dietadvisor/common/recipe/service/recipeParserServiceTest/%s.txt", fileName)).getFile();
        return Files.readString(file.toPath());
    }
}