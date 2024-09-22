package com.curso.controlador;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/seguro/SVClientes")
public class SVClientes extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SVClientes() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Petición recibida en SVClientes");
		
		/*
		HttpSession sesion = request.getSession(false);
		if(sesion==null || sesion.getAttribute("usuario")==null) {
			response.sendRedirect("../login.html");
			return;
		}
		*/
		
		request.getRequestDispatcher("clientes.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}



