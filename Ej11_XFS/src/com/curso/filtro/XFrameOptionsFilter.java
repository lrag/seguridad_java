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
 
//Este hay que colocarlo en el ejercicio 4 de sesiones
//@WebFilter(urlPatterns = "/*") 
public class XFrameOptionsFilter implements Filter { 
 
    @Override 
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, 
                         FilterChain filterChain) throws IOException, ServletException { 
 
        HttpServletResponse response = (HttpServletResponse) servletResponse; 
        response.addHeader("X-Frame-Options", "DENY"); 
        //response.addHeader("X-Frame-Options", "SAMEORIGIN"); 
        //response.addHeader("X-Frame-Options", "ALLOW-FROM http://localhost:8080"); 
 
        filterChain.doFilter(servletRequest, response); 
    } 
 
    @Override 
    public void init(FilterConfig filterConfig) throws ServletException { 
    } 
 
    @Override 
    public void destroy() { 
    } 
}