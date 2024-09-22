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
//El objetivo de este filtro ser�a sanear los parametros de entrada

@WebFilter("/SVPeliculas")
public class FiltroXSS implements Filter {

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		System.out.println("FiltroXSS");
		HttpServletRequest rq = (HttpServletRequest) request;
		//Aqui tendriamos que cambiar los parametros de entrada de
		//que nos vienen en la request, el problema es que los parametros
		//del request son inmutables
		//Por lo que vamos a crearnos un wrapper de la request y ah�
		//ser� donde alteremos los parametros, de hecho rq no tiene
		//setParameter por ejemplo
		XSSRequestWrapper xssRq = new XSSRequestWrapper(rq);
		//la clave luego esta en que en el doFilter, el objeto que pasamos
		//es el que acabamos de crear, por ello en todos los servlet
		//le llegar� el objeto envoltorio o wrapper
		chain.doFilter(xssRq, response);
	}

}







