package com.curso.controlador;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/seguro/SVTransferencias")
public class SVTransferencias extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SVTransferencias() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		/*
		HttpSession sesion = request.getSession(false);
		//Generamos un numero UUID (Universally unique identifier)
		//para crear el CSRF Token para guardarlo en la session y 
		//meterlo en el jsp. 
		String token = UUID.randomUUID().toString();
		sesion.setAttribute("CSRFToken", token);
		*/		
		
		request.getRequestDispatcher("formularioTransferencias.jsp").forward(request,response);
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		/*
		String CSRFTokenFormulario = request.getParameter("CSRFToken");
		HttpSession sesion = request.getSession(false);
		String CSRFTokenSesion = (String) sesion.getAttribute("CSRFToken");
		//Sacamos el token que en
		System.out.println("CSRFTokenFormulario" + CSRFTokenFormulario);
		System.out.println("CSRFTokenSesion" + CSRFTokenSesion);
		//Aqui comparamos el token que se metio en la session con 
		//el token que se envio al formulario. Si no coinciden es que ha
		//habido un ataque CSRF
		if(CSRFTokenSesion == null || CSRFTokenFormulario==null 
				|| !CSRFTokenFormulario.equals(CSRFTokenSesion)){
			System.out.println("Ataque CSRF detectado!");
			//Faltaría invalidar la sesión
			response.sendRedirect("../login.html");
			return;
		}
		*/		
			
		String cuenta = request.getParameter("cuenta");
		String cantidad = request.getParameter("cantidad");
		String codigoValidacion = request.getParameter("codigoValidacion");
	
		System.out.println("Transferencia de "+cantidad+" euros a la cuenta '"+cuenta+"' efectuada...");
	
		response.sendRedirect("inicio.jsp");
		
	}

}
