package me.vasylkov.steamparser.selenium.component;

import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.parsing.configuration.ParsingProperties;
import me.vasylkov.steamparser.selenium.configuration.SeleniumProperties;
import me.vasylkov.steamparser.selenium.entity.ProxyWrapper;
import me.vasylkov.steamparser.selenium.entity.WebDriverWrapper;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChromeDriverFactory implements DriverFactory
{
    private final SeleniumProperties properties;
    private final Logger logger;
    private final ParsingProperties parsingProperties;
    private final ResourceLoader resourceLoader;

    @Override
    public synchronized WebDriverWrapper createWebDriver(ProxyWrapper proxyWrapper)
    {
        List<Path> tempFiles = new ArrayList<>();

        ChromeDriver chromeDriver = new ChromeDriver(createBrowserOptions(proxyWrapper, tempFiles));
        WebDriverWait webDriverWait = new WebDriverWait(chromeDriver, Duration.ofSeconds(parsingProperties.getElementsWaitingDuration()));
        return new WebDriverWrapper(chromeDriver, proxyWrapper, webDriverWait, tempFiles);
    }

    private ChromeOptions createBrowserOptions(ProxyWrapper proxyWrapper, List<Path> tempFiles)
    {
        try
        {
            ChromeOptions options = new ChromeOptions();

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
            Resource resource = resourceLoader.getResource("classpath:checker.crx");
            Path tempCheckerFile = Files.createTempFile("checker", ".crx");
            try (InputStream inputStream = resource.getInputStream())
            {
                Files.copy(inputStream, tempCheckerFile, StandardCopyOption.REPLACE_EXISTING);
            }
            tempFiles.addAll(List.of(tempCheckerFile, tempDir));

            options.addExtensions(tempCheckerFile.toFile());
            options.addArguments("--no-sandbox");
            options.addArguments("--remote-debugging-pipe");
            options.addArguments("--ignore-certificate-errors");
            options.addArguments("user-data-dir=" + tempDir.toString());

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

    @PostConstruct
    public void importDriver()
    {
        WebDriverManager.chromedriver().setup();
    }
}
