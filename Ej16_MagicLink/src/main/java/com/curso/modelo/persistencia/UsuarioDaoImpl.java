package com.curso.modelo.persistencia;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.curso.modelo.entidad.Usuario;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

@Repository
public class UsuarioDaoImpl implements UsuarioDao {

	@PersistenceContext
	private EntityManager em;

	@Override
	public Usuario guardar(Usuario usuario) {
		em.persist(usuario);
		return usuario;
	}

	@Override
	public Usuario buscarPorId(Long id) {
		return em.find(Usuario.class, id);
	}

	@Override
	public Usuario buscarPorUsername(String username) {
		try {
			return em.createQuery("select u from Usuario u where u.username = :username", Usuario.class)
					.setParameter("username", username)
					.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	@Override
	public Usuario buscarPorCorreoE(String correoE) {
		try {
			return em.createQuery("select u from Usuario u where u.correoE = :correoE", Usuario.class)
					.setParameter("correoE", correoE)
					.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	@Override
	public List<Usuario> listarTodos() {
		return em.createQuery("select u from Usuario u", Usuario.class).getResultList();
	}

	@Override
	public long contar() {
		return em.createQuery("select count(u) from Usuario u", Long.class).getSingleResult();
	}

	@Override
	public Usuario actualizar(Usuario usuario) {
		return em.merge(usuario);
	}

	@Override
	public void borrar(Long id) {
		Usuario usuario = em.find(Usuario.class, id);
		if (usuario != null) {
			em.remove(usuario);
		}
	}

}
