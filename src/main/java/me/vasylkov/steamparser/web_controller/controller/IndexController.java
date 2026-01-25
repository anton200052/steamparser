package me.vasylkov.steamparser.web_controller.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String redirectToEndpoint() {
        return "redirect:/parsing/control";
    }
}
