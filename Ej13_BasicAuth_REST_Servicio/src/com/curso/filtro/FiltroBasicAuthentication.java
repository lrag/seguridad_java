package com.curso.filtro;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.curso.modelo.entidad.Usuario;
import com.curso.modelo.negocio.GestorUsuarios;

@WebFilter("/servicios/*")
public class FiltroBasicAuthentication implements Filter {

	private GestorUsuarios gestorUsuarios = new GestorUsuarios();
	
    public FiltroBasicAuthentication() {
    }

	public void init(FilterConfig fConfig) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest rq = (HttpServletRequest) request;
		HttpServletResponse rp = (HttpServletResponse) response;
		
		String auth = rq.getHeader("Authorization");
		System.out.println("Auth:"+auth);
		if(auth == null) {
			rp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		String base64 = auth.split(" ")[1];
		String txt = null;
		try {
			byte[] bytes = Base64.getDecoder().decode(base64);
			txt = new String(bytes, "utf-8");
		} catch (UnsupportedEncodingException e) {
			rp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		} 
		
		System.out.println("Credenciales: "+txt);
		String[] credenciales = txt.split(":");
		if(credenciales.length != 2) {
			rp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		String login = credenciales[0];
		String pw = credenciales[1];
		System.out.println("Login: "+login);
		System.out.println("Pw   : "+pw);
		
		Usuario usr = gestorUsuarios.buscarPorLogin(login);
		if(usr == null || !usr.getPw().equals(pw) ) {
			rp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		System.out.println("Usuario: "+usr.getNombre());
		chain.doFilter(request, response);
	}

	public void destroy() {
	}

}
