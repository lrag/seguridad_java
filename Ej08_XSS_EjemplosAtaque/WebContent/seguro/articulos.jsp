<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Insert title here</title>
	<script type="text/javascript" src="avisoTimeout.jsp"></script>
    <link rel="stylesheet" href="../css/bootstrap.min.css">
</head>

<body>

	<div class="row">
		<div class="col-6 offset-3 text-center">
			<table class="table-sm table-bordered" width="100%">
				<tr>
					<th colspan="3">Cookies recibidas en el servidor</th>
				</tr>
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
		</div>
	</div>
	
	<br/>
	<br/>

	<div class="pb-2 mt-4 mb-2 border-bottom text-center">
		<h2>
			<font color="green">
				Artículos. Usuario autenticado: ${usuario.nombre}
			</font>
		</h2>
	</div>

	<br/>

	<div class="form-group row">
		<div class="col-6 offset-3">
			<label for="ex1">Caja de texto en la que escribir algo vital:</label> 
			<input class="form-control" type="text">
		</div>
	</div>

	<div class="row">
		<div class="col-6 offset-3 text-center">
			<form action="../SVLogin" method="post">
				<input type="hidden" name="accion" value="logout" /> 
				<input type="button" class="btn btn-primary" value="Crear artículo" onclick="window.location='SVArticulos?accion=crearArticulo'"/>	
				<input type="submit" class="btn btn-warning" value="Salir" />
			</form>
		</div>
	</div>
	
	<div class="card card-primary my-3 p-0 col-8 offset-2">
		<div class="card-body" id="div">
			Listado de artículos
		</div>
	</div>	

	<c:forEach var="a" items="${articulos}">
		<div class="card card-primary my-3 p-0 col-8 offset-2">
			<div class="card-header"><b>${a.titulo}</b></div>
			<div class="card-body">
				<p align="center">${a.texto}</p>
			</div>
		</div>
	</c:forEach>

</body>
</html>