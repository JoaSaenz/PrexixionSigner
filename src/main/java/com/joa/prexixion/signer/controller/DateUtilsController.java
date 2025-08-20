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


@RestController
@RequestMapping("/api/dateUtils")
public class DateUtilsController {

    @GetMapping("/getAnio")
    public ResponseEntity<Map<String, Object>> getAnio(
            @RequestParam String anio,
            @RequestParam int evento) {

        Map<String, Object> response = new HashMap<>();

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
        valorAnio = valorAnio.substring(0,4);
        response.put("valorAnio", valorAnio);

        return ResponseEntity.ok(response);
    }
}
