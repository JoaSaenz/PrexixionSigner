package com.joa.prexixion.signer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestLogController {
    
    private static final Logger logger = LoggerFactory.getLogger(TestLogController.class);

    @GetMapping("/test-log")
    public String testLog() {
        logger.info("Entrando al endpoint /test-log");
        logger.debug("Este es un log DEBUG (solo se vera si configuraste DEBUG en application.properties)");
        logger.warn("Ejemplo de log WARN");
        logger.error("Ejemplo de log ERROR");

        return "Logs generados, revisa consola o el archivo en /logs/prexixion-signer.log";
    }
}
