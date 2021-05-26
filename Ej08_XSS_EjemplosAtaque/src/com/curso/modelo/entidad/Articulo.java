package com.curso.modelo.entidad;

public class Articulo {

	private Usuario usuario;
	private String titulo;
	private String texto;

	public Articulo() {
		super();
	}

	public Articulo(Usuario usuario, String titulo, String texto) {
		super();
		this.usuario = usuario;
		this.titulo = titulo;
		this.texto = texto;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
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
