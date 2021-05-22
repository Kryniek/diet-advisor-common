package pl.dietadvisor.common.recipeScraper.model.dynamodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
