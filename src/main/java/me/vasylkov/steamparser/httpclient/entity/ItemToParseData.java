package me.vasylkov.steamparser.httpclient.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.jackson.PriceDeserializer;

@RequiredArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SteamItem
{
    @JsonProperty("median_price")
    @JsonDeserialize(using = PriceDeserializer.class)
    private Double medianPrice;
    private String hashName;
    private String url;
}
