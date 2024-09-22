package com.curso.filtro;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

//Cachearlo todo tiene un precio, y es que el servidor puede recibir m�s
//peticiones
//@WebFilter("/seguro/*")
public class FiltroNoCache implements Filter {

    public FiltroNoCache() {
    }

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		System.out.println("Filtro no cach�");

		//Add todas estas cabeceras para eviar el cacheo en el navegador
		//, algunas pueden funcionar, otras no
		//dependiendo del navegador y su versi�n y el protocolo http usado
		HttpServletResponse rp = (HttpServletResponse) response;
		rp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
		rp.setHeader("Pragma", "no-cache"); // HTTP 1.0
		rp.setDateHeader("Expires", 0); //esta es la mas efectiva de todas
									    //cuando debe expirar una pagina en milisegundos
							            //desde el 1 de 1 de 1970

		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

}
