package com.curso.modelo.negocio;

import java.util.ArrayList;
import java.util.List;

import com.curso.modelo.entidad.Articulo;
import com.curso.modelo.entidad.Usuario;

//simulamos negocio pero la capa de acceso a datos esta en memoria
public class GestorArticulos {

	private static List<Articulo> articulos = new ArrayList<Articulo>();
	
	static{
		
		Usuario u = new Usuario(1,"Usr1","a","a");
		Articulo a1 = new Articulo(u,"Titulo 1","Este es el texto del artículo 1");
		Articulo a2 = new Articulo(u,"Titulo 2","Este es el texto del artículo 2");
		Articulo a3 = new Articulo(u,"Titulo 3","Este es el texto del artículo 3");
		Articulo a4 = new Articulo(u,"Titulo 4","Este es el texto del artículo 4");
		articulos.add(a1);
		articulos.add(a2);
		articulos.add(a3);
		//articulos.add(a4);
	}
	
	public List<Articulo> listarArticulos(){
		return articulos;
	}
	
	public void insertar(Articulo articulo){
		articulos.add(articulo);
	}
	
}
