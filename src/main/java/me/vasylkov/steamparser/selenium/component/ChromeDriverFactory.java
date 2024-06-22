package me.vasylkov.steamparser.selenium.component;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.general.component.ApplicationShutdownManager;
import me.vasylkov.steamparser.parsing.configuration.ParsingProperties;
import me.vasylkov.steamparser.selenium.configuration.SeleniumProperties;
import me.vasylkov.steamparser.selenium.entity.ProxyWrapper;
import me.vasylkov.steamparser.selenium.entity.WebDriverWrapper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class ChromeDriverFactory implements DriverFactory
{
    private final SeleniumProperties properties;
    private final Logger logger;
    private final ApplicationShutdownManager applicationShutdownManager;
    private final ParsingProperties parsingProperties;

    @Override
    public WebDriverWrapper createWebDriver(ProxyWrapper proxyWrapper)
    {
        ChromeDriver chromeDriver = new ChromeDriver(createBrowserOptions(proxyWrapper));
        WebDriverWait webDriverWait = new WebDriverWait(chromeDriver, Duration.ofSeconds(parsingProperties.getElementsWaitingDuration()));
        return new WebDriverWrapper(chromeDriver, proxyWrapper, webDriverWait);
    }

    private ChromeOptions createBrowserOptions(ProxyWrapper proxyWrapper)
    {
        try
        {
            ChromeOptions options = new ChromeOptions();
            System.setProperty("webdriver.chrome.driver", properties.getDriverPath());

            // Копирование директории профиля во временное хранилище
            Path sourceDir = Path.of(properties.getProfilePath());
            Path tempDir = Files.createTempDirectory("chrome-profile-");
            Files.walk(sourceDir).forEach(sourcePath ->
            {
                Path destinationPath = tempDir.resolve(sourceDir.relativize(sourcePath));
                try
                {
                    Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                }
                catch (Exception e)
                {
                    logger.error("Ошибка при копировании файла: {}", sourcePath, e);
                    throw new RuntimeException(e);
                }
            });

            options.addArguments("--ignore-certificate-errors");
            options.addArguments("user-data-dir=" + tempDir.toString());
            options.addExtensions(new File(properties.getExtensionPath()));

            if (proxyWrapper != null)
            {
                options.setProxy(proxyWrapper.getProxy());
            }

            return options;
        }
        catch (Exception e)
        {
            logger.error("Ошибка при настройке ChromeOptions: ", e);
            throw new RuntimeException(e);
        }
    }
}
