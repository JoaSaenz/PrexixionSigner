//FUNCIÓN PARA OBTENER EL TIPO DE CAMBIO DADA UNA FECHA
async function getTipoCambio(fecha) {
  return (tipoCambio = (
    await $.ajax({
      url: "GetTipoCambio",
      type: "POST",
      dataType: "json",
      data: { param: fecha },
    })
  ).tipoCambio);
}

//FUNCIÓN PARA COMPARAR FECHAS
//RETORNA -1: fecha1 ES MAYOR
//RETORNA  0: MISMO DÍA
//RETORNA  1: fecha2 ES MAYOR
async function compararFechas(fecha1, fecha2) {
  return await $.ajax({
    url: "DateUtilsController?action=7",
    type: "POST",
    dataType: "json",
    data: { fecha1: fecha1, fecha2: fecha2 },
  });
}

async function getLastDateCurrentMonth(someday) {
  try {
    return await $.ajax({
      url: "GetLastDateCurrentMonth",
      type: "POST",
      dataType: "json",
      data: { someday: someday },
    });
  } catch (error) {
    console.log(error);
  }
}

async function isSameMonth(parametroFecha1, parametroFecha2) {
  return await $.ajax({
    url: "IsSameMonth",
    type: "POST",
    dataType: "json",
    data: {
      parametroFecha1: parametroFecha1,
      parametroFecha2: parametroFecha2,
    },
  });
}

//NUEVAS FUNCIONES
function formatPositive(element) {
  var value = removeCommas($(element).val().replace(/-/g, ""));
  if (isNaN(value)) {
    $(element).val(0);
  } else {
    $(element).val(addCommasNoRemoveZero(value));
  }
}

function formatNegative(element) {
  if ($(element).val() !== "") {
    var value = removeCommas($(element).val().replace(/-/g, ""));
    if (isNaN(value)) {
      $(element).val(0);
    } else {
      $(element).val("-" + addCommasNoRemoveZero(value));
    }
  }
}

function formatNegativeText(element) {
  if ($(element).text() !== "") {
    var texto = removeCommas($(element).text().replace(/-/g, ""));
    if (isNaN(texto)) {
      $(element).text(0);
    } else {
      $(element).text("-" + addCommasNoRemoveZero(texto));
    }
  }
}

//END OF NUEVAS FUNCIONES

function format(element) {
  if ($(element).val() == 0) {
    $(element).val(0);
  } else {
    $(element).val(addCommas(removeCommas($(element).val())));
  }
}

function formatNoRemoveZero(element) {
  $(element).val(addCommasNoRemoveZero(removeCommas($(element).val())));
}

//ADD COMAS EACH 3 SPACES AND PRESERVS DECIMAL PART
//AGREGA COMAS CADA 3 ESPACIOS Y MANTIENE LA PARTE DECIMAL
function addCommas(nStr) {
  nStr += "";
  nStr = nStr.replace(/^(-?)0+/, "$1");
  x = nStr.split(".");
  x1 = x[0];
  x2 = x.length > 1 ? "." + x[1] : "";
  var rgx = /(\d+)(\d{3})/;
  while (rgx.test(x1)) {
    x1 = x1.replace(rgx, "$1" + "," + "$2");
  }
  return x1 + x2;
}

function addCommasNoRemoveZero(nStr) {
  nStr += "";
  nStr = nStr.replace(/^(-?)+/, "$1");
  x = nStr.split(".");
  x1 = x[0];
  x2 = x.length > 1 ? "." + x[1] : "";
  var rgx = /(\d+)(\d{3})/;
  while (rgx.test(x1)) {
    x1 = x1.replace(rgx, "$1" + "," + "$2");
  }
  return x1 + x2;
}

function removeCommasTest(str) {
  return str.replace(/,/g, "");
}

function removeCommas(str) {
  return str.replace(/,/g, "");
}

function removeLastChar(string) {
  return string.slice(0, -1);
}

function borrarUtimoCaracter(string) {
  return string.slice(0, -1);
}

function splitOnChar(str, char) {
  if (str !== null) {
    return (values = str.split(char));
  }
}

function arrayToString(array) {
  var string = "";
  for (var i = 0; i < array.length; i++) {
    string += array[i] + ",";
  }
  return string;
}

//FORMAT LEGALES
function formatLegales(element) {
  $(element).val(
    addCommasMillions(removeCommas(removeApostrophes($(element).val())))
  );
}

function removeApostrophes(str) {
  return str.replace(/\'/g, "");
}

function removeString(str, remove) {
  return str.replace(remove, "");
}

//ADD COMAS EACH 3 SPACES AND PRESERVS DECIMAL PART - MILLIONS
function addCommasMillions(nStr) {
  nStr += "";
  x = nStr.split(".");
  x1 = x[0];
  x2 = x.length > 1 ? "." + x[1] : "";

  var tam = x1.length;
  var arrayStr = [];

  while (tam > 0) {
    arrayStr.push(x1.substring(tam - 3, tam));
    tam = tam - 3;
  }

  var strReturn = "";

  for (var strAux in arrayStr) {
    if (strAux == 0) {
      strReturn = arrayStr[strAux];
    }
    if (strAux == 1) {
      strReturn = arrayStr[strAux] + "," + strReturn;
    }
    if (strAux > 1) {
      strReturn = arrayStr[strAux] + "'" + strReturn;
    }
  }

  return strReturn + x2;
}

function getToday() {
  var today = new Date();
  return new Date(today.getTime() - today.getTimezoneOffset() * 60000)
    .toJSON()
    .slice(0, 10);
}

//TODO: When parseFloat admit 0. and wait for next digit
function getDoubleVal(id) {
  if ($("#" + id).val() === "" || isNaN(removeCommas($("#" + id).val()))) {
    return 0;
  } else {
    return parseFloat(removeCommas($("#" + id).val()));
  }
}

function getDoubleNro(nro) {
  if (nro === "" || isNaN(removeCommas(nro))) {
    return 0;
  } else {
    return parseFloat(removeCommas(nro));
  }
}

function getIntegerVal(id) {
  if ($("#" + id).val() === "" || isNaN(removeCommas($("#" + id).val()))) {
    return 0;
  } else {
    return parseInt(removeCommas($("#" + id).val()));
  }
}

function getDoubleValLegales(id) {
  if (
    $("#" + id).val() === "" ||
    isNaN(removeCommas(removeApostrophes($("#" + id).val())))
  ) {
    return 0;
  } else {
    return parseFloat(removeCommas(removeApostrophes($("#" + id).val())));
  }
}

function getDoubleNroLegales(nro) {
  if (nro === "" || isNaN(removeCommas(removeApostrophes(nro)))) {
    return 0;
  } else {
    return parseFloat(removeCommas(removeApostrophes(nro)));
  }
}

function checkConstancia(input, tabla, columna) {
  let constancia = $("#" + input).val();
  $.ajax({
    url: "BuscarConstancia",
    type: "POST",
    data: { constancia: constancia, tabla: tabla, columna: columna },
    dataType: "json",
    success: function (data) {
      if (data == false) {
        $("#" + input).removeClass("is-invalid");
        $("#" + input).addClass("is-valid");

        $("#guardar").prop("disabled", false);

        alertTop("No existe la constancia, adelante.", "success");
      } else {
        $("#" + input).removeClass("is-valid");
        $("#" + input).addClass("is-invalid");

        $("#guardar").prop("disabled", true);

        alertTop("Constancia ya registrada, revisar.", "danger");
      }
    },
  });
}

function completeWithZeroes(value, nroZeroes) {
  value = value.replace(/^0+/, "");
  while (value.toString().length < nroZeroes) {
    value = "0" + value;
  }

  return value;
}

//ADD ROW
function addRowRegistro(tableID, nroItems) {
  var table = document.getElementById(tableID);

  var rowCount = table.rows.length;
  var row = table.insertRow(rowCount);
  var nroCol = 0;

  //CHK
  var cellChk = row.insertCell(nroCol);
  cellChk.style.paddingBottom = 0;
  cellChk.style.paddingTop = "10px";
  cellChk.className = "text-center";
  var elementChk = document.createElement("input");
  elementChk.type = "checkbox";
  elementChk.name = "chkbox[" + nroItems + "]";
  cellChk.appendChild(elementChk);
  nroCol++;

  //ID
  var cellItem = row.insertCell(nroCol);
  cellItem.style.paddingTop = "5px";
  cellItem.style.paddingBottom = 0;
  cellItem.hidden = "true";
  var elementItem = document.createElement("input");
  elementItem.type = "text";
  elementItem.className = "form-control text-center";
  elementItem.value = rowCount;
  elementItem.readOnly = true;
  elementItem.name = "idItem" + nroItems;
  elementItem.id = "idItem" + nroItems;
  cellItem.appendChild(elementItem);
  nroCol++;

  //TIPO DE REGISTRO
  var cellTipoRegistro = row.insertCell(nroCol);
  cellTipoRegistro.style.paddingTop = "5px";
  cellTipoRegistro.style.paddingBottom = 0;
  cellTipoRegistro.style.paddingLeft = 0;
  cellTipoRegistro.style.paddingRight = "10px";

  var elementTipoRegistro = document.createElement("select");
  elementTipoRegistro.className = "form-control";
  elementTipoRegistro.name = "tipoRegistro" + nroItems;
  elementTipoRegistro.id = "tipoRegistro" + nroItems;
  cellTipoRegistro.appendChild(elementTipoRegistro);

  for (var i = 0; i < tiposRegistro.length; i++) {
    var tipoRegistro = document.createElement("option");
    tipoRegistro.value = tiposRegistro[i].id;
    tipoRegistro.text = tiposRegistro[i].descripcion;

    if (tiposRegistro[i].id == 2) {
      tipoRegistro.selected = "selected";
    }

    elementTipoRegistro.appendChild(tipoRegistro);
  }
  nroCol++;

  //FECHA REGISTRO
  var cellFechaRegistro = row.insertCell(nroCol);
  cellFechaRegistro.style.paddingTop = "5px";
  cellFechaRegistro.style.paddingBottom = 0;
  cellFechaRegistro.style.paddingLeft = 0;
  cellFechaRegistro.style.paddingRight = "10px";

  var elementFechaRegistro = document.createElement("input");
  elementFechaRegistro.type = "date";
  elementFechaRegistro.className = "form-control";
  elementFechaRegistro.name = "fechaRegistro" + nroItems;
  elementFechaRegistro.id = "fechaRegistro" + nroItems;
  elementFechaRegistro.value = "";
  cellFechaRegistro.appendChild(elementFechaRegistro);
  nroCol++;

  //NRO RECTIFICACIÓN
  var cellNroRectificacion = row.insertCell(nroCol);
  cellNroRectificacion.style.paddingTop = "5px";
  cellNroRectificacion.style.paddingBottom = 0;
  cellNroRectificacion.style.paddingLeft = 0;
  cellNroRectificacion.style.paddingRight = "10px";

  var elementNroRectificacion = document.createElement("input");
  elementNroRectificacion.type = "text";
  elementNroRectificacion.className = "form-control text-right";
  elementNroRectificacion.name = "nroRectificacion" + nroItems;
  elementNroRectificacion.id = "nroRectificacion" + nroItems;
  elementNroRectificacion.value = "";
  cellNroRectificacion.appendChild(elementNroRectificacion);
  nroCol++;

  //NRO ORDEN
  var cellNroOrden = row.insertCell(nroCol);
  cellNroOrden.style.paddingTop = "5px";
  cellNroOrden.style.paddingBottom = 0;
  cellNroOrden.style.paddingLeft = 0;
  //        cellNroOrden.style.paddingRight = "10px";

  var elementNroOrden = document.createElement("input");
  elementNroOrden.type = "text";
  elementNroOrden.className = "form-control";
  elementNroOrden.name = "nroOrden" + nroItems;
  elementNroOrden.id = "nroOrden" + nroItems;
  elementNroOrden.value = "";
  cellNroOrden.appendChild(elementNroOrden);
  nroCol++;
}

//DELETE ROW
function deleteRowRegistro(tableID) {
  try {
    var table = document.getElementById(tableID);
    var rowCount = table.rows.length;

    for (var i = 0; i < rowCount; i++) {
      var row = table.rows[i];
      var chkbox = row.cells[0].childNodes[0];
      var item = row.cells[1].childNodes[0];
      if (null != chkbox && true == chkbox.checked) {
        var id = removeString(item.name, "idItem");
        var itemsRegistros = $("#itemsRegistros").val();
        var tempArray = splitOnChar(itemsRegistros, ",");
        tempArray = $.grep(tempArray, function (value) {
          return value != id;
        });
        //ACTUALIZAR IDS PARA FORMULARIO
        tempArray.pop();
        itemsRegistros = arrayToString(tempArray);
        $("#itemsRegistros").val(itemsRegistros);

        table.deleteRow(i);
        rowCount--;
        i--;
      }
    }
    updateNroItemsAndCodigos(tableID);
  } catch (e) {
    alert(e);
  }
}

//UPDATE NRO ITEMS
function updateNroItemsAndCodigos(tableID) {
  var table = document.getElementById(tableID);
  var rowCount = table.rows.length;

  for (var i = 0; i < rowCount; i++) {
    var row = table.rows[i];
    var inputItem = row.cells[1].childNodes[0];
    inputItem.value = i;
  }
}

//RESET TABLE
function tableReset(tableID) {
  $("#" + tableID)
    .find("tr:gt(0)")
    .remove();
}

//LOAD GLASS LIST
async function getGlassList(tableName) {
  try {
    return await $.ajax({
      url: "GclassGetList",
      type: "POST",
      data: { tableName: tableName },
      dataType: "json",
    });
  } catch (error) {
    console.log(error);
  }
}

function reemplazarCadena(cadenaVieja, cadenaNueva, cadenaCompleta) {
  // Reemplaza cadenaVieja por cadenaNueva en cadenaCompleta

  for (var i = 0; i < cadenaCompleta.length; i++) {
    if (cadenaCompleta.substring(i, i + cadenaVieja.length) == cadenaVieja) {
      cadenaCompleta =
        cadenaCompleta.substring(0, i) +
        cadenaNueva +
        cadenaCompleta.substring(i + cadenaVieja.length, cadenaCompleta.length);
    }
  }
  return cadenaCompleta;
}

/**
 * Parse a localized number to a float.
 * @param {string} stringNumber - the localized number
 * @param {string} locale - [optional] the locale that the number is represented in. Omit this parameter to use the current locale.
 */
function parseLocaleNumber(stringNumber) {
  var thousandSeparator = Intl.NumberFormat("es-PE")
    .format(11111)
    .replace(/\p{Number}/gu, "");
  var decimalSeparator = Intl.NumberFormat("es-PE")
    .format(1.1)
    .replace(/\p{Number}/gu, "");

  return parseFloat(
    stringNumber
      .replace(new RegExp("\\" + thousandSeparator, "g"), "")
      .replace(new RegExp("\\" + decimalSeparator), ".")
  );
}

//RECEPCIÓN DE DOCUMENTOS
function mayusculas(campo) {
  var texto = "";
  $("#" + campo).keyup(function () {
    texto = $("#" + campo)
      .val()
      .toUpperCase();
    $("#" + campo).val(texto);
  });
}

function validaNumeros(campo) {
  var texto = "";
  var tamTexto = 0;
  var ascii;
  $("#" + campo).keyup(function () {
    texto = $("#" + campo).val();
    tamTexto = texto.length;
    if (tamTexto >= 1) {
      ascii = texto.charCodeAt(tamTexto - 1);
      if (ascii < 48 || ascii > 57) {
        alert("Solo se acepta numeros");
        $("#" + campo).val("");
      }
    }
  });
}

//REGISTROS FUNCIONES
// var tiposRegistro;
// //LOAD REGISTROS TIPOS
// async function getTiposRegistro() {
//   try {
//     return await $.ajax({
//       url: "GclassGetList",
//       type: "POST",
//       data: { tableName: "pdtRegistrosTipos" },
//       dataType: "json",
//     });
//   } catch (error) {
//     console.log(error);
//   }
// }

// (async function () {
//   tiposRegistro = await getTiposRegistro();
// })();

// $(document).ready(function () {
//   //AGREGAR O QUITAR DE FAVORITOS
//   $("#iconoFavorito").click(function () {
//     if ($(".cambiar").hasClass("fa fa-plus")) {
//       //AGREGAR A FAVORITOS
//       $(".cambiar").removeClass("fa fa-plus").addClass("fa fa-heart");

//       $.ajax({
//         url: "FavoritoModuloController?action=1",
//         type: "POST",
//         data: {
//           dniModulo: $("#dniModulo").val(),
//           nombreModulo: $("#nombreModulo").val(),
//           controllerModulo: $("#controllerModulo").val(),
//           iconoNavbarModulo: $("#iconoNavbarModulo").val(),
//           nombreNavbarModulo: $("#nombreNavbarModulo").val(),
//         },
//         datatype: "json",
//         success: function (data) {
//           alertGrowl(
//             "Atención: AGREGADO A <strong>FAVORITOS</strong>.",
//             "primary"
//           );

//           // Almacenar la cadena JSON en localStorage ('favoritos')
//           localStorage.setItem("favoritos", data);

//           // Acceder a la cadena JSON almacenada en favoritos
//           var miJSONStringRecuperado = localStorage.getItem("favoritos");
//           // Convertir la cadena JSON de nuevo a un objeto JSON
//           var miObjetoJSONRecuperado = JSON.parse(miJSONStringRecuperado);

//           //Obteniendo ID Elemento DIV
//           var bloqueFavoritos = document.getElementById("bloqueFavoritos");

//           // Eliminar todos los hijos del elemento <div>
//           while (bloqueFavoritos.firstChild) {
//             bloqueFavoritos.removeChild(bloqueFavoritos.firstChild);
//           }

//           for (var i = 0; i < miObjetoJSONRecuperado.length; i++) {
//             let a = document.createElement("a");
//             a.className = "dropdown-item";
//             a.setAttribute("href", miObjetoJSONRecuperado[i].controllerModulo);

//             let aTexto = document.createTextNode(
//               " " + miObjetoJSONRecuperado[i].nombreNavbarModulo
//             );
//             let icono = document.createElement("i");
//             icono.className = miObjetoJSONRecuperado[i].iconoNavbarModulo;

//             a.appendChild(icono);
//             a.appendChild(aTexto);
//             bloqueFavoritos.appendChild(a);
//           }
//         },
//       });
//     } else {
//       //QUITAR DE FAVORITOS
//       $(".cambiar").removeClass("fa fa-heart").addClass("fa fa-plus");
//       $.ajax({
//         url: "FavoritoModuloController?action=2",
//         type: "POST",
//         data: {
//           dniModulo: $("#dniModulo").val(),
//           nombreModulo: $("#nombreModulo").val(),
//         },
//         datatype: "json",
//         success: function (data) {
//           alertGrowl(
//             "Atención: ELIMINADO DE <strong>FAVORITOS</strong>.",
//             "danger"
//           );

//           // Almacenar la cadena JSON en localStorage ('favoritos')
//           localStorage.setItem("favoritos", data);

//           // Acceder a la cadena JSON almacenada en favoritos
//           var miJSONStringRecuperado = localStorage.getItem("favoritos");
//           // Convertir la cadena JSON de nuevo a un objeto JSON
//           var miObjetoJSONRecuperado = JSON.parse(miJSONStringRecuperado);

//           //Obteniendo ID Elemento DIV
//           var bloqueFavoritos = document.getElementById("bloqueFavoritos");

//           // Eliminar todos los hijos del elemento <div>
//           while (bloqueFavoritos.firstChild) {
//             bloqueFavoritos.removeChild(bloqueFavoritos.firstChild);
//           }

//           for (var i = 0; i < miObjetoJSONRecuperado.length; i++) {
//             let a = document.createElement("a");
//             a.className = "dropdown-item";
//             a.setAttribute("href", miObjetoJSONRecuperado[i].controllerModulo);

//             let aTexto = document.createTextNode(
//               " " + miObjetoJSONRecuperado[i].nombreNavbarModulo
//             );
//             let icono = document.createElement("i");
//             icono.className = miObjetoJSONRecuperado[i].iconoNavbarModulo;

//             a.appendChild(icono);
//             a.appendChild(aTexto);
//             bloqueFavoritos.appendChild(a);
//           }
//         },
//       });
//     }
//   });

//   (function () {
//     // Acceder a la cadena JSON almacenada en localStorage
//     var miJSONStringRecuperado = localStorage.getItem("favoritos");
//     // Convertir la cadena JSON de nuevo a un objeto JSON
//     var miObjetoJSONRecuperado = JSON.parse(miJSONStringRecuperado);

//     //Obteniendo ID Elemento DIV
//     var bloqueFavoritos = document.getElementById("bloqueFavoritos");
//     for (var i = 0; i < miObjetoJSONRecuperado.length; i++) {
//       let a = document.createElement("a");
//       a.className = "dropdown-item";
//       a.setAttribute("href", miObjetoJSONRecuperado[i].controllerModulo);

//       let aTexto = document.createTextNode(
//         " " + miObjetoJSONRecuperado[i].nombreNavbarModulo
//       );
//       let icono = document.createElement("i");
//       icono.className = miObjetoJSONRecuperado[i].iconoNavbarModulo;

//       a.appendChild(icono);
//       a.appendChild(aTexto);
//       bloqueFavoritos.appendChild(a);

//       var controllerText = miObjetoJSONRecuperado[i].controllerModulo;
//       if (document.getElementById(controllerText)) {
//         document.getElementById(controllerText).classList.remove("fa-plus");
//         document.getElementById(controllerText).classList.add("fa-heart");
//       }
//     }
//   })();
// });
