<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Document</title>

    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="css/bootstrap-theme.min.css">
    
</head>

<script type="text/javascript" src="js/jquery.js"></script>
<script type="text/javascript" nonce="${nonce}">

function login(){	
	let parametros = "username="+$("#username").val()+"&password="+$("#password").val()
		
    $.ajax( 
    	{ 
    	  'url'  : 'http://localhost:8080/Ej14_JWT_REST_Servicio/SVAutenticacion',
          'type' : 'post',
          'data' : parametros ,
          'contentType' : 'application/x-www-form-urlencoded'
    	}
    )
    .done((body,textStatus, jqXHR ) => loginExito(jqXHR.getResponseHeader('authorization')))	
	.fail(loginFallo)    
}

function loginExito(authorization){
	//console.log(authorization)
	//podemos ver el token en www.jwt.io
	sessionStorage.setItem("jwt",authorization)
	window.location = "peliculas.jsp"
}

function loginFallo(){
	$("#mensaje").show()
}

$(inicializar);

function inicializar(){
	$("#mensaje").hide();
    $("#btnEntrar").click(login);
}

</script>


<body>

    <div class="text-center page-header">
        <h1>
            Login - MISMO SERVIDOR 
        </h1>
    </div>


	<div class="row">
	  <div class="col-xs-0 col-sm-4"></div>
	  <div class="col-xs-12 col-sm-4">
	    <div class="panel panel-primary">
	      <div class="panel-heading">Login</div>
	      <div class="panel-body">
	
	        <div class="form-horizontal" id="formulario">
	          <div class="form-group">
	            <label class="control-label col-xs-2" for="username">Login</label>
	            <div class="col-xs-10">
	              <input type="text" class="form-control" id="username"/>
	            </div>
	          </div>   
	          <div class="form-group">
	            <label class="control-label col-xs-2" for="password">Pw</label>
	            <div class="col-xs-10">
	              <input type="text" class="form-control" id="password"/>
	            </div>
	          </div>  
	          
	          <div class="form-group">
	            <div class="col-xs-2"></div>
	            <div class="col-xs-8">
	              <input type="button" value="Entrar" id="btnEntrar"/>
	            </div>
	          </div>   
	          
	        
	          <div id="mensaje" class="alert alert-danger">Credenciales incorrectas</div>
	        	          
        
	        </div> <!--formulario-->
	      </div>
	    </div>
	  </div>
	  <div class="col-xs-0 col-sm-4"></div>
	</div>  


</body>
</html>

    