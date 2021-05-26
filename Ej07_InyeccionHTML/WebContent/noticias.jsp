<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">    
    <title>Insert title here</title>

    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="css/bootstrap-theme.min.css">

</head>

<script type="text/javascript" src="js/jquery.js"></script>
<script type="text/javascript" src="js/bootstrap.min.js"></script>

<body>

    <div class="page-header">
        <h1 class="text-center">Noticias de rabiosa actualidad</h1> 
    </div>
    
    <div class="row">
        <div class="col-xs-1"></div>  
        <div class="text-center col-xs-5">  
			
			<c:forEach var="n" items="${noticias}">
				
				<div class="jumbotron">
					<h2>
						<font color="green">
							${n.titulo}
						</font>
					</h2>
					<p style="text-align : justify;">
						${n.texto}
					</p>		
				</div>
				
			</c:forEach>	

        </div>

        <div class="text-center col-xs-5">  

			<form action="SVNoticias" method="post">
			
		        <div class="form-horizontal">
		            <div class="form-group">
		                <label class="control-label col-xs-2" for="titulo">Título</label>
		                <div class="col-xs-8">
		                    <input type="text" id="titulo" name="titulo" class="form-control" />
		                </div>
		            </div>  
		            <div class="form-group">
		                <label class="control-label col-xs-2" for="texto">Texto</label>
		                <div class="col-xs-8">
		                	<textarea rows="10" cols="80" name="texto" class="form-control"></textarea>
		                </div>
		            </div>  
				</div>			
			
		        <div class="text-center">  
		            <input type="submit" id="btnInsertar" class="btn btn-primary" value="Enviar"/>
		        </div>
			
				<p style="text-align:justify;width:100%">
					&lt;form action="http://www.paginapirataamasnopoder.com">
						&lt;table border="1" align="center">
							&lt;tr>
								&lt;td>Login&lt;/td>
								&lt;td>
									&lt;input type="text" name="login"/>
								&lt;/td>
							&lt;/tr>
							&lt;tr>
								&lt;td>Pw&lt;/td>
								&lt;td>
									&lt;input type="text" name="pw"/>
								&lt;/td>
							&lt;/tr>
							&lt;tr>
								&lt;td colspan="2" align="right">
									&lt;input type="submit" value="Entrar"/>
								&lt;/td>
							&lt;/tr>	
						&lt;/table>
					&lt;/form>
				</p>
			
			</form>

        </div>
        <div class="col-xs-1"></div>  
    </div>


	<!-- 

	<div style="width:50%;float:left">
		<h1>
			Noticias
		</h1>
		
		<c:forEach var="n" items="${noticias}">
			<h2>
				<font color="green">
					${n.titulo}
				</font>
			</h2>
			<p style="text-align : justify;">
				${n.texto}
			</p>		
		</c:forEach>	
	</div>
	
	<div>
	
		<form action="SVNoticias" method="post">
			<table align="center">
				<tr>
					<td>Título</td>
					<td>
						<input type="text" name="titulo"/>
					</td>
				</tr>
				<tr>
					<td valign="top">Texto</td>
					<td>
						<textarea rows="10" cols="80" name="texto"></textarea>
					</td>
				</tr>
				<tr>
					<td></td>
					<td>
						<input type="submit" value="Enviar"/>
					</td>				
				</tr>
			</table>
		
		</form>
									
	</div>
	-->

</body>
</html>