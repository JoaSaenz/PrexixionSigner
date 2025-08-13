package com.joa.prexixion.signer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatComportamiento {
    private String periodo;
    private String ventas;
    private String totalVentasIgv;
    private String compras;
    private String totalComprasIgv;
    private String mesIgv;
    private String porcentajeIgv;
    private String mesPercepciones;
    private String mesRetenciones;
    private String saldo;
    private String baseRenta;
    private String mesRenta;
    private String porcentajeRenta;
    private String anio;

    // GRÁFICO CANTIDAD DOCUMENTOS
    private String nroDocumentosVentas;
    private String nroDocumentosCompras;

    //TREND KPIs
    private String trendVentas;
    private String trendCompras;
    private String trendIgv;
    private String trendPorcentaje;
}
