package com.curso.cfg;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class SessionAuthorizationFilter extends BasicAuthenticationFilter {

    public SessionAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
    								HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {        
    	System.out.println("=====================================");
    	System.out.println("SessionAuthorizationFilter");
    	Authentication authentication = ((Authentication) request.getSession().getAttribute("authentication"));
        System.out.println(authentication);
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        filterChain.doFilter(request, response);
    }

}

