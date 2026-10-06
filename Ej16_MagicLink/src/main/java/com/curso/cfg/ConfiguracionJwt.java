package com.curso.cfg;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class ConfiguracionJwt {

	@Value("${app.jwt.clave-secreta}")
	private String claveSecreta;

	@Bean
	SecretKey jwtSecretKey() {
		byte[] keyBytes = Base64.getDecoder().decode(claveSecreta);
		return new SecretKeySpec(keyBytes, "HmacSHA256");
	}

	@Bean
	JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
		JWKSource<SecurityContext> jwkSource = new ImmutableSecret<>(jwtSecretKey);
		return new NimbusJwtEncoder(jwkSource);
	}

}
