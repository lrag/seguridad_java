package com.curso.modelo.servicio.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

//DTO es un objeto de transferencia de datos, de ahi sus siglas
//su objetivo es transportar datos entre procesos
//a diferencia de un objeto de negocio o de un DAO, el DTO no tiene ningun
//tipo de lógica de negocio
//Por ejemplo es posible que tengamos un cliente con un atributo que sea
//datos bancarios, puede ser que cuando mandemos el cliente a otro proceso
//es posible que no se quiera mandar toda la información, es ahi donde entraría
//el DTO, sería como un subconjunto del objeto con el que trabajamos y que su 
//cometido principal es mandar información entre procesos
//Este ejemplo lleva JAXB para serializar y deserializar los objetos a xml
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UsuarioDTO", propOrder = {
    "id",
    "login",
    "pw",
    "rol"
})
public class UsuarioDTO {
	
	//@XmlElement(name="idUsuario")
	//@XmlAttribute 
	//@XmlTransient
	private int id;
	private String login;
	private String pw;
	private String rol;

	public UsuarioDTO() {
		super();
	}

	public UsuarioDTO(int id, String login, String pw, String rol) {
		super();
		this.id = id;
		this.login = login;
		this.pw = pw;
		this.rol = rol;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPw() {
		return pw;
	}

	public void setPw(String pw) {
		this.pw = pw;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

	@Override
	public String toString() {
		return "Usuario [id=" + id + ", login=" + login + ", pw=" + pw
				+ ", rol=" + rol + "]";
	}

}


