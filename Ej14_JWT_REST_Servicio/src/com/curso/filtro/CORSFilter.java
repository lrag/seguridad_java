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
        System.out.println("CORSFilter HTTP Request: " + request.getMethod()+", "+request.getRequestURI());

        //Los headers para el CORS hay que colocarlos en todas las respuestas, no solo tras una petición OPTIONS
        
        //Los navegadores no siempre hacen el preflight y en esas ocasiones esperan a la respuesta para aceptarla o no dependiendo de
        //los headers recibidos
        //
        //Esas peticiones sin preflight son:
        //-Peticiones con los métodos:
        //	GET, HEAD y POST
        //-Que incluyan solo los headers:
        //	User-Agent, Accept, Accept-Language, Content-Language (estas las ponen los navegadores porque les da la gana)
        //  Content-Type
        //-Y además para content type solo con los valores
	    //  application/x-www-form-urlencoded
	    //  multipart/form-data
	    //  text/plain  
        
        // Autorizamos a cualquier dominio a consumir nuestros recursos        
        ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Origin", "*"); //El * es muy peligroso
        ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Methods","GET, OPTIONS, HEAD, PUT, POST, DELETE");
        ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Headers", "*");
        ((HttpServletResponse) servletResponse).addHeader("Access-Control-Expose-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
 
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