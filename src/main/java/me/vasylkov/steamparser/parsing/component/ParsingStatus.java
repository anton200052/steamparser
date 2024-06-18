package me.vasylkov.steamparser.parsing.component;

public interface ParsingStatus
{
    boolean isParsingStarted();

    void setParsingStarted(boolean parsingStarted);
}
