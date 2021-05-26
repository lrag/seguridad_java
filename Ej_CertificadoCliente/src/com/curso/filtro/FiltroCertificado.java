package com.curso.filtro;

import java.io.IOException;

import java.security.cert.X509Certificate;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

@WebFilter("/seguro/*")
public class FiltroCertificado implements Filter {

    public FiltroCertificado() {
    }

    public void init(FilterConfig fConfig) throws ServletException {
    }

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		System.out.println("FILTRO CERTIFICADO");
		HttpServletRequest rq = (HttpServletRequest) request;
		
		X509Certificate[] certificados = (X509Certificate[]) rq.getAttribute("javax.servlet.request.X509Certificate");
		if(certificados.length > 0) {
			X509Certificate certificado = certificados[0];
			System.out.println(certificado.getIssuerDN());	
		}
	    		
		chain.doFilter(request, response);
	}

	public void destroy() {
	}

}
