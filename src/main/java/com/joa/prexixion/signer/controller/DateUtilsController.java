package com.joa.prexixion.signer.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joa.prexixion.signer.utils.DateUtils;
import com.joa.prexixion.signer.utils.LoggerUtils;

@RestController
@RequestMapping("/api/dateUtils")
public class DateUtilsController {

    @Autowired
    LoggerUtils loggerUtils;

    @GetMapping("/getAnio")
    public ResponseEntity<Map<String, Object>> getAnio(
            @RequestParam String anio,
            @RequestParam int evento) {

        Map<String, Object> response = new HashMap<>();

        try {
            String fecha = anio + "-01-01";
            String valorAnio = "";
            switch (evento) {
                case -1:
                    valorAnio = DateUtils.minusYears(fecha, 1);
                    break;
                case 1:
                    valorAnio = DateUtils.addYears(fecha, 1);
                    break;
            }
            valorAnio = valorAnio.substring(0, 4);
            response.put("valorAnio", valorAnio);

            String fecha2 = valorAnio + "-01-01";
            String valorAnioUnoAtras = DateUtils.minusYears(fecha2, 1);
            valorAnioUnoAtras = valorAnioUnoAtras.substring(0, 4);
            response.put("valorAnioUnoAtras", valorAnioUnoAtras);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(loggerUtils.error(e));
        }
    }

    @GetMapping("getPeriodo")
    public ResponseEntity<Map<String, Object>> getPeriodo(
            @RequestParam String periodo,
            @RequestParam int evento) {
        Map<String, Object> response = new HashMap<>();

        try {
            String fecha = periodo + "-01";
            String valorPeriodo = "";
            switch (evento) {
                case -1:
                    valorPeriodo = DateUtils.minusMonths(fecha, 1);
                    break;
                case 1:
                    valorPeriodo = DateUtils.addMonths(fecha, 1);
                    break;
            }
            valorPeriodo = valorPeriodo.substring(0, 7);
            response.put("valorPeriodo", valorPeriodo);

            String valorPeriodoAnio = valorPeriodo.substring(0, 4);
            response.put("valorPeriodoAnio", valorPeriodoAnio);
            String valorPeriodoMes = valorPeriodo.substring(5, 7);
            response.put("valorPeriodoMes", valorPeriodoMes);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(loggerUtils.error(e));
        }
    }
}
