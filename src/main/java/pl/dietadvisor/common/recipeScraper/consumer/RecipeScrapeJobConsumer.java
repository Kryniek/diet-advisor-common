package pl.dietadvisor.common.recipeScraper.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.dietadvisor.common.recipeScraper.definition.scrape.RecipeScrapeSource;
import pl.dietadvisor.common.recipe.model.dynamodb.RecipeScrapeJob;
import pl.dietadvisor.common.recipe.model.dynamodb.RecipeScrapeLog;
import pl.dietadvisor.common.recipe.service.RecipeScrapeJobService;
import pl.dietadvisor.common.recipe.service.RecipeScrapeLogService;
import pl.dietadvisor.common.recipeScraper.service.scrape.RecipeScrapeSourceFactory;
import pl.dietadvisor.common.shared.util.aop.KafkaConsumerLog;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;
import static pl.dietadvisor.common.recipe.enums.RecipeScrapeJobState.*;

@Component
@RequiredArgsConstructor
@Log4j2
public class RecipeScrapeJobConsumer {
    private final RecipeScrapeJobService recipeScrapeJobService;
    private final RecipeScrapeLogService recipeScrapeLogService;
    private final RecipeScrapeSourceFactory recipeScrapeSourceFactory;

    @KafkaConsumerLog
    @KafkaListener(topics = "${diet-advisor.kafka.topic.recipes-scrape-job}",
            groupId = "${diet-advisor.kafka.consumer.group-id}")
    public void listen(RecipeScrapeJob recipeScrapeJob) {
        if (!isJobValidToProcess(recipeScrapeJob.getId())) {
            log.error("Recipe scrape job: {} can't be processed.", recipeScrapeJob.getId());
            return;
        }

        try {
            log.info("Started scraping recipe scrape job: {}", recipeScrapeJob.getId());
            recipeScrapeJob = recipeScrapeJobService.update(RecipeScrapeJob.builder()
                    .id(recipeScrapeJob.getId())
                    .state(IN_PROGRESS)
                    .build());
            List<RecipeScrapeLog> scrapeLogs = getScrapeLogs(recipeScrapeJob);
            if (!isEmpty(scrapeLogs)) {
                recipeScrapeLogService.createAll(scrapeLogs);
            }

            recipeScrapeJob.setScrapedRecipesNumber(scrapeLogs.size());
            recipeScrapeJobService.update(recipeScrapeJob);
        } catch (Exception exception) {
            log.error("Unexpected exception thrown when processing source: {}, recipe scrape job id: {}, message: {}",
                    recipeScrapeJob.getSource(),
                    recipeScrapeJob.getId(),
                    exception.getMessage());
            log.error(exception);

            recipeScrapeJob.setState(FAILED);
            recipeScrapeJob.setErrorMessage(exception.getMessage());
            recipeScrapeJobService.update(recipeScrapeJob);
        } finally {
            log.info("Finished scraping recipe scrape job: {}", recipeScrapeJob.getId());
        }
    }

    private boolean isJobValidToProcess(String id) {
        RecipeScrapeJob job = recipeScrapeJobService.getById(id);
        return CREATED.equals(job.getState());
    }

    private List<RecipeScrapeLog> getScrapeLogs(RecipeScrapeJob recipeScrapeJob) {
        RecipeScrapeSource scrapeSource = recipeScrapeSourceFactory.getSource(recipeScrapeJob.getSource());
        List<RecipeScrapeLog> scrapeLogs = scrapeSource.scrape(recipeScrapeJob);

        if (FAILED.equals(recipeScrapeJob.getState())) {
            if (isEmpty(scrapeLogs)) {
                return List.of();
            }

            recipeScrapeJob.setState(FINISHED_WITH_ERRORS);
        } else if (IN_PROGRESS.equals(recipeScrapeJob.getState())) {
            recipeScrapeJob.setState(FINISHED);
        }

        return scrapeLogs
                .stream()
                .map(sl -> sl.toBuilder()
                        .jobId(recipeScrapeJob.getId())
                        .build())
                .collect(toList());
    }
}
