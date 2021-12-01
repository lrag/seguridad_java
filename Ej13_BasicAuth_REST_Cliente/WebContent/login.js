
function login(){	
	//Y por esto no debemos utilizar la basic authentication:
	sessionStorage.setItem("username",username.value)
	sessionStorage.setItem("password",password.value)	
	document.location = "peliculas.html"	
}

$(inicializar)

function inicializar(){
    $("#btnEntrar").click(login)
	$("#mensaje").hide()
}
