package com.curso.filtro;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter("/seguro/*")
public class FiltroAutenticacion implements Filter {

    public FiltroAutenticacion() {
    }

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		System.out.println("Filtro seguridad");
		
		HttpServletRequest rq = (HttpServletRequest) request;
		HttpServletResponse rp = (HttpServletResponse) response;
		
		HttpSession sesion = rq.getSession(false); 
		if(sesion == null || sesion.getAttribute("usuario")==null){
			//Si pusieramos "@WebFilter("/*")" podriamos tener problemas de 
			//redireccinamiento, ya que si solicitamos cualquier registro
			//vamos a login, y login es cualquier registro
			rp.sendRedirect("../login.html");
			return;
		}
		
		//Si se ejecuta esta línea la petición sigue hacia delante
		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

}
