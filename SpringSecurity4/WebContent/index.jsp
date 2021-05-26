<%@ page session="false" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
  <head>
      <meta http-equiv="content-type" content="text/html; charset=UTF-8">
      <link rel="stylesheet" href="<c:url value='/css/estilos.css'/>" type="text/css" />
      <title>Bienvenido a los Expedientes X del FBI</title>
  </head>
<body>

<img src="imagenes/xfiles.jpg" align="right"/>

<div id="content">

	<h1>Página Principal</h1>
	
	<!-- LOGOUT -->	
	<p> 
		Solo los Agentes pueden ver este recurso. Bienvenido <%= request.getUserPrincipal().getName() %> 

		<form action="<c:url value='/logout'/>" method="post">
			<input type="submit" value="Salir"/>
		</form>
	</p> 
	<hr/>
	<%= request.getUserPrincipal() %> 	
	
	
	
	<p>
		<a href="<c:url value='/expedientesx/mostrar/todos'/>">
			Mostrar	expedientes
		</a>
	</p>
	
</div>

</body>
</html>
