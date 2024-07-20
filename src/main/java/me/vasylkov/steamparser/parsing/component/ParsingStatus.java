package me.vasylkov.steamparser.parsing.component;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ParsingStatus
{
    private volatile boolean isParsingStarted = false;
}
