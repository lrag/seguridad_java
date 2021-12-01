package com.curso.modelo.entidad;

public class Empleado {

	private Integer id;
	private String nombre;
	private String apellidos;
	private Integer sueldo;

	public Empleado() {
		super();
	}

	public Empleado(Integer id, String nombre, String apellidos, Integer sueldo) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.sueldo = sueldo;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public Integer getSueldo() {
		return sueldo;
	}

	public void setSueldo(Integer sueldo) {
		this.sueldo = sueldo;
	}

	@Override
	public String toString() {
		return "Empleado [id=" + id + ", nombre=" + nombre + ", apellidos=" + apellidos + ", sueldo=" + sueldo + "]";
	}
	
}
