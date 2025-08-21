package com.joa.prexixion.signer.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.joa.prexixion.signer.security.CustomUserDetails;
import com.joa.prexixion.signer.utils.DateUtils;

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

            //Periodo
            String periodoActual = (DateUtils.reduceOneMonth(DateUtils.localDateToString(DateUtils.getToday()))).substring(0,7);
            model.addAttribute("periodoActual", periodoActual);

            //Anio
            String anioActual = DateUtils.getYear();
            model.addAttribute("anioActual", anioActual);
        } catch (Exception e) {
            model.addAttribute("error", "Error al acceder al dashboard: " + e.getMessage());
        }
        return "user/dashboard"; // Retorna la vista "dashboard.html"
    }
}
