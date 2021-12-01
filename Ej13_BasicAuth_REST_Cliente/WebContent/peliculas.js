
let url = 'http://localhost:8080/Ej13_BasicAuth_REST_Servicio/servicios/peliculas'
//Guardamos en esta variable el id de la película seleccionada
var idPeliculaSel = null;
var cabeceraAuth = null

function crearCabeceraAuth(){
	
	let username = sessionStorage.getItem("username")
	let password = sessionStorage.getItem("password")
	
	cabeceraAuth = {
				"Authorization": "Basic " + btoa(username + ":" + password)
			}
}

function procesarErrorPeticion(jqXHR){
	let status = jqXHR.status
	if(status=="401" || status=="403"){
		window.location = "login.html" 
	}
}

function enviarPeticionListarPeliculas(){

    console.log(arguments.length)
    for(var a=0; a<arguments.length; a++){
        console.log(arguments[a])
    }

    //llamada sin autenticación
    //$.ajax( { 'url'     : 'http://localhost:8080/Ej13_REST_Servicio/servicios/peliculas', 
    //          'success' : rellenarTablaPeliculas  } );    

    var usuario = "a";
    var pw = "a";
    $.ajax( { url     : url, 
    	      headers : cabeceraAuth } )
    .done(rellenarTablaPeliculas) 
    .fail(procesarErrorPeticion)
}

function rellenarTablaPeliculas(peliculas){

    var tabla = $("#tablaPeliculas");
    tabla.html("");
    for(let a=0; a<peliculas.length; a++){
        let p = peliculas[a];
        let tr = $("<tr id='pelicula_"+p.id+"'>"+
                "<td>"+p.titulo+"</td>"+
                "<td>"+p.director+"</td>"+
                "<td>"+p.genero+"</td>"+
                "<td>"+p.fechaEstreno+"</td>"+
          "</tr>").appendTo(tabla)
        tr.click(()=>{
			seleccionarPelicula(p.id)
		})
    }
    
    vaciarFormularioPelicula()

}

function seleccionarPelicula(id){
    
    $("#pelicula_"+idPeliculaSel).removeClass("success")

    idPeliculaSel = id
    $("#pelicula_"+id).addClass("success")

    $("#btnInsertar").prop("disabled",true)
    $("#btnModificar").prop("disabled",false)
    $("#btnBorrar").prop("disabled",false)

    $.ajax( { url     : url+"/"+id,
    		  headers : cabeceraAuth,
              success : rellenarFormularioPelicula,
              error   : function(){ alert('ZASCA!')} } )
}

function rellenarFormularioPelicula(pelicula){
    $("#titulo").val(pelicula.titulo)
    $("#director").val(pelicula.director)
    $("#genero").val(pelicula.genero)
    $("#estreno").val(pelicula.fechaEstreno)
}

function insertarPelicula(){

    var pelicula = { 
    	'titulo'       : titulo.value,
        'director'     : director.value,
        'genero'       : genero.value,
        'fechaEstreno' : estreno.value 
    }
    var json = JSON.stringify(pelicula)

    $.ajax( { type        : 'post', 
              url         : url,
    		  headers     : cabeceraAuth,
              success     : enviarPeticionListarPeliculas,
              error       : function(){ alert("ZASCA!") },
              contentType : 'application/json; charset=utf-8',
              data        : json               
            } )
}

function modificarPelicula(){

    var pelicula = { 
    	'titulo'       : titulo.value,
        'director'     : director.value,
        'genero'       : genero.value,
        'fechaEstreno' : estreno.value 
    };
	var json = JSON.stringify(pelicula)

    $.ajax( { type        : 'put', 
              url         : url,
    		  headers     : cabeceraAuth,
              success     : enviarPeticionListarPeliculas,
              error       : function(){ alert("ZASCA!") },
              contentType : 'application/json; charset=utf-8',
              data        : json               
            } )
}

function borrarPelicula(){
    console.log("Borrar:"+idPeliculaSel)
    $.ajax( { type        : 'delete', 
		  	  headers : cabeceraAuth,
              url         : url+'/'+idPeliculaSel,
              success     : enviarPeticionListarPeliculas,
              error       : function(){ alert("ZASCA!") } } )
}

function vaciarFormularioPelicula(){

    $("#pelicula_"+idPeliculaSel).removeClass("success")

    $("#btnInsertar").prop("disabled",false)
    $("#btnModificar").prop("disabled",true)
    $("#btnBorrar").prop("disabled",true)

    idPeliculaSel = null
    $("#titulo").val("")
    $("#director").val("")
    $("#genero").val("")
    $("#estreno").val("")
}

$(function(){

    $("#btnInsertar").click(insertarPelicula)
    $("#btnModificar").click(modificarPelicula)
    $("#btnBorrar").click(borrarPelicula)
    $("#btnVaciar").click(vaciarFormularioPelicula)

    $("#btnModificar").prop("disabled",true)
    $("#btnBorrar").prop("disabled",true)

    crearCabeceraAuth()
    enviarPeticionListarPeliculas()

});

