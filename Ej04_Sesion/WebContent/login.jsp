<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<!DOCTYPE html>
<html>
<head>

<meta charset="ISO-8859-1">
	<%
	HttpSession sesion = request.getSession();
	if(sesion != null && sesion.getAttribute("usuario")!=null){
	%>
	<script>
		alert("No trastees con la barra del navegador...")
		document.location = "seguro/inicio.jsp"
	</script>	
	<%
	}
	%>     
    <title>Session</title>
    <link rel="stylesheet" href="css/bootstrap.css">	
</head>

<body>

	<div class="text-center text-success">
	    <h1 class="mt-5 mb-5">
			Manejo de la sesi�n
		</h1>
	</div>
	
	<hr/>

	<br/>
	
	<div class="row container-fluid">	
		<div class="col-4 offset-4">
			<div class="card card-primary">
				<div class="card-header">Login</div>
				<div class="card-body">		   
					<form action="SVLogin" method="post">		
						<input type="hidden" name="accion" value="login"/>			
						<div class="form-horizontal" id="formulario">
							<div class="form-group">
								<label class="control-label col-xs-2" for="login">Login</label>
								<div class="col-xs-10">
									<input type="text" class="form-control" name="login" />
								</div>
							</div>   
							<div class="form-group">
								<label class="control-label col-xs-2" for="pw">Pw</label>
								<div class="col-xs-10">
									<input type="text" class="form-control" name="pw"/>
								</div>
							</div>  
						</div> 			
						<br/>			
						<div class="text-center">
							<input type="submit" class="btn mr-1 btn-primary" style="width:110px" value="Entrar"/>
						</div>  		     
					</form>
				</div>
			</div>		
		</div>
	</div>

</body>
</html>    