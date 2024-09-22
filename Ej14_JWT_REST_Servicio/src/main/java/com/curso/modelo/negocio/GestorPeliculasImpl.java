package com.curso.modelo.negocio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.curso.modelo.entidad.Pelicula;
import com.curso.modelo.persistencia.PeliculaDao;

@Service
public class GestorPeliculasImpl implements GestorPeliculas{

	
	@Autowired private PeliculaDao peliculaDao;
	@Autowired private GestorPeliculas proxy; //La 'autoinyeccion' solo funciona a partir de la version 4.3

	@Override
	@Transactional
	public void insertar(Pelicula pelicula) throws Exception {	
		
		System.out.print("Insertando la pelicula "+pelicula.getTitulo());
		if(pelicula.getTitulo() == null || pelicula.getTitulo().trim().equals("") ) {
			System.out.println("...MAL");
			throw new Exception("El titulo esta mal");
		}
		System.out.println("...OK");
		peliculaDao.insertar(pelicula);	
		
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={ Exception.class } )
	public void insertar(List<Pelicula> peliculas) throws Exception {
		for(Pelicula p: peliculas) {
			proxy.insertar(p);
		}		
	}
	
	@Override
	@Transactional(propagation=Propagation.SUPPORTS)
	public List<Pelicula> listar(){
		return peliculaDao.listar();
	}
	
	@Override
	public Pelicula buscar(Integer id) {
		return peliculaDao.buscar(id);
	}

	@Override
	public void modificar(Pelicula pelicula) {
		peliculaDao.modificar(pelicula);		
	}

	@Override
	public void borrar(Pelicula pelicula) {
		peliculaDao.borrar(pelicula);
	}
	
}












