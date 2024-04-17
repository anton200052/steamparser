package me.vasylkov.steamparser.general.service;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ApplicationShutdownService
{

    private final ApplicationContext appContext;

    public ApplicationShutdownService(ApplicationContext appContext)
    {
        this.appContext = appContext;
    }

    public void initiateShutdown(int returnCode)
    {
        SpringApplication.exit(appContext, () -> returnCode);
    }
}
