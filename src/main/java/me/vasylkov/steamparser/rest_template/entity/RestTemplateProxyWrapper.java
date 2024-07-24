package me.vasylkov.steamparser.rest_template.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.vasylkov.steamparser.common.abstraction.ProxyWrapper;

@Data
@AllArgsConstructor
public class RestTemplateProxyWrapper implements ProxyWrapper<RestTemplateProxy>
{
    private RestTemplateProxy proxy;
    private boolean blocked;
}
