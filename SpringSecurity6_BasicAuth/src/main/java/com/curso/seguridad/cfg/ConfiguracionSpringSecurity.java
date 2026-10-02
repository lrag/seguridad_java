package com.curso.seguridad.cfg;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class ConfiguracionSpringSecurity {

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
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    	http
    		.sessionManagement(sess -> sess
    			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    		);

    	http
            .csrf(csrf -> csrf.disable());

    	http
	        .authorizeHttpRequests( auth -> auth
	            .requestMatchers(HttpMethod.GET,  "/cliente/**").permitAll()
	            .requestMatchers(HttpMethod.GET,  "/peliculas").hasAnyRole("AGENTE", "AGENTE_ESPECIAL", "DIRECTOR")
	            .requestMatchers(HttpMethod.GET,  "/peliculas/*").hasAnyRole("AGENTE", "AGENTE_ESPECIAL", "DIRECTOR")
	            .requestMatchers(HttpMethod.POST, "/peliculas").hasAnyRole("AGENTE_ESPECIAL", "DIRECTOR")
	            .requestMatchers(HttpMethod.PUT,  "/peliculas/*").hasAnyRole("AGENTE_ESPECIAL", "DIRECTOR")
	            .requestMatchers(HttpMethod.DELETE, "/peliculas/*").hasAnyRole("DIRECTOR")
	            .requestMatchers("/**").hasAnyRole("DIRECTOR") //Para que pueda verse prueba.html
	            .anyRequest().authenticated());

    	//Esta es la configuración para cuando no tenemos pantalla de login
    	//La pone el navegador
        //http.httpBasic(Customizer.withDefaults());
        
        http.httpBasic(basic -> basic
        	    .authenticationEntryPoint((request, response, authException) ->
        	        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
        	);

        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
