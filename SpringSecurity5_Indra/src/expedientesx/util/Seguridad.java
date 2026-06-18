package expedientesx.util;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import expedientesx.modelo.entidad.Expediente;

@Component
public class Seguridad {	
	
	public boolean getPermiso(Integer dato, Expediente expediente){	
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		System.out.println("GET PERMISO: "+authentication.getAuthorities()+", "+dato+", "+expediente.getTitulo());
		Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) authentication.getAuthorities();
		return authorities.contains(new SimpleGrantedAuthority("ROLE_AGENTE_ESPECIAL")) 
			   || authorities.contains(new SimpleGrantedAuthority("ROLE_DIRECTOR"));
	}
		
}