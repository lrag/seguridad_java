<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%

HttpSession sesion = request.getSession();
if(sesion == null || sesion.getAttribute("usuario")==null){
	response.sendRedirect("../login.html");
	return;
}
%>       
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Session</title>
    <link rel="stylesheet" href="../css/bootstrap.css">		
	<script type="text/javascript" src="avisoTimeout.jsp"></script>
</head>

<body>

	<div class="text-center text-success">
	    <br/>
	    <br/>
	    	<h1>
			Página de inicio. Usr: ${usuario.nombre}
		</h1>
	    <br/>
	    <br/>
	</div>
	
	<hr/>
	
	<div class="text-center">
		<a href="SVClientes">Clientes</a>
		Pedidos
		Facturas
	
		<br/>
		<br/>
	
		<form action="../SVLogin" method="post">
			<input type="hidden" name="accion" value="logout"/>		
			<p align="center">
				<input type="submit" class="btn btn-primary" value="salir"/>
			</p>
		</form>
		
	</div>	

</body>
</html>