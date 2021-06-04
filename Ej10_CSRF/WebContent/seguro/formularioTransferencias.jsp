<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>BFF</title>
    <link rel="stylesheet" href="../css/bootstrap.css">	
</head>
<body>

	<div class="text-center text-primary bg-light border-bottom">
	    <br/>
	    <br/>
	    <h1>
			Banco Fiduciario de Fidelidad
		</h1>
	    <br/>
	    <br/>
	</div>

	<br/>

	<h2 align="center">
		Formulario de transferencias		
	</h2>

	<br/>

	<form name="formulario" action="SVTransferencias" method="post">
		<!-- 
		-->
		<input type="text" name="CSRFToken" value="${CSRFToken}"/>

		<div class="row">
			
			<div class="col-8 offset-2">

				<div class="row">
		
				    <div class="col-3 mt-1">
				        <label>Cuenta</label>
				    </div>
				    <div class="col-9 mt-1">
				        <input class="form-control" name="cuenta" value="1111"/>
				    </div>
				
				    <div class="col-3 mt-1">
				        <label>Cantidad</label>
				    </div>
				    <div class="col-9 mt-1">
				        <input class="form-control" name="cantidad"/>
				    </div>
				
				    <div class="col-3 mt-1">
				        <label>Código de validación</label>
				    </div>
				    <div class="col-9 mt-1">
				        <input class="form-control" name="codigoValidacion"/>
				    </div>
		
				    <div class="col-3 mt-1"></div>
				    <div class="col-9 mt-1 text-right">
				        <input type="submit" class="btn btn-primary" value="Transferir"/>
				    </div>
		
				</div>
			</div>
		</div>

	</form>
	
</body>
</html>