package me.vasylkov.steamparser.rest.component;

import me.vasylkov.steamparser.rest.dto.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ResponseEntityConverter<T>
{
    public ResponseEntity<Response<T>> convertToResponseEntity(T entity, HttpStatus status)
    {
        Response<T> apiResponse = new Response<>(status.is2xxSuccessful(), entity);
        return new ResponseEntity<>(apiResponse, status);
    }
}
