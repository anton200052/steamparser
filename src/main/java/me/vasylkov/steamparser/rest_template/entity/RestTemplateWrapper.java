package me.vasylkov.steamparser.rest_template.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.client.RestTemplate;

@Data
@AllArgsConstructor
public class RestTemplateWrapper
{
    private RestTemplate restTemplate;
    private RestTemplateProxyWrapper restTemplateProxyWrapper;
}

