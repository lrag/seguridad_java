package com.curso.controlador;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.curso.modelo.entidad.Pelicula;
import com.curso.xss.XSSEncoder;
import com.curso.xss.XSSException;

//@WebServlet("/SVPeliculas")
public class SVPeliculas extends HttpServlet {

	private static final long serialVersionUID = 1L;
    
    public SVPeliculas() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String accion = request.getParameter("accion");
		
		String siguienteVista = "formularioPeliculas.jsp";
		if("verResumen".equals(accion)){			
			siguienteVista = "resumenPelicula.jsp";
		} 	
		
		request.getRequestDispatcher(siguienteVista).
			forward(request,response);		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		//Recoger los par�metros y hacer una primera conversion
		//de String al tipo adecuado
		
		Integer idPelicula = null;
		try {
			idPelicula = Integer.valueOf(request.getParameter("idPelicula"));
		} catch (NumberFormatException e) {
			//No hacemos nada: solo falla cuando estan insertando
		}
		String titulo = request.getParameter("titulo");
		String director = request.getParameter("director");
		String genero = request.getParameter("genero");
		Integer year = new Integer(request.getParameter("year"));
		
		//Construir objetos del modelo con los parametros recibidos
		Pelicula p = new Pelicula(idPelicula, titulo, director, genero, year);
		
		//Validar los objetos
		
		/////////////////////////////////////////////		
		// Validaci�n XSS mediante las anotaciones //
		/////////////////////////////////////////////		
		try {
			XSSEncoder.getInstancia().encode(p);
		} catch (XSSException e) {
			e.printStackTrace();
		}				
		
		//Averiguar que nos est�n pidiendo
		String accion = request.getParameter("accion");
		
		//Llamar al m�todo de negocio adecuado
		if("insertar".equals(accion)){
			//gestorPeliculas.insertar(p);
			HttpSession sesion = request.getSession(true);
			sesion.setAttribute("peliculaSel", p);
		} else if("modificar".equals(accion)){
			//gestorPeliculas.modificar(p);
		} else if("borrar".equals(accion)){
			//gestorPeliculas.borrar(p);
		}
		
		//Despues de una petici�n post se hace un redirect.
		//Si se est� usando MVC no se puede hacer un redirect a una vista
		//Se hace un redirect al controlador que muestre la vista que nos 
		//interesa
		//GET SVPeliculas
		response.sendRedirect("SVPeliculas?accion=verResumen");
	
	}

}
