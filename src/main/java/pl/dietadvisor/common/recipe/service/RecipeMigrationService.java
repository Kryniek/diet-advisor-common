package pl.dietadvisor.common.recipe.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dietadvisor.common.recipe.enums.RecipeSource;
import pl.dietadvisor.common.recipe.model.RecipeMigration;
import pl.dietadvisor.common.recipe.model.RecipeMigrationResult;
import pl.dietadvisor.common.recipe.model.dynamodb.Recipe;
import pl.dietadvisor.common.recipe.model.dynamodb.RecipeScrapeJob;
import pl.dietadvisor.common.recipe.model.dynamodb.RecipeScrapeLog;
import pl.dietadvisor.common.shared.exception.custom.BadRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;
import static pl.dietadvisor.common.recipe.enums.RecipeScrapeJobState.FINISHED;
import static pl.dietadvisor.common.recipe.enums.RecipeScrapeJobState.MIGRATED;

@Service
@RequiredArgsConstructor
public class RecipeMigrationService {
    private final RecipeScrapeJobService recipeScrapeJobService;
    private final RecipeScrapeLogService recipeScrapeLogService;
    private final RecipeService recipeService;

    public RecipeMigration getById(String jobId) {
        List<RecipeScrapeLog> logsWithDuplicates = recipeScrapeLogService.getByJobId(jobId);
        List<RecipeScrapeLog> logs = getLogsWithoutDuplicates(logsWithDuplicates);
        List<Recipe> existingRecipes = recipeService.getByNames(logs.stream()
                .map(RecipeScrapeLog::getName)
                .collect(toList()));
        List<String> existingRecipesNames = existingRecipes.stream()
                .map(Recipe::getName)
                .collect(toList());
        List<RecipeScrapeLog> existingLogs = new ArrayList<>();
        List<RecipeScrapeLog> nonExistingLogs = new ArrayList<>();

        logs.forEach(log -> {
            if (existingRecipesNames.contains(log.getName())) {
                existingLogs.add(log);
            } else {
                nonExistingLogs.add(log);
            }
        });

        return RecipeMigration.builder()
                .job(recipeScrapeJobService.getById(jobId))
                .existingLogs(existingLogs)
                .nonExistingLogs(nonExistingLogs)
                .build();
    }

    public RecipeMigrationResult migrate(RecipeMigration recipeMigration) {
        RecipeMigrationResult migrationResult = new RecipeMigrationResult();
        RecipeScrapeJob job = recipeScrapeJobService.getById(recipeMigration.getJob().getId());
        validateIfJobCanBeMigrated(job);

        List<RecipeScrapeLog> logs = getLogsWithoutDuplicates(getMigrationLogs(recipeMigration));
        validateIfProductsIntendedToMigrationNotExists(migrationResult, logs);

        migrationResult.setMigratedRecipes(
                recipeService.create(
                        logs.stream()
                                .map(log -> Recipe.builder()
                                        .source(RecipeSource.parse(job.getSource().name()))
                                        .mealNumbers(log.getMealNumbers())
                                        .name(log.getName())
                                        .productsNamesToQuantities(log.getProductsNamesToQuantities())
                                        .recipe(log.getRecipe())
                                        .build())
                                .collect(toList())));

        job.setState(MIGRATED);
        job.setMigratedRecipesNumber(migrationResult.getMigratedRecipes().size());
        recipeScrapeJobService.update(job);

        return migrationResult;
    }

    private void validateIfJobCanBeMigrated(RecipeScrapeJob job) {
        if (!FINISHED.equals(job.getState())) {
            throw new BadRequestException("Job: %s has illegal state: %s. Migration is only available for jobs with state: %s",
                    job.getId(),
                    job.getState().name(),
                    FINISHED.name());
        }
    }

    private List<RecipeScrapeLog> getMigrationLogs(RecipeMigration recipeMigration) {
        if (recipeMigration.isSaveAll()) {
            return recipeScrapeLogService.getByJobId(recipeMigration.getJob().getId());
        }

        return recipeScrapeLogService.getByIds(recipeMigration.getMigrationLogsIds());
    }

    private List<RecipeScrapeLog> getLogsWithoutDuplicates(List<RecipeScrapeLog> recipeScrapeLogs) {
        Map<String, List<RecipeScrapeLog>> namesToCollectionsOfLogs = recipeScrapeLogs.stream()
                .collect(groupingBy(RecipeScrapeLog::getName));

        List<String> duplicatedIds = new ArrayList<>();
        namesToCollectionsOfLogs.forEach((name, logs) -> {
            if (logs.size() > 1) {
                IntStream.range(1, logs.size())
                        .forEach(index ->
                                duplicatedIds.add(logs.get(index).getId()));
            }
        });

        return recipeScrapeLogs.stream()
                .filter(log -> !duplicatedIds.contains(log.getId()))
                .collect(toList());
    }

    private void validateIfProductsIntendedToMigrationNotExists(RecipeMigrationResult migrationResult, List<RecipeScrapeLog> logs) {
        List<Recipe> existingRecipes = recipeService.getByNames(
                logs.stream()
                        .map(RecipeScrapeLog::getName)
                        .collect(toList()));
        if (!isEmpty(existingRecipes)) {
            migrationResult.setAlreadyExistingRecipes(existingRecipes);
            logs.removeIf(log ->
                    existingRecipes.stream()
                            .anyMatch(recipe -> recipe.getName().equals(log.getName())));
        }
    }
}
