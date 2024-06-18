package me.vasylkov.steamparser.selenium.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

@Data
@AllArgsConstructor
public class WebDriverWrapper
{
    private WebDriver driver;
    private ProxyWrapper proxy;
    private WebDriverWait webDriverWait;
}
