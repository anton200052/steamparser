package me.vasylkov.steamparser.parsing.component;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class BlacklistChecker {
    private final Set<String> blacklistedIds = new HashSet<>();

    public void addToBlacklist(String id) {
        blacklistedIds.add(id);
    }

    public void removeFromBlacklist(String id) {
        blacklistedIds.remove(id);
    }

    public boolean isBlacklisted(String id) {
        return blacklistedIds.contains(id);
    }
}
