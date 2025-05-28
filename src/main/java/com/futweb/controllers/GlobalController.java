package com.futweb.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalController {

    @ModelAttribute
    public void addUserInfoToModel(HttpSession session, Model model) {
        String userName = (String) session.getAttribute("userName");
        String userImage = (String) session.getAttribute("userImage");

        model.addAttribute("userName", "Olá " + userName + "!");
        model.addAttribute("userImage", userImage);
    }
}
