package me.vasylkov.steamparser.httpclient.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.jackson.PriceDeserializer;

@RequiredArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Item
{
    @JsonProperty("median_price")
    @JsonDeserialize(using = PriceDeserializer.class) // Используйте ваш десериализатор здесь
    private Double medianPrice;
    private String name;
    private String url;
}
