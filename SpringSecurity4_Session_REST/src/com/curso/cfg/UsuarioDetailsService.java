package com.curso.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.curso.modelo.entidad.Usuario;

public class UsuarioDetailsService implements UserDetailsService {

	private static Map<String,Usuario> usuarios;
	
	static {		
		GrantedAuthority agente = new SimpleGrantedAuthority("ROLE_AGENTE");
		GrantedAuthority agenteEspecial = new SimpleGrantedAuthority("ROLE_AGENTE_ESPECIAL");
		GrantedAuthority director = new SimpleGrantedAuthority("ROLE_DIRECTOR");
		List<GrantedAuthority> lista1 = new ArrayList<>();
		lista1.add(agente);
		List<GrantedAuthority> lista2 = new ArrayList<>();
		lista2.add(agenteEspecial);
		List<GrantedAuthority> lista3 = new ArrayList<>();
		lista3.add(director);
		
		usuarios = new HashMap<>();
		
		Usuario fernando = new Usuario("Fernando","$2a$10$SMPYtil7Hs2.cV7nrMjrM.dRAkuoLdYM8NdVrF.GeHfs/MrzcQ/zi",lista1,"Fernando R.R.", "fernando@fbi.gov","abcde");
		Usuario mulder   = new Usuario("Mulder","$2a$10$M2JRRHUHTfv4uMR4NWmCLebk1r9DyWSwCMZmuq4LKbImOkfhGFAIa",lista2,"Fox William Mulder", "mulder@fbi.gov","fghij");
		Usuario scully   = new Usuario("Scully","$2a$10$cbF5xp0grCOGcI6jZvPhA.asgmILATW1hNbM2MEqGJEFnRhhQd3ba",lista2,"Dana Katherine Scully", "scully@fbi.gov","klmnñ");
		Usuario skinner   = new Usuario("Skinner","$2a$10$ZFtPIULMcxPe3r/5VunbVujMD7Lw8hkqAmJlxmK5Y1TK3L1bf8ULG",lista3,"Walter Sergei Skinner", "skinner@fbi.gov","opqrs");
		
		usuarios.put("Fernando",fernando);
		usuarios.put("Mulder"  ,mulder);
		usuarios.put("Scully"  ,scully);
		usuarios.put("Skinner" ,skinner);        		
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usr = usuarios.get(username);
		if(usr == null) {
			throw new UsernameNotFoundException(username);		
		}
		return usr;
	}

}
