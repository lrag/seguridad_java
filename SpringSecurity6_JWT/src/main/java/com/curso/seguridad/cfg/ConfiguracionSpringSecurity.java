package com.curso.seguridad.cfg;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class ConfiguracionSpringSecurity {
	
    @Autowired
    private JwtAuthFilter jwtAuthFilter;	
    
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
	  String usersByUsernameQuery = "select username, password, enabled from users where username = ?";
	  String authsByUserQuery = "select username, authority from authorities where username = ?";
	  JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
	  userDetailsManager.setUsersByUsernameQuery(usersByUsernameQuery);
	  userDetailsManager.setAuthoritiesByUsernameQuery(authsByUserQuery);
	  
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
				.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/controlAutenticacion")).permitAll()
				//.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/peliculas**")).hasAnyRole("VISITANTE", "USUARIO", "ADMIN")
				//.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/peliculas")).hasAnyRole("USUARIO", "ADMIN")
				//.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.DELETE, "/peliculas/*")).hasAnyRole("ADMIN")
				.anyRequest().authenticated());
    	
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); 

        return http.build();
    }	
	
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }    
	
}
