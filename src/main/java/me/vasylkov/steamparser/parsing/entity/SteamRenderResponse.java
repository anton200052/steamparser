package me.vasylkov.steamparser.parsing.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
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

    @Setter
    public static class SteamRenderAssetLink
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
}
