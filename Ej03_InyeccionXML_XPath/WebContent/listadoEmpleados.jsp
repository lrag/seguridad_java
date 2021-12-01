<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<link href="css/estilos.css" rel="stylesheet" type="text/css">
</head>

<body>
	
	<div class="contenedor">

		<h1 align="center">
			Resultados de la búsqueda	
		</h1>

		<table align="center" border="1">
			<tr>
				<th>Nombre</th>
				<th>Apellidos</th>
				<th>Sueldo</th>
			</tr>
			<c:forEach var="e" items="${empleados}">
				<tr>
					<td>${e.nombre}</td>
					<td>${e.apellidos}</td>
					<td>${e.sueldo}</td>
				</tr>
			</c:forEach>		
		</table>		
		
		<p align="center">
			<a href="formularioEmpleados.jsp">Volver</a>
		</p>

	</div>

</body>
</html>




