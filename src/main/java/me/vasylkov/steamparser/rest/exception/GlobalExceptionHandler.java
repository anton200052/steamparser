package me.vasylkov.steamparser.rest.exception;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.rest.component.ResponseEntityConverter;
import me.vasylkov.steamparser.rest.dto.MessageDTO;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler
{
    private final ResponseEntityConverter<MessageDTO> responseEntityConverter;
    private final Logger logger;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception exception)
    {
        logger.error("Исключение в рест-контроллере:", exception);
        return responseEntityConverter.convertToResponseEntity(new MessageDTO("Произошла ошибка при взаимодействии с сервисом!"), HttpStatus.BAD_REQUEST);
    }
}
