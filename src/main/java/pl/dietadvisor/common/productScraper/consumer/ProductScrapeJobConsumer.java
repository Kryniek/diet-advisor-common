package pl.dietadvisor.common.productScraper.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.dietadvisor.common.productScraper.definition.scrape.ProductScrapeSource;
import pl.dietadvisor.common.productScraper.model.dynamodb.ProductScrapeJob;
import pl.dietadvisor.common.productScraper.model.dynamodb.ProductScrapeLog;
import pl.dietadvisor.common.productScraper.service.ProductScrapeJobService;
import pl.dietadvisor.common.productScraper.service.ProductScrapeLogService;
import pl.dietadvisor.common.productScraper.service.scrape.ProductScrapeSourceFactory;
import pl.dietadvisor.common.shared.util.aop.KafkaConsumerLog;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;
import static pl.dietadvisor.common.productScraper.enums.ProductScrapeJobState.*;

@Component
@RequiredArgsConstructor
@Log4j2
public class ProductScrapeJobConsumer {
    private final ProductScrapeJobService productScrapeJobService;
    private final ProductScrapeLogService productScrapeLogService;
    private final ProductScrapeSourceFactory productScrapeSourceFactory;

    @KafkaConsumerLog
    @KafkaListener(topics = "${diet-advisor.kafka.topic.products-scrape-job}",
            groupId = "${diet-advisor.kafka.consumer.group-id}")
    public void listen(ProductScrapeJob productScrapeJob) {
        if (!isJobValidToProcess(productScrapeJob.getId())) {
            log.error("Product scrape job: {} can't be processed.", productScrapeJob.getId());
            return;
        }

        try {
            log.info("Started scraping product scrape job: {}", productScrapeJob.getId());
            productScrapeJob = productScrapeJobService.update(ProductScrapeJob.builder()
                    .id(productScrapeJob.getId())
                    .state(IN_PROGRESS)
                    .build());
            List<ProductScrapeLog> scrapeLogs = getScrapeLogs(productScrapeJob);
            if (!isEmpty(scrapeLogs)) {
                productScrapeLogService.createAll(scrapeLogs);
            }

            productScrapeJob.setScrapedProductsNumber(scrapeLogs.size());
            productScrapeJobService.update(productScrapeJob);
        } catch (Exception exception) {
            log.error("Unexpected exception thrown when processing source: {}, product scrape job id: {}, message: {}",
                    productScrapeJob.getSource(),
                    productScrapeJob.getId(),
                    exception.getMessage());
            log.error(exception);

            productScrapeJob.setState(FAILED);
            productScrapeJob.setErrorMessage(exception.getMessage());
            productScrapeJobService.update(productScrapeJob);
        } finally {
            log.info("Finished scraping product scrape job: {}", productScrapeJob.getId());
        }
    }

    private boolean isJobValidToProcess(String id) {
        ProductScrapeJob job = productScrapeJobService.getById(id);
        return CREATED.equals(job.getState());
    }

    private List<ProductScrapeLog> getScrapeLogs(ProductScrapeJob productScrapeJob) {
        ProductScrapeSource scrapeSource = productScrapeSourceFactory.getSource(productScrapeJob.getSource());
        List<ProductScrapeLog> scrapeLogs = scrapeSource.scrape(productScrapeJob);

        if (FAILED.equals(productScrapeJob.getState())) {
            if (isEmpty(scrapeLogs)) {
                return List.of();
            }

            productScrapeJob.setState(FINISHED_WITH_ERRORS);
        } else if (IN_PROGRESS.equals(productScrapeJob.getState())) {
            productScrapeJob.setState(FINISHED);
        }

        return scrapeLogs
                .stream()
                .map(sl -> sl.toBuilder()
                        .jobId(productScrapeJob.getId())
                        .build())
                .collect(toList());
    }
}
