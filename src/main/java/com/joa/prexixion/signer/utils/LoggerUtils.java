package com.joa.prexixion.signer.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggerUtils {

    private static final Logger logger = LoggerFactory.getLogger(LoggerUtils.class);

    public Map<String, Object> error(Exception e) {

        Map<String, Object> response = new HashMap<>();
        StackTraceElement element = e.getStackTrace()[0];
        String className = element.getClassName();
        String methodName = element.getMethodName();
        

        response.put("clase", className);
        response.put("método", methodName);
        response.put("error", "Ocurrió un problema");
        response.put("detalle", e.getMessage()); // opcional, para debug

        logger.error(className + " - " + methodName + " - " + e.toString());

        return response;
    }
}
