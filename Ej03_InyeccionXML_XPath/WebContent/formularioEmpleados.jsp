<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
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
			Formulario de búsqueda de empleados	
		</h1>
	
		<form id="formulario" action="ServicioEmpleados" method="GET">

			<table align="center">
				<tr>
					<td>ID</td>
					<td>
						<input type="text" name="idEmpleado"/>
					</td>
					<td>
						<input type="submit" value="Buscar"/>
					</td>
				</tr>
			</table>
			
			<p align="center">
				' or '1'='1
				<br/>
				' or contains(@apellidos,'Baracus') or '
			</p>
		
		</form>

	</div>

</body>
</html>




