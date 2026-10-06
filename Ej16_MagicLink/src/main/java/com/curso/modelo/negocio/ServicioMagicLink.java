package com.curso.modelo.negocio;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.curso.modelo.entidad.TokenMagicLink;
import com.curso.modelo.entidad.Usuario;
import com.curso.modelo.persistencia.TokenMagicLinkDao;
import com.curso.modelo.persistencia.UsuarioDao;
import com.curso.util.EmisorCorreoSimulado;
import com.curso.util.JWTUtil;

@Service
public class ServicioMagicLink {

	private static final Logger log = LoggerFactory.getLogger(ServicioMagicLink.class);

	private static final int MINUTOS_CADUCIDAD = 10;
	private static final int SEGUNDOS_CADUCIDAD_CODIGO_TEMPORAL = 60;
	private static final SecureRandom GENERADOR_ALEATORIO = new SecureRandom();

	private final UsuarioDao usuarioDao;
	private final TokenMagicLinkDao tokenMagicLinkDao;
	private final JWTUtil jwtUtil;
	private final EmisorCorreoSimulado emisorCorreo;

	@Value("${app.base-url}")
	private String baseUrl;

	ServicioMagicLink(
			TokenMagicLinkDao tokenMagicLinkDao,
			UsuarioDao usuarioDao,
			JWTUtil jwtUtil,
			EmisorCorreoSimulado emisorCorreo
		) {
		this.tokenMagicLinkDao = tokenMagicLinkDao;
		this.usuarioDao = usuarioDao;
		this.jwtUtil = jwtUtil;
		this.emisorCorreo = emisorCorreo;
	}

	@Transactional
	public void solicitarEnlace(String correo) {
		Usuario usuario = usuarioDao.buscarPorCorreoE(correo);
		if (usuario == null) {
			log.info("Solicitado enlace para '{}', pero no hay ningun Usuario con ese correo. No se escribe nada.", correo);
			return;
		}

		String token = generarToken();
		TokenMagicLink tokenMagicLink = new TokenMagicLink(usuario, token,
				LocalDateTime.now().plusMinutes(MINUTOS_CADUCIDAD));
		tokenMagicLinkDao.guardar(tokenMagicLink);

		String enlace = baseUrl + "/magic-link/verificar?token=" + token;
		String cuerpo = "Pulsa este enlace para entrar (caduca en %d minutos):\n%s"
				.formatted(MINUTOS_CADUCIDAD, enlace);
		emisorCorreo.enviar(usuario.getCorreoE(), "Tu enlace de acceso", cuerpo);
	}

	@Transactional
	public String verificarEnlace(String token) {
		TokenMagicLink tokenMagicLink = tokenValido(token);
		if (tokenMagicLink == null) {
			return null;
		}

		//Segundo token, de vida mucho mas corta: es el que viaja en la URL hacia la
		//aplicacion cliente, nunca el original (ese ya ha quedado consumido)
		String codigoTemporal = generarToken();
		TokenMagicLink tokenTemporal = new TokenMagicLink(tokenMagicLink.getUsuario(), codigoTemporal,
				LocalDateTime.now().plusSeconds(SEGUNDOS_CADUCIDAD_CODIGO_TEMPORAL));
		tokenMagicLinkDao.guardar(tokenTemporal);

		return codigoTemporal;
	}

	@Transactional
	public String obtenerJwt(String codigo) {
		TokenMagicLink tokenMagicLink = tokenValido(codigo);
		if (tokenMagicLink == null) {
			return null;
		}

		return jwtUtil.generarJwt(tokenMagicLink.getUsuario());
	}

	//Comprueba que el token existe, no ha caducado, y lo marca como utilizado (atomico:
	//si dos peticiones llegan a la vez con el mismo token, solo una de las dos lo consigue)
	private TokenMagicLink tokenValido(String token) {
		TokenMagicLink tokenMagicLink = tokenMagicLinkDao.buscarPorToken(token);
		if (tokenMagicLink == null) {
			return null;
		}
		if (tokenMagicLink.getFechaCaducidad().isBefore(LocalDateTime.now())) {
			return null;
		}
		if (!tokenMagicLinkDao.marcarComoUtilizado(token)) {
			//ya estaba utilizado (otra peticion se nos ha adelantado)
			return null;
		}
		return tokenMagicLink;
	}

	//Token opaco: bytes aleatorios sin ningun significado propio, codificados para que quepan en una URL
	private String generarToken() {
		byte[] bytes = new byte[32];
		GENERADOR_ALEATORIO.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

}
