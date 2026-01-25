package me.vasylkov.steamparser.web_controller.exception_handler;

import me.vasylkov.steamparser.parsing.exception.ParsingRunningException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(ParsingRunningException.class)
    public String handleParsingRunningException(RedirectAttributes ra, ParsingRunningException ex) {
        ra.addFlashAttribute("infoMsg", ex.getMessage());
        return "redirect:/parsing/control";
    }
}
