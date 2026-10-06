package com.curso.modelo.entidad;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tokens_magic_link")
public class TokenMagicLink {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "usuario_id", nullable = false)
	private Usuario usuario;

	@Column(nullable = false, unique = true)
	private String token;

	@Column(nullable = false)
	private LocalDateTime fechaCaducidad;

	@Column(nullable = false)
	private boolean utilizado;

	public TokenMagicLink() {
		super();
	}

	public TokenMagicLink(Usuario usuario, String token, LocalDateTime fechaCaducidad) {
		super();
		this.usuario = usuario;
		this.token = token;
		this.fechaCaducidad = fechaCaducidad;
		this.utilizado = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public LocalDateTime getFechaCaducidad() {
		return fechaCaducidad;
	}

	public void setFechaCaducidad(LocalDateTime fechaCaducidad) {
		this.fechaCaducidad = fechaCaducidad;
	}

	public boolean isUtilizado() {
		return utilizado;
	}

	public void setUtilizado(boolean utilizado) {
		this.utilizado = utilizado;
	}

	@Override
	public String toString() {
		return "TokenMagicLink [id=" + id + ", usuario=" + (usuario != null ? usuario.getUsername() : null)
				+ ", token=" + token + ", fechaCaducidad=" + fechaCaducidad + ", utilizado=" + utilizado + "]";
	}

}
