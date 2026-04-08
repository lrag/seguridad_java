package com.curso.seguridad.cfg;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.curso.seguridad.util.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
//OncePerRequestFilter se ejecutará una única vez aunque haya forwards
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    /*
    GET /peliculas
    Authorization: Bearer fhryu374firrumbio4767f63.t8759esnafrillo823jfdhrh3.fijf9598y8348uw
	*/
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    	    	
        String authHeader = request.getHeader("Authorization");
        String token = null;

        //Si viene el token se examina
        //Si todo es correcto se añade el usuario al security context holder
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            
            Claims claims = jwtUtil.extractAllClaims(token);
            
            String username = claims.getSubject();
            List<String> rol = claims.get("rol", ArrayList.class);
            
            UserDetails userDetails = User
            	.withUsername(username)
            	.password("[PROTECTED]")
            	.roles(rol.toArray(new String[0]))
            	.build();
            
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );    
            
            //Tambien deberíamos dejar el UserPrincipal' en el request
            SecurityContextHolder.getContext().setAuthentication(authToken);            
        }        

        //Si no venía el token la petición continúa:
        //-puede ser una petición que todo el mundo puede hacer
        //-si la petición exíge el token ya la detendrán más adelante, cuando se ejecute la autorización
        filterChain.doFilter(request, response);
        
    }
}