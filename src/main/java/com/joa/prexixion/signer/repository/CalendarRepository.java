package com.joa.prexixion.signer.repository;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.joa.prexixion.signer.model.CalendarEvent;
import com.joa.prexixion.signer.utils.DateUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;

@Repository
public class CalendarRepository {

    private static final Logger logger = LoggerFactory.getLogger(CalendarRepository.class);

    @PersistenceContext
    private EntityManager em;

    // LIST DE FERIADOS
    public List<CalendarEvent> listFeriados() throws Exception {
        List<CalendarEvent> list = new ArrayList<>();

        String sql = """
                SELECT titulo, fecha FROM cronogramaFeriados
                """;

        try {
            Query query = em.createNativeQuery(sql, Tuple.class);

            @SuppressWarnings("unchecked")
            List<Tuple> resultTuples = query.getResultList();

            for (Tuple tuple : resultTuples) {
                CalendarEvent obj = new CalendarEvent();
                obj.setTitle(tuple.get("titulo", String.class));
                obj.setStart(DateUtils.sqlDateToString(tuple.get("fecha", java.sql.Date.class)));
                list.add(obj);
            }
        } catch (Exception e) {
            // TODO: handle exception
            logger.error(e.toString());
        }

        return list;
    }

    public List<CalendarEvent> getObligaciones() throws Exception {
        List<CalendarEvent> list = new ArrayList();

        String sql = "SELECT titulo, fecha, color, area \n"
                + "FROM ( \n"
                + "    -- cronogramaPDT \n"
                + "    SELECT \n"
                + "        titulos.titulo, titulos.fecha, '#090C9B' AS color, 8 AS area \n"
                + "    FROM cronogramaPDT \n"
                + "    CROSS APPLY ( \n"
                + "        VALUES \n"
                + "            ('DJ PDT RUC 0', fecha0), \n"
                + "            ('DJ PDT RUC 1', fecha1), \n"
                + "            ('DJ PDT RUC 2 - 3', fecha2), \n"
                + "            ('DJ PDT RUC 4 - 5', fecha4), \n"
                + "            ('DJ PDT RUC 6 - 7', fecha6), \n"
                + "            ('DJ PDT RUC 8 - 9', fecha8), \n"
                + "            ('DJ PDT BUCS', fechab) \n"
                + "    ) AS titulos(titulo, fecha) \n"
                + " \n"
                + "    UNION ALL \n"
                + " \n"
                + "    -- cronogramaSire \n"
                + "    SELECT \n"
                + "        titulos.titulo, titulos.fecha, '#3066BE' AS color, 8 AS area \n"
                + "    FROM cronogramaSire \n"
                + "    CROSS APPLY ( \n"
                + "        VALUES \n"
                + "            ('DJ SIRE RUC 0', fecha0), \n"
                + "            ('DJ SIRE RUC 1', fecha1), \n"
                + "            ('DJ SIRE RUC 2 - 3', fecha2), \n"
                + "            ('DJ SIRE RUC 4 - 5', fecha4), \n"
                + "            ('DJ SIRE RUC 6 - 7', fecha6), \n"
                + "            ('DJ SIRE RUC 8 - 9', fecha8) \n"
                + "    ) AS titulos(titulo, fecha) \n"
                + " \n"
                + "    UNION ALL \n"
                + " \n"
                + "    -- cronogramaPLE_Diario con abreviaturas \n"
                + "    SELECT \n"
                + "        'DJ PRICO ' + \n"
                + "        CASE mes \n"
                + "            WHEN '01' THEN 'ENE' \n"
                + "            WHEN '02' THEN 'FEB' \n"
                + "            WHEN '03' THEN 'MAR' \n"
                + "            WHEN '04' THEN 'ABR' \n"
                + "            WHEN '05' THEN 'MAY' \n"
                + "            WHEN '06' THEN 'JUN' \n"
                + "            WHEN '07' THEN 'JUL' \n"
                + "            WHEN '08' THEN 'AGO' \n"
                + "            WHEN '09' THEN 'SEP' \n"
                + "            WHEN '10' THEN 'OCT' \n"
                + "            WHEN '11' THEN 'NOV' \n"
                + "            WHEN '12' THEN 'DIC' \n"
                + "        END AS titulo, \n"
                + "        fecha, '#0A2472' AS color, 8 AS area \n"
                + "    FROM cronogramaPLE_Diario \n"
                + " \n"
                + "	UNION ALL \n"
                + " \n"
                + "	SELECT \n"
                + "		'DJ AFP ' + \n"
                + "		CASE mes \n"
                + "            WHEN '01' THEN 'ENE' \n"
                + "            WHEN '02' THEN 'FEB' \n"
                + "            WHEN '03' THEN 'MAR' \n"
                + "            WHEN '04' THEN 'ABR' \n"
                + "            WHEN '05' THEN 'MAY' \n"
                + "            WHEN '06' THEN 'JUN' \n"
                + "            WHEN '07' THEN 'JUL' \n"
                + "            WHEN '08' THEN 'AGO' \n"
                + "            WHEN '09' THEN 'SEP' \n"
                + "            WHEN '10' THEN 'OCT' \n"
                + "            WHEN '11' THEN 'NOV' \n"
                + "            WHEN '12' THEN 'DIC' \n"
                + "		END AS titulo, \n"
                + "		fecha, '#1B3B96' AS color, 4 AS area \n"
                + "    FROM cronogramaAfp \n"
                + " \n"
                + "     UNION ALL \n"
                + " \n"
                + "	SELECT \n"
                + "		'CTS ' + \n"
                + "		CASE mes \n"
                + "            WHEN '05' THEN 'MAY' \n"
                + "            WHEN '11' THEN 'NOV' \n"
                + "		END AS titulo, \n"
                + "		fecha, '#274690' AS color, 4 AS area \n"
                + "    FROM cronogramaCts \n"
                + "\n"
                + "	UNION ALL \n"
                + " \n"
                + "	SELECT \n"
                + "		'GRATIFICACION ' + \n"
                + "		CASE mes  \n"
                + "            WHEN '07' THEN 'JUL' \n"
                + "            WHEN '12' THEN 'DIC' \n"
                + "		END AS titulo, \n"
                + "		fecha, '#2C6EBA' AS color, 4 AS area \n"
                + "    FROM cronogramaGratificacion"
                + " \n"
                + "	UNION ALL \n"
                + " \n"
                + "	SELECT \n"
                + "		'DJ DETRAC ' + \n"
                + "		CASE mes \n"
                + "            WHEN '01' THEN 'ENE' \n"
                + "            WHEN '02' THEN 'FEB' \n"
                + "            WHEN '03' THEN 'MAR' \n"
                + "            WHEN '04' THEN 'ABR' \n"
                + "            WHEN '05' THEN 'MAY' \n"
                + "            WHEN '06' THEN 'JUN' \n"
                + "            WHEN '07' THEN 'JUL' \n"
                + "            WHEN '08' THEN 'AGO' \n"
                + "            WHEN '09' THEN 'SEP' \n"
                + "            WHEN '10' THEN 'OCT' \n"
                + "            WHEN '11' THEN 'NOV' \n"
                + "            WHEN '12' THEN 'DIC' \n"
                + "		END AS titulo, \n"
                + "		fecha, '#4682B4' AS color, 8 AS area \n"
                + "	FROM cronogramaDetracciones \n"
                + " \n"
                + "	UNION ALL \n"
                + " \n"
                + "	SELECT \n"
                + "        titulos.titulo, titulos.fecha, '#3498DB' AS color, 8 AS area \n"
                + "    FROM cronogramaAnual \n"
                + "    CROSS APPLY ( \n"
                + "        VALUES \n"
                + "            ('DJ ANUAL RUC 0', fecha0), \n"
                + "            ('DJ ANUAL RUC 1', fecha1), \n"
                + "            ('DJ ANUAL RUC 2', fecha2), \n"
                + "            ('DJ ANUAL RUC 3', fecha3), \n"
                + "            ('DJ ANUAL RUC 4', fecha4), \n"
                + "            ('DJ ANUAL RUC 5', fecha5), \n"
                + "            ('DJ ANUAL RUC 6', fecha6), \n"
                + "            ('DJ ANUAL RUC 7', fecha7), \n"
                + "            ('DJ ANUAL RUC 8', fecha8), \n"
                + "            ('DJ ANUAL RUC 9', fecha9) \n"
                + "    ) AS titulos(titulo, fecha) \n"
                + " \n"
                + "	UNION ALL \n"
                + " \n"
                + "	SELECT \n"
                + "        titulos.titulo, titulos.fecha, '#7BAAF7' AS color, 8 AS area \n"
                + "    FROM cronogramaReporteLocal \n"
                + "    CROSS APPLY ( \n"
                + "        VALUES \n"
                + "            ('DJ REP LOCAL RUC 0', fecha0), \n"
                + "            ('DJ REP LOCAL RUC 1', fecha1), \n"
                + "            ('DJ REP LOCAL RUC 2 - 3', fecha2), \n"
                + "            ('DJ REP LOCAL RUC 4 - 5', fecha4), \n"
                + "            ('DJ REP LOCAL RUC 6 - 7', fecha6), \n"
                + "            ('DJ REP LOCAL RUC 8 - 9', fecha8), \n"
                + "            ('DJ REP LOCAL BUCS', fechab) \n"
                + "    ) AS titulos(titulo, fecha) \n"
                + " \n"
                + "    UNION ALL \n"
                + " \n"
                + "    SELECT \n"
                + "        titulos.titulo, titulos.fecha, '#3D73B9' AS color, 8 AS area \n"
                + "    FROM cronogramaDaot \n"
                + "    CROSS APPLY ( \n"
                + "        VALUES \n"
                + "            ('DJ DAOT RUC 0', fecha0), \n"
                + "            ('DJ DAOT RUC 1', fecha1), \n"
                + "            ('DJ DAOT RUC 2 - 3', fecha2), \n"
                + "            ('DJ DAOT RUC 4 - 5', fecha4), \n"
                + "            ('DJ DAOT RUC 6 - 7', fecha6), \n"
                + "            ('DJ DAOT RUC 8 - 9', fecha8) \n"
                + "    ) AS titulos(titulo, fecha) \n"
                + " \n"
                + "    UNION ALL \n"
                + " \n"
                + "    SELECT \n"
                + "        titulos.titulo, titulos.fecha, '#4F6D8C' AS color, 8 AS area \n"
                + "    FROM cronogramaDonaciones \n"
                + "    CROSS APPLY ( \n"
                + "        VALUES \n"
                + "            ('DJ DONAC RUC 0', fecha0), \n"
                + "            ('DJ DONAC RUC 1', fecha1), \n"
                + "            ('DJ DONAC RUC 2 - 3', fecha2), \n"
                + "            ('DJ DONAC RUC 4 - 5', fecha4), \n"
                + "            ('DJ DONAC RUC 6 - 7', fecha6), \n"
                + "            ('DJ DONAC RUC 8 - 9', fecha8) \n"
                + "    ) AS titulos(titulo, fecha) \n"
                + " \n"
                + "    UNION ALL \n"
                + " \n"
                + "    SELECT \n"
                + "        titulos.titulo, titulos.fecha, '#5C6F82' AS color, 8 AS area \n"
                + "    FROM cronogramaRfEcr \n"
                + "    CROSS APPLY ( \n"
                + "        VALUES \n"
                + "            ('DJ RF ECR RUC 0', fecha0), \n"
                + "            ('DJ RF ECR RUC 1', fecha1), \n"
                + "            ('DJ RF ECR RUC 2 - 3', fecha2), \n"
                + "            ('DJ RF ECR RUC 4 - 5', fecha4), \n"
                + "            ('DJ RF ECR RUC 6 - 7', fecha6), \n"
                + "            ('DJ RF ECR RUC 8 - 9', fecha8) \n"
                + "    ) AS titulos(titulo, fecha) \n"
                + " \n"
                + "    UNION ALL \n"
                + " \n"
                + "    SELECT \n"
                + "        titulos.titulo, titulos.fecha, '#7A8A99' AS color, 8 AS area \n"
                + "    FROM cronogramaPDT \n"
                + "    CROSS APPLY ( \n"
                + "        VALUES \n"
                + "            ('DJ PDT 648 0', fecha0), \n"
                + "            ('DJ PDT 648 1', fecha1), \n"
                + "            ('DJ PDT 648 2 - 3', fecha2), \n"
                + "            ('DJ PDT 648 4 - 5', fecha4), \n"
                + "            ('DJ PDT 648 6 - 7', fecha6), \n"
                + "            ('DJ PDT 648 8 - 9', fecha8) \n"
                + "    ) AS titulos(titulo, fecha) \n"
                + "    WHERE mes = '03' AND titulos.fecha IS NOT NULL \n"
                + " \n"
                + "    UNION ALL \n"
                + " \n"
                + "    SELECT \n"
                + "        titulos.titulo, titulos.fecha, '#3B3F4E' AS color, 8 AS area \n"
                + "    FROM cronogramaPDT \n"
                + "    CROSS APPLY ( \n"
                + "        VALUES \n"
                + "            ('DJ B. FINAL 0', fecha0), \n"
                + "            ('DJ B. FINAL 1', fecha1), \n"
                + "            ('DJ B. FINAL 2 - 3', fecha2), \n"
                + "            ('DJ B. FINAL 4 - 5', fecha4), \n"
                + "            ('DJ B. FINAL 6 - 7', fecha6), \n"
                + "            ('DJ B. FINAL 8 - 9', fecha8) \n"
                + "    ) AS titulos(titulo, fecha) \n"
                + "    WHERE mes IN ('10','12') AND titulos.fecha IS NOT NULL \n"
                + " \n"
                + "    UNION ALL \n"
                + " \n"
                + "    SELECT \n"
                + "        titulos.titulo, titulos.fecha, '#1E2D3A' AS color, 8 AS area \n"
                + "    FROM cronogramaPDT \n"
                + "    CROSS APPLY ( \n"
                + "        VALUES \n"
                + "            ('DJ IF SSERIF 0', fecha0), \n"
                + "            ('DJ IF SSERIF 1', fecha1), \n"
                + "            ('DJ IF SSERIF 2 - 3', fecha2), \n"
                + "            ('DJ IF SSERIF 4 - 5', fecha4), \n"
                + "            ('DJ IF SSERIF 6 - 7', fecha6), \n"
                + "            ('DJ IF SSERIF 8 - 9', fecha8) \n"
                + "    ) AS titulos(titulo, fecha) \n"
                + "    WHERE mes IN ('06','12') AND titulos.fecha IS NOT NULL \n"
                + " \n"
                + "     UNION ALL \n"
                + " \n"
                + "	SELECT \n"
                + "		'DEV ISC ' + \n"
                + "		CASE mes \n"
                + "            WHEN '03' THEN 'MAR' \n"
                + "            WHEN '06' THEN 'JUN' \n"
                + "            WHEN '09' THEN 'SEP' \n"
                + "            WHEN '12' THEN 'DIC' \n"
                + "		END AS titulo, \n"
                + "		fecha, '#4A5D73' AS color, 8 AS area \n"
                + "	FROM cronogramaDevolucionesISC \n"
                + " \n"
                + "     UNION ALL \n"
                + " \n"
                + "	SELECT \n"
                + "		'DEV LIB. DEO ' + \n"
                + "		CASE mes \n"
                + "            WHEN '01' THEN 'ENE' \n"
                + "            WHEN '04' THEN 'ABR' \n"
                + "            WHEN '07' THEN 'JUL' \n"
                + "            WHEN '10' THEN 'OCT' \n"
                + "		END AS titulo, \n"
                + "		fecha, '#607D8B' AS color, 8 AS area \n"
                + "	FROM cronogramaDevolucionesLiberacionDEO \n"
                + " \n"
                + ") AS calendario \n"
                + "WHERE fecha IS NOT NULL \n";

        try {
            Query query = em.createNativeQuery(sql, Tuple.class);

            @SuppressWarnings("unchecked")
            List<Tuple> resultTuples = query.getResultList();

            for (Tuple tuple : resultTuples) {
                CalendarEvent obj = new CalendarEvent();
                obj.setTitle(tuple.get("titulo", String.class));
                obj.setStart(DateUtils.sqlDateToString(tuple.get("fecha", java.sql.Date.class)));
                obj.setColor(tuple.get("color", String.class));
                obj.setArea(tuple.get("area", Integer.class));
                list.add(obj);
            }

        } catch (Exception e) {
            // TODO: handle exception
            logger.error(e.toString());
        }

        return list;
    }

}
