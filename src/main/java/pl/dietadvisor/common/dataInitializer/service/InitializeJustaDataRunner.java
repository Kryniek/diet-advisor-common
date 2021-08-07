package pl.dietadvisor.common.dataInitializer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import pl.dietadvisor.common.recipe.model.dynamodb.Recipe;
import pl.dietadvisor.common.recipe.repository.dynamodb.RecipeRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Configuration
@RequiredArgsConstructor
@Profile("dev-justa")
@Log4j2
public class InitializeJustaDataRunner implements ApplicationRunner {
    private final RecipeRepository recipeRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        insertRecipes();
    }

    private void insertRecipes() throws IOException {
        String rawRecipes = getFileContent("recipes");
        List<Recipe> recipes = objectMapper.readValue(rawRecipes,
                objectMapper
                        .getTypeFactory()
                        .constructCollectionType(List.class, Recipe.class));

        Integer insertedRecipesSize = ((List<Recipe>) recipeRepository.saveAll(recipes.stream()
                .peek(recipe -> {
                    if (!recipe.getName().startsWith("Justa - ")) {
                        recipe.setName(format("Justa - %s", recipe.getName()));
                    }
                })
                .collect(toList())))
                .size();
        log.info("Inserted test Justa recipes: {}", insertedRecipesSize);
    }

    private String getFileContent(String fileName) throws IOException {
        File file = new ClassPathResource(format("justa-data/%s.json", fileName)).getFile();
        return Files.readString(file.toPath());
    }
}
