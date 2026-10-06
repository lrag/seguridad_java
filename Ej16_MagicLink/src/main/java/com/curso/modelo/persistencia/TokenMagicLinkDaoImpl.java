package com.curso.modelo.persistencia;

import java.time.LocalDateTime;

import org.springframework.stereotype.Repository;

import com.curso.modelo.entidad.TokenMagicLink;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

@Repository
public class TokenMagicLinkDaoImpl implements TokenMagicLinkDao {

	@PersistenceContext
	private EntityManager em;

	@Override
	public TokenMagicLink guardar(TokenMagicLink token) {
		em.persist(token);
		return token;
	}

	@Override
	public TokenMagicLink buscarPorToken(String token) {
		try {
			return em.createQuery(
						"select t from TokenMagicLink t join fetch t.usuario where t.token = :token",
						TokenMagicLink.class)
					.setParameter("token", token)
					.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	@Override
	public boolean marcarComoUtilizado(String token) {
		int filasActualizadas = em.createQuery(
					"update TokenMagicLink t set t.utilizado = true where t.token = :token and t.utilizado = false")
				.setParameter("token", token)
				.executeUpdate();
		return filasActualizadas == 1;
	}

	@Override
	public void borrarCaducados(LocalDateTime ahora) {
		em.createQuery("delete from TokenMagicLink t where t.fechaCaducidad < :ahora")
				.setParameter("ahora", ahora)
				.executeUpdate();
	}

}
