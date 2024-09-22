package com.curso.modelo.negocio;

import com.curso.modelo.entidad.Usuario;

public class GestorUsuarios {

	public Usuario buscar(Integer id) {
		return null;
	}
	
	public Usuario buscarPorLogin(String login) {
		
		switch(login) {
			case "a" : return new Usuario(1,"Bud Spencer","a","a","USR");
			case "b" : return new Usuario(2,"Harry Callahan","b","b","ADMIN");
			default  : return null;
		}
		
	}
	
}
