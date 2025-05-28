package com.futweb.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/")
    public String showHomeLogin(){
        return "login";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
}
