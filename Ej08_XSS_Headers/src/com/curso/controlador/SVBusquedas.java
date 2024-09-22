package com.curso.controlador;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/seguro/SVBusquedas")
public class SVBusquedas extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public SVBusquedas() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String criterio = request.getParameter("criterio");
	
		//Aqui podríamos sanear la entrada para eliminar el JS
		
		//
		//Se ejecuta la búqueda...
		//
		
		request.setAttribute("criterio", criterio);
		request.setAttribute("resultado", "Este es el resultado de la búsqueda");
		request.getRequestDispatcher("inicio.jsp").forward(request, response);
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
