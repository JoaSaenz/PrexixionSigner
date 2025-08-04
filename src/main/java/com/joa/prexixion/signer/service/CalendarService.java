package com.joa.prexixion.signer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joa.prexixion.signer.dto.StatComportamiento;
import com.joa.prexixion.signer.model.CalendarEvent;
import com.joa.prexixion.signer.repository.CalendarRepository;

@Service
public class CalendarService {

    @Autowired
    CalendarRepository calendarRepository;

    public List<Map<String, Object>> listFeriados() throws Exception {
        List<Map<String, Object>> response = new ArrayList<>();

        List<CalendarEvent> list = calendarRepository.listFeriados();

        for (CalendarEvent obj : list) {
            Map<String, Object> event = new HashMap<>();
            event.put("title", obj.getTitle());
            event.put("start", obj.getStart());
            event.put("allDay", true);
            event.put("type", "feriados"); // Tipo

            Map<String, Object> extendedProps = new HashMap<>();
            extendedProps.put("colorFeriado", "#E6E3F3");
            event.put("extendedProps", extendedProps);
 
            response.add(event);
        }

        return response;
    }

    public List<Map<String, Object>> getObligaciones() throws Exception {
        List<Map<String, Object>> response = new ArrayList<>();

        List<CalendarEvent> list = calendarRepository.getObligaciones();

        List<Map<String, Object>> events = new ArrayList<>();
        for (CalendarEvent obj : list) {
            Map<String, Object> event = new HashMap<>();
            event.put("title", obj.getTitle());
            event.put("start", obj.getStart());
            event.put("backgroundColor", obj.getColor());
            event.put("borderColor", obj.getColor());
            event.put("allDay", true);
            events.add(event);

            response.add(event);
        }

        return response;
    }

}
