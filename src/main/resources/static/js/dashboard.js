var historicoEnSolesPasadoGlobalChart;
var historicoEnSolesActualGlobalChart;
var comportamientoIGVPasadoGlobalChart;
var comportamientoIGVActualGlobalChart;
var ventasVsComprasPasadoGlobalChart;
var ventasVsComprasActualGlobalChart;

function getHistoricoEnSoles(anio, anioLabel, grafico, graficoGlobal) {
  fetch(`/api/dashboard/getHistoricoEnSoles?anio=${anio}`, {
    method: "GET",
    headers: {
      Accept: "application/json",
    },
    credentials: "same-origin",
  })
    .then((res) => res.json())
    .then((data) => {
      let statsHistoricosEnSoles = data.statsHistoricosEnSoles;

      let labelsHS = [];
      let dataVentasHS = [];
      let dataTotalVentasIgvHS = [];
      let dataComprasHS = [];
      let dataTotalComprasIgvHS = [];
      for (var i = 0; i < statsHistoricosEnSoles.length; i++) {
        labelsHS.push(statsHistoricosEnSoles[i].periodo);
        dataVentasHS.push(statsHistoricosEnSoles[i].ventas);
        dataTotalVentasIgvHS.push(statsHistoricosEnSoles[i].totalVentasIgv);
        dataComprasHS.push(statsHistoricosEnSoles[i].compras);
        dataTotalComprasIgvHS.push(statsHistoricosEnSoles[i].totalComprasIgv);
      }

      let historicoEnSolesActualChart = document.getElementById(grafico);

      let historicoEnSolesData = {
        labels: labelsHS,

        datasets: [
          {
            label: "VENTAS",
            barPercentage: 0.8,
            categoryPercentage: 0.7,
            barThickness: undefined,
            backgroundColor: "rgb(88, 56, 202)",
            data: dataVentasHS,
          },
          {
            label: "VENTAS IGV",
            barPercentage: 0.8,
            categoryPercentage: 0.7,
            barThickness: undefined,
            backgroundColor: "rgb(183, 173, 226)",
            data: dataTotalVentasIgvHS,
          },
          {
            label: "COMPRAS",
            barPercentage: 0.8,
            categoryPercentage: 0.7,
            barThickness: undefined,
            backgroundColor: "rgb(0, 168, 150)",
            data: dataComprasHS,
          },
          {
            label: "COMPRAS IGV",
            barPercentage: 0.8,
            categoryPercentage: 0.7,
            barThickness: undefined,
            backgroundColor: "rgb(145, 224, 214)",
            data: dataTotalComprasIgvHS,
          },
        ],
      };

      if (historicoEnSolesActualChart) {
        if (graficoGlobal) {
          graficoGlobal.destroy();
        }
        graficoGlobal = new Chart(historicoEnSolesActualChart, {
          type: "bar",
          data: historicoEnSolesData,
          options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
              xAxes: [
                {
                  gridLines: {
                    display: false,
                  },
                  ticks: {
                    fontColor: "#343a40",
                    fontStyle: "500", // también puedes usar "bold"
                    fontSize: 12,
                  },
                },
              ],
              yAxes: [
                {
                  gridLines: {
                    color: "#e0e0e0", // sigue mostrando la grilla horizontal
                    drawTicks: false, // quita las pequeñas líneas en los ticks
                    drawBorder: false, // quita la línea vertical del eje
                  },
                  ticks: {
                    beginAtZero: true,
                    fontColor: "#343a40", // gris oscuro estilo Azia
                    fontStyle: "500", // o "600" para seminegrita
                    fontSize: 12,
                    callback: function (value) {
                      return addCommas(Math.round(value));
                    },
                  },
                },
              ],
            },
            legend: {
              display: true,
              position: "top",
              labels: {
                fontColor: "#333",
                fontSize: 12,
              },
            },
            title: {
              display: true,
              text: "Histórico en Soles " + anio + " (Ventas y Compras)",
              fontSize: 18,
              fontColor: "#333",
              padding: 20,
            },
            tooltips: {
              callbacks: {
                label: function (tooltipItem, data) {
                  var value =
                    data.datasets[tooltipItem.datasetIndex].data[
                    tooltipItem.index
                    ];
                  value = value.toString();
                  return addCommas(Math.round(value));
                },
              },
            },
          },
        });
      }
    })
    .catch((err) => console.error("Error al cargar resumen:", err));
}

function getHistoricoIGV(anio, anioLabel, tabla) {
  fetch(`/api/dashboard/getHistoricoIGV?anio=${anio}`, {
    method: "GET",
    headers: {
      Accept: "application/json",
    },
    credentials: "same-origin",
  })
    .then((res) => res.json())
    .then((data) => {
      //PINTADO DE LA TABLA HISTÓRICO IGV 1
      $("#" + tabla + " tbody tr").remove();
      $("#" + tabla + " tfoot tr").remove();
      let totalVentasHistoricoIgv = 0;
      let totalComprasHistoricoIgv = 0;
      let totalMesIgvHistoricoIgv = 0;
      let totalPorcentajeHistoricoIgv = 0;
      let cantidadPorcentajeHistoricoIgv = 0;
      let promedioPorcentajeHistoricoIgv = 0;
      let totalMesPercepcionesHistoricoIgv = 0;
      let totalMesRetencionesHistoricoIgv = 0;
      let totalSaldoHistoricoIgv = 0;

      document.getElementById("" + anioLabel).textContent = anio;

      console.log(data);

      let statsHistoricoIGV = data.statsHistoricoIGV;

      let jPdf = 1;
      statsHistoricoIGV.forEach(function (e) {
        let ventasCol =
          e.ventas === "0.00" ? "" : "S/ " + addCommas(Math.round(e.ventas));
        let comprasCol =
          e.compras === "0.00" ? "" : "S/ " + addCommas(Math.round(e.compras));
        let mesIgvCol =
          e.mesIgv === "0.00" ? "" : "S/ " + addCommas(Math.round(e.mesIgv));
        let mesPercepcionesCol =
          e.mesPercepciones === "0.00"
            ? ""
            : "S/ " + addCommas(Math.round(e.mesPercepciones));
        let mesRetencionesCol =
          e.mesRetenciones === "0.00"
            ? ""
            : "S/ " + addCommas(Math.round(e.mesRetenciones));
        let saldoCol =
          e.saldo === "0.00" ? "" : "S/ " + addCommas(Math.round(e.saldo));

        totalVentasHistoricoIgv +=
          e.ventas === "0.00" ? 0 : Math.round(e.ventas);
        totalComprasHistoricoIgv +=
          e.compras === "0.00" ? 0 : Math.round(e.compras);
        if (e.mesIgv > 0) {
          totalMesIgvHistoricoIgv += Math.round(e.mesIgv);
        }
        if (e.porcentajeIgv > 0) {
          totalPorcentajeHistoricoIgv += Math.round(e.porcentajeIgv);
          cantidadPorcentajeHistoricoIgv++;
        }
        jPdf++;

        if (jPdf == statsHistoricoIGV.length) {
          if (totalPorcentajeHistoricoIgv > 0) {
            promedioPorcentajeHistoricoIgv =
              totalPorcentajeHistoricoIgv / cantidadPorcentajeHistoricoIgv;
          }
        }

        totalMesPercepcionesHistoricoIgv +=
          e.mesPercepciones === "0.00" ? 0 : Math.round(e.mesPercepciones);
        totalMesRetencionesHistoricoIgv +=
          e.mesRetenciones === "0.00" ? 0 : Math.round(e.mesRetenciones);
        totalSaldoHistoricoIgv += e.saldo === "0.00" ? 0 : Math.round(e.saldo);

        let dataHistoricoIgvPdf =
          "<tr >" +
          "<td class='text-center table-dashboard-body'>" +
          e.periodo +
          "</td>" +
          "<td class='text-end table-dashboard-body-bold'>" +
          ventasCol +
          "</td>" +
          "<td class='text-end table-dashboard-body-bold'>" +
          comprasCol +
          "</td>" +
          "<td class='text-end table-dashboard-body-bold'>" +
          mesIgvCol +
          "</td>" +
          "<td class='text-center table-dashboard-body-bold'>" +
          e.porcentajeIgv +
          " %" +
          "</td>" +
          "<td class='text-end table-dashboard-body-bold'>" +
          mesPercepcionesCol +
          "</td>" +
          "<td class='text-end table-dashboard-body-bold'>" +
          mesRetencionesCol +
          "</td>" +
          "<td class='text-end table-dashboard-body-bold'>" +
          saldoCol +
          "</td> </tr>";
        $("#" + tabla + " tbody").append(dataHistoricoIgvPdf);
      });

      let totalVentasHistoricoIgvCol =
        totalVentasHistoricoIgv === 0
          ? "0"
          : "S/ " + addCommas(Math.round(totalVentasHistoricoIgv));
      let totalComprasHistoricoIgvCol =
        totalComprasHistoricoIgv === 0
          ? "0"
          : "S/ " + addCommas(Math.round(totalComprasHistoricoIgv));
      let totalMesIgvHistoricoIgvCol =
        totalMesIgvHistoricoIgv === 0
          ? "0"
          : "S/ " + addCommas(Math.round(totalMesIgvHistoricoIgv));
      let promedioPorcentajeHistoricoIgvCol =
        promedioPorcentajeHistoricoIgv === 0
          ? "0"
          : addCommas(Math.round(promedioPorcentajeHistoricoIgv));
      let totalMesPercepcionesHistoricoIgvCol =
        totalMesPercepcionesHistoricoIgv === 0
          ? "0"
          : "S/ " + addCommas(Math.round(totalMesPercepcionesHistoricoIgv));
      let totalMesRetencionesHistoricoIgvCol =
        totalMesRetencionesHistoricoIgv === 0
          ? "0"
          : "S/ " + addCommas(Math.round(totalMesRetencionesHistoricoIgv));
      let totalSaldoHistoricoIgvCol =
        totalSaldoHistoricoIgv === 0
          ? "0"
          : "S/ " + addCommas(Math.round(totalSaldoHistoricoIgv));

      let dataHistoricoIgvFoot =
        "<tr class='total-row'>" +
        "<td class='text-center table-dashboard-footer'>TOTAL</td>" +
        "<td class='text-end table-dashboard-footer'>" +
        totalVentasHistoricoIgvCol +
        "</td>" +
        "<td class='text-end table-dashboard-footer'>" +
        totalComprasHistoricoIgvCol +
        "</td>" +
        "<td class='text-end table-dashboard-footer'>" +
        totalMesIgvHistoricoIgvCol +
        "</td>" +
        "<td class='text-center table-dashboard-footer'>" +
        promedioPorcentajeHistoricoIgvCol +
        " %</td>" +
        "<td class='text-end table-dashboard-footer'>" +
        totalMesPercepcionesHistoricoIgvCol +
        "</td>" +
        "<td class='text-end table-dashboard-footer'>" +
        totalMesRetencionesHistoricoIgvCol +
        "</td>" +
        "<td class='text-end table-dashboard-footer'>" +
        totalSaldoHistoricoIgvCol +
        "</td>" +
        "</tr>";
      $("#" + tabla + " tfoot").append(dataHistoricoIgvFoot);
    })
    .catch((err) => console.error("Error al cargar resumen:", err));
}

function getComportamientoIgv(anio, anioLabel, grafico, graficoGlobal) {
  fetch(`/api/dashboard/getComportamientoIGV?anio=${anio}`, {
    method: "GET",
    headers: {
      Accept: "application/json",
    },
    credentials: "same-origin",
  })
    .then((res) => res.json())
    .then((data) => {
      console.log(data);
      let statsComportamientoIGV = data.statsComportamientoIGV;

      let labels = [];
      let dataVentas = [];
      let dataCompras = [];
      let dataIgv = [];
      let dataPorcentajeIgv = [];
      for (var i = 0; i < statsComportamientoIGV.length; i++) {
        labels.push(statsComportamientoIGV[i].periodo);
        dataVentas.push(statsComportamientoIGV[i].ventas);
        dataCompras.push(statsComportamientoIGV[i].compras);
        dataIgv.push(statsComportamientoIGV[i].mesIgv);
        dataPorcentajeIgv.push(statsComportamientoIGV[i].porcentajeIgv);
      }

      let comportamientoIGVChart = document.getElementById(grafico);
      let barColorsComportamientoIGV = dataIgv.map(value => value >= 0 ? 'rgb(55, 178, 5)' : 'rgb(255, 0, 0)');

      let comportamientoIGVData = {
        labels: labels,

        datasets: [
          {
            label: 'IGV',
            barPercentage: 0.4,
            categoryPercentage: 0.5,
            barThickness: 8,
            backgroundColor: barColorsComportamientoIGV,
            borderColor: 'rgb(75, 192, 192)',
            data: dataIgv
          }
        ]
      };

      if (comportamientoIGVChart) {
        if (graficoGlobal) {
          graficoGlobal.destroy();
        }
        graficoGlobal = new Chart(comportamientoIGVChart, {
          type: 'horizontalBar',
          data: comportamientoIGVData,
          options: {
            scales: {
              xAxes: [{
                ticks: {
                  beginAtZero: true,
                  userCallback: function (value, index, values) {
                    value = value.toString();
                    return addCommas(Math.round(value));
                  }
                }
              }]
            },
            legend: {
              display: false
            },
            title: {
              display: true,
              text: "Comportamiento IGV " + anio,
              fontSize: 18,
              fontColor: "#333",
              padding: 20,
            },
            tooltips: {
              callbacks: {
                label: function (tooltipItem, data) {
                  var value = data.datasets[0].data[tooltipItem.index];
                  value = value.toString();
                  return addCommas(Math.round(value));
                }
              }
            }
          }
        });
      }
    })
    .catch((err) => console.error("Error al cargar resumen:", err));
}

function getVentasVsCompras(anio, anioLabel, grafico, graficoGlobal) {
  fetch(`/api/dashboard/getVentasVsCompras?anio=${anio}`, {
    method: "GET",
    headers: {
      Accept: "application/json",
    },
    credentials: "same-origin",
  })
    .then((res) => res.json())
    .then((data) => {
      let statsVentasVsCompras = data.statsVentasVsCompras;

      let labelsHS = [];
      let dataVentasHS = [];
      let dataTotalVentasIgvHS = [];
      let dataComprasHS = [];
      let dataTotalComprasIgvHS = [];
      for (var i = 0; i < statsHistoricosEnSoles.length; i++) {
        labelsHS.push(statsHistoricosEnSoles[i].periodo);
        dataVentasHS.push(statsHistoricosEnSoles[i].ventas);
        dataTotalVentasIgvHS.push(statsHistoricosEnSoles[i].totalVentasIgv);
        dataComprasHS.push(statsHistoricosEnSoles[i].compras);
        dataTotalComprasIgvHS.push(statsHistoricosEnSoles[i].totalComprasIgv);
      }

      let historicoEnSolesActualChart = document.getElementById(grafico);

      let historicoEnSolesData = {
        labels: labelsHS,

        datasets: [
          {
            label: "VENTAS",
            barPercentage: 0.8,
            categoryPercentage: 0.7,
            barThickness: undefined,
            backgroundColor: "rgb(88, 56, 202)",
            data: dataVentasHS,
          },
          {
            label: "VENTAS IGV",
            barPercentage: 0.8,
            categoryPercentage: 0.7,
            barThickness: undefined,
            backgroundColor: "rgb(183, 173, 226)",
            data: dataTotalVentasIgvHS,
          },
          {
            label: "COMPRAS",
            barPercentage: 0.8,
            categoryPercentage: 0.7,
            barThickness: undefined,
            backgroundColor: "rgb(0, 168, 150)",
            data: dataComprasHS,
          },
          {
            label: "COMPRAS IGV",
            barPercentage: 0.8,
            categoryPercentage: 0.7,
            barThickness: undefined,
            backgroundColor: "rgb(145, 224, 214)",
            data: dataTotalComprasIgvHS,
          },
        ],
      };

      if (historicoEnSolesActualChart) {
        if (graficoGlobal) {
          graficoGlobal.destroy();
        }
        graficoGlobal = new Chart(historicoEnSolesActualChart, {
          type: "bar",
          data: historicoEnSolesData,
          options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
              xAxes: [
                {
                  gridLines: {
                    display: false,
                  },
                  ticks: {
                    fontColor: "#343a40",
                    fontStyle: "500", // también puedes usar "bold"
                    fontSize: 12,
                  },
                },
              ],
              yAxes: [
                {
                  gridLines: {
                    color: "#e0e0e0", // sigue mostrando la grilla horizontal
                    drawTicks: false, // quita las pequeñas líneas en los ticks
                    drawBorder: false, // quita la línea vertical del eje
                  },
                  ticks: {
                    beginAtZero: true,
                    fontColor: "#343a40", // gris oscuro estilo Azia
                    fontStyle: "500", // o "600" para seminegrita
                    fontSize: 12,
                    callback: function (value) {
                      return addCommas(Math.round(value));
                    },
                  },
                },
              ],
            },
            legend: {
              display: true,
              position: "top",
              labels: {
                fontColor: "#333",
                fontSize: 12,
              },
            },
            title: {
              display: true,
              text: "Histórico en Soles " + anio + " (Ventas y Compras)",
              fontSize: 18,
              fontColor: "#333",
              padding: 20,
            },
            tooltips: {
              callbacks: {
                label: function (tooltipItem, data) {
                  var value =
                    data.datasets[tooltipItem.datasetIndex].data[
                    tooltipItem.index
                    ];
                  value = value.toString();
                  return addCommas(Math.round(value));
                },
              },
            },
          },
        });
      }
    })
    .catch((err) => console.error("Error al cargar resumen:", err));
}

// Puedes hacer esto al cargar la página:
document.addEventListener("DOMContentLoaded", function () {
  getHistoricoEnSoles("2024", "historicoEnSolesPasadoAnio", "historicoEnSolesPasadoChart", historicoEnSolesPasadoGlobalChart);
  getHistoricoEnSoles("2025", "historicoEnSolesActualAnio", "historicoEnSolesActualChart", historicoEnSolesActualGlobalChart);

  getHistoricoIGV("2024", "historicoIGVPasadoAnio", "historicoIGVPasadoTabla");
  getHistoricoIGV("2025", "historicoIGVActualAnio", "historicoIGVActualTabla");

  getComportamientoIgv("2024", "comportamientoIGVPasadoAnio", "comportamientoIGVPasadoChart", comportamientoIGVPasadoGlobalChart);
  getComportamientoIgv("2025", "comportamientoIGVActualAnio", "comportamientoIGVActualChart", comportamientoIGVActualGlobalChart);
});
