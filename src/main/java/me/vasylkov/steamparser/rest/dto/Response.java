package me.vasylkov.steamparser.rest.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response<T>
{
    private boolean success;

    @JsonUnwrapped
    private T data;
}
