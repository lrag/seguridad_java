<%@ page language="java" contentType="text/javascript; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

function avisoSesion(){
	<%
	int segundos = session.getMaxInactiveInterval()-60;	
	%>
	setTimeout(mensaje, <%=segundos*1000%>);
}

function mensaje(){
	alert("La sesión va a expirar");
}

avisoSesion();
   