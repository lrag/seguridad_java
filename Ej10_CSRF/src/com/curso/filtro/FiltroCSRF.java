package com.curso.filtro;

import java.io.IOException;
import java.util.UUID;

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
public class FiltroCSRF implements Filter {

    public FiltroCSRF() {
    }

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		System.out.println("Filtro CSRF");
		
		HttpServletRequest rq = (HttpServletRequest) request;
		HttpServletResponse rp = (HttpServletResponse) response;
		
		HttpSession sesion = rq.getSession(false); 
		if(sesion==null) {
			rp.sendRedirect("../login.html");
			return;
		}
		
		if(rq.getMethod().equals("GET")) {
			//Generamos un numero UUID (Universally unique identifier)
			//para crear el CSRF Token para guardarlo en la session y 
			//meterlo en el jsp. 
			String token = UUID.randomUUID().toString();
			sesion.setAttribute("CSRFToken", token);
		} else {
			String CSRFTokenFormulario = request.getParameter("CSRFToken");
			String CSRFTokenSesion = (String) sesion.getAttribute("CSRFToken");
			//Sacamos el token que en
			System.out.println("CSRFTokenFormulario:" + CSRFTokenFormulario);
			System.out.println("CSRFTokenSesion:" + CSRFTokenSesion);
			//Aqui comparamos el token que se metio en la session con 
			//el token que se envio al formulario. Si no coinciden es que ha
			//habido un ataque CSRF
			if(CSRFTokenSesion == null || CSRFTokenFormulario==null 
					|| !CSRFTokenFormulario.equals(CSRFTokenSesion)){
				System.out.println("Ataque CSRF detectado!");
				//Simplificacion: aqui hay que hacer el LOGOUT!!!!!
				rp.sendRedirect("../login.html");
				return;
			}			
		}
		
		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

}
