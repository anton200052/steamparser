package me.vasylkov.steamparser.parsing.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
public class SteamRenderAssetLink
{
    private String id;
    @Getter
    private String link;

    public SteamRenderAssetLink()
    {
    }

    public SteamRenderAssetLink(String id, String link)
    {
        this.id = id;
        this.link = link;
    }

    @JsonProperty("id")
    public String getId()
    {
        return id;
    }

    @JsonProperty("actions")
    private void unpackNested(List<Action> actions)
    {
        if (actions != null && !actions.isEmpty())
        {
            this.link = actions.get(0).getLink().replace("%assetid%", this.id);
        }
    }

    @Setter
    public static class Action
    {
        private String link;

        @JsonProperty("link")
        public String getLink()
        {
            return link;
        }

    }
}
