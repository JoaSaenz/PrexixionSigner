package com.joa.prexixion.signer.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class DateUtils {
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /** Resta meses a una fecha en formato yyyy-MM-dd */
    public static String restarMeses(String fecha, int meses) {
        try {
            return localDateToString(stringToLocalDate(fecha).minusMonths(meses));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Fecha inválida: " + fecha + ". Se esperaba formato yyyy-MM-dd.");
        }
    }

    /** Devuelve lista de strings entre dos fechas con paso de mes */
    public static List<String> getFechasBetweenStrings(String fechaInicial, String fechaFinal) {
        List<String> fechas = new ArrayList<>();

        if (!fechaInicial.equals(fechaFinal)) {
            LocalDate initialDay = stringToLocalDate(fechaInicial);
            LocalDate endDay = stringToLocalDate(fechaFinal);

            while (initialDay.isBefore(endDay)) {
                fechas.add(localDateToString(initialDay));
                // Ir al primer día del siguiente mes
                initialDay = initialDay.withDayOfMonth(1).plusMonths(1);
            }

            int mesFinal = getMonth(fechaFinal);
            int mesUltimoAgregado = getMonth(fechas.get(fechas.size() - 1));

            if (mesFinal == mesUltimoAgregado) {
                fechas.set(fechas.size() - 1, localDateToString(endDay));
            } else {
                fechas.add(fechaFinal);
            }
        }

        return fechas;
    }

    /** Devuelve la abreviatura basado en el número del mes */
    public static String getAbrMonthNameCamelCase(String nroMes) {
        String mes = "";
        switch (nroMes) {
            case "01":
                mes = "Ene";
                break;
            case "02":
                mes = "Feb";
                break;
            case "03":
                mes = "Mar";
                break;
            case "04":
                mes = "Abr";
                break;
            case "05":
                mes = "May";
                break;
            case "06":
                mes = "Jun";
                break;
            case "07":
                mes = "Jul";
                break;
            case "08":
                mes = "Ago";
                break;
            case "09":
                mes = "Sep";
                break;
            case "10":
                mes = "Oct";
                break;
            case "11":
                mes = "Nov";
                break;
            case "12":
                mes = "Dec";
                break;
        }
        return mes;
    }

    /** Convierte String a LocalDate */
    public static LocalDate stringToLocalDate(String fecha) {
        return LocalDate.parse(fecha, FORMATO_FECHA);
    }

    /** Convierte LocalDate a String */
    public static String localDateToString(LocalDate fecha) {
        return fecha.format(FORMATO_FECHA);
    }

    /** Devuelve el mes (1-12) de una fecha en String */
    public static int getMonth(String fecha) {
        return stringToLocalDate(fecha).getMonthValue();
    }

}
