package expedientesx.util;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class Seguridad {
	
	public boolean getPermiso(){	
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(authentication.getAuthorities());
		Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) authentication.getAuthorities();
		return authorities.contains(new SimpleGrantedAuthority("ROLE_AGENTE_ESPECIAL")) 
			   || authorities.contains(new SimpleGrantedAuthority("ROLE_DIRECTOR"));
	}	
	
}
