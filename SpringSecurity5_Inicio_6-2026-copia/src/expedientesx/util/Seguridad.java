package expedientesx.util;

import java.util.Collection;

import expedientesx.cfg.Configuracion;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class Seguridad {

    private final Configuracion configuracion;

    Seguridad(Configuracion configuracion) {
        this.configuracion = configuracion;
    }	
	
	public boolean getPermiso(){	
		
		System.out.println(">>>>>GET PERMISO>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		System.out.println(authentication.getAuthorities());
		Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) authentication.getAuthorities();
		return authorities.contains(new SimpleGrantedAuthority("ROLE_AGENTE_ESPECIAL")) 
			   || authorities.contains(new SimpleGrantedAuthority("ROLE_DIRECTOR"));
	}
	
}