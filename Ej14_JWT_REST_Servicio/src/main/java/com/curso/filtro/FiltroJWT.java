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

import com.curso.controlador.CriptografiaUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

@WebFilter("/servicios/seguro/*")
public class FiltroJWT implements Filter {

    public FiltroJWT() {
    }

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		System.out.println("Filtro JWT");
		
		HttpServletRequest rq = (HttpServletRequest) request;
		HttpServletResponse rp = (HttpServletResponse) response;
		
        String token = rq.getHeader("Authorization");
        
        if (token!=null && token.trim().length()>0) {
            try {
            	byte[] clave = CriptografiaUtil.clave.getBytes();

                Jws<Claims> parsedToken = Jwts.parserBuilder()
                    .setSigningKey(clave)
                    .build()
                    .parseClaimsJws(token.replace("Bearer ", ""));
                
                String username = parsedToken
                    .getBody()
                    .getSubject();
                
                System.out.print("Usuario:"+username+" ");
                System.out.println("Rol:"+parsedToken.getBody().get("rol"));

                //DO FILTER
                chain.doFilter(request, response);
                return;
                
            } catch (ExpiredJwtException exception) {
                System.out.println("Request to parse expired JWT : {} failed : {}"+ token+","+ exception.getMessage());
            } catch (UnsupportedJwtException exception) {
                System.out.println("Request to parse unsupported JWT : {} failed : {}"+ token+","+ exception.getMessage());
            } catch (MalformedJwtException exception) {
                System.out.println("Request to parse invalid JWT : {} failed : {}"+ token+","+ exception.getMessage());
            } catch (SignatureException exception) {
                System.out.println("Request to parse JWT with invalid signature : {} failed : {}"+ token+","+ exception.getMessage());
            } catch (IllegalArgumentException exception) {
                System.out.println("Request to parse empty or null JWT : {} failed : {}"+ token+","+ exception.getMessage());
            }
        }
		
        rp.sendError(403,"Forbidden");
		
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

}
