package com.curso.filtro;
import java.io.IOException;
import java.util.Collection;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

@WebFilter("/*") 
public class SameSiteFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        chain.doFilter(request, response);
        
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        /*
        Atributo same site
        
        strict: No se envía en peticiones POST y GET iniciadas por terceros. Tampoco en iframes.
        lax   : No se envía en peticiones POST iniciadas por terceros. Tampoco en iframes.
        none  : Se envía donde haga falta...
        */
        
        Collection<String> headers = httpResponse.getHeaders("Set-Cookie");
        boolean first = true;
        for (String header : headers) {
            if (header != null && !header.toLowerCase().contains("samesite")) {
                header = header + "; SameSite=Lax";
            }
            
            if (first) {
                httpResponse.setHeader("Set-Cookie", header); // Primera cookie
                first = false;
            } else {
                httpResponse.addHeader("Set-Cookie", header); // Siguientes cookies si las hay
            }
        }
    }

    @Override
    public void destroy() {
        // Método de destrucción del filtro (opcional)
    }
}