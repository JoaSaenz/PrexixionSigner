var sparkVentasGlobalChart;
var sparkComprasGlobalChart;
var sparkIgvGlobalChart;
var sparkPorcentajeGlobalChart;
var saludTributariaGlobalChart;
var historicoEnSolesPasadoGlobalChart;
var historicoEnSolesActualGlobalChart;
var comportamientoIGVPasadoGlobalChart;
var comportamientoIGVActualGlobalChart;
var ventasVsComprasPasadoGlobalChart;
var ventasVsComprasActualGlobalChart;

function getValoresKpis(anio, mes, ventasMes, comprasMes, igvMes, porcentjeMes) {
  fetch(`/api/dashboard/getValoresKpis?anio=${anio}&mes=${mes}`, {
    method: "GET",
    headers: {
      Accept: "application/json",
    },
    credentials: "same-origin",
  })
    .then((res) => res.json())
    .then((data) => {
      let statsValoresKpis = data.statsValoresKpis;
      document.getElementById("" + ventasMes).textContent = statsValoresKpis.ventas === '0.00' ? 0 : addCommas(Math.round(statsValoresKpis.ventas));
      document.getElementById("" + comprasMes).textContent = statsValoresKpis.compras === '0.00' ? 0 : addCommas(Math.round(statsValoresKpis.compras));
      document.getElementById("" + igvMes).textContent = statsValoresKpis.mesIgv === '0.00' ? 0 : addCommas(Math.round(statsValoresKpis.mesIgv));
      document.getElementById("" + porcentjeMes).textContent = statsValoresKpis.porcentajeIgv;

      // Pintar tendencias
      pintarTendencia("trendVentas", statsValoresKpis.trendVentas);
      pintarTendencia("trendCompras", statsValoresKpis.trendCompras);
      pintarTendencia("trendIgv", statsValoresKpis.trendIgv);
      pintarTendencia("trendPorcentaje", statsValoresKpis.trendPorcentaje);
    })
    .catch((err) => console.error("Error al cargar resumen:", err));
}

// Función para poner flechas y colores
function pintarTendencia(idElemento, valor) {
  const elem = document.getElementById(idElemento);
  const num = parseFloat(valor);

  if (isNaN(num) || num === 0) {
    elem.textContent = `→ 0% respecto al mes pasado`;
    elem.className = "kpi-tendencia trend-neutral";
  } else if (num > 0) {
    elem.textContent = `↑ ${num}% respecto al mes pasado`;
    elem.className = "kpi-tendencia trend-up";
  } else {
    elem.textContent = `↓ ${Math.abs(num)}% respecto al mes pasado`;
    elem.className = "kpi-tendencia trend-down";
  }
}

// function renderSparklineVentas(data) {
//   const ctx = document.getElementById('sparkVentas').getContext('2d');

//   // 🔹 Plugin para sombra en la línea + degradado extra hacia abajo
//   Chart.plugins.register({
//     beforeDatasetsDraw: function (chart) {
//       const ctx = chart.ctx;
//       ctx.save();

//       chart.data.datasets.forEach((dataset, i) => {
//         const meta = chart.getDatasetMeta(i);
//         if (!meta.hidden && dataset.type !== 'bar') {
//           // Sombra alrededor de la línea
//           ctx.shadowColor = 'rgba(88, 56, 202, 0.25)';
//           ctx.shadowBlur = 4;   // un poco más difuso
//           ctx.shadowOffsetX = 2;
//           ctx.shadowOffsetY = 3;

//           // 🔹 Degradado vertical debajo de la línea
//           const gradient = ctx.createLinearGradient(0, 0, 0, chart.chartArea.bottom);
//           gradient.addColorStop(0, 'rgba(88, 56, 202, 0.15)');
//           gradient.addColorStop(0.2, 'rgba(88, 56, 202, 0.05)');
//           gradient.addColorStop(1, 'rgba(88, 56, 202, 0)');

//           dataset.backgroundColor = gradient;
//           dataset.fill = true; // aplica solo este degradado
//         }
//       });
//     },
//     afterDatasetsDraw: function (chart) {
//       chart.ctx.restore();
//     }
//   });

//   new Chart(ctx, {
//     type: 'line',
//     data: {
//       labels: data.map(function (_, i) { return i + 1; }),
//       datasets: [{
//         data: data,
//         borderColor: 'rgb(88, 56, 202)',
//         borderWidth: 2,
//         fill: false,        // inicialmente sin relleno
//         lineTension: 0.4,
//         pointRadius: 0,
//         pointHitRadius: 15
//       }]
//     },
//     options: {
//       responsive: true,
//       maintainAspectRatio: false,
//       legend: { display: false },
//       layout: { padding: { top: 10, bottom: 10 } },
//       tooltips: {
//         enabled: false,
//         custom: function (tooltipModel) {
//           let tooltipEl = document.getElementById('chartjs-tooltip');
//           if (!tooltipEl) {
//             tooltipEl = document.createElement('div');
//             tooltipEl.id = 'chartjs-tooltip';
//             tooltipEl.style.position = 'absolute';
//             tooltipEl.style.background = 'rgba(0, 0, 0, 0.7)';
//             tooltipEl.style.color = '#fff';
//             tooltipEl.style.padding = '4px 8px';
//             tooltipEl.style.borderRadius = '4px';
//             tooltipEl.style.pointerEvents = 'none';
//             tooltipEl.style.fontSize = '0.8rem';
//             document.body.appendChild(tooltipEl);
//           }

//           if (tooltipModel.opacity === 0) {
//             tooltipEl.style.opacity = 0;
//             return;
//           }

//           if (tooltipModel.body) {
//             const bodyLines = tooltipModel.body.map(item => item.lines);
//             const formatted = bodyLines.map(value => {
//               const num = parseFloat(value);
//               if (!isNaN(num)) {
//                 return `S/. ${num.toLocaleString('es-PE', {
//                   minimumFractionDigits: 0,
//                   maximumFractionDigits: 0
//                 })}`;
//               }
//               return value;
//             });
//             tooltipEl.innerHTML = formatted.join('<br>');
//           }

//           const position = this._chart.canvas.getBoundingClientRect();
//           tooltipEl.style.opacity = 1;
//           tooltipEl.style.left = position.left + window.pageXOffset + tooltipModel.caretX + 'px';
//           tooltipEl.style.top = position.top + window.pageYOffset + tooltipModel.caretY + 'px';
//         }
//       },
//       hover: { mode: 'nearest', intersect: false },
//       scales: {
//         xAxes: [{ display: false }],
//         yAxes: [{ display: false }]
//       },
//       elements: { line: { borderCapStyle: 'round' } }
//     }
//   });
// }

function getSparklineKpis(anio, mes, grafico, graficoGlobal) {
  fetch(`/api/dashboard/getSparklineKpis?anio=${anio}&mes=${mes}`, {
    method: "GET",
    headers: {
      Accept: "application/json",
    },
    credentials: "same-origin",
  })
    .then((res) => res.json())
    .then((data) => {
      let statsSparklineKpis = data.statsSparklineKpis;
      let labelsSK = [];
      let dataVentasSK = [];
      let dataComprasSK = [];
      let dataIgvSK = [];
      let dataPorcentajeSK = [];
      for (var i = 0; i < statsSparklineKpis.length; i++) {
        labelsSK.push(statsSparklineKpis[i].periodo);
        dataVentasSK.push(statsSparklineKpis[i].ventas);
        dataComprasSK.push(statsSparklineKpis[i].compras);
        dataIgvSK.push(statsSparklineKpis[i].mesIgv);
        dataPorcentajeSK.push(statsSparklineKpis[i].porcentajeIgv);
      }

      // Variables dinámicas
      let colorData;
      let backgroundColorData;
      let chartData;
      let formatoTooltip;

      switch (grafico) {
        case 'sparkVentasChart':
          colorData = 'rgb(88, 56, 202)';
          backgroundColorData = 'rgba(88, 56, 202, 0.2)';
          chartData = dataVentasSK;
          formatoTooltip = (num) => `S/. ${num.toLocaleString('es-PE', {
            minimumFractionDigits: 0,
            maximumFractionDigits: 0
          })}`;
          break;
        case 'sparkComprasChart':
          colorData = 'rgb(0, 168, 150)';
          backgroundColorData = 'rgba(0, 168, 150, 0.2)';
          chartData = dataComprasSK;
          formatoTooltip = (num) => `S/. ${num.toLocaleString('es-PE', {
            minimumFractionDigits: 0,
            maximumFractionDigits: 0
          })}`;
          break;
        case 'sparkIgvChart':
          colorData = 'rgb(76, 110, 219)';
          backgroundColorData = 'rgba(76, 110, 219, 0.2)';
          chartData = dataIgvSK;
          formatoTooltip = (num) => `S/. ${num.toLocaleString('es-PE', {
            minimumFractionDigits: 0,
            maximumFractionDigits: 0
          })}`;
          break;
        case 'sparkPorcentajeChart':
          colorData = 'rgb(139, 123, 207)';
          backgroundColorData = 'rgba(139, 123, 207, 0.2)';
          chartData = dataPorcentajeSK;
          formatoTooltip = (num) => `${Math.round(num)} %`;
          break;
      }

      let sparklineKpiChart = document.getElementById(grafico).getContext('2d');

      let sparklineKpiData = {
        //labels: chartData.map(function (_, i) { return i + 1; }),
        labels: labelsSK,
        datasets: [{
          data: chartData,
          borderColor: colorData,
          backgroundColor: backgroundColorData,
          borderWidth: 2,
          fill: true,
          //fill: false,        // inicialmente sin relleno
          lineTension: 0.4,
          pointRadius: 0,
          pointHitRadius: 15
        }]
      };

      if (sparklineKpiChart) {
        if (graficoGlobal) {
          graficoGlobal.destroy();
        }
        graficoGlobal = new Chart(sparklineKpiChart, {
          type: 'line',
          data: sparklineKpiData,
          options: {
            responsive: true,
            maintainAspectRatio: false,
            legend: { display: false },
            layout: { padding: { top: 10, bottom: 10 } },
            tooltips: {
              enabled: false,
              custom: function (tooltipModel) {
                let tooltipEl = document.getElementById('chartjs-tooltip');
                if (!tooltipEl) {
                  tooltipEl = document.createElement('div');
                  tooltipEl.id = 'chartjs-tooltip';
                  tooltipEl.style.position = 'absolute';
                  tooltipEl.style.background = 'rgba(0, 0, 0, 0.7)';
                  tooltipEl.style.color = '#fff';
                  tooltipEl.style.padding = '4px 8px';
                  tooltipEl.style.borderRadius = '4px';
                  tooltipEl.style.pointerEvents = 'none';
                  tooltipEl.style.fontSize = '0.8rem';
                  document.body.appendChild(tooltipEl);
                }

                if (tooltipModel.opacity === 0) {
                  tooltipEl.style.opacity = 0;
                  return;
                }

                if (tooltipModel.dataPoints && tooltipModel.dataPoints.length) {
                  const dp = tooltipModel.dataPoints[0];
                  const periodo = dp.xLabel;             // ← viene de labelsSK
                  const valor = Number(dp.yLabel);       // ← tu dato numérico

                  tooltipEl.innerHTML = `<div style="opacity:.8;margin-bottom:2px">${periodo}</div>
                  <div><strong>${formatoTooltip(valor)}</strong></div>`;
                }

                const position = this._chart.canvas.getBoundingClientRect();
                tooltipEl.style.opacity = 1;
                tooltipEl.style.left = position.left + window.pageXOffset + tooltipModel.caretX + 'px';
                tooltipEl.style.top = position.top + window.pageYOffset + tooltipModel.caretY + 'px';
              }
            },
            hover: { mode: 'nearest', intersect: false },
            scales: {
              xAxes: [{ display: false }],
              yAxes: [{ display: false }]
            },
            elements: { line: { borderCapStyle: 'round' } }
          }
        });
      }

    })
    .catch((err) => console.error("Error al cargar resumen:", err));
}

function getSaludTributaria(anio, mes, periodoLabel, grafico, graficoGlobal) {
  fetch(`/api/dashboard/getSaludTributaria?anio=${anio}&mes=${mes}`, {
    method: "GET",
    headers: {
      Accept: "application/json",
    },
    credentials: "same-origin",
  })
    .then((res) => res.json())
    .then((data) => {
      console.log(data.statsSaludTributaria);

      let statsSaludTributaria = data.statsSaludTributaria;
      let totalVentasST = statsSaludTributaria.ventas === '0.00' ? 0 : Math.round(statsSaludTributaria.ventas);
      let totalVentasIgvST = statsSaludTributaria.totalVentasIgv === '0.00' ? 0 : Math.round(statsSaludTributaria.totalVentasIgv);
      let totalComprasST = statsSaludTributaria.compras === '0.00' ? 0 : Math.round(statsSaludTributaria.compras);
      let totalComprasIgvST = statsSaludTributaria.totalComprasIgv === '0.00' ? 0 : Math.round(statsSaludTributaria.totalComprasIgv);
      let dataTotalVentasMasVentasIgvST = totalVentasST + totalVentasIgvST;
      let dataTotalComprasMasComprasIgvST = totalComprasST + totalComprasIgvST;

      if (dataTotalVentasMasVentasIgvST < dataTotalComprasMasComprasIgvST) {
        $('#saludTributariaMensaje').text('Alerta, existen posibles contingencias tributarias.');
      } else {
        $('#saludTributariaMensaje').text('');
      }

      //console.log(dataTotalVentasMasVentasIgvST);
      //console.log(dataTotalComprasMasComprasIgvST);

      Chart.plugins.unregister(ChartDataLabels);

      let saludTributariaChart = document.getElementById(grafico);

      document.getElementById("" + periodoLabel).textContent = anio + "-" + mes;

      let saludTributariaData = {
        labels: ['VENTAS', 'COMPRAS'],

        datasets: [
          {
            label: 'TOTAL',
            data: [dataTotalVentasMasVentasIgvST, dataTotalComprasMasComprasIgvST],
            backgroundColor: ['rgb(88, 56, 202)', 'rgb(0, 168, 150)'],
            borderWidth: 1
          }
        ]
      };

      var allZero = saludTributariaData.datasets[0].data.every(value => value === 0);
      console.log(allZero);

      if (saludTributariaChart) {
        if (graficoGlobal) {
          graficoGlobal.destroy();
        }
        if (allZero) {
          saludTributariaChart.font = '17px Arial';
          saludTributariaChart.textAlign = 'center';
          saludTributariaChart.fillText('NO HAY DATOS DISPONIBLES.', saludTributariaChart.canvas.width / 2, saludTributariaChart.canvas.height / 2);
        } else {
          graficoGlobal = new Chart(saludTributariaChart, {
            type: 'doughnut',
            data: saludTributariaData,
            options: {
              responsive: true,
              //maintainAspectRatio: false,
              legend: {
                display: false,
                position: "top",
                labels: {
                  fontColor: "#333",
                  fontSize: 12,
                },
              },
              animation: {
                animateScale: true,
                animateRotate: true
              },
              tooltips: {
                callbacks: {
                  label: function (tooltipItem, data) {
                    var value = data.datasets[tooltipItem.datasetIndex].data[tooltipItem.index];
                    value = value.toString();
                    return addCommas(Math.round(value));
                  }

                }
              }
              ,
              plugins: {
                datalabels: {
                  display: true, // Asegura que las etiquetas se muestren
                  formatter: (value, context) => {
                    let sum = 0;
                    let dataArr = context.chart.data.datasets[0].data;
                    dataArr.map(data => {
                      sum += data;
                    });
                    let percentage = (value * 100 / sum).toFixed(2) + "%";
                    return percentage;
                  },
                  color: '#fff',
                  font: {
                    size: 16, // Tamaño de la fuente en píxeles
                    weight: 'bold'
                  },
                  labels: {
                    title: {
                      font: {
                        size: 16, // Tamaño de la fuente en píxeles
                        weight: 'bold'
                      }
                    }
                  }
                }
              },
              //cutoutPercentage: 70
            },
            plugins: [ChartDataLabels] // Reactivar el plugin para este gráfico
          });

        }
      }

    })
    .catch((err) => console.error("Error al cargar resumen:", err));

}

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

      document.getElementById("" + anioLabel).textContent = anio;

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
            // title: {
            //   display: true,
            //   text: "Histórico en Soles " + anio + " (Ventas y Compras)",
            //   fontSize: 18,
            //   fontColor: "#333",
            //   padding: 20,
            // },
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
      let statsComportamientoIGV = data.statsComportamientoIGV;

      document.getElementById("" + anioLabel).textContent = anio;

      let labels = [];
      let dataIgv = [];
      for (var i = 0; i < statsComportamientoIGV.length; i++) {
        labels.push(statsComportamientoIGV[i].periodo);
        dataIgv.push(statsComportamientoIGV[i].mesIgv);
      }

      let comportamientoIGVChart = document.getElementById(grafico);
      let barColorsComportamientoIGV = dataIgv.map((value) =>
        value >= 0 ? "rgb(0, 168, 150)" : "rgb(255, 87, 94)"
      );

      let comportamientoIGVData = {
        labels: labels,

        datasets: [
          {
            label: "IGV",
            barPercentage: 0.5,
            categoryPercentage: 0.6,
            barThickness: undefined,
            backgroundColor: barColorsComportamientoIGV,
            data: dataIgv,
          },
        ],
      };

      if (comportamientoIGVChart) {
        if (graficoGlobal) {
          graficoGlobal.destroy();
        }
        graficoGlobal = new Chart(comportamientoIGVChart, {
          type: "horizontalBar",
          data: comportamientoIGVData,
          options: {
            scales: {
              xAxes: [
                {
                  gridLines: {
                    color: "#e0e0e0", // sigue mostrando la grilla horizontal
                    drawTicks: false, // quita las pequeñas líneas en los ticks
                    drawBorder: false, // quita la línea vertical del eje
                  },
                  ticks: {
                    fontColor: "#343a40",
                    fontStyle: "500", // también puedes usar "bold"
                    fontSize: 12,
                    beginAtZero: true,
                    userCallback: function (value, index, values) {
                      value = value.toString();
                      return addCommas(Math.round(value));
                    },
                  },
                },
              ],
              yAxes: [
                {
                  gridLines: {
                    display: false,
                  },
                  ticks: {
                    beginAtZero: true,
                    fontColor: "#343a40", // gris oscuro estilo Azia
                    fontStyle: "500", // o "600" para seminegrita
                    fontSize: 12,
                  },
                },
              ],
            },
            legend: {
              display: false,
            },
            // title: {
            //   display: true,
            //   text: "Comportamiento IGV " + anio,
            //   fontSize: 18,
            //   fontColor: "#333",
            //   padding: 20,
            // },
            tooltips: {
              callbacks: {
                label: function (tooltipItem, data) {
                  var value = data.datasets[0].data[tooltipItem.index];
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
      let dataComprasHS = [];
      for (var i = 0; i < statsVentasVsCompras.length; i++) {
        labelsHS.push(statsVentasVsCompras[i].periodo);
        dataVentasHS.push(statsVentasVsCompras[i].ventas);
        dataComprasHS.push(statsVentasVsCompras[i].compras);
      }

      let historicoEnSolesActualChart = document.getElementById(grafico);

      document.getElementById("" + anioLabel).textContent = anio;

      let historicoEnSolesData = {
        labels: labelsHS,

        datasets: [
          {
            label: "VENTAS",
            data: dataVentasHS,
            type: "line",
            fill: false,
            pointRadius: 5,
            borderColor: "rgb(183, 173, 226)",
            pointBackgroundColor: "rgb(88, 56, 202)",
          },
          {
            label: "COMPRAS",
            data: dataComprasHS,
            type: "line",
            fill: false,
            pointRadius: 5,
            borderColor: "rgb(145, 224, 214)",
            pointBackgroundColor: "rgb(0, 168, 150)",
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
            // maintainAspectRatio: false,
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
            // title: {
            //   display: true,
            //   text: "Histórico en Soles " + anio + " (Ventas y Compras)",
            //   fontSize: 18,
            //   fontColor: "#333",
            //   padding: 20,
            // },
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

function getHistoricoRenta(anio, anioLabel, tabla) {
  fetch(`/api/dashboard/getHistoricoRenta?anio=${anio}`, {
    method: "GET",
    headers: {
      Accept: "application/json",
    },
    credentials: "same-origin",
  })
    .then((res) => res.json())
    .then((data) => {

      //PINTADO DE LA TABLA HISTÓRICO RENTA 1
      $("#" + tabla + " tbody tr").remove();
      $("#" + tabla + " tfoot tr").remove();
      let totalBaseRentaHistoricoRenta = 0;
      let totalMesRentaHistoricoRenta = 0;
      let totalPorcentajeHistoricoRenta = 0;
      let cantidadPorcentajeHistoricoRenta = 0;
      let promedioPorcentajeHistoricoRenta = 0;

      document.getElementById("" + anioLabel).textContent = anio;

      let statsHistoricoRenta = data.statsHistoricoRenta;

      let k = 1;
      statsHistoricoRenta.forEach(function (e) {
        let baseRentaCol = e.baseRenta === '0.00' ? '0' : addCommas(Math.round(e.baseRenta));
        let mesRentaCol = e.mesRenta === '0.00' ? '0' : addCommas(Math.round(e.mesRenta));

        totalBaseRentaHistoricoRenta += e.baseRenta === '0.00' ? 0 : Math.round(e.baseRenta);
        totalMesRentaHistoricoRenta += e.mesRenta === '0.00' ? 0 : Math.round(e.mesRenta);
        if (e.porcentajeRenta != 0) {
          totalPorcentajeHistoricoRenta += parseFloat(e.porcentajeRenta);
          cantidadPorcentajeHistoricoRenta++;
        }
        k++;

        if (k == statsHistoricoRenta.length) {
          if (totalPorcentajeHistoricoRenta != 0) {
            promedioPorcentajeHistoricoRenta = totalPorcentajeHistoricoRenta / cantidadPorcentajeHistoricoRenta;
          }
        }

        let dataHistoricoRenta =
          "<tr >" +
          "<td class='text-center table-dashboard-body'>" + e.periodo + "</td>" +
          "<td class='text-end table-dashboard-body-bold'>" + baseRentaCol + "</td>" +
          "<td class='text-end table-dashboard-body-bold'>" + mesRentaCol + "</td>" +
          "<td class='text-end table-dashboard-body-bold'>" + e.porcentajeRenta + " %</td>" +
          "</tr>";
        $("#" + tabla + " tbody").append(dataHistoricoRenta);

      });

      let totalBaseRentaHistoricoRentaCol = totalBaseRentaHistoricoRenta === 0 ? '0' : addCommas(Math.round(totalBaseRentaHistoricoRenta));
      let totalMesRentaHistoricoRentaCol = totalMesRentaHistoricoRenta === 0 ? '0' : addCommas(Math.round(totalMesRentaHistoricoRenta));
      let promedioPorcentajeHistoricoRentaCol = promedioPorcentajeHistoricoRenta === 0 ? '0.0' : promedioPorcentajeHistoricoRenta.toFixed(1);

      let dataHistoricoRentaFoot =
        "<tr class='total-row'>" +
        "<td class='text-center table-dashboard-footer'>TOTAL</td>" +
        "<td class='text-end table-dashboard-footer'>" + totalBaseRentaHistoricoRentaCol + "</td>" +
        "<td class='text-end table-dashboard-footer'>" + totalMesRentaHistoricoRentaCol + "</td>" +
        "<td class='text-end table-dashboard-footer'>" + promedioPorcentajeHistoricoRentaCol + " %</td>" +
        "</tr>";
      $("#" + tabla + " tfoot").append(dataHistoricoRentaFoot);
    })
    .catch((err) => console.error("Error al cargar resumen:", err));
}

// Puedes hacer esto al cargar la página:
document.addEventListener("DOMContentLoaded", function () {

  //ON CLICK
  $('#prevPeriodo').on('click', function () {
    getGraficosPorPeriodos(-1);
  })
  $('#nextPeriodo').on('click', function () {
    getGraficosPorPeriodos(1);
  })

  $('#prevAnio').on('click', function () {
    getGraficosAnuales(-1);
  })
  $('#nextAnio').on('click', function () {
    getGraficosAnuales(1);
  })

  function getGraficosPorPeriodos(evento) {
    var periodoHTML = document.getElementById('periodo').innerHTML;

    fetch(`/api/dateUtils/getPeriodo?periodo=${periodoHTML}&evento=${evento}`, {
      method: "GET",
      headers: {
        Accept: "application/json",
      },
      credentials: "same-origin",
    })
      .then((res) => res.json())
      .then((data) => {
        document.getElementById('periodo').innerHTML = data.valorPeriodo;

        //Por Periodos
        getValoresKpis(data.valorPeriodoAnio, data.valorPeriodoMes, "ventasMes", "comprasMes", "igvMes", "porcentajeMes");
        getSparklineKpis(data.valorPeriodoAnio, data.valorPeriodoMes, "sparkVentasChart", sparkVentasGlobalChart);
        getSparklineKpis(data.valorPeriodoAnio, data.valorPeriodoMes, "sparkComprasChart", sparkComprasGlobalChart);
        getSparklineKpis(data.valorPeriodoAnio, data.valorPeriodoMes, "sparkIgvChart", sparkIgvGlobalChart);
        getSparklineKpis(data.valorPeriodoAnio, data.valorPeriodoMes, "sparkPorcentajeChart", sparkPorcentajeGlobalChart);
        getSaludTributaria(data.valorPeriodoAnio, data.valorPeriodoMes, "saludTributariaPeriodo", "saludTributariaChart", saludTributariaGlobalChart)
      })
      .catch((err) => console.error("Error al cargar resumen:", err));
  }

  function getGraficosAnuales(evento) {
    var anioHTML = document.getElementById('anio').innerHTML;

    fetch(`/api/dateUtils/getAnio?anio=${anioHTML}&evento=${evento}`, {
      method: "GET",
      headers: {
        Accept: "application/json",
      },
      credentials: "same-origin",
    })
      .then((res) => res.json())
      .then((data) => {
        document.getElementById('anio').innerHTML = data.valorAnio;

        //Anuales
        getHistoricoEnSoles(
          data.valorAnioUnoAtras,
          "historicoEnSolesPasadoAnio",
          "historicoEnSolesPasadoChart",
          historicoEnSolesPasadoGlobalChart
        );
        getHistoricoEnSoles(
          data.valorAnio,
          "historicoEnSolesActualAnio",
          "historicoEnSolesActualChart",
          historicoEnSolesActualGlobalChart
        );
        getHistoricoIGV(data.valorAnioUnoAtras, "historicoIGVPasadoAnio", "historicoIGVPasadoTabla");
        getHistoricoIGV(data.valorAnio, "historicoIGVActualAnio", "historicoIGVActualTabla");
        getComportamientoIgv(
          data.valorAnioUnoAtras,
          "comportamientoIGVPasadoAnio",
          "comportamientoIGVPasadoChart",
          comportamientoIGVPasadoGlobalChart
        );
        getComportamientoIgv(
          data.valorAnio,
          "comportamientoIGVActualAnio",
          "comportamientoIGVActualChart",
          comportamientoIGVActualGlobalChart
        );
        getVentasVsCompras(
          data.valorAnioUnoAtras,
          "ventasVsComprasPasadoAnio",
          "ventasVsComprasPasadoChart",
          ventasVsComprasPasadoGlobalChart
        );
        getVentasVsCompras(
          data.valorAnio,
          "ventasVsComprasActualAnio",
          "ventasVsComprasActualChart",
          ventasVsComprasActualGlobalChart
        );
        getHistoricoRenta(data.valorAnioUnoAtras, "historicoRentaPasadoAnio", "historicoRentaPasadoTabla");
        getHistoricoRenta(data.valorAnio, "historicoRentaActualAnio", "historicoRentaActualTabla");

      })
      .catch((err) => console.error("Error al cargar resumen:", err));
  }

  //Graficos por Periodos
  let periodo = document.getElementById('periodo').innerHTML;
  let [anioPeriodo, mesPeriodo] = periodo.split('-');
  getValoresKpis(anioPeriodo, mesPeriodo, "ventasMes", "comprasMes", "igvMes", "porcentajeMes");
  getSparklineKpis(anioPeriodo, mesPeriodo, "sparkVentasChart", sparkVentasGlobalChart);
  getSparklineKpis(anioPeriodo, mesPeriodo, "sparkComprasChart", sparkComprasGlobalChart);
  getSparklineKpis(anioPeriodo, mesPeriodo, "sparkIgvChart", sparkIgvGlobalChart);
  getSparklineKpis(anioPeriodo, mesPeriodo, "sparkPorcentajeChart", sparkPorcentajeGlobalChart);
  getSaludTributaria(anioPeriodo, mesPeriodo, "saludTributariaPeriodo", "saludTributariaChart", saludTributariaGlobalChart)

  //Graficos Anuales
  let anio = document.getElementById('anio').innerHTML;
  let anioUnoAtras = (parseInt(document.getElementById('anio').innerHTML) - 1).toString();
  getHistoricoEnSoles(
    anioUnoAtras,
    "historicoEnSolesPasadoAnio",
    "historicoEnSolesPasadoChart",
    historicoEnSolesPasadoGlobalChart
  );
  getHistoricoEnSoles(
    anio,
    "historicoEnSolesActualAnio",
    "historicoEnSolesActualChart",
    historicoEnSolesActualGlobalChart
  );
  getHistoricoIGV(anioUnoAtras, "historicoIGVPasadoAnio", "historicoIGVPasadoTabla");
  getHistoricoIGV(anio, "historicoIGVActualAnio", "historicoIGVActualTabla");
  getComportamientoIgv(
    anioUnoAtras,
    "comportamientoIGVPasadoAnio",
    "comportamientoIGVPasadoChart",
    comportamientoIGVPasadoGlobalChart
  );
  getComportamientoIgv(
    anio,
    "comportamientoIGVActualAnio",
    "comportamientoIGVActualChart",
    comportamientoIGVActualGlobalChart
  );
  getVentasVsCompras(
    anioUnoAtras,
    "ventasVsComprasPasadoAnio",
    "ventasVsComprasPasadoChart",
    ventasVsComprasPasadoGlobalChart
  );
  getVentasVsCompras(
    anio,
    "ventasVsComprasActualAnio",
    "ventasVsComprasActualChart",
    ventasVsComprasActualGlobalChart
  );
  getHistoricoRenta(anioUnoAtras, "historicoRentaPasadoAnio", "historicoRentaPasadoTabla");
  getHistoricoRenta(anio, "historicoRentaActualAnio", "historicoRentaActualTabla");

  //<editor-fold defaultstate="collapsed" desc="CALENDARIO OBLIGACIONES">
  const calendarObligaciones = new FullCalendar.Calendar(
    document.getElementById("calendarObligaciones"),
    {
      locale: "es", // Establece el idioma a español
      headerToolbar: {
        left: "prev,next today",
        center: "title",
        right: "dayGridMonth",
      },
      initialView: "dayGridMonth",
      editable: false,
      navLinks: true,
      hiddenDays: [0], // Oculta el día domingo
      events: {
        url: "/api/calendar/getObligaciones",
        method: "GET",
        failure: function () {
          alert("Error al cargar los eventos");
        },
      },
      eventContent: function (info) {
        if (
          info.event.extendedProps.colorFeriado ||
          info.event.extendedProps.colorDiasFestivos
        ) {
          let content = document.createElement("div");
          content.className = "fc-event-title ";

          //Contenedor de icono + palabra feriado o festivo
          let contenedorFeriado_Festivo = document.createElement("div");
          contenedorFeriado_Festivo.style.color = "black";
          contenedorFeriado_Festivo.style.textAlign = "center";
          let icono = document.createElement("i");
          icono.className = "far fa-smile";
          icono.style.marginRight = "5px";
          let feriado_Festivo = document.createElement("span");
          if (info.event.extendedProps.colorFeriado) {
            feriado_Festivo.innerHTML = "FERIADO";
          } else {
            feriado_Festivo.innerHTML = "FESTIVO";
          }
          //feriado_Festivo.style = "font-weight: bold";
          contenedorFeriado_Festivo.appendChild(icono);
          contenedorFeriado_Festivo.appendChild(feriado_Festivo);

          let title = document.createElement("div");
          title.innerHTML = info.event.title;
          title.style.color = "black";
          content.appendChild(contenedorFeriado_Festivo);
          content.appendChild(title);
          return { domNodes: [content] };
        }
        if (info.event.extendedProps.type === "fiscalizacion") {
          // Contenedor principal del evento
          let content = document.createElement("div");
          content.className = "fc-event-title";

          let iconoString = "";
          if (info.event.extendedProps.flagFiscalizacion == "PRESENCIAL") {
            iconoString = "fa fa-users";
          } else {
            iconoString = "fa fa-desktop";
          }

          let icono = document.createElement("i");
          icono.className = iconoString;
          icono.style.marginLeft = "3px"; //Espacio entre el margen y el icono
          icono.style.marginRight = "5px"; //Espacio entre el ícono y el time
          let time = document.createElement("span");
          time.textContent = info.event.start.toLocaleTimeString([], {
            hour: "2-digit",
            minute: "2-digit",
          });
          time.style = "font-weight: normal";
          time.style.marginRight = "5px"; //Espacio entre el time y el title
          let title = document.createElement("span");
          title.textContent = info.event.title;
          content.appendChild(icono);
          content.appendChild(time);
          content.appendChild(title);
          return { domNodes: [content] };
        }
        if (info.event.extendedProps.type === "fiscalizacionPay") {
          // Contenedor principal del evento
          let content = document.createElement("div");
          content.className = "fc-event-title";

          let time = document.createElement("span");
          time.textContent = info.event.start.toLocaleTimeString([], {
            hour: "2-digit",
            minute: "2-digit",
          });
          time.style = "font-weight: normal";
          time.style.marginRight = "5px"; //Espacio entre el time y el title
          let title = document.createElement("span");
          title.textContent = info.event.title;
          content.appendChild(time);
          content.appendChild(title);
          return { domNodes: [content] };
        }
      },
      eventDidMount: function (info) {
        //console.log(info);
        //FERIADOS y DIAS FESTIVOS: Color fondo
        if (
          info.event.extendedProps.type === "feriados" ||
          info.event.extendedProps.type === "diasFestivos"
        ) {
          // Pintar toda la celda del día en la vista dayGridMonth
          if (info.view.type === "dayGridMonth") {
            let calendarEl = info.el.closest(".fc"); // obtiene el contenedor del calendario actual
            let dayCell = calendarEl.querySelector(
              `.fc-day[data-date="${info.event.start.toISOString().split("T")[0]
              }"]`
            );
            if (dayCell) {
              if (info.event.extendedProps.type === "feriados") {
                //dayCell.style.backgroundColor = "#E6E3F3"; // Color de fondo para feriados
              } else if (info.event.extendedProps.type === "diasFestivos") {
                dayCell.style.backgroundColor = "#D2E0FB"; // Color de fondo para dias festivos
              }
            }
          }
        }
        //FERIADOS y DIAS FESTIVOS: Color etiqueta
        if (info.event.extendedProps.colorFeriado) {
          info.el.style.backgroundColor = info.event.extendedProps.colorFeriado;
          info.el.style.borderColor = info.event.extendedProps.colorFeriado;
        }
        if (info.event.extendedProps.colorDiasFestivos) {
          info.el.style.backgroundColor =
            info.event.extendedProps.colorDiasFestivos;
          info.el.style.borderColor =
            info.event.extendedProps.colorDiasFestivos;
        }
        //FISCALIZACION: Color etiqueta por estado
        if (info.event.extendedProps.stateFiscalizacion === 2) {
          info.el.style.backgroundColor = "#DA1212";
          info.el.style.borderColor = "#DA1212";
          info.el.style.color = "white";
        } else if (
          info.event.extendedProps.stateFiscalizacion === 3 ||
          info.event.extendedProps.stateFiscalizacion === 5
        ) {
          info.el.style.backgroundColor = "#99a3a4";
          info.el.style.borderColor = "#99a3a4";
          info.el.style.color = "white";
          info.el.style.textDecoration = "line-through";
          info.el.style.textDecorationColor = "black";
        }
        //FISCALIZACION PAY: Color etiqueta
        if (
          info.event.extendedProps.stateFiscalizacionPay === 2 ||
          info.event.extendedProps.stateFiscalizacionPay === 10
        ) {
          info.el.style.backgroundColor = "#abebc6";
          info.el.style.borderColor = "#abebc6";
        } else if (info.event.extendedProps.stateFiscalizacionPay === 5) {
          info.el.style.backgroundColor = "#98A1BC";
          info.el.style.borderColor = "#98A1BC";
          info.el.style.color = "white";
          info.el.style.textDecoration = "line-through";
          info.el.style.textDecorationColor = "black";
        }
        //TRAMITES SUNAT: Color etiqueta por estado
        let titleEl = info.el.querySelector(".fc-event-title"); // Selecciona el título del evento
        if (info.event.extendedProps.stateTramiteSunat === 2) {
          info.el.style.backgroundColor = "#6495ED";
          info.el.style.borderColor = "#6495ED";
          info.el.style.color = "white";
          if (titleEl) {
            titleEl.style.textDecoration = "line-through";
            titleEl.style.textDecorationColor = "black";
          }
        }
      },
      eventClick: function (info) {
        if (
          info.event.extendedProps.type == "fiscalizacion" ||
          info.event.extendedProps.type == "tramitesSunat"
        ) {
          document.getElementById("obligacionTitulo").innerText =
            info.event.title;
          document.getElementById("obligacionDetalle").innerText =
            info.event.start.toLocaleString("es-Es", {
              day: "2-digit",
              month: "2-digit",
              year: "numeric",
            });
          document.getElementById("obligacionTema").innerText =
            info.event.extendedProps.topic || "No especificado";
          document.getElementById("obligacionResponsable").innerText =
            info.event.extendedProps.attendee || "No especificado";
          $("#modalObligaciones").modal("show");
        }
      },
    }
  );

  calendarObligaciones.addEventSource(function (
    fetchInfo,
    successCallback,
    failureCallback
  ) {
    fetch("/api/calendar/getFeriados", {
      method: "GET",
      headers: {
        Accept: "application/json",
      },
    })
      .then((res) => {
        if (!res.ok) throw new Error("Error de red");
        return res.json();
      })
      .then((events) => successCallback(events))
      .catch((err) => {
        console.error("Error al cargar eventos del cronograma:", err);
        failureCallback(err);
      });
  });

  calendarObligaciones.render();

  //NO VA PORQUE VIENEN LOS DÍAS COMO EL DÍA DEL CONTADOR, ETC
  // calendarObligaciones.addEventSource(function (
  //   fetchInfo,
  //   successCallback,
  //   failureCallback
  // ) {
  //   fetch("CalendarEventController?action=13", {
  //     method: "GET",
  //     headers: {
  //       Accept: "application/json",
  //     },
  //   })
  //     .then((res) => {
  //       if (!res.ok) throw new Error("Error de red");
  //       return res.json();
  //     })
  //     .then((events) => successCallback(events))
  //     .catch((err) => {
  //       console.error("Error al cargar eventos del cronograma:", err);
  //       failureCallback(err);
  //     });
  // });

  // VAN LAS FISCALIZACIONES DE LA EMPRESA LOGEADA
  // calendarObligaciones.addEventSource(function (
  //   fetchInfo,
  //   successCallback,
  //   failureCallback
  // ) {
  //   fetch("CalendarEventController?action=11", {
  //     method: "GET",
  //     headers: {
  //       Accept: "application/json",
  //     },
  //   })
  //     .then((res) => {
  //       if (!res.ok) throw new Error("Error de red");
  //       return res.json();
  //     })
  //     .then((events) => successCallback(events))
  //     .catch((err) => {
  //       console.error("Error al cargar eventos del cronograma:", err);
  //       failureCallback(err);
  //     });
  // });

  // calendarObligaciones.addEventSource(function (
  //   fetchInfo,
  //   successCallback,
  //   failureCallback
  // ) {
  //   fetch("CalendarEventController?action=14", {
  //     method: "GET",
  //     headers: {
  //       Accept: "application/json",
  //     },
  //   })
  //     .then((res) => {
  //       if (!res.ok) throw new Error("Error de red");
  //       return res.json();
  //     })
  //     .then((events) => successCallback(events))
  //     .catch((err) => {
  //       console.error("Error al cargar eventos del cronograma:", err);
  //       failureCallback(err);
  //     });
  // });

  // calendarObligaciones.addEventSource(function (
  //   fetchInfo,
  //   successCallback,
  //   failureCallback
  // ) {
  //   fetch("CalendarEventController?action=15", {
  //     method: "GET",
  //     headers: {
  //       Accept: "application/json",
  //     },
  //   })
  //     .then((res) => {
  //       if (!res.ok) throw new Error("Error de red");
  //       return res.json();
  //     })
  //     .then((events) => successCallback(events))
  //     .catch((err) => {
  //       console.error("Error al cargar eventos del cronograma:", err);
  //       failureCallback(err);
  //     });
  // });
  //</editor-fold>
});
