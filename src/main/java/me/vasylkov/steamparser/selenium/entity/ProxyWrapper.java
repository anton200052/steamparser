package me.vasylkov.steamparser.selenium.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.openqa.selenium.Proxy;

@Data
@AllArgsConstructor
public class ProxyWrapper
{
    private Proxy proxy;
    private boolean blocked;
}
