package me.vasylkov.steamparser.selenium.configuration;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.spring.service.ApplicationShutdownService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
@RequiredArgsConstructor
public class SeleniumConfiguration
{
    private final SeleniumProperties properties;
    private final Logger logger;
    private final ApplicationShutdownService applicationShutdownService;

    @Bean
    public ChromeOptions chromeOptions()
    {
        try
        {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("user-data-dir=" + properties.getProfilePath());
            options.addExtensions(new File(properties.getExtensionPath()));
            return options;
        }
        catch (Exception e)
        {
            logger.error("Ошибка при настройке ChromeOptions: ", e);
            applicationShutdownService.initiateShutdown(1);
            throw e;
        }
    }
}