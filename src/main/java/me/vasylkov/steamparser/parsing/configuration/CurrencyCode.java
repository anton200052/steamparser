package me.vasylkov.steamparser.parsing.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public enum CurrencyCode
{
    UAH(18);

    private final int code;
    CurrencyCode (int code)
    {
        this.code = code;
    }

    @Override
    public String toString()
    {
        return this.name();
    }
}
