package pl.dietadvisor.common.productScraper.service;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Service;
import pl.dietadvisor.common.shared.config.properties.selenium.SeleniumProperties;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import static org.openqa.selenium.support.ui.Sleeper.SYSTEM_SLEEPER;

@Service
@RequiredArgsConstructor
public class WebDriverService {
    private final SeleniumProperties seleniumProperties;

    public RemoteWebDriver create(String url) throws MalformedURLException {
        RemoteWebDriver webDriver = new RemoteWebDriver(new URL(seleniumProperties.getHost()), new MutableCapabilities(new ChromeOptions()));
        webDriver.navigate().to(url);
        webDriver.manage().window().maximize();

        return webDriver;
    }

    public boolean isElementExist(RemoteWebDriver webDriver, By selector) {
        try {
            webDriver.findElement(selector);
        } catch (NoSuchElementException exception) {
            return false;
        }

        return true;
    }

    public void sleep(Integer seconds) throws InterruptedException {
        SYSTEM_SLEEPER.sleep(Duration.ofSeconds(seconds));
    }
}
