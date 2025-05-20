var historicoEnSolesPasadoGlobalChart;
var historicoEnSolesActualGlobalChart;

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
      // document.getElementById("monto").textContent = data.monto;
      console.log(grafico);
      console.log(anioLabel);
      console.log(graficoGlobal);

      document.getElementById("" + anioLabel).textContent = anio;

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
            barPercentage: 0.4,
            categoryPercentage: 0.5,
            barThickness: 8,
            backgroundColor: "rgb(0, 51, 204)",
            borderColor: "rgb(0, 51, 204)",
            data: dataVentasHS,
          },
          {
            label: "VENTAS IGV",
            barPercentage: 0.4,
            categoryPercentage: 0.5,
            barThickness: 8,
            backgroundColor: "rgb(147, 173, 255)",
            borderColor: "rgb(147, 173, 255)",
            data: dataTotalVentasIgvHS,
          },
          {
            label: "COMPRAS",
            barPercentage: 0.4,
            categoryPercentage: 0.5,
            barThickness: 8,
            backgroundColor: "rgba(55, 178, 5)",
            borderColor: "rgb(55, 178, 5)",
            data: dataComprasHS,
          },
          {
            label: "COMPRAS IGV",
            barPercentage: 0.4,
            categoryPercentage: 0.5,
            barThickness: 8,
            backgroundColor: "rgba(141, 214, 92)",
            borderColor: "rgb(141, 214, 92)",
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
            scales: {
              yAxes: [
                {
                  ticks: {
                    beginAtZero: true,
                    userCallback: function (value, index, values) {
                      value = value.toString();
                      return addCommas(Math.round(value));
                    },
                  },
                },
              ],
            },
            legend: {
              display: false,
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

function getHistoricoEnSoles(anio, anioLabel, grafico, graficoGlobal) {
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
      $("#historicoIGVTable tbody tr").remove();
      $("#historicoIGVTable tfoot tr").remove();
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
            barPercentage: 0.4,
            categoryPercentage: 0.5,
            barThickness: 8,
            backgroundColor: "rgb(0, 51, 204)",
            borderColor: "rgb(0, 51, 204)",
            data: dataVentasHS,
          },
          {
            label: "VENTAS IGV",
            barPercentage: 0.4,
            categoryPercentage: 0.5,
            barThickness: 8,
            backgroundColor: "rgb(147, 173, 255)",
            borderColor: "rgb(147, 173, 255)",
            data: dataTotalVentasIgvHS,
          },
          {
            label: "COMPRAS",
            barPercentage: 0.4,
            categoryPercentage: 0.5,
            barThickness: 8,
            backgroundColor: "rgba(55, 178, 5)",
            borderColor: "rgb(55, 178, 5)",
            data: dataComprasHS,
          },
          {
            label: "COMPRAS IGV",
            barPercentage: 0.4,
            categoryPercentage: 0.5,
            barThickness: 8,
            backgroundColor: "rgba(141, 214, 92)",
            borderColor: "rgb(141, 214, 92)",
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
            scales: {
              yAxes: [
                {
                  ticks: {
                    beginAtZero: true,
                    userCallback: function (value, index, values) {
                      value = value.toString();
                      return addCommas(Math.round(value));
                    },
                  },
                },
              ],
            },
            legend: {
              display: false,
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
  getHistoricoEnSoles(
    "2024",
    "historicoEnSolesPasadoAnio",
    "historicoEnSolesPasadoChart",
    historicoEnSolesPasadoGlobalChart
  );
  getHistoricoEnSoles(
    "2025",
    "historicoEnSolesActualAnio",
    "historicoEnSolesActualChart",
    historicoEnSolesActualGlobalChart
  );
});
