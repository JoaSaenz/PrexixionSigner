package com.joa.prexixion.signer.controller;

import com.joa.prexixion.signer.security.CustomUserDetails;
import com.joa.prexixion.signer.service.StatComportamientoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardApiController {

    @Autowired
    StatComportamientoService comportamientoService;

    // Ejemplo: resumen general por periodo
    @GetMapping("/getHistoricoEnSoles")
    public ResponseEntity<Map<String, Object>> getHistoricoEnSoles(
            @RequestParam String anio,
            Authentication authentication) {

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        Map<String, Object> response = new HashMap<>();
        response.put("anio", anio);
        response.put("usuario", user.getUsername());
        // response.put("monto", 12750.80); // simulado

        response.put("statsHistoricosEnSoles", comportamientoService.getHistoricoEnSoles(anio, user.getUsername()));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getHistoricoIGV")
    public ResponseEntity<Map<String, Object>> getHistoricoIGV(
            @RequestParam String anio,
            Authentication authentication) {

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        Map<String, Object> response = new HashMap<>();
        response.put("anio", anio);
        response.put("usuario", user.getUsername());

        response.put("statsHistoricoIGV", comportamientoService.getHistoricoIGV(anio, user.getUsername()));

        return ResponseEntity.ok(response);
    }
}
