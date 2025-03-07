package com.joa.prexixion.signer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/user")
public class UserController {

    @GetMapping("/dashboard")
    public String listFiles(Model model) {
        try {

        } catch (Exception e) {
            model.addAttribute("error", "Error al acceder al dashboard: " + e.getMessage());
        }
        return "user/dashboard"; // Retorna la vista "dashboard.html"
    }
}
