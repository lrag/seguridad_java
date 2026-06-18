package com.curso.modelo.entidad;

public class Pelicula {

	private Integer id;
	private String titulo;
	private String director;
	private String genero;
	private Integer year;

	public Pelicula() {
		super();
	}
	
	public Pelicula(Integer id) {
		super();
		this.id = id;
	}

	public Pelicula(Integer id, String titulo, String director, String genero, Integer year) {
		super();
		this.id = id;
		this.titulo = titulo;
		this.director = director;
		this.genero = genero;
		this.year = year;
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

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

}
