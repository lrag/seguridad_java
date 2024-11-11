package com.curso.filtro;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
 
        HttpServletRequest  request   = (HttpServletRequest) servletRequest;
        HttpServletResponse respuesta = (HttpServletResponse) servletResponse;
        System.out.println("CORSFilter HTTP Request: " + request.getMethod()+", "+request.getRequestURI());

        //Los headers para el CORS hay que colocarlos en todas las respuestas, no solo tras una petición OPTIONS
        
        //Los navegadores no siempre hacen el preflight y en esas ocasiones esperan a la respuesta para aceptarla o no dependiendo de
        //los headers recibidos
        //
        //Esas peticiones sin preflight son:
        //-Peticiones con los métodos:
        //	GET, HEAD y POST
        //-Que incluyan solo los headers:
        //	User-Agent, Accept, Accept-Language, Content-Language
        //  Content-Type
        //-Y adem�s para content type solo con los valores
	    //  application/x-www-form-urlencoded
	    //  multipart/form-data
	    //  text/plain  
        //Autorizamos a cualquier dominio a consumir nuestros recursos   
        //Colocar '*' solo en apis públicas 
        //respuesta.addHeader("Access-Control-Allow-Origin", "*");

        //Si admitimos peticiones de solo un origen lo a�adimos con protocolo://IP:puerto
        //respuesta.addHeader("Access-Control-Allow-Origin", "http://localhost:8081");
        
        //Si admitimos peticiones de más de un origen debemos comprobar si el origen de la petición está
        //en la lista y responder en consecuencia, porque solo se puede colocar un origen en 'Access-Control-Allow-Origin'
        String origin = request.getHeader("Origin");
        System.out.println("Petición recibida de:"+origin);

        List<String> origenesPermitidos = new ArrayList<>();
        origenesPermitidos.add("http://localhost:8081");
        origenesPermitidos.add("http://www.nosequé.es");
        origenesPermitidos.add("http://www.nosecuántos.es");
        
        System.out.println(origenesPermitidos.contains(origin));
        if(origenesPermitidos.contains(origin)) {
        	System.out.println("OK");
        	respuesta.addHeader("Access-Control-Allow-Origin", origin);
        }
        
        respuesta.addHeader("Access-Control-Allow-Methods","GET, OPTIONS, HEAD, PUT, POST, DELETE");
        respuesta.addHeader("Access-Control-Allow-Headers", "*");
        respuesta.addHeader("Access-Control-Expose-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
 
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
	 
        //Si la petici�n ha sido un OPTIONS respondemos con el status code 'ACCEPTED',
        //como se espera en el 'cors handsake'
        
        if (request.getMethod().equals("OPTIONS")) {
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            return;
        }
 
        //Si no ha sido un options continuamos procesando la petici�n en el siguiente filtro/controlador
        chain.doFilter(request, servletResponse);
    }
 
    public void destroy() {
    }
 
}