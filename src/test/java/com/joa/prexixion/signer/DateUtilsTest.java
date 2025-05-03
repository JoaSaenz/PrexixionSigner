package com.joa.prexixion.signer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.joa.prexixion.signer.utils.DateUtils;

public class DateUtilsTest {
    public static void main(String[] args) {
        String fechaInicio = "2020-01-23";
        String fechaFin = "2020-06-03";

        // Restar meses
        System.out.println(DateUtils.restarMeses(fechaInicio, 4));

        System.out.println("------------------------");

        // ObtenerFechasEntreStrings
        DateUtils.getFechasBetweenStrings(fechaInicio, fechaFin).forEach(e -> {
            System.out.println(e);
        });

    }
}
