package com.curso.servicio.dto;

import javax.xml.bind.annotation.XmlRootElement;

import com.curso.modelo.entidad.Pelicula;

@XmlRootElement
public class PeliculaDTO {

	private Integer id;
	private String titulo;
	private String director;
	private String genero;
	private String fechaEstreno;

	public PeliculaDTO() {
		super();
	}

	public PeliculaDTO(Pelicula pelicula){
		super();
		this.id = pelicula.getId();
		this.titulo = pelicula.getTitulo();
		this.director = pelicula.getDirector();
		this.genero = pelicula.getGenero();
		this.fechaEstreno = pelicula.getFechaEstreno();
	}
	
	public PeliculaDTO(Integer id, String titulo, String director, String genero, String fechaEstreno) {
		super();
		this.id = id;
		this.titulo = titulo;
		this.director = director;
		this.genero = genero;
		this.fechaEstreno = fechaEstreno;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getGenero() {
		return genero;
	}

	public void setGenero(String genero) {
		this.genero = genero;
	}

	public String getFechaEstreno() {
		return fechaEstreno;
	}

	public void setFechaEstreno(String fechaEstreno) {
		this.fechaEstreno = fechaEstreno;
	}

	public Pelicula asPelicula(){
		return new Pelicula(id, titulo, director, genero, fechaEstreno);
	}
}
