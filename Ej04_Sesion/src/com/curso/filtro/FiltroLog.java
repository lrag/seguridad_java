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

import com.curso.modelo.entidad.Usuario;

@WebFilter("/*")
public class FiltroLog implements Filter {

    public FiltroLog() {
    }

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest rq = (HttpServletRequest) request;
		HttpServletResponse rp = (HttpServletResponse) response;
		
		HttpSession sesion = rq.getSession(false);
		String log = "Filtro log. Petición recibida: "+rq.getMethod()+" "+rq.getRequestURI();
		if(sesion!=null && sesion.getAttribute("usuario")!=null) {
			log = log + ". Usuario: "+((Usuario) sesion.getAttribute("usuario")).getLogin();
		} else {
			log = log + ". Usuario no autenticado.";
		}
		
		System.out.println(log);

		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

}
