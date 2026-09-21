<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>XSS</title>
    <link rel="stylesheet" href="css/bootstrap.css">
</head>

<script type="application/javascript">

function XSS(){	
	 let xss = hex2a("273B616C65727428537472696E672E66726F6D43686172436F64652838382C38332C383329292F2F273B616C65727428537472696E672E66726F6D43686172436F64652838382C38332C383329292F2F223B0A616C65727428537472696E672E66726F6D43686172436F64652838382C38332C383329292F2F223B616C65727428537472696E672E66726F6D43686172436F64652838382C38332C383329292F2F2D2D0A3E3C2F5343524950543E223E273E3C5343524950543E616C65727428537472696E672E66726F6D43686172436F64652838382C38332C383329293C2F5343524950543E")
	 
	 document.getElementById("textoHtml").value = xss
	 document.getElementById("atributo").value = xss
	 document.getElementById("css").value = xss
	 document.getElementById("parametroUrl").value = xss 
	 document.getElementById("url").value = xss
	 document.getElementById("html").value = xss	 
	 document.getElementById("javascript").value = xss
}

//Payload polyglot mas moderno, intenta romper varios contextos a la vez
//(atributo con/sin comillas, javascript:, </style>/</title>/</textarea>/</script>
//ya abiertos, y un <svg onload=...> por si nada de lo anterior cuela)
function XSSModerno(){
	 let xss = hex2a("6A6156617343726970743A2F2A2D2F2A602F2A5C602F2A272F2A222F2A2A2F282F2A202A2F6F4E636C69436B3D616C657274282920292F2F2530442530412530642530612F2F3C2F7374596C652F3C2F7469744C652F3C2F74655874617245612F3C2F7363526970742F2D2D213E5C7833637356672F3C7356672F6F4E6C6F41643D616C65727428292F2F3E5C783365")

	 document.getElementById("textoHtml").value = xss
	 document.getElementById("atributo").value = xss
	 document.getElementById("css").value = xss
	 document.getElementById("parametroUrl").value = xss
	 document.getElementById("url").value = xss
	 document.getElementById("html").value = xss
	 document.getElementById("javascript").value = xss
}

function hex2a(hexx) {
    var hex = hexx.toString()
    var str = ''
    for (var i = 0; (i < hex.length && hex.substr(i, 2) !== '00'); i += 2)
        str += String.fromCharCode(parseInt(hex.substr(i, 2), 16))
    return str
}

window.onload = function(){
	document.getElementById("btnXSS").onclick = XSS
	document.getElementById("btnXSSModerno").onclick = XSSModerno
}

</script>

<body>

	<div class="text-center text-warning">
	    <br/>
	    <br/>
	    <h1>
			Validación XSS con ESAPI
		</h1>
	    <br/>
	    <br/>
	</div>
	
	<hr/>

	<br/>

	<form id="formulario" action="SVESAPI" method="POST">
	
		<div class="row">			
			<div class="col-8 offset-2">
				<div class="row">
		
				    <div class="col-2 mt-1">
				        <label>Nodo de texto</label>
				    </div>
				    <div class="col-10 mt-1">
				    	<input type="text" name="textoHtml" id="textoHtml" class="form-control" value="<h2>Texto</h2>"/>
				    </div>
				
				    <div class="col-2 mt-1">
				        <label>Atributo</label>
				    </div>
				    <div class="col-10 mt-1">
						<input type="text" name="atributo" id="atributo" class="form-control" value="green"/>
				    </div>
				
				    <div class="col-2 mt-1">
				        <label>Css</label>
				    </div>
				    <div class="col-10 mt-1">
						<input type="text" name="css" id="css" class="form-control" value="blue"/>
				    </div>

				    <div class="col-2 mt-1">
				        <label>Parámetro url</label>
				    </div>
				    <div class="col-10 mt-1">
						<input type="text" name="parametroUrl" id="parametroUrl" class="form-control" value="VALOR"/>
				    </div>

				    <div class="col-2 mt-1">
				        <label>Url</label>
				    </div>
				    <div class="col-10 mt-1">
						<input type="text" name="url" id="url" class="form-control" value="http://www.google.es"/>
				    </div>

				    <div class="col-2 mt-1">
				        <label>Html</label>
				    </div>
				    <div class="col-10 mt-1">
						<input type="text" name="html" id="html" class="form-control" value="<h2>Html</h2>"/>
				    </div>

				    <div class="col-2 mt-1">
				        <label>Javascript</label>
				    </div>
				    <div class="col-10 mt-1">
						<input type="text" name="javascript" id="javascript" class="form-control" value="Hola"/>
				    </div>
		
				    <div class="col-2 mt-1"></div>
				    <div class="col-10 mt-1 text-left">
				        <input type="submit" class="btn btn-primary" value="Guardar"/>
       					<input type="button" class="btn mr-1 btn-danger" style="width:110px" id="btnXSS" value="XSS"/>
       					<input type="button" class="btn mr-1 btn-danger" style="width:110px" id="btnXSSModerno" value="XSS moderno"/>
				    </div>
		
				</div>
			</div>
		</div>

		<hr/>
		
		<div class="text-center">
			&#x27;&#x3B;&#x61;&#x6C;&#x65;&#x72;&#x74;&#x28;&#x53;&#x74;&#x72;&#x69;&#x6E;&#x67;&#x2E;&#x66;&#x72;&#x6F;&#x6D;&#x43;&#x68;&#x61;&#x72;&#x43;&#x6F;&#x64;&#x65;&#x28;&#x38;&#x38;&#x2C;&#x38;&#x33;&#x2C;&#x38;&#x33;&#x29;&#x29;&#x2F;&#x2F;&#x27;&#x3B;&#x61;&#x6C;&#x65;&#x72;&#x74;&#x28;&#x53;&#x74;&#x72;&#x69;&#x6E;&#x67;&#x2E;&#x66;&#x72;&#x6F;&#x6D;&#x43;&#x68;&#x61;&#x72;&#x43;&#x6F;&#x64;&#x65;&#x28;&#x38;&#x38;&#x2C;&#x38;&#x33;&#x2C;&#x38;&#x33;&#x29;&#x29;&#x2F;&#x2F;&#x22;&#x3B;&#x0A;&#x61;&#x6C;&#x65;&#x72;&#x74;&#x28;&#x53;&#x74;&#x72;&#x69;&#x6E;&#x67;&#x2E;&#x66;&#x72;&#x6F;&#x6D;&#x43;&#x68;&#x61;&#x72;&#x43;&#x6F;&#x64;&#x65;&#x28;&#x38;&#x38;&#x2C;&#x38;&#x33;&#x2C;&#x38;&#x33;&#x29;&#x29;&#x2F;&#x2F;&#x22;&#x3B;&#x61;&#x6C;&#x65;&#x72;&#x74;&#x28;&#x53;&#x74;&#x72;&#x69;&#x6E;&#x67;&#x2E;&#x66;&#x72;&#x6F;&#x6D;&#x43;&#x68;&#x61;&#x72;&#x43;&#x6F;&#x64;&#x65;&#x28;&#x38;&#x38;&#x2C;&#x38;&#x33;&#x2C;&#x38;&#x33;&#x29;&#x29;&#x2F;&#x2F;&#x2D;&#x2D;&#x0A;&#x3E;&#x3C;&#x2F;&#x53;&#x43;&#x52;&#x49;&#x50;&#x54;&#x3E;&#x22;&#x3E;&#x27;&#x3E;&#x3C;&#x53;&#x43;&#x52;&#x49;&#x50;&#x54;&#x3E;&#x61;&#x6C;&#x65;&#x72;&#x74;&#x28;&#x53;&#x74;&#x72;&#x69;&#x6E;&#x67;&#x2E;&#x66;&#x72;&#x6F;&#x6D;&#x43;&#x68;&#x61;&#x72;&#x43;&#x6F;&#x64;&#x65;&#x28;&#x38;&#x38;&#x2C;&#x38;&#x33;&#x2C;&#x38;&#x33;&#x29;&#x29;&#x3C;&#x2F;&#x53;&#x43;&#x52;&#x49;&#x50;&#x54;&#x3E;
		</div>
		
	</form>

</body>
</html>