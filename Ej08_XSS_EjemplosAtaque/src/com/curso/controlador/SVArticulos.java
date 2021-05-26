package com.curso.controlador;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.curso.modelo.entidad.Articulo;
import com.curso.modelo.entidad.Usuario;
import com.curso.modelo.negocio.GestorArticulos;

@WebServlet("/seguro/SVArticulos")
public class SVArticulos extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private GestorArticulos gestorArticulos = new GestorArticulos();
	
    public SVArticulos() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String accion = request.getParameter("accion");

		String sigVista = "articulos.jsp";
		if("crearArticulo".equals(accion)){
			sigVista = "formularioArticulo.jsp";
		} else {
			request.setAttribute("articulos", gestorArticulos.listarArticulos());
		}
		
		request.getRequestDispatcher(sigVista).forward(request, response);
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		HttpSession sesion = request.getSession(false);
		Usuario usr = (Usuario) sesion.getAttribute("usuario");
		String titulo = request.getParameter("titulo");
		String texto  = request.getParameter("texto");
		
		//Aqui deberiamos sanear la entrada
		//no lo hacemos para que el ejemplo funcione
		
		Articulo articulo = new Articulo(usr, titulo, texto);
		//simulamos negocio pero la capa de acceso a datos esta en memoria
		gestorArticulos.insertar(articulo);
		
		response.sendRedirect("SVArticulos");		
	
	}

}







