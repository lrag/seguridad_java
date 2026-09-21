package com.curso.controlador;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//Simula un recurso que NUNCA deberia ser accesible desde fuera: el
//equivalente casero al endpoint de metadata de la nube
//(169.254.169.254 en AWS/GCP/Azure), o a un panel de administracion
//interno que solo confia en que "nadie de fuera puede llegar hasta aqui".
//En un despliegue real estaria en una red privada; aqui vive en el mismo
//Tomcat que SVAvatar para poder hacer la demo sin depender de otro
//proyecto desplegado aparte, pero la idea es exactamente la misma: solo
//deberia poder alcanzarse desde dentro
@WebServlet("/interno/secreto")
public class SVInterno extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain");
		response.getWriter().println("Servicio interno. Si estas leyendo esto desde fuera del servidor, tienes un SSRF.");
		response.getWriter().println("ADMIN_TOKEN=s3cr3t0-que-nadie-deberia-ver-desde-fuera");
	}

}
