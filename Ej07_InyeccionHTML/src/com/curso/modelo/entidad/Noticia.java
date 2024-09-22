package com.curso.modelo.entidad;

public class Noticia {

	private String titulo;
	private String texto;

	public Noticia() {
		super();
	}

	public Noticia(String titulo, String texto) {
		super();
		this.titulo = titulo;
		this.texto = texto;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

}
