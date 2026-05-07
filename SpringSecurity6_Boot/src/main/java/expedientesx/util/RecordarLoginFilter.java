package expedientesx.util;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RecordarLoginFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String login = request.getParameter("j_username");
		if(login!=null&&!login.isEmpty()){
			System.out.println("RecordarLoginFilter  login:"+login);
			response.addCookie(new Cookie("SPRING_SECURITY_LAST_USERNAME", login));
		}
		chain.doFilter(request, response);
	}

}
