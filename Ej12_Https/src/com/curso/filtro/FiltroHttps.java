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

@WebFilter("/*")
public class FiltroHttps implements Filter {
	
	public void destroy() {
	}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		System.out.println(httpRequest.isSecure());
		
		System.out.println("Filtro HTTPS:" + httpRequest.getRequestURI());
		
		if(!httpRequest.isSecure()){
			
			//Esto es adecuado para un api rest: la aplicaci�n cliente debe saber que esto es a trav�s de HTTPS
			//response.getWriter().append("No se admiten peticiones que no sean https");
			
			httpResponse.sendRedirect("Https://"
									  +httpRequest.getServerName()
									  +":8443"+httpRequest.getRequestURI());
	
			return;
		}
		chain.doFilter(request, response);
	}


	public void init(FilterConfig fConfig) throws ServletException {

	}

}
