<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">    
    <title>Listado de clientes</title>
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="css/bootstrap-theme.min.css">
</head>

<script>

function botonPulsado(){
	alert(this.dataset.idcliente)
}

window.onload = function(){	
	for(let boton of document.querySelectorAll("#tablaClientes [type=button]")){
		boton.onclick = botonPulsado
	}	
}

</script>

<body>

    <div class="page-header">
        <h1 class="text-center">Listado de clientes</h1> 
    </div>
	
	<form action="SVClientes" method="get">

		<input type="hidden" name="accion" value="verFormulario"/>

	    <div class="row">
	        <div class="col-xs-1"></div>  
	        <div class="text-center col-xs-10">  
	            <input type="submit" class="btn btn-primary" value="Nuevo"/>
	        </div>
	        <div class="col-xs-1"></div>  
	    </div>
	
	    <br/>
		
	    <div class="row">
	        <div class="col-xs-2"></div>  
	        <div class="col-xs-8">  
	            <table class="table table-hover table-striped">
	                <thead>
	                	<tr>
		                    <th>Nombre</th>
		                    <th>Dirección</th>
		                    <th>Teléfono</th>
	                    </tr>
	                </thead>
	                <tbody id="tablaClientes">
						<c:forEach var="c" items="${listadoClientes}">
							<tr>
								<td>
									<input type="button" data-idcliente="${c.id}" value="Pulsar"/>
									(${c.id})&nbsp;
									<!-- Cuidado que esto no nos coloque el sessionId en la url -->
									<c:url var="enlace" value="SVClientes">
										<c:param name="accion" value="seleccionar"/>							
										<c:param name="idCliente" value="${c.id}"/>
									</c:url>
									<a href="${enlace}">${c.nombre}</a>
								</td>
								<td>${c.direccion}</td>
								<td>
									${c.telefono}
								</td>
							</tr>			
						</c:forEach>		
	                </tbody>
	            </table>
	        </div>
	        <div class="col-xs-2"></div>  
	    </div>	
	
	</form>

</body>
</html>





