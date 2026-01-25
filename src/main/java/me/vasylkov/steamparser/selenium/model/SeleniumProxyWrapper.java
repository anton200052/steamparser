package me.vasylkov.steamparser.selenium.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.vasylkov.steamparser.common.abstraction.ProxyWrapper;
import org.openqa.selenium.Proxy;

@Data
@AllArgsConstructor
public class SeleniumProxyWrapper implements ProxyWrapper<Proxy>
{
    private Proxy proxy;
    private boolean blocked;
}
