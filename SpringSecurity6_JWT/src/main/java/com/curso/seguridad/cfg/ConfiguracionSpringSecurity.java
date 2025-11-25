package com.curso.seguridad.cfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class ConfiguracionSpringSecurity {
	
    @Autowired
    private JwtAuthFilter authFilter;	
	
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
				.requestMatchers(AntPathRequestMatcher.antMatcher("/css/*")).permitAll()
				.requestMatchers(AntPathRequestMatcher.antMatcher("/js/*")).permitAll()
				.requestMatchers(AntPathRequestMatcher.antMatcher("/*.html")).permitAll()
				.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/controlAutenticacion")).permitAll()
				//.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/peliculas**")).hasAnyRole("VISITANTE", "USUARIO", "ADMIN")
				//.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/peliculas")).hasAnyRole("USUARIO", "ADMIN")
				//.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.DELETE, "/peliculas/*")).hasAnyRole("ADMIN")
				.anyRequest().authenticated());
    	
        http.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class); 

        return http.build();
    }	
	
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }    
	
}
