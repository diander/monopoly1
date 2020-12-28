package com.game.monopoly.controller;

import com.game.monopoly.domain.User;
import com.game.monopoly.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class ProfileController {

    @GetMapping("{user}")
    public String getProfile(
            Model model,
            @PathVariable User user,
            @AuthenticationPrincipal User currentUser){
        model.addAttribute("targetUser", user);
        model.addAttribute("user", currentUser);
        return "userProfile";
    }

}
