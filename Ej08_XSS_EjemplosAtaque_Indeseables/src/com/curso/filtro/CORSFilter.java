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
 
@WebFilter(urlPatterns = "/*") 
public class CORSFilter implements Filter {
 
    public CORSFilter() {
    }
 
    public void init(FilterConfig fConfig) throws ServletException {
    }
 
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
 
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        System.out.println("CORSFilter HTTP Request: " + request.getMethod());
 
        // Autorizamos a cualquier dominio a consumir nuestros recursos        
        ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Origin", "*");
        ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Methods","GET, OPTIONS, HEAD, PUT, POST");
        ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
 
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
 
        //Si la petición ha sido un OPTIONS respondemos con el status code 'ACCEPTED'
        //como se espera en el 'cors handsake'
        if (request.getMethod().equals("OPTIONS")) {
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            return;
        }
 
        //Si no ha sido un options continuamos procesando la petición en el siguiente filtro/controlador
        chain.doFilter(request, servletResponse);
    }
 
    public void destroy() {
    }
 
}