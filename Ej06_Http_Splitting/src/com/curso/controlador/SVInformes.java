package com.curso.controlador;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/SVInformes")
public class SVInformes extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SVInformes() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String tipo    = request.getParameter("tipo");
		String inicio  = request.getParameter("inicio");
		String fin     = request.getParameter("fin");
		String formato = request.getParameter("formato");	
		
		//Para evitar este tipo de ataques siempre debemos de sanear la entrada
		//%0d%0a
		formato = formato.replaceAll("\\r|\\n", "");
		
		System.out.println(formato);
		response.setHeader("Content-Type", formato);
		response.getWriter().append("Informe generado y bla bla blá").append(request.getContextPath());
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
