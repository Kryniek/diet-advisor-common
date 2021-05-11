package pl.dietadvisor.common.productScraper.service.productScrapeSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Service;
import pl.dietadvisor.common.productScraper.definition.ProductScrapeSource;
import pl.dietadvisor.common.productScraper.enums.ProductScrapeJobSource;
import pl.dietadvisor.common.productScraper.model.ProductScrapeJob;
import pl.dietadvisor.common.productScraper.model.ProductScrapeLog;
import pl.dietadvisor.common.productScraper.service.WebDriverService;
import pl.dietadvisor.common.productScraper.service.redis.ProductScrapeJobCancelRedisService;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;
import static pl.dietadvisor.common.productScraper.enums.ProductScrapeJobSource.CALORIES_CALCULATOR;
import static pl.dietadvisor.common.productScraper.enums.ProductScrapeJobState.CANCELLED;
import static pl.dietadvisor.common.productScraper.enums.ProductScrapeJobState.FAILED;

@Service
@RequiredArgsConstructor
@Log4j2
public class CaloriesCalculatorSource implements ProductScrapeSource {
    private final WebDriverService webDriverService;
    private final ProductScrapeJobCancelRedisService productScrapeJobCancelRedisService;

    @Override
    public ProductScrapeJobSource getSource() {
        return CALORIES_CALCULATOR;
    }

    @Override
    public List<ProductScrapeLog> scrape(ProductScrapeJob productScrapeJob) {
        List<ProductScrapeLog> scrapeLogs = new ArrayList<>();
        RemoteWebDriver webDriver = null;

        try {
            webDriver = openSite();

            Integer page = 1;
            boolean hasNextPage;

            do {
                log.info("Job id: {}, scraping page: {}", productScrapeJob.getId(), page);

                if (productScrapeJobCancelRedisService.isCancelled(productScrapeJob.getId())) {
                    log.info("Job: {} has been canceled on page: {}", productScrapeJob.getId(), page);
                    productScrapeJob.setState(CANCELLED);
                    return scrapeLogs;
                }

                scrapePage(webDriver, scrapeLogs);
                hasNextPage = goToNextPage(webDriver);
                page++;
            } while (hasNextPage);
        } catch (InterruptedException | MalformedURLException exception) {
            log.error("Exception thrown when processing source: {}, product scrape job id: {}, message: {}",
                    getSource(),
                    productScrapeJob.getId(),
                    exception.getMessage());
            log.error(exception);

            productScrapeJob.setState(FAILED);
            productScrapeJob.setErrorMessage(exception.getMessage());
        } finally {
            if (nonNull(webDriver)) {
                webDriver.quit();
            }
        }
        return scrapeLogs;
    }

    private RemoteWebDriver openSite() throws MalformedURLException, InterruptedException {
        RemoteWebDriver webDriver = webDriverService.create("https://kalkulatorkalorii.net/tabela-kalorii");
        webDriverService.sleep(2);

        return webDriver;
    }

    private void scrapePage(RemoteWebDriver webDriver, List<ProductScrapeLog> scrapeLogs) {
        List<WebElement> trElements = webDriver.findElements(By.cssSelector("#content > div > div.container-fluid > div.container-fluid > section > div > div.span9 > div.tab-content > table > tbody > tr"));
        trElements.stream()
                .map(this::getScrapeLog)
                .forEach(scrapeLogs::add);
    }

    private ProductScrapeLog getScrapeLog(WebElement trElement) {
        return ProductScrapeLog.builder()
                .name(trElement.findElement(By.cssSelector("td:nth-child(1) > a")).getText().trim())
                .kcal(Integer.valueOf(trElement.findElement(By.cssSelector("td:nth-child(2)")).getText().trim()))
                .proteins(parseToBigDecimal(trElement.findElement(By.cssSelector("td:nth-child(3)")).getText()))
                .carbohydrates(parseToBigDecimal(trElement.findElement(By.cssSelector("td:nth-child(4)")).getText()))
                .fats(parseToBigDecimal(trElement.findElement(By.cssSelector("td:nth-child(5)")).getText()))
                .build();
    }

    private BigDecimal parseToBigDecimal(String rawValue) {
        return new BigDecimal(rawValue.trim().replaceFirst(",", "."));
    }

    private boolean goToNextPage(RemoteWebDriver webDriver) throws InterruptedException {
        String nextPageButtonSelector = "#content > div > div.container-fluid > div.container-fluid > section > div > div.span9 > div.pagination-2 > ul > li:last-child > a";
        if (webDriverService.isElementExist(webDriver, By.cssSelector(nextPageButtonSelector + " > i"))) {
            WebElement nextPageButton = webDriver.findElement(By.cssSelector(nextPageButtonSelector));

            webDriver.executeScript("arguments[0].click()", nextPageButton);
            webDriverService.sleep(1);
            return true;
        }

        return false;
    }
}