package com.curso.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import com.curso.modelo.entidad.Usuario;

@Component
public class JWTUtil {

	private static final int MINUTOS_CADUCIDAD_JWT = 30;

	private final JwtEncoder jwtEncoder;

	public JWTUtil(JwtEncoder jwtEncoder) {
		this.jwtEncoder = jwtEncoder;
	}

	public String generarJwt(Usuario usuario) {
		Instant ahora = Instant.now();

		JwtClaimsSet claims = JwtClaimsSet.builder()
				.subject(usuario.getUsername())
				.issuedAt(ahora)
				.expiresAt(ahora.plus(MINUTOS_CADUCIDAD_JWT, ChronoUnit.MINUTES))
				.claim("nombre", usuario.getNombre())
				.claim("correoE", usuario.getCorreoE())
				.claim("rol", usuario.getRol())
				.build();

		return jwtEncoder.encode(
				JwtEncoderParameters.from(JwsHeader.with(() -> "HS256").build(), claims)
			).getTokenValue();
	}

}
