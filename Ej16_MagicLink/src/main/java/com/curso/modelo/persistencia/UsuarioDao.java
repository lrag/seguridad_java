package com.curso.modelo.persistencia;

import java.util.List;

import com.curso.modelo.entidad.Usuario;

public interface UsuarioDao {

	Usuario guardar(Usuario usuario);

	Usuario buscarPorId(Long id);

	Usuario buscarPorUsername(String username);

	Usuario buscarPorCorreoE(String correoE);

	List<Usuario> listarTodos();

	long contar();

	Usuario actualizar(Usuario usuario);

	void borrar(Long id);

}
