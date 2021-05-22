package pl.dietadvisor.common.api.controller.recipes;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dietadvisor.common.recipe.model.RecipeMigration;
import pl.dietadvisor.common.recipe.model.RecipeMigrationResult;
import pl.dietadvisor.common.recipe.service.RecipeMigrationService;
import pl.dietadvisor.common.shared.exception.custom.BadRequestException;

import javax.validation.constraints.NotBlank;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.util.CollectionUtils.isEmpty;

@RestController
@RequiredArgsConstructor
@RequestMapping("recipes/migrations")
public class RecipeMigrationController {
    private final RecipeMigrationService service;

    @GetMapping("{jobId}")
    public ResponseEntity<RecipeMigration> getById(@PathVariable @NotBlank String jobId) {
        return ResponseEntity.ok(service.getById(jobId));
    }

    @PostMapping
    public ResponseEntity<RecipeMigrationResult> migrate(@RequestBody @NonNull RecipeMigration recipeMigration) {
        validateMigrationRequestData(recipeMigration);

        return new ResponseEntity<>(service.migrate(recipeMigration), CREATED);
    }

    private void validateMigrationRequestData(@RequestBody @NonNull RecipeMigration recipeMigration) {
        requireNonNull(recipeMigration.getJob(), "Job must be set.");
        requireNonNull(recipeMigration.getJob().getId(), "Job id must be set.");

        if (!recipeMigration.isSaveAll()) {
            requireNonNull(recipeMigration.getMigrationLogsIds(), "Migration logs ids must be set.");
            if (isEmpty(recipeMigration.getMigrationLogsIds())) {
                throw new BadRequestException("Migration logs ids must be not empty.");
            }
            boolean isAnyIdBlank = recipeMigration.getMigrationLogsIds()
                    .stream()
                    .anyMatch(String::isBlank);
            if (isAnyIdBlank) {
                throw new BadRequestException("Migration logs ids must be not blank.");
            }
        }
    }
}
