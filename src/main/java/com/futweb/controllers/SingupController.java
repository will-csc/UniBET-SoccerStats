package com.futweb.controllers;

import com.futweb.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SingupController {

    @GetMapping("/signup")
    public String showSignUp(Model model){
        model.addAttribute("user", new User()); // Necessário para manter os dados do form
        return "signup";
    }
}
