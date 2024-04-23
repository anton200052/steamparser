package me.vasylkov.steamparser.general.component;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ApplicationShutdownManager
{
    private final ApplicationContext appContext;

    public ApplicationShutdownManager(ApplicationContext appContext)
    {
        this.appContext = appContext;
    }

    public void initiateShutdown(int returnCode)
    {
        SpringApplication.exit(appContext, () -> returnCode);
    }
}
