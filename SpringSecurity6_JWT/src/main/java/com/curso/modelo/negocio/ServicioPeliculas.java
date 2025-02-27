package com.curso.modelo.negocio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.curso.modelo.entidad.Pelicula;
import com.curso.modelo.persistencia.PeliculaDao;

@Service
public class ServicioPeliculas {

	
	@Autowired private PeliculaDao peliculaDao;

	@Transactional
	public void insertar(Pelicula pelicula) throws Exception {	
    	System.out.println("=====================================");
		System.out.println("GestorPeliculasImpl.insertar:");
		System.out.println(SecurityContextHolder.getContext().getAuthentication());
		
		System.out.print("Insertando la pelicula "+pelicula.getTitulo());
		if(pelicula.getTitulo() == null || pelicula.getTitulo().trim().equals("") ) {
			System.out.println("...MAL");
			throw new Exception("El titulo esta mal");
		}
		System.out.println("...OK");
		peliculaDao.insertar(pelicula);	
		
	}
	
	@Transactional(propagation=Propagation.SUPPORTS)
	public List<Pelicula> listar(){
		return peliculaDao.listar();
	}
	
	public Pelicula buscar(Integer id) {
		return peliculaDao.buscar(id);
	}

	public void modificar(Pelicula pelicula) {
		peliculaDao.modificar(pelicula);		
	}

	public void borrar(Pelicula pelicula) {
		peliculaDao.borrar(pelicula);
	}
	
}












