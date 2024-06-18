package me.vasylkov.steamparser.rest.controller;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.parsing.service.SteamParsingService;
import me.vasylkov.steamparser.parsing.service.SteamParsingTaskManager;
import me.vasylkov.steamparser.rest.component.ResponseEntityConverter;
import me.vasylkov.steamparser.rest.dto.MessageDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/parsing/steam")
public class SteamParsingController
{
    private final ResponseEntityConverter<MessageDTO> responseEntityConverter;
    private final SteamParsingTaskManager steamParsingTaskManager;

    @GetMapping("/start")
    public ResponseEntity<?> startParsing()
    {
        steamParsingTaskManager.startParsingProcess();
        return responseEntityConverter.convertToResponseEntity(new MessageDTO("Парсинг начат!"), HttpStatus.OK);
    }

    @GetMapping("/stop")
    public ResponseEntity<?> stopParsing()
    {
        steamParsingTaskManager.stopParsingProcess();
        return responseEntityConverter.convertToResponseEntity(new MessageDTO("Парсинг скоро будет остановлен!"), HttpStatus.OK);
    }
}
