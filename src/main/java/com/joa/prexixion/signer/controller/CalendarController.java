package com.joa.prexixion.signer.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joa.prexixion.signer.model.CalendarEvent;
import com.joa.prexixion.signer.repository.StatComportamientoRepository;
import com.joa.prexixion.signer.security.CustomUserDetails;
import com.joa.prexixion.signer.service.CalendarService;
import com.joa.prexixion.signer.utils.LoggerUtils;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    @Autowired
    CalendarService calendarService;

    @Autowired
    LoggerUtils loggerUtils;

    @GetMapping("/getObligaciones")
    public ResponseEntity<List<Map<String, Object>>> getObligaciones() {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = calendarService.getObligaciones();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(loggerUtils.errorList(e));
        }
    }

    @GetMapping("/getFeriados")
    public ResponseEntity<List<Map<String, Object>>> getFeriados(Authentication authentication) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = calendarService.listFeriados();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(loggerUtils.errorList(e));
        }
    }

    @GetMapping("/getFiscalizaciones")
    public ResponseEntity<List<Map<String, Object>>> getFiscalizaciones(Authentication authentication) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = calendarService.listFeriados();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(loggerUtils.errorList(e));
        }
    }
}
