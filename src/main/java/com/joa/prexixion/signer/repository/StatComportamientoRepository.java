package com.joa.prexixion.signer.repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import com.joa.prexixion.signer.utils.DateUtils;

import com.joa.prexixion.signer.dto.StatComportamiento;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;

@Repository
public class StatComportamientoRepository {

    @PersistenceContext
    private EntityManager em;

    public StatComportamiento getValoresKpis(String anio, String mes, String username) {
        // Calcular mes anterior
        int mesInt = Integer.parseInt(mes);
        int anioInt = Integer.parseInt(anio);

        if (mesInt == 1) {
            mesInt = 12;
            anioInt--;
        } else {
            mesInt--;
        }
        String mesAnterior = String.format("%02d", mesInt);
        String anioAnterior = String.valueOf(anioInt);

        String sql = """
                SELECT anio, mes, ventas, compras, mesIgv,
                       CASE WHEN ventas = 0 OR mesIgv = 0
                            THEN 0
                            ELSE CAST(((mesIgv/ventas) * 100) AS DECIMAL(18, 0))
                       END AS porcentaje
                FROM (
                    -- PERIODO ACTUAL
                    SELECT anio, mes,
                           (COALESCE(ventasG,0) + COALESCE(ventasNetas10,0) + COALESCE(ventasNg,0) + COALESCE(expFactPer,0) + COALESCE(expEmbrPer,0)) AS ventas,
                           (COALESCE(comprasG,0) + COALESCE(comprasNetas10,0) + COALESCE(comprasMixtas,0) + COALESCE(comprasNgE,0) + COALESCE(comprasNg,0) + COALESCE(impComprasG,0)) AS compras,
                           mesIgv
                    FROM PDT621DATANEW pdt621
                    WHERE pdt621.idCliente = :idCliente AND pdt621.anio = :anio AND pdt621.mes = :mes

                    UNION ALL

                    SELECT anio, mes,
                           (COALESCE(t.ventasGravadas18,0) + COALESCE(t.ventasGravadas10,0) + COALESCE(t.ventasNoGravadas,0) + COALESCE(t.ventasFacturadas,0) + COALESCE(t.ventasEmbarcadas,0)) AS ventas,
                           (COALESCE(t.comprasGravadas18,0) + COALESCE(t.comprasGravadas10,0) + COALESCE(t.comprasMixtas,0) + COALESCE(t.comprasNoGravadas,0) + COALESCE(t.comprasExoneradas,0) + COALESCE(t.comprasDuas,0)) AS compras,
                           t.igvMes as mesIgv
                    FROM taxReviewPDT621 t
                    WHERE t.idCliente = :idCliente AND t.anio = :anio AND t.mes = :mes
                    AND NOT EXISTS (
                        SELECT 1
                        FROM pdt621DataNew p
                        WHERE p.idCliente = t.idCliente
                          AND p.anio = t.anio
                          AND p.mes = t.mes
                    )

                    -- PERIODO ANTERIOR
                    UNION ALL

                    SELECT anio, mes,
                           (COALESCE(ventasG,0) + COALESCE(ventasNetas10,0) + COALESCE(ventasNg,0) + COALESCE(expFactPer,0) + COALESCE(expEmbrPer,0)) AS ventas,
                           (COALESCE(comprasG,0) + COALESCE(comprasNetas10,0) + COALESCE(comprasMixtas,0) + COALESCE(comprasNgE,0) + COALESCE(comprasNg,0) + COALESCE(impComprasG,0)) AS compras,
                           mesIgv
                    FROM PDT621DATANEW pdt621
                    WHERE pdt621.idCliente = :idCliente AND pdt621.anio = :anioAnterior AND pdt621.mes = :mesAnterior

                    UNION ALL

                    SELECT anio, mes,
                           (COALESCE(t.ventasGravadas18,0) + COALESCE(t.ventasGravadas10,0) + COALESCE(t.ventasNoGravadas,0) + COALESCE(t.ventasFacturadas,0) + COALESCE(t.ventasEmbarcadas,0)) AS ventas,
                           (COALESCE(t.comprasGravadas18,0) + COALESCE(t.comprasGravadas10,0) + COALESCE(t.comprasMixtas,0) + COALESCE(t.comprasNoGravadas,0) + COALESCE(t.comprasExoneradas,0) + COALESCE(t.comprasDuas,0)) AS compras,
                           t.igvMes as mesIgv
                    FROM taxReviewPDT621 t
                    WHERE t.idCliente = :idCliente AND t.anio = :anioAnterior AND t.mes = :mesAnterior
                    AND NOT EXISTS (
                        SELECT 1
                        FROM pdt621DataNew p
                        WHERE p.idCliente = t.idCliente
                          AND p.anio = t.anio
                          AND p.mes = t.mes
                    )
                ) AS valoresKpis
                """;

        Query query = em.createNativeQuery(sql, Tuple.class);
        query.setParameter("anio", anio);
        query.setParameter("mes", mes);
        query.setParameter("anioAnterior", anioAnterior);
        query.setParameter("mesAnterior", mesAnterior);
        query.setParameter("idCliente", username);

        @SuppressWarnings("unchecked")
        List<Tuple> results = query.getResultList();

        // Identificar actual y anterior
        Tuple actual = results.stream()
                .filter(r -> r.get("anio").equals(anio) && r.get("mes").equals(mes))
                .findFirst().orElse(null);

        Tuple anterior = results.stream()
                .filter(r -> r.get("anio").equals(anioAnterior) && r.get("mes").equals(mesAnterior))
                .findFirst().orElse(null);

        StatComportamiento stat = new StatComportamiento();
        if (actual != null) {
            stat.setVentas(actual.get("ventas", BigDecimal.class).toString());
            stat.setCompras(actual.get("compras", BigDecimal.class).toString());
            stat.setMesIgv(actual.get("mesIgv", BigDecimal.class).toString());
            stat.setPorcentajeIgv(actual.get("porcentaje", BigDecimal.class).toString());
        }

        // Calcular tendencias
        if (actual != null && anterior != null) {
            stat.setTrendVentas(
                    calcTrend(actual.get("ventas", BigDecimal.class), anterior.get("ventas", BigDecimal.class)));
            stat.setTrendCompras(
                    calcTrend(actual.get("compras", BigDecimal.class), anterior.get("compras", BigDecimal.class)));
            //stat.setTrendIgv(calcTrend(actual.get("mesIgv", BigDecimal.class), anterior.get("mesIgv", BigDecimal.class)));
            //stat.setTrendPorcentaje(calcTrend(actual.get("porcentaje", BigDecimal.class), anterior.get("porcentaje", BigDecimal.class)));
        }

        return stat;
    }

    private String calcTrend(BigDecimal actual, BigDecimal anterior) {
        if (anterior == null || anterior.compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }
        BigDecimal diferencia = actual.subtract(anterior);
        BigDecimal porcentaje = diferencia
                .divide(anterior, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        return porcentaje.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public StatComportamiento getSaludTributaria(String anio, String mes, String username) {
        String sql = """
                SELECT
                (ventasGravadas + ventasGravadas10 + ventasNoGravadas + exportacionesFacturadas + exportacionesEmbarcadas) AS totalVentas,
                (ventasGravadasIgv + ventasGravadas10Igv) AS totalVentasIgv,
                (comprasGravadas + comprasGravadas10 + comprasMixtas + comprasNoGravadasExclusicamente + comprasNoGravadas + importaciones) AS totalCompras,
                (comprasGravadasIgv + comprasGravadas10Igv + comprasMixtasIgv + importacionesIgv) AS totalComprasIgv
                FROM (
                SELECT
                cli.ruc, cli.razonSocial,

                COALESCE(pdt621.ventasG, 0) AS ventasGravadas,
                COALESCE(pdt621.ventasNetas10, 0) AS ventasGravadas10,
                COALESCE(pdt621.ventasNg, 0) AS ventasNoGravadas,
                COALESCE(pdt621.expFactPer, 0) AS exportacionesFacturadas,
                COALESCE(pdt621.expEmbrPer, 0) AS exportacionesEmbarcadas,
                CAST ( (COALESCE(pdt621.ventasG, 0) * i.valor) AS DECIMAL(18, 0)) AS ventasGravadasIgv,
                CAST ( (COALESCE(pdt621.ventasNetas10, 0) * 0.10) AS DECIMAL(18, 0)) AS ventasGravadas10Igv,

                COALESCE(pdt621.comprasG, 0) AS comprasGravadas,
                COALESCE(pdt621.comprasNetas10, 0) AS comprasGravadas10,
                COALESCE(pdt621.comprasMixtas, 0) AS comprasMixtas,
                COALESCE(pdt621.comprasNgE, 0) AS comprasNoGravadasExclusicamente,
                COALESCE(pdt621.comprasNg, 0) AS comprasNoGravadas,
                COALESCE(pdt621.impComprasG, 0) AS importaciones,
                CAST ( (COALESCE(pdt621.comprasG, 0) * i.valor) AS DECIMAL(18, 0)) AS comprasGravadasIgv,
                CAST ( (COALESCE(pdt621.comprasNetas10, 0) * 0.10) AS DECIMAL(18, 0)) AS comprasGravadas10Igv,
                CAST ( (COALESCE(pdt621.comprasMixtas, 0) * i.valor * pdt621.coeficiente) AS DECIMAL(18, 0)) AS comprasMixtasIgv,
                CAST ( (COALESCE(pdt621.impComprasGigv, 0)) AS DECIMAL(18, 0)) AS importacionesIgv

                FROM cliente cli
                LEFT JOIN PDT621DATANEW pdt621 ON cli.ruc = pdt621.idCliente AND pdt621.anio = :anio AND pdt621.mes = :mes
                LEFT JOIN igv i ON i.anio = :anio AND i.mes = :mes
                WHERE cli.ruc = :idCliente
                ) AS basicData;
                """;

        Query query = em.createNativeQuery(sql, Tuple.class);
        query.setParameter("anio", anio);
        query.setParameter("mes", mes);
        query.setParameter("idCliente", username);
        Tuple tuple = (Tuple) query.getSingleResult();

        StatComportamiento stat = new StatComportamiento();
        stat.setVentas(tuple.get("totalVentas", BigDecimal.class).toString());
        stat.setTotalVentasIgv(tuple.get("totalVentasIgv", BigDecimal.class).toString());
        stat.setCompras(tuple.get("totalCompras", BigDecimal.class).toString());
        stat.setTotalComprasIgv(tuple.get("totalComprasIgv", BigDecimal.class).toString());

        return stat;
    }

    public List<StatComportamiento> getHistoricoEnSoles(String anio, String username) {
        List<StatComportamiento> stats = new ArrayList<>();
        List<StatComportamiento> statsResultSet = new ArrayList<>();
        List<StatComportamiento> statsPeriodos = new ArrayList<>();

        String fechaFinal = anio + "-12-01";
        String fechaInicial = DateUtils.restarMeses(fechaFinal, 11);
        List<String> periodosList = DateUtils.getFechasBetweenStrings(fechaInicial, fechaFinal);

        for (String string : periodosList) {
            StatComportamiento stat = new StatComportamiento();
            stat.setPeriodo(DateUtils.getAbrMonthNameCamelCase(string.substring(5, 7)) + "-" + string.substring(2, 4));
            statsPeriodos.add(stat);
        }

        String sql = """
                SELECT anio, mes,
                (ventasGravadas + ventasGravadas10 + ventasNoGravadas + exportacionesFacturadas + exportacionesEmbarcadas) AS totalVentas,
                (ventasGravadasIgv + ventasGravadas10Igv) AS totalVentasIgv,
                (comprasGravadas + comprasGravadas10 + comprasMixtas + comprasNoGravadasExclusicamente + comprasNoGravadas + importaciones) AS totalCompras,
                (comprasGravadasIgv + comprasGravadas10Igv + comprasMixtasIgv + importacionesIgv) AS totalComprasIgv
                FROM (
                    SELECT
                    pdt621.anio, pdt621.mes,
                    COALESCE(pdt621.ventasG, 0) AS ventasGravadas,
                    COALESCE(pdt621.ventasNetas10, 0) AS ventasGravadas10,
                    COALESCE(pdt621.ventasNg, 0) AS ventasNoGravadas,
                    COALESCE(pdt621.expFactPer, 0) AS exportacionesFacturadas,
                    COALESCE(pdt621.expEmbrPer, 0) AS exportacionesEmbarcadas,
                    CAST ( (COALESCE(pdt621.ventasG, 0) * i.valor) AS DECIMAL(18, 0)) AS ventasGravadasIgv,
                    CAST ( (COALESCE(pdt621.ventasNetas10, 0) * 0.10) AS DECIMAL(18, 0)) AS ventasGravadas10Igv,

                    COALESCE(pdt621.comprasG, 0) AS comprasGravadas,
                    COALESCE(pdt621.comprasNetas10, 0) AS comprasGravadas10,
                    COALESCE(pdt621.comprasMixtas, 0) AS comprasMixtas,
                    COALESCE(pdt621.comprasNgE, 0) AS comprasNoGravadasExclusicamente,
                    COALESCE(pdt621.comprasNg, 0) AS comprasNoGravadas,
                    COALESCE(pdt621.impComprasG, 0) AS importaciones,
                    CAST ( (COALESCE(pdt621.comprasG, 0) * i.valor) AS DECIMAL(18, 0)) AS comprasGravadasIgv,
                    CAST ( (COALESCE(pdt621.comprasNetas10, 0) * 0.10) AS DECIMAL(18, 0)) AS comprasGravadas10Igv,
                    CAST ( (COALESCE(pdt621.comprasMixtas, 0) * i.valor) AS DECIMAL(18, 0)) AS comprasMixtasIgv,
                    CAST ( (COALESCE(pdt621.impComprasG, 0) * i.valor) AS DECIMAL(18, 0)) AS importacionesIgv

                    FROM PDT621DATANEW pdt621
                    LEFT JOIN igv i ON i.anio = pdt621.anio AND i.mes = pdt621.mes
                    WHERE
                    ( ( CAST(pdt621.anio + pdt621.mes + '01' as date) <= :fechaFinal)
                    AND
                    ( CAST(pdt621.anio + pdt621.mes + '01' as date) >= :fechaInicial) )
                    AND pdt621.idCliente = :idCliente

                    UNION ALL

                    SELECT
                    t.anio, t.mes,
                    COALESCE(t.ventasGravadas18, 0) AS ventasGravadas,
                    COALESCE(t.ventasGravadas10, 0) AS ventasGravadas10,
                    COALESCE(t.ventasNoGravadas, 0) AS ventasNoGravadas,
                    COALESCE(t.ventasFacturadas, 0) AS exportacionesFacturadas,
                    COALESCE(t.ventasEmbarcadas, 0) AS exportacionesEmbarcadas,
                    CAST ( (COALESCE(t.ventasGravadas18, 0) * i.valor) AS DECIMAL(18, 0)) AS ventasGravadasIgv,
                    CAST ( (COALESCE(t.ventasGravadas10, 0) * 0.10) AS DECIMAL(18, 0)) AS ventasGravadas10Igv,

                    COALESCE(t.comprasGravadas18, 0) AS comprasGravadas,
                    COALESCE(t.comprasGravadas10, 0) AS comprasGravadas10,
                    COALESCE(t.comprasMixtas, 0) AS comprasMixtas,
                    COALESCE(t.comprasNoGravadas, 0) AS comprasNoGravadasExclusicamente,
                    COALESCE(t.comprasExoneradas, 0) AS comprasNoGravadas,
                    COALESCE(t.comprasDuas, 0) AS importaciones,
                    CAST ( (COALESCE(t.comprasGravadas18, 0) * i.valor) AS DECIMAL(18, 0)) AS comprasGravadasIgv,
                    CAST ( (COALESCE(t.comprasGravadas10, 0) * 0.10) AS DECIMAL(18, 0)) AS comprasGravadas10Igv,
                    CAST ( (COALESCE(t.comprasMixtas, 0) * i.valor) AS DECIMAL(18, 0)) AS comprasMixtasIgv,
                    CAST ( (COALESCE(t.comprasDuas, 0) * i.valor) AS DECIMAL(18, 0)) AS importacionesIgv

                    FROM taxReviewPDT621 t
                    LEFT JOIN igv i ON i.anio = t.anio AND i.mes = t.mes
                    WHERE
                    ( ( CAST(t.anio + t.mes + '01' as date) <= :fechaFinal)
                    AND
                    ( CAST(t.anio + t.mes + '01' as date) >= :fechaInicial) )
                    AND t.idCliente = :idCliente
                    AND NOT EXISTS (
                    SELECT 1
                                FROM pdt621DataNew p
                                WHERE p.idCliente = t.idCliente
                                AND p.anio = t.anio
                                AND p.mes = t.mes
                    )
                ) AS historicoEnSoles
                order by anio, mes asc
                """;

        Query query = em.createNativeQuery(sql, Tuple.class);
        query.setParameter("fechaInicial", fechaInicial);
        query.setParameter("fechaFinal", fechaFinal);
        query.setParameter("idCliente", username);

        @SuppressWarnings("unchecked")
        List<Tuple> resultTuples = query.getResultList();

        for (Tuple tuple : resultTuples) {
            StatComportamiento stat = new StatComportamiento();
            stat.setPeriodo(DateUtils.getAbrMonthNameCamelCase(tuple.get("mes", String.class)) + "-"
                    + tuple.get("anio", String.class).substring(2, 4));
            stat.setVentas(tuple.get("totalVentas", BigDecimal.class).toString());
            stat.setTotalVentasIgv(tuple.get("totalVentasIgv", BigDecimal.class).toString());
            stat.setCompras(tuple.get("totalCompras", BigDecimal.class).toString());
            stat.setTotalComprasIgv(tuple.get("totalComprasIgv", BigDecimal.class).toString());
            statsResultSet.add(stat);
        }

        for (StatComportamiento statPeriodo : statsPeriodos) {
            // Agregamos valores por defecto en caso no tengan datos en ventas,
            // totalVentasIgv, etc
            StatComportamiento stat = new StatComportamiento();
            stat.setPeriodo(statPeriodo.getPeriodo());
            stat.setVentas("0.00");
            stat.setTotalVentasIgv("0");
            stat.setCompras("0.00");
            stat.setTotalComprasIgv("0");
            for (StatComportamiento statResultSet : statsResultSet) {
                if (statPeriodo.getPeriodo().equalsIgnoreCase(statResultSet.getPeriodo())) {
                    // Agregamos valores ventas, totalVentasIgv, etc en caso se encuentre un periodo
                    // homólogo en el resultset
                    stat.setVentas(statResultSet.getVentas());
                    stat.setTotalVentasIgv(statResultSet.getTotalVentasIgv());
                    stat.setCompras(statResultSet.getCompras());
                    stat.setTotalComprasIgv(statResultSet.getTotalComprasIgv());
                }
            }
            // Agregamos el objeto asi tenga o no datos en ventas, totalVentasIgv, etc
            stats.add(stat);
        }

        return stats;
    }

    public List<StatComportamiento> getHistoricoIGV(String anio, String username) {
        List<StatComportamiento> stats = new ArrayList<>();
        List<StatComportamiento> statsResultSet = new ArrayList<>();
        List<StatComportamiento> statsPeriodos = new ArrayList<>();

        String fechaFinal = anio + "-12-01";
        // String fechaFinal = DateUtils.restarMeses(fechaParametro, 12);
        String fechaInicial = DateUtils.restarMeses(fechaFinal, 11);
        List<String> periodosList = DateUtils.getFechasBetweenStrings(fechaInicial, fechaFinal);

        for (String string : periodosList) {
            StatComportamiento stat = new StatComportamiento();
            stat.setPeriodo(DateUtils.getAbrMonthNameCamelCase(string.substring(5, 7)) + "-" + string.substring(2, 4));
            stat.setAnio(string.substring(0, 4));
            statsPeriodos.add(stat);
        }

        String sql = """
                SELECT anio, mes, ventas, compras, mesIgv,
                CASE WHEN ventas = 0 OR mesIgv = 0
                     THEN 0
                     ELSE CAST( ((mesIgv/ventas) * 100) AS DECIMAL(18, 0))
                END AS porcentaje, mesPer, mesRet, saldo
                FROM (
                    SELECT anio, mes,
                        (COALESCE(ventasG,0) + COALESCE(ventasNetas10,0) + COALESCE(ventasNg,0) + COALESCE(expFactPer,0) + COALESCE(expEmbrPer,0)) AS ventas,
                        (COALESCE(comprasG,0) + COALESCE(comprasNetas10,0) + COALESCE(comprasMixtas,0) + COALESCE(comprasNgE,0) + COALESCE(comprasNg,0) + COALESCE(impComprasG,0)) AS compras,
                        mesIgv, mesPer, mesRet,
                        (COALESCE(mesIgv,0) + COALESCE(mesPer,0) + COALESCE(mesRet,0)) AS saldo
                    FROM PDT621DATANEW pdt621
                    WHERE ( ( CAST(pdt621.anio + pdt621.mes + '01' as date) <= :fechaFinal)
                    AND ( CAST(pdt621.anio + pdt621.mes + '01' as date) >= :fechaInicial) )
                    AND pdt621.idCliente = :idCliente

                    UNION ALL

                    SELECT t.anio, t.mes,
                        (COALESCE(t.ventasGravadas18,0) + COALESCE(t.ventasGravadas10,0) + COALESCE(t.ventasNoGravadas,0) + COALESCE(t.ventasFacturadas,0) + COALESCE(t.ventasEmbarcadas,0)) AS ventas,
                        (COALESCE(t.comprasGravadas18,0) + COALESCE(t.comprasGravadas10,0) + COALESCE(t.comprasMixtas,0) + COALESCE(t.comprasNoGravadas,0) + COALESCE(t.comprasExoneradas,0) + COALESCE(t.comprasDuas,0)) AS compras,
                        t.igvMes as mesIgv, t.percepcionesMes as mesPer, t.retencionesMes as mesRet,
                        (COALESCE(t.igvMes,0) + COALESCE(t.percepcionesMes,0) + COALESCE(t.retencionesMes,0)) AS saldo
                    FROM taxReviewPDT621 t
                    WHERE ( ( CAST(t.anio + t.mes + '01' as date) <= :fechaFinal)
                    AND ( CAST(t.anio + t.mes + '01' as date) >= :fechaInicial) )
                    AND t.idCliente = :idCliente
                    AND NOT EXISTS ( SELECT 1
                                    FROM pdt621DataNew p
                                    WHERE p.idCliente = t.idCliente
                                    AND p.anio = t.anio
                                    AND p.mes = t.mes
                                    )
                ) AS historicoIgv
                ORDER BY anio, mes asc
                """;

        Query query = em.createNativeQuery(sql, Tuple.class);
        query.setParameter("fechaInicial", fechaInicial);
        query.setParameter("fechaFinal", fechaFinal);
        query.setParameter("idCliente", username);

        @SuppressWarnings("unchecked")
        List<Tuple> resultTuples = query.getResultList();

        for (Tuple tuple : resultTuples) {
            StatComportamiento stat = new StatComportamiento();
            stat.setPeriodo(DateUtils.getAbrMonthNameCamelCase(tuple.get("mes", String.class)) + "-"
                    + tuple.get("anio", String.class).substring(2, 4));
            stat.setVentas(tuple.get("ventas", BigDecimal.class).toString());
            stat.setCompras(tuple.get("compras", BigDecimal.class).toString());
            stat.setMesIgv(tuple.get("mesIgv", BigDecimal.class).toString());
            stat.setPorcentajeIgv(tuple.get("porcentaje", BigDecimal.class).toString());
            stat.setMesPercepciones(tuple.get("mesPer", BigDecimal.class).toString());
            stat.setMesRetenciones(tuple.get("mesRet", BigDecimal.class).toString());
            stat.setSaldo(tuple.get("saldo", BigDecimal.class).toString());
            statsResultSet.add(stat);
        }

        for (StatComportamiento statPeriodo : statsPeriodos) {
            // Agregamos valores por defecto en caso no tengan datos en ventas, compras, etc
            StatComportamiento stat = new StatComportamiento();
            stat.setPeriodo(statPeriodo.getPeriodo());
            stat.setAnio(statPeriodo.getAnio());
            stat.setVentas("0.00");
            stat.setCompras("0.00");
            stat.setMesIgv("0.00");
            stat.setPorcentajeIgv("0");
            stat.setMesPercepciones("0.00");
            stat.setMesRetenciones("0.00");
            stat.setSaldo("0.00");
            for (StatComportamiento statResultSet : statsResultSet) {
                if (statPeriodo.getPeriodo().equalsIgnoreCase(statResultSet.getPeriodo())) {
                    // Agregamos valores ventas, compras, etc en caso se encuentre un periodo
                    // homólogo en el resultset
                    stat.setVentas(statResultSet.getVentas());
                    stat.setCompras(statResultSet.getCompras());
                    stat.setMesIgv(statResultSet.getMesIgv());
                    stat.setPorcentajeIgv(statResultSet.getPorcentajeIgv());
                    stat.setMesPercepciones(statResultSet.getMesPercepciones());
                    stat.setMesRetenciones(statResultSet.getMesRetenciones());
                    stat.setSaldo(statResultSet.getSaldo());
                }
            }
            // Agregamos el objeto asi tenga o no datos en ventas, compras, etc
            stats.add(stat);
        }

        return stats;
    }

    public List<StatComportamiento> getComportamientoIGV(String anio, String username) {
        List<StatComportamiento> stats = new ArrayList<>();
        List<StatComportamiento> statsResultSet = new ArrayList<>();
        List<StatComportamiento> statsPeriodos = new ArrayList<>();

        String fechaFinal = anio + "-12-01";
        // String fechaFinal = DateUtils.restarMeses(fechaParametro, 12);
        String fechaInicial = DateUtils.restarMeses(fechaFinal, 11);
        List<String> periodosList = DateUtils.getFechasBetweenStrings(fechaInicial, fechaFinal);

        for (String string : periodosList) {
            StatComportamiento stat = new StatComportamiento();
            stat.setPeriodo(DateUtils.getAbrMonthNameCamelCase(string.substring(5, 7)) + "-" + string.substring(2, 4));
            stat.setAnio(string.substring(0, 4));
            statsPeriodos.add(stat);
        }

        String sql = """
                SELECT anio, mes, mesIgv
                FROM (
                    SELECT anio, mes, mesIgv
                    FROM PDT621DATANEW pdt621
                    WHERE ( ( CAST(pdt621.anio + pdt621.mes + '01' as date) <= :fechaFinal)
                    AND ( CAST(pdt621.anio + pdt621.mes + '01' as date) >= :fechaInicial) )
                    AND pdt621.idCliente = :idCliente

                    UNION ALL

                    SELECT t.anio, t.mes, t.igvMes as mesIgv
                    FROM taxReviewPDT621 t
                    WHERE ( ( CAST(t.anio + t.mes + '01' as date) <= :fechaFinal)
                    AND ( CAST(t.anio + t.mes + '01' as date) >= :fechaInicial) )
                    AND t.idCliente = :idCliente
                    AND NOT EXISTS ( SELECT 1
                                    FROM pdt621DataNew p
                                    WHERE p.idCliente = t.idCliente
                                    AND p.anio = t.anio
                                    AND p.mes = t.mes
                                    )
                ) AS historicoIgv
                ORDER BY anio, mes asc
                """;

        Query query = em.createNativeQuery(sql, Tuple.class);
        query.setParameter("fechaInicial", fechaInicial);
        query.setParameter("fechaFinal", fechaFinal);
        query.setParameter("idCliente", username);

        @SuppressWarnings("unchecked")
        List<Tuple> resultTuples = query.getResultList();

        for (Tuple tuple : resultTuples) {
            StatComportamiento stat = new StatComportamiento();
            stat.setPeriodo(DateUtils.getAbrMonthNameCamelCase(tuple.get("mes", String.class)) + "-"
                    + tuple.get("anio", String.class).substring(2, 4));
            stat.setMesIgv(tuple.get("mesIgv", BigDecimal.class).toString());
            statsResultSet.add(stat);
        }

        for (StatComportamiento statPeriodo : statsPeriodos) {
            // Agregamos valores por defecto en caso no tengan datos en ventas, compras, etc
            StatComportamiento stat = new StatComportamiento();
            stat.setPeriodo(statPeriodo.getPeriodo());
            stat.setAnio(statPeriodo.getAnio());
            stat.setMesIgv("0.00");
            for (StatComportamiento statResultSet : statsResultSet) {
                if (statPeriodo.getPeriodo().equalsIgnoreCase(statResultSet.getPeriodo())) {
                    // Agregamos valores ventas, compras, etc en caso se encuentre un periodo
                    // homólogo en el resultset
                    stat.setMesIgv(statResultSet.getMesIgv());
                }
            }
            // Agregamos el objeto asi tenga o no datos en ventas, compras, etc
            stats.add(stat);
        }

        return stats;
    }

    public List<StatComportamiento> getHistoricoRenta(String anio, String username) {
        List<StatComportamiento> stats = new ArrayList<>();
        List<StatComportamiento> statsResultSet = new ArrayList<>();
        List<StatComportamiento> statsPeriodos = new ArrayList<>();

        String fechaFinal = anio + "-12-01";
        String fechaInicial = DateUtils.restarMeses(fechaFinal, 11);
        List<String> periodosList = DateUtils.getFechasBetweenStrings(fechaInicial, fechaFinal);

        for (String string : periodosList) {
            StatComportamiento stat = new StatComportamiento();
            stat.setPeriodo(DateUtils.getAbrMonthNameCamelCase(string.substring(5, 7)) + "-" + string.substring(2, 4));
            statsPeriodos.add(stat);
        }

        String sql = """
                SELECT anio, mes, baseRenta, mesRenta,
                CASE WHEN baseRenta = 0 OR mesRenta = 0
                     THEN 0
                     ELSE CAST( ((mesRenta/baseRenta) * 100) AS DECIMAL(18, 1))
                END AS porcentaje
                FROM (
                SELECT anio, mes, baseRenta, mesRenta
                FROM PDT621DATANEW pdt621
                WHERE
                ( ( CAST(pdt621.anio + pdt621.mes + '01' as date) <= :fechaFinal)
                AND ( CAST(pdt621.anio + pdt621.mes + '01' as date) >= :fechaInicial) )
                AND pdt621.idCliente = :idCliente

                UNION ALL

                SELECT t.anio, t.mes, t.rentaIngreso as baseRenta, t.rentaMes as mesRenta
                FROM taxReviewPDT621 t
                WHERE
                ( ( CAST(t.anio + t.mes + '01' as date) <= :fechaFinal)
                AND ( CAST(t.anio + t.mes + '01' as date) >= :fechaInicial) )
                AND t.idCliente = :idCliente
                AND NOT EXISTS (
                SELECT 1
                		FROM pdt621DataNew p
                             WHERE p.idCliente = t.idCliente
                                 AND p.anio = t.anio
                                 AND p.mes = t.mes
                )
                ) AS historicoRenta
                order by anio, mes asc
                """;

        Query query = em.createNativeQuery(sql, Tuple.class);
        query.setParameter("fechaInicial", fechaInicial);
        query.setParameter("fechaFinal", fechaFinal);
        query.setParameter("idCliente", username);

        @SuppressWarnings("unchecked")
        List<Tuple> resultTuples = query.getResultList();

        for (Tuple tuple : resultTuples) {
            StatComportamiento stat = new StatComportamiento();
            stat.setPeriodo(DateUtils.getAbrMonthNameCamelCase(tuple.get("mes", String.class)) + "-"
                    + tuple.get("anio", String.class).substring(2, 4));
            stat.setBaseRenta(tuple.get("baseRenta", BigDecimal.class).toString());
            stat.setMesRenta(tuple.get("mesRenta", BigDecimal.class).toString());
            stat.setPorcentajeRenta(tuple.get("porcentaje", BigDecimal.class).toString());
            statsResultSet.add(stat);
        }

        for (StatComportamiento statPeriodo : statsPeriodos) {
            // Agregamos valores por defecto en caso no tengan datos en baseRenta, mesRenta,
            // etc
            StatComportamiento stat = new StatComportamiento();
            stat.setPeriodo(statPeriodo.getPeriodo());
            stat.setBaseRenta("0.00");
            stat.setMesRenta("0.00");
            stat.setPorcentajeRenta("0.0");
            for (StatComportamiento statResultSet : statsResultSet) {
                if (statPeriodo.getPeriodo().equalsIgnoreCase(statResultSet.getPeriodo())) {
                    // Agregamos valores baseRenta, mesRenra, etc en caso se encuentre un periodo
                    // homólogo en el resultset
                    stat.setBaseRenta(statResultSet.getBaseRenta());
                    stat.setMesRenta(statResultSet.getMesRenta());
                    stat.setPorcentajeRenta(statResultSet.getPorcentajeRenta());
                }
            }
            // Agregamos el objeto asi tenga o no datos en baseRenta, mesRenta, etc
            stats.add(stat);
        }

        return stats;
    }
}
