package com.game.monopoly.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/restroom")
    public String greet() {
        return "restRoom";
    }

    @GetMapping("/quit")
    public String quit() {
        return "redirect:/restroom";
    }

    @GetMapping("/error")
    public String error() { return "error"; }

}
