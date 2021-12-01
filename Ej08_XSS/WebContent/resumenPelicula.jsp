<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>    
    
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>XSS</title>	
    <link rel="stylesheet" href="css/bootstrap.css">
</head>

<body>

	<div class="text-center text-warning">
	    <br/>
	    <br/>
	    <h1>
			XSS
		</h1>
	    <br/>
	    <br/>
	</div>
	
	<hr/>

	<br/>

	<h3 class="text-center">
		Datos de la película
	</h3>
	
	<form action="SVPeliculas" method="get">	
	
		<input type="hidden" name="accion" value="verFormulario"/>
	
		<table align="center" width="300px" border="1">
			<col style="width:25%">
			<col style="width:75%">
			<tr>
				<td>Título:</td>
				<td>
					${peliculaSel.titulo}
				</td>
			</tr>
			<tr>
				<td>Director:</td>
				<td>
					${peliculaSel.director}
				</td>
			</tr>
			<tr>
				<td>Genero:</td>
				<td>
					${peliculaSel.genero}
				</td>
			</tr>	
			<tr>
				<td>Año:</td>
				<td>
					${peliculaSel.year}
				</td>
			</tr>	
		</table>
		
		<br/>
	
		<div class="text-center">
			<input type="submit" class="btn btn-primary" value="Volver"/>
		</div>	

	</form>

</body>
</html>






