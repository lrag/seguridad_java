package com.curso.controlador.filtro;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import com.curso.modelo.persistencia.util.HibernateUtil;

//
//Open Session In View Filter
//
//Esto es un antipatr�n, pero buscamos aqu� la simplicidad
//
@WebFilter("/*")
public class FiltroTransacciones implements Filter {

    public FiltroTransacciones() {
    }

    public void init(FilterConfig fConfig) throws ServletException {
    }

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		//Solo inicializaremos una transacci�n si la petici�n va dirigida a un controlador
		//..qu� menos...
		HttpServletRequest rq = (HttpServletRequest) request;
		String uri = rq.getRequestURI();
		if( uri.endsWith(".css") || uri.endsWith(".html") || uri.endsWith(".js") ) {
			System.out.println(uri);
			chain.doFilter(request, response);
			return;
		}			
		
		System.out.println("TX");
		HibernateUtil.getSF().getCurrentSession().beginTransaction();
		chain.doFilter(request, response);
		HibernateUtil.getSF().getCurrentSession().getTransaction().commit();
		
	}

	public void destroy() {
	}

}
