package com.curso.modelo.negocio;

import java.util.ArrayList;
import java.util.List;

import com.curso.modelo.entidad.Pelicula;

public class GestorPeliculas {

	private static List<Pelicula> peliculas;

	static{

		peliculas = new ArrayList<>();
		peliculas.add(new Pelicula(1,"2001","Stanley Kubrik","Ci-Fi","1968"));
		peliculas.add(new Pelicula(2,"Alien","Ridley Scott","Ci-Fi","1979"));
		peliculas.add(new Pelicula(3,"Die Hard","John McTiernan","Accion","1988"));
		peliculas.add(new Pelicula(4,"Young Frankenstein","Mel Brooks","Comedia","1974"));
		peliculas.add(new Pelicula(5,"Moon","Duncan Jones","Ci-Fi","2009"));
		peliculas.add(new Pelicula(6,"El bueno, el feo y el malo","Sergio Leone","Western","1968"));
	}

	public void insertarPelicula(Pelicula pelicula){
		System.out.println("Insertando la pelicula:"+pelicula.getTitulo());
		pelicula.setId(peliculas.size()+1);
		peliculas.add(pelicula);
	}

	public List<Pelicula> listarPeliculas(){
		return peliculas;
	}

	public Pelicula buscarPelicula(Integer id){
		return peliculas.get(id-1);
	}

}
