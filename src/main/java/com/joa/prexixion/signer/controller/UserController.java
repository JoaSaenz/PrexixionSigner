package com.joa.prexixion.signer.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.joa.prexixion.signer.security.CustomUserDetails;

@Controller
@RequestMapping("/user")
public class UserController {

    @GetMapping("/dashboard")
    public String listFiles(Model model) {
        try {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            String nombreFront = userDetails.getFrontNombreUsuario();
            model.addAttribute("nombreUsuario", nombreFront);

        } catch (Exception e) {
            model.addAttribute("error", "Error al acceder al dashboard: " + e.getMessage());
        }
        return "user/dashboard"; // Retorna la vista "dashboard.html"
    }
}
