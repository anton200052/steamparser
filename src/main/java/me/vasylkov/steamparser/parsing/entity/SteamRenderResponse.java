package me.vasylkov.steamparser.parsing.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class SteamRenderResponse
{
    private boolean success;
    private int start;
    @JsonProperty("pagesize")
    private int pageSize;
    private int totalCount;
    private Map<String, Map<String, Map<String, SteamRenderAssetLink>>> assets;
}
