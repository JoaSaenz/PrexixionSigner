package com.joa.prexixion.signer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joa.prexixion.signer.dto.StatComportamiento;
import com.joa.prexixion.signer.repository.StatComportamientoRepository;

@Service
public class StatComportamientoService {

    @Autowired
    StatComportamientoRepository comportamientoRepository;

    public List<StatComportamiento> getHistoricoEnSoles(String anio, String username) {
        return comportamientoRepository.getHistoricoEnSoles(anio, username);
    }

    public List<StatComportamiento> getHistoricoIGV(String anio, String username) {
        return comportamientoRepository.getHistoricoIGV(anio, username);
    }

    public List<StatComportamiento> getComportamientoIGV(String anio, String username) {
        return comportamientoRepository.getComportamientoIGV(anio, username);
    }

    public List<StatComportamiento> getVentasVsCompras(String anio, String username) {
        return comportamientoRepository.getHistoricoIGV(anio, username);
    }

    public List<StatComportamiento> getHistoricoRenta(String anio, String username) {
        return comportamientoRepository.getHistoricoRenta(anio, username);
    }
}
