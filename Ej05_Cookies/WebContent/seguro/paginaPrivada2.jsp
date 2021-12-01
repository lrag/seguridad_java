<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>

<script>

function getCookie(nombre) {
	var nombre = nombre + "=";
    //obtenemos todas las cookies separadas por ';'
    var carray = document.cookie.split(';');
    for(var i=0; i<carray.length; i++) {
        var c = carray[i];
        //Mientras haya espacio en blanco, hacemos una subcadena
        //del siguiente caracter en adelante
        //Sirve para quitar los espacios en blanco
        while (c.charAt(0)==' ') c = c.substring(1);
        //Devolvemos el valor de la cookie buscada
        if (c.indexOf(nombre) == 0) 
        	return c.substring(nombre.length, c.length);
    }
    return "";
}

function leerCookie(nombre) {
    var valor=getCookie(nombre);
    if (valor!="") {
        alert(valor);
    }else{
        alert("Cookie no detectada");
    }
}

function leerCookies(){
	alert("Cookies:"+document.cookie);
}

</script>

<body>

	<p align="center">Cookies recibidas en el servidor:</p>
	<table border="1" align="center">
		<tr>
			<th>Nombre</th>
			<th>Valor</th>
			<th>Path</th>
		</tr>
	<%
    Cookie[] cookies = request.getCookies();
	if( cookies != null){
	    for (int i = 0; i < cookies.length; i++) {
	    	String name = cookies[i].getName();
			String value = cookies[i].getValue();
			String path  = cookies[i].getPath();
			
			out.println("<tr>");
			out.println("<td>"+name+"</td>");
			out.println("<td>"+value+"</td>");
			out.println("<td>"+path+"</td>");			
			out.println("</tr>");
	    }
	}
	%>
	</table>


	<h1 align="center">
		<font color="green">
			Página privada 2.
		</font>
	</h1>

	<h2>		
		<p align="center">
			<a href="paginaPrivada1.jsp">Volver a página privada 1</a>
			<br/>
			<input type="button" value="LEER POR JAVASCRIPT COOKIES" onclick="leerCookies()"/>
			<input type="button" value="LEER POR JAVASCRIPT GALLETILLA" onclick="leerCookie('galletilla')"/>
			<br/>
			<a href="../paginaPublica.jsp">Página pública</a>
		</p>
	</h2>		

</body>
</html>