package com.curso.modelo.negocio;

import java.util.List;

import com.curso.modelo.entidad.Pelicula;

public interface GestorPeliculas {

	void insertar(Pelicula pelicula) throws Exception;
	void insertar(List<Pelicula> peliculas) throws Exception;	
	List<Pelicula> listar();
	Pelicula buscar(Integer idPelicula);
	void modificar(Pelicula pelicula);
	void borrar(Pelicula pelicula);

}