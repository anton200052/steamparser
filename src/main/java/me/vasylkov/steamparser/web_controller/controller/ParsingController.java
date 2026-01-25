package me.vasylkov.steamparser.web_controller.controller;

import lombok.RequiredArgsConstructor;
import me.vasylkov.steamparser.parsing.component.ParsingStatus;
import me.vasylkov.steamparser.parsing.service.ParsingTaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/parsing")
public class ParsingController {
    private final ParsingTaskService parsingTaskService;
    private final ParsingStatus statusManager;

    @GetMapping("/control")
    public String control(Model model, @ModelAttribute(value = "infoMsg", binding = false) String infoMsg) {
        model.addAttribute("isParsingStarted", statusManager.isParsingStarted());
        return "control";
    }

    @GetMapping("/enable")
    public String enable(RedirectAttributes ra) {
        parsingTaskService.startParsingProcess();
        ra.addFlashAttribute("infoMsg", "Парсинг запущен");
        return "redirect:/parsing/control";
    }

    @GetMapping("/disable")
    public String disable(RedirectAttributes ra) {
        parsingTaskService.stopParsingProcess();
        ra.addFlashAttribute("infoMsg", "Парсинг остановлен");
        return "redirect:/parsing/control";
    }
}
