package com.curso.seguridad.endpoint;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.curso.seguridad.modelo.LoginRequest;

@RestController
public class ControlAcceso {

    private AuthenticationManager authenticationManager;
    private JwtEncoder jwtEncoder;

    public ControlAcceso(AuthenticationManager authenticationManager, JwtEncoder jwtEncoder) {
		super();
		this.authenticationManager = authenticationManager;
		this.jwtEncoder = jwtEncoder;
	}

	@PostMapping("/controlAutenticacion")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

		if (!authentication.isAuthenticated()) {
			return new ResponseEntity<>("Credenciales incorrectas.", HttpStatus.UNAUTHORIZED);
		}

    	List<String> roles = authentication
    		.getAuthorities()
    		.stream()
    		.map( a -> a.getAuthority().replaceAll("ROLE_", ""))
    		.collect(Collectors.toList());

    	Instant ahora = Instant.now();

    	JwtClaimsSet claims = JwtClaimsSet.builder()
    			.subject(loginRequest.getUsername())
    			.issuedAt(ahora)
    			.expiresAt(ahora.plus(30, ChronoUnit.MINUTES))
    			.claim("rol", roles)
    			.build();

    	String tk = jwtEncoder.encode(
    			JwtEncoderParameters.from(JwsHeader.with(() -> "HS256").build(), claims)
    		).getTokenValue();
        System.out.println(tk);

        return new ResponseEntity<>(tk, HttpStatus.OK);
    }

}
