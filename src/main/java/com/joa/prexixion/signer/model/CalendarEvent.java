package com.joa.prexixion.signer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalendarEvent {
    // VARIABLES PARA EVNTOS PRUEBA EN FULL CALENDAR
    private String title;
    private String start;
    private String end;
    private String attendee;
    private String topic;
    private int idEstado;

    // VARIABLE PARA CELNDARIO OBLIGACIONES;
    private String color;
    private int area;

    // VARIABLES ADICIONALES:
    private boolean allDay; // Para indicar si es un evento de todo el día
    // private String description; // Descripción adicional para el evento del
    // cronograma

    // PARA CUMPLEANIOS
    private String flag; // Para determinar si es signer o gear

    // PARA REUNIONES
    private String flagReunion; // Para determinar si es presencial o virtual

    // PARA FISCALIZACIONES
    private String flagFiscalizacion; // Para determinar si es presencial o electronico
}
