package pl.dietadvisor.common.productScraper.service.scrape.productScrapeSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Service;
import pl.dietadvisor.common.product.enums.ProductScrapeJobSource;
import pl.dietadvisor.common.product.model.dynamodb.ProductScrapeJob;
import pl.dietadvisor.common.product.model.dynamodb.ProductScrapeLog;
import pl.dietadvisor.common.productScraper.definition.scrape.ProductScrapeSource;
import pl.dietadvisor.common.productScraper.service.redis.ProductScrapeJobCancelRedisService;
import pl.dietadvisor.common.shared.config.properties.selenium.SeleniumProperties;
import pl.dietadvisor.common.shared.service.scrape.WebDriverService;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.nonNull;
import static org.apache.logging.log4j.util.Strings.isNotBlank;
import static pl.dietadvisor.common.product.enums.ProductScrapeJobSource.STRENGTH_FACTORY;
import static pl.dietadvisor.common.product.enums.ProductScrapeJobState.CANCELLED;
import static pl.dietadvisor.common.product.enums.ProductScrapeJobState.FAILED;

@Service
@RequiredArgsConstructor
@Log4j2
public class StrengthFactoryProductSource implements ProductScrapeSource {
    private final WebDriverService webDriverService;
    private final SeleniumProperties seleniumProperties;
    private final ProductScrapeJobCancelRedisService productScrapeJobCancelRedisService;

    @Override
    public ProductScrapeJobSource getSource() {
        return STRENGTH_FACTORY;
    }

    @Override
    public List<ProductScrapeLog> scrape(ProductScrapeJob productScrapeJob) {
        List<ProductScrapeLog> scrapeLogs = new ArrayList<>();
        RemoteWebDriver webDriver = null;

        try {
            webDriver = openSite();
            login(webDriver);
            goToProductsSite(webDriver);
            createElementThatStoresFetchProducts(webDriver);

            char requestedFirstLetter = ((String)
                    productScrapeJob
                            .getAdditionalFields()
                            .getOrDefault("firstLetter", 'a'))
                    .charAt(0);
            char requestedLastLetter = ((String)
                    productScrapeJob
                            .getAdditionalFields()
                            .getOrDefault("lastLetter", 'z'))
                    .charAt(0);

            for (char firstLetter = requestedFirstLetter; firstLetter <= requestedLastLetter; firstLetter++) {
                for (char secondLetter = 'a'; secondLetter <= 'z'; secondLetter++) {
                    for (char thirdLetter = 'a'; thirdLetter <= 'z'; thirdLetter++) {
                        String query = String.valueOf(firstLetter) + secondLetter + thirdLetter;
                        log.info("Scraping job: {} with query: {}. Three levels depth.", productScrapeJob.getId(), query);

                        if (productScrapeJobCancelRedisService.isCancelled(productScrapeJob.getId())) {
                            log.info("Job: {} has been canceled on query: {}", productScrapeJob.getId(), query);
                            productScrapeJob.setState(CANCELLED);
                            return scrapeLogs;
                        }

                        fetchProducts(webDriver, query);
                        scrapeLogs(webDriver, scrapeLogs, productScrapeJob.getId(), query);
                    }
                }
            }
        } catch (Exception exception) {
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
        RemoteWebDriver webDriver = webDriverService.create("https://www.fabrykasily.pl/");
        webDriverService.sleep(2);

        return webDriver;
    }

    private void login(RemoteWebDriver webDriver) throws InterruptedException {
        WebElement loginButton = webDriver.findElement(By.cssSelector("body > header:nth-child(4) > div > div > a.fs-btn--sign-in"));
        loginButton.click();
        webDriverService.sleep(2);

        WebElement emailInput = webDriver.findElement(By.cssSelector("#email"));
        emailInput.sendKeys(seleniumProperties.getStrengthFactory().getEmail());

        WebElement passwordInput = webDriver.findElement(By.cssSelector("#password"));
        passwordInput.sendKeys(seleniumProperties.getStrengthFactory().getPassword());

        WebElement submitButton = webDriver.findElement(By.cssSelector("body > div.main-container.body-plan-rel > div > div > div > form > div:nth-child(4) > div > button"));
        submitButton.submit();
        webDriverService.sleep(2);
    }

    private void goToProductsSite(RemoteWebDriver webDriver) throws InterruptedException {
        webDriver.navigate().to("https://www.fabrykasily.pl/konto/dziennik/dietetyczny");
        webDriverService.sleep(2);
    }

    private void createElementThatStoresFetchProducts(RemoteWebDriver webDriver) throws InterruptedException {
        String createElementFunction = "(function(){\n" +
                "    let myData = document.createElement('DIV');\n" +
                "    myData.setAttribute(\"id\", \"myData\");\n" +
                "    document.body.appendChild(myData);\n" +
                "})()";
        webDriver.executeScript(createElementFunction);
        webDriverService.sleep(2);
    }

    private void fetchProducts(RemoteWebDriver webDriver, String query) throws InterruptedException {
        String fetch = "fetch(\"https://www.fabrykasily.pl/konto/dziennik/dietetyczny/autocomplete\", {\n" +
                "  \"headers\": {\n" +
                "    \"accept\": \"application/json, text/javascript, */*; q=0.01\",\n" +
                "    \"accept-language\": \"pl-PL,pl;q=0.9,en-US;q=0.8,en;q=0.7\",\n" +
                "    \"content-type\": \"application/x-www-form-urlencoded; charset=UTF-8\",\n" +
                "    \"sec-ch-ua\": \"\\\" Not A;Brand\\\";v=\\\"99\\\", \\\"Chromium\\\";v=\\\"90\\\", \\\"Google Chrome\\\";v=\\\"90\\\"\",\n" +
                "    \"sec-ch-ua-mobile\": \"?0\",\n" +
                "    \"sec-fetch-dest\": \"empty\",\n" +
                "    \"sec-fetch-mode\": \"cors\",\n" +
                "    \"sec-fetch-site\": \"same-origin\",\n" +
                "    \"x-requested-with\": \"XMLHttpRequest\"\n" +
                "  },\n" +
                "  \"referrer\": \"https://www.fabrykasily.pl/konto/dziennik/dietetyczny\",\n" +
                "  \"referrerPolicy\": \"strict-origin-when-cross-origin\",\n" +
                "  \"body\": \"q=" + query + "\",\n" +
                "  \"method\": \"POST\",\n" +
                "  \"mode\": \"cors\",\n" +
                "  \"credentials\": \"include\"\n" +
                "}).then(res => res.json()).then(res => {\n" +
                "    console.log(res);\n" +
                "    let myData = document.getElementById('myData');\n" +
                "    myData.innerHTML = '';\n" +
                "    if(res.suggestions.length === 0){\n" +
                "        return;\n" +
                "    }\n" +
                "    res.suggestions.forEach((suggestion, index) => {\n" +
                "        let product = document.createElement('DIV');\n" +
                "        product.setAttribute(\"id\", \"myProduct\" + index);\n" +
                "\n" +
                "        let name = document.createElement('P');\n" +
                "        name.setAttribute(\"class\", \"myName\");\n" +
                "        name.textContent = suggestion.value;\n" +
                "        \n" +
                "        let kcal = document.createElement('P');\n" +
                "        kcal.setAttribute(\"class\", \"myKcal\");\n" +
                "        kcal.textContent = suggestion.per_gram.calories;\n" +
                "\n" +
                "        let proteins = document.createElement('P');\n" +
                "        proteins.setAttribute(\"class\", \"myProteins\");\n" +
                "        proteins.textContent = suggestion.per_gram.proteins;\n" +
                "\n" +
                "        let carbohydrates = document.createElement('P');\n" +
                "        carbohydrates.setAttribute(\"class\", \"myCarbohydrates\");\n" +
                "        carbohydrates.textContent = suggestion.per_gram.carbohydrates;\n" +
                "\n" +
                "        let fats = document.createElement('P');\n" +
                "        fats.setAttribute(\"class\", \"myFats\");\n" +
                "        fats.textContent = suggestion.per_gram.fats;\n" +
                "        \n" +
                "        product.appendChild(fats);\n" +
                "        product.appendChild(carbohydrates);\n" +
                "        product.appendChild(proteins);\n" +
                "        product.appendChild(kcal);\n" +
                "        product.appendChild(name);\n" +
                "        myData.appendChild(product);\n" +
                "    });\n" +
                "});";

        webDriver.executeScript(fetch);
        webDriverService.sleep(2);
    }

    private void scrapeLogs(RemoteWebDriver webDriver, List<ProductScrapeLog> scrapeLogs, String productScrapeJobId, String query) {
        By myDataElementsSelector = By.cssSelector("#myData > div");
        if (!webDriverService.areElementsExists(webDriver, myDataElementsSelector)) {
            log.info("Scraping job: {}. No results for query: {}", productScrapeJobId, query);
            return;
        }

        List<WebElement> myDataElements = webDriver.findElements(myDataElementsSelector);
        try {
            myDataElements.forEach(element -> {
                String name = element.findElement(By.cssSelector(".myName")).getText().trim();
                String rawKcal = element.findElement(By.cssSelector(".myKcal")).getText().trim();
                String rawProteins = element.findElement(By.cssSelector(".myProteins")).getText().trim();
                String rawCarbohydrates = element.findElement(By.cssSelector(".myCarbohydrates")).getText().trim();
                String rawFats = element.findElement(By.cssSelector(".myFats")).getText().trim();

                if (areAllFieldsNotEmpty(name, rawKcal, rawProteins, rawCarbohydrates, rawFats)) {
                    boolean isDuplicated = scrapeLogs
                            .stream()
                            .anyMatch(log -> log.getName().equals(name));
                    if (isDuplicated) {
                        return;
                    }

                    int kcal = new BigDecimal(rawKcal).multiply(new BigDecimal("100")).intValue();
                    BigDecimal proteins = new BigDecimal(rawProteins).multiply(new BigDecimal("100"));
                    BigDecimal carbohydrates = new BigDecimal(rawCarbohydrates).multiply(new BigDecimal("100"));
                    BigDecimal fats = new BigDecimal(rawFats).multiply(new BigDecimal("100"));

                    if (!areAllFieldsZeros(kcal, proteins, carbohydrates, fats)) {
                        scrapeLogs.add(ProductScrapeLog.builder()
                                .name(name)
                                .kcal(kcal)
                                .proteins(proteins)
                                .carbohydrates(carbohydrates)
                                .fats(fats)
                                .build());
                    }
                }
            });
        } catch (StaleElementReferenceException exception) {
            log.info("StaleElementReferenceException in job: {} with query: {}. Skipped.", productScrapeJobId, query);
        }
    }

    private boolean areAllFieldsNotEmpty(String name, String kcal, String proteins, String carbohydrates, String fats) {
        return isNotBlank(name) && isNotBlank(kcal) && isNotBlank(proteins) && isNotBlank(carbohydrates) && isNotBlank(fats);
    }

    private boolean areAllFieldsZeros(int kcal, BigDecimal proteins, BigDecimal carbohydrates, BigDecimal fats) {
        return kcal == 0 && proteins.compareTo(ZERO) == 0 && carbohydrates.compareTo(ZERO) == 0 && fats.compareTo(ZERO) == 0;
    }
}
