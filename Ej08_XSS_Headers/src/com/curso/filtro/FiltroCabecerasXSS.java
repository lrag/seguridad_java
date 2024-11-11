/*
 * Copyright (C) 2017 Dominik Schadow, dominikschadow@gmail.com
 *
 * This file is part of the Java Security project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.curso.filtro;

import java.io.IOException;
import java.security.SecureRandom;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter(urlPatterns = "/*" )
public class FiltroCabecerasXSS implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

    	//Chrome, hasta jul-2019
    	//No todos los navegadores llegaron a soportarlo
    	HttpServletResponse response = (HttpServletResponse) servletResponse;
    	//X-XSS-Protection: 0
		//X-XSS-Protection: 1
		//X-XSS-Protection: 1; mode=block
        //response.setHeader("X-XSS-Protection", "1; mode=block");
        
        System.out.println("Cabeceras XSS");

        //Indicando el src por defecto para:
        //-js
        //-fuentes
        //-imágenes
        //-frames
        //-css
        //-...
        //response.setHeader("Content-Security-Policy", "default-src 'self';");
        
        //Específicando el src para los js y las imágenes (serían distintos de 'self')
        //response.setHeader("Content-Security-Policy", "default-src 'self'; script-src 'self otro-sitio'; img-src 'otro-sitio-distinto';");
        
        //Espec�ficando m�s de un origen para javascript (o para cualquier otra cosa)
        //response.setHeader("Content-Security-Policy", "default-src 'self'; script-src 'self otro y_otro';");
        
        //Permitiendo js-inline (no puede prevenir el XSS)
        //response.setHeader("Content-Security-Policy", "default-src 'self'; script-src 'self' 'unsafe-inline';");
        
        //
        
        HttpServletRequest rq = (HttpServletRequest) servletRequest;
        String nonce = randomString(20);
        rq.getSession().setAttribute("nonce", nonce);

        response.setHeader("Content-Security-Policy", "default-src 'self'; script-src 'self' 'nonce-"+nonce+"';");
                
        filterChain.doFilter(servletRequest, response);
    }

    /**********************************/
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();
    
    private String randomString( int len ){
    	StringBuilder sb = new StringBuilder( len );
    	for( int i = 0; i < len; i++ ) 
    		sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
    	return sb.toString();
    }
    /**********************************/

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

}