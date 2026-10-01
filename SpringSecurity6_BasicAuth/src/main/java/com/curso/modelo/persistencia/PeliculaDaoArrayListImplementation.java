package com.curso.modelo.persistencia;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.curso.modelo.entidad.Pelicula;

@Repository
public class PeliculaDaoArrayListImplementation implements PeliculaDao {

	private static List<Pelicula> peliculas;
	private static Integer contadorId;
	
	static {
		peliculas = new ArrayList<>();
		peliculas.add(new Pelicula(1, "Titulo 1", "Director 1", "Genero 1", 1980));
		peliculas.add(new Pelicula(2, "Titulo 2", "Director 2", "Genero 2", 1981));
		peliculas.add(new Pelicula(3, "Titulo 3", "Director 3", "Genero 3", 1982));
		peliculas.add(new Pelicula(4, "Titulo 4", "Director 4", "Genero 4", 1983));
		peliculas.add(new Pelicula(5, "Titulo 5", "Director 5", "Genero 5", 1984));		
		contadorId = peliculas.size();
	}
	
	@Override
	public void insertar(Pelicula pelicula) {
		pelicula.setId(contadorId);
		contadorId++;
		peliculas.add(pelicula);		
	}

	@Override
	public void modificar(Pelicula pelicula) {
		for(int a=0; a<peliculas.size(); a++) {
			if(peliculas.get(a).getId() == pelicula.getId()) {
				peliculas.set(a, pelicula);
			}
		}		
	}

	@Override
	public void borrar(Pelicula pelicula) {
		for(int a=0; a<peliculas.size(); a++) {
			if(peliculas.get(a).getId() == pelicula.getId()) {
				peliculas.remove(a);
			}
		}			
	}

	@Override
	public List<Pelicula> listar() {
		return peliculas;
	}

	@Override
	public Pelicula buscar(Integer id) {
		for(int a=0; a<peliculas.size(); a++) {
			if(peliculas.get(a).getId() == id) {
				return peliculas.get(a);
			}
		}
		return null;
	}

	
	
	
}

















