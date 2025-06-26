package com.curso.modelo.persistencia;

import java.util.List;

import com.curso.modelo.entidad.Cliente;

public interface ClienteDao {

	void insertar(Cliente cliente);
	void modificar(Cliente cliente);
	void borrar(Cliente cliente);
	Cliente buscar(int id);
	List<Cliente> listar(int primero, int cantidad);

}