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

//No cachearlo todo tiene un precio, y es que el servidor puede recibir m�s
//peticiones
@WebFilter("/seguro/*")
public class FiltroNoCache implements Filter {

    public FiltroNoCache() {
    }

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		System.out.println("Filtro no caché");

		//Los navegadores modernos no utilizan la caché para el botón "atrás" y los siguientes valores
		//se ignoran cuando el usuario lo pulsa:
		//
		//no-cache: se puede guardar en la caché, pero debe revalidarla con el servidor antes de reutilizarla. en caso de que el servidor no esté podría utilizarse la caché
		//no-store: No guardar en la caché 
		//must-revalidate: debe revalidarse siempre. si el servidor no está no se utiliza la caché
		HttpServletResponse rp = (HttpServletResponse) response;
		rp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, max-age=0"); // HTTP 1.1
		rp.setHeader("Pragma", "no-cache"); // HTTP 1.0, ya en desuso
		rp.setDateHeader("Expires", 0); //esta es la mas efectiva de todas
									    //cuando debe expirar una pagina en milisegundos
							            //desde el 1 de 1 de 1970

		//ANTES
		
		chain.doFilter(request, response);
		
		//DESPUES
		
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

}

