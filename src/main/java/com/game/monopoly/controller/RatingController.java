package com.game.monopoly.controller;

import com.game.monopoly.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/rating")
public class RatingController {

    @Autowired
    private UserService userService;


    @GetMapping
    public String userList(Model model){

        model.addAttribute("users", userService.getRating());

        return "userList";
    }

}
