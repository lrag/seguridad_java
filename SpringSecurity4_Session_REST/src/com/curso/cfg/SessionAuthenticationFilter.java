package com.curso.cfg;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.DefaultCsrfToken;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class SessionAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public SessionAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        setFilterProcessesUrl("/servicios/login");
    }

    /*
    POST /servicios/login
    CT: application/x-www-form-urlencoded
    -------------------------------------
    username=aaa&password=bbb
    */
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        System.out.println("=====================================");
        System.out.println(username+":"+password);
        
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, 
    		                                HttpServletResponse response,
                                            FilterChain filterChain, 
                                            Authentication authentication) {
    	UserDetails user = ((User) authentication.getPrincipal());       
    	System.out.println("=====================================");
        System.out.println("Usuario:"+user); 
        
        if(request.getSession(false)!=null) {
        	request.getSession().invalidate();
        }   
        
        request.getSession().setAttribute("authentication", authentication);  
    }
}