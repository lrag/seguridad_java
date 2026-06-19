package com.curso.controlador;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/SVTeclas")
public class SVTeclas extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SVTeclas() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String t = request.getParameter("t");
		String hash = request.getParameter("hash");
		String pagina = request.getParameter("pagina");
		System.out.println(hash+", "+pagina+", "+t);
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
