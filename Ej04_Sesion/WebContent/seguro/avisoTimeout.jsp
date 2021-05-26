<%@ page language="java" contentType="text/javascript; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

function avisoSesion(){
	<%
	//Avisamos al usuario 1 minuto antes de que se desconecte
	//la sesion una vez pasado el intervalo de inactividad
	int segundos = session.getMaxInactiveInterval()-60;	
	%>
	//la funcion establece cuando queremos ejecutar
	//una funcion cuando hayan pasado X milesimas de segundos
	setTimeout(mensaje, <%=segundos*1000%>);
}

function mensaje(){
	alert("La sesión va a expirar");
	//
	
}
//cuando carguemos la pagina, establecemos esta funcion
avisoSesion();
   