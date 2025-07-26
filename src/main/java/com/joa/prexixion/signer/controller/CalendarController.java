package com.joa.prexixion.signer.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joa.prexixion.signer.model.CalendarEvent;
import com.joa.prexixion.signer.security.CustomUserDetails;
import com.joa.prexixion.signer.service.CalendarService;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    @Autowired
    CalendarService calendarService;

    @GetMapping("/getObligaciones")
    public ResponseEntity<List<Map<String, Object>>> getObligaciones() {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = calendarService.getObligaciones();

        } catch (Exception e) {
            System.out.println(e);
        }

        return ResponseEntity.ok(list);
    }

    @GetMapping("/getFeriados")
    public ResponseEntity<List<Map<String, Object>>> getFeriados(Authentication authentication) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = calendarService.listFeriados();

        } catch (Exception e) {
            System.out.println(e);
        }

        return ResponseEntity.ok(list);
    }

    @GetMapping("/getFiscalizaciones")
    public ResponseEntity<List<Map<String, Object>>> getFiscalizaciones(Authentication authentication) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = calendarService.listFeriados();

        } catch (Exception e) {
            System.out.println(e);
        }

        return ResponseEntity.ok(list);
    }

}
