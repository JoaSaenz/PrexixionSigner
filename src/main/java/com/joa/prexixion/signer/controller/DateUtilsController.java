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

    @Autowired
    DateUtils dateUtils;

    @GetMapping("/getAnio")
    public ResponseEntity<Map<String, Object>> addYears(
            @RequestParam String anio,
            @RequestParam int evento) {

        Map<String, Object> response = new HashMap<>();
        response.put("anio", anio);
        response.put("evento", evento);

        switch (evento) {
            case -1:
                anio = dateUtils.minusYears(anio, 1);
                break;
            case 1:
                anio = dateUtils.addYears(anio, 1);
                break;
        }

        response.put("getAnio", anio);

        return ResponseEntity.ok(response);
    }
}
