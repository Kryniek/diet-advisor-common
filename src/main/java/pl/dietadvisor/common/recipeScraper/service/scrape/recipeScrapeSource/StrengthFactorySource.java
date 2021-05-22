package pl.dietadvisor.common.recipeScraper.service.scrape.recipeScrapeSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Service;
import pl.dietadvisor.common.recipeScraper.definition.scrape.RecipeScrapeSource;
import pl.dietadvisor.common.recipe.enums.RecipeScrapeJobSource;
import pl.dietadvisor.common.recipe.model.dynamodb.RecipeScrapeJob;
import pl.dietadvisor.common.recipe.model.dynamodb.RecipeScrapeLog;
import pl.dietadvisor.common.recipeScraper.service.redis.RecipeScrapeJobCancelRedisService;
import pl.dietadvisor.common.shared.config.properties.selenium.SeleniumProperties;
import pl.dietadvisor.common.shared.service.scrape.WebDriverService;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;
import static pl.dietadvisor.common.recipe.enums.RecipeScrapeJobSource.STRENGTH_FACTORY;
import static pl.dietadvisor.common.recipe.enums.RecipeScrapeJobState.CANCELLED;
import static pl.dietadvisor.common.recipe.enums.RecipeScrapeJobState.FAILED;

/*
 * To check new products macro and kcal: https://www.fabrykasily.pl/konto/dziennik/dietetyczny
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class StrengthFactorySource implements RecipeScrapeSource {
    private final WebDriverService webDriverService;
    private final RecipeScrapeJobCancelRedisService recipeScrapeJobCancelRedisService;
    private final SeleniumProperties seleniumProperties;

    @Override
    public RecipeScrapeJobSource getSource() {
        return STRENGTH_FACTORY;
    }

    @Override
    public List<RecipeScrapeLog> scrape(RecipeScrapeJob recipeScrapeJob) {
        List<RecipeScrapeLog> scrapeLogs = new ArrayList<>();
        RemoteWebDriver webDriver = null;

        try {
            webDriver = openSite();
            login(webDriver);
            goToDietTab(webDriver);
            clickFirstDietDayButton(webDriver);

            boolean hasNextDayButtonEnabled;
            do {
                String scrapingDay = getScrapingDate(webDriver);
                log.info("Job id: {}, scraping day: {}", recipeScrapeJob.getId(), scrapingDay);

                if (recipeScrapeJobCancelRedisService.isCancelled(recipeScrapeJob.getId())) {
                    log.info("Job: {} has been canceled on day: {}", recipeScrapeJob.getId(), scrapingDay);
                    recipeScrapeJob.setState(CANCELLED);
                    return scrapeLogs;
                }

                scrapeRecipe(webDriver, scrapeLogs);
                hasNextDayButtonEnabled = goToNextDay(webDriver);
            } while (hasNextDayButtonEnabled);
        } catch (InterruptedException | MalformedURLException exception) {
            log.error("Exception thrown when processing source: {}, recipe scrape job id: {}, message: {}",
                    getSource(),
                    recipeScrapeJob.getId(),
                    exception.getMessage());
            log.error(exception);

            recipeScrapeJob.setState(FAILED);
            recipeScrapeJob.setErrorMessage(exception.getMessage());
        } finally {
            if (nonNull(webDriver)) {
                webDriver.quit();
            }
        }

        return scrapeLogs;
    }

    private String getScrapingDate(RemoteWebDriver webDriver) {
        WebElement scrapingDayButton = webDriver.findElement(By.cssSelector("ul.user-diet-date > li.active > a > span > span.date"));
        return scrapingDayButton.getText()
                .trim()
                .replace("(", "")
                .replace(")", "");
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

    private void goToDietTab(RemoteWebDriver webDriver) throws InterruptedException {
        WebElement dietTab = webDriver.findElement(By.cssSelector("body > div.container.sidenav_v1.sidenav_v1_page-container.hader_panel_v1__content > div > div.col-lg-3.col-md-3.hidden-sm.hidden-xs > div > div.sidenav-v1_box-nav > ul.main-nav-list > li.nav-item-2 > a"));
        dietTab.click();
        webDriverService.sleep(2);
    }

    private void clickFirstDietDayButton(RemoteWebDriver webDriver) throws InterruptedException {
        WebElement firstDietDayButton = webDriver.findElement(By.cssSelector("body > div.container.sidenav_v1.sidenav_v1_page-container.hader_panel_v1__content > div > div.col-lg-9.col-md-9.col-sm-12.col-xs-12 > div > div.plan-user-view > ul:nth-child(2) > li:nth-child(1) > a"));
        firstDietDayButton.click();
        webDriverService.sleep(1);
    }

    private void scrapeRecipe(RemoteWebDriver webDriver, List<RecipeScrapeLog> scrapeLogs) {
        scrapeLogs.add(getRecipeScrapeLog(webDriver, "#posilek1 > div.meal", 1));
        scrapeLogs.add(getRecipeScrapeLog(webDriver, "#posilek2 > div.meal", 2));
        scrapeLogs.add(getRecipeScrapeLog(webDriver, "#posilek3 > div.meal", 3));
        scrapeLogs.add(getRecipeScrapeLog(webDriver, "#posilek4 > div.meal", 4));
        scrapeLogs.add(getRecipeScrapeLog(webDriver, "#posilek5 > div.meal", 5));
    }

    private RecipeScrapeLog getRecipeScrapeLog(RemoteWebDriver webDriver, String mealSelector, Integer mealNumber) {
        WebElement meal = webDriver.findElement(By.cssSelector(mealSelector));

        return RecipeScrapeLog.builder()
                .mealNumbers(List.of(mealNumber))
                .name(meal.findElement(By.cssSelector("h2")).getText().trim())
                .productsNamesToQuantities(getProductsNamesToQuantities(meal))
                .recipe(meal.findElement(By.cssSelector("p:last-child")).getText().trim())
                .build();
    }

    private Map<String, BigDecimal> getProductsNamesToQuantities(WebElement meal) {
        Map<String, BigDecimal> productsNamesToQuantities = new HashMap<>();

        List<WebElement> productsElements = meal.findElements(By.cssSelector("ul:nth-child(6) > li > span.meal-exchange > span.exchange-ingredient > span.exchange-value"));
        productsElements.stream()
                .map(productElement -> productElement.getText().trim())
                .map(rawProduct -> {
                    String[] rawProductSplitByDash = rawProduct.split(" - ");
                    String[] rawQuantitySplitByOpenBracket = rawProductSplitByDash[1].split("[(]");

                    return Map.entry(
                            rawProductSplitByDash[0].trim(),
                            new BigDecimal(rawQuantitySplitByOpenBracket[1].trim().replace(" g)", "")));
                })
                .forEach(entry -> productsNamesToQuantities.put(entry.getKey(), entry.getValue()));

        return productsNamesToQuantities;
    }

    private boolean goToNextDay(RemoteWebDriver webDriver) throws InterruptedException {
        By nextDayButtonSelector = By.cssSelector("body > div.container.sidenav_v1.sidenav_v1_page-container.hader_panel_v1__content > div > div.col-lg-9.col-md-9.col-sm-12.col-xs-12 > div > div.plan-user-view > div.user-diet-bottom-nav.clearfix > div:nth-child(2) > a");
        if (webDriverService.isElementExist(webDriver, nextDayButtonSelector)) {
            WebElement nextDayButton = webDriver.findElement(nextDayButtonSelector);
            nextDayButton.click();
            webDriverService.sleep(1);

            return true;
        }
        return false;
    }
}
