<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%
//De esto se encarga el filtro de seguridad
//HttpSession sesion = request.getSession(false);
//if(sesion == null || sesion.getAttribute("usuario")==null){
//	response.sendRedirect("../login.html");
//	return;
//}
%>  
       
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Session</title>
    <link rel="stylesheet" href="../css/bootstrap.css">	
    
	<!-- Usamos este script para avisar de la próxima expiración de la sesión -->
	<script type="text/javascript" src="avisoTimeout.jsp"></script> 
</head>
<body>

	<div class="text-center text-success">
	    <br/>
	    <br/>
	    <h1>
			Clientes
		</h1>
	    <br/>
	    <br/>
	</div>

	<hr/>
				
	<div class="text-center">
		<a href="inicio.jsp">Volver</a>
		<br/>
		<br/>
		<form action="../SVLogin" method="post">
			<input type="hidden" name="accion" value="logout"/>
			<input type="submit" class="btn btn-primary"  value="salir"/>
		</form>		
	</div>	
				

</body>
</html>