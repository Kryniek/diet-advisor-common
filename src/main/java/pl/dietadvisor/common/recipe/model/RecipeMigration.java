package pl.dietadvisor.common.recipe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.dietadvisor.common.recipe.model.dynamodb.RecipeScrapeJob;
import pl.dietadvisor.common.recipe.model.dynamodb.RecipeScrapeLog;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipeMigration {
    private RecipeScrapeJob job;
    private List<RecipeScrapeLog> existingLogs;
    private List<RecipeScrapeLog> nonExistingLogs;
    private List<String> migrationLogsIds;
    private boolean saveAll;
}
