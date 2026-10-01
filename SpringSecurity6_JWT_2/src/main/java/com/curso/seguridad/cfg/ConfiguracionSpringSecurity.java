package com.curso.seguridad.cfg;

import java.util.Base64;
import java.util.Collection;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class ConfiguracionSpringSecurity {

	@Value("${jwtKey}")
	private String jwtKey;

	@Bean
	PasswordEncoder passwordEncoder(){
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder;
	}

	@Bean
	DataSource dataSource() {
		return new EmbeddedDatabaseBuilder()
			.setType(EmbeddedDatabaseType.H2)
			.addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
			.build();
	}

	@Bean
	UserDetailsService jdbcUserDetailsService(DataSource dataSource) {
	  //String usersByUsernameQuery = "select username, password, enabled from users where username = ?";
	  //String authsByUserQuery = "select username, authority from authorities where username = ?";
	  JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
	  //userDetailsManager.setUsersByUsernameQuery(usersByUsernameQuery);
	  //userDetailsManager.setAuthoritiesByUsernameQuery(authsByUserQuery);

	  //Este código está aqui porque la base de datos desaparece al parar la aplicación y hay que volver a insertar los usuarios
	  UserDetails usuario1 = User.builder().username("Fernando").password(passwordEncoder().encode("1234")).roles("AGENTE").build();
	  UserDetails usuario2 = User.builder().username("Mulder").password(passwordEncoder().encode("fox")).roles("AGENTE_ESPECIAL").build();
	  UserDetails usuario3 = User.builder().username("Scully").password(passwordEncoder().encode("dana")).roles("AGENTE_ESPECIAL").build();
	  UserDetails usuario4 = User.builder().username("Skinner").password(passwordEncoder().encode("walter")).roles("DIRECTOR").build();
	  userDetailsManager.createUser(usuario1);
	  userDetailsManager.createUser(usuario2);
	  userDetailsManager.createUser(usuario3);
	  userDetailsManager.createUser(usuario4);

	  return userDetailsManager;
	}

	@Bean
	SecretKey jwtSecretKey() {
		byte[] keyBytes = Base64.getDecoder().decode(jwtKey);
		return new SecretKeySpec(keyBytes, "HmacSHA256");
	}

	@Bean
	JwtDecoder jwtDecoder(SecretKey jwtSecretKey) {
		return NimbusJwtDecoder.withSecretKey(jwtSecretKey).build();
	}

	@Bean
	JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
		JWKSource<SecurityContext> jwkSource = new ImmutableSecret<>(jwtSecretKey);
		return new NimbusJwtEncoder(jwkSource);
	}

	@Bean
	JwtAuthenticationConverter jwtAuthenticationConverter() {
		//El claim con los roles se sigue llamando "rol", igual que en el proyecto con jjwt
		JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
		authoritiesConverter.setAuthoritiesClaimName("rol");
		authoritiesConverter.setAuthorityPrefix("ROLE_");

		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
		return converter;
	}

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    	http
    		.sessionManagement(sess -> sess
    			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    		);

    	http
            .csrf(csrf -> csrf.disable());
    	
    	http
        .authorizeHttpRequests( auth -> auth
            .requestMatchers(HttpMethod.POST, "/controlAutenticacion").permitAll()
            .requestMatchers(HttpMethod.GET, "/cliente/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/peliculas**").hasAnyRole("AGENTE", "AGENTE_ESPECIAL", "DIRECTOR")
            .requestMatchers(HttpMethod.POST, "/peliculas").hasAnyRole("AGENTE_ESPECIAL", "DIRECTOR")
            .requestMatchers(HttpMethod.PUT, "/peliculas/*").hasAnyRole("AGENTE_ESPECIAL", "DIRECTOR")
            .requestMatchers(HttpMethod.DELETE, "/peliculas/*").hasAnyRole("DIRECTOR")
            .anyRequest().authenticated());    	

    	http
    		.oauth2ResourceServer(oauth2 -> oauth2
    			.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
    		);

        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}


/*
{
	"permisos" : rol1, rol2, rol3
}
*/
class CustomJwtConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

	@Override
	public Collection<GrantedAuthority> convert(Jwt source) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

