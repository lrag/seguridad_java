function clasificar(id){
	document.formulario.action = "../clasificar";
	document.getElementById("id").value = id;
	document.formulario.submit();
}

function desclasificar(id){
	document.formulario.action = "../desclasificar";
	document.getElementById("id").value = id;
	document.formulario.submit();
}

window.onload = function(){
	enlacesClasificar = document.querySelectorAll('[id^="clasificar-"]')
	enlacesClasificar.forEach( enlace => enlace.onclick = () => clasificar(enlace.id.substring(11)))
	
	enlacesDesclasificar = document.querySelectorAll('[id^="desclasificar-"]')
	enlacesDesclasificar.forEach( enlace => enlace.onclick = () => desclasificar(enlace.id.substring(14)))
}
