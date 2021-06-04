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

@WebFilter("/seguro/*")
public class FiltroOrigin implements Filter {

    public FiltroOrigin() {
    }

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		System.out.println("Filtro ORIGIN");
		
		HttpServletRequest rq = (HttpServletRequest) request;
		HttpServletResponse rp = (HttpServletResponse) response;
		
		String origin = rq.getHeader("origin");
		System.out.println("Origin:"+origin);	
		if(!"http://localhost:8080".equals(origin) && origin!=null && !rq.getMethod().equals("GET")) {
			System.out.println("Peticion rechazada por ser de un origen desconocido");
			rp.sendRedirect("../login.html");
			return;
		}
		
		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

}
