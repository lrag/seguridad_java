package com.curso;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

// Autenticacion por sesion con el formulario de login por defecto de Spring
// Security. @EnableMethodSecurity activa las anotaciones @PreAuthorize de
// ServicioPedidos.
@Configuration
@EnableMethodSecurity
public class SeguridadConfig {

    @Bean
    SecurityFilterChain filtros(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(peticiones -> peticiones.anyRequest().authenticated())
                .formLogin(Customizer.withDefaults())
                .logout(Customizer.withDefaults());
        return http.build();
    }

    // Usuarios en memoria. {noop}: contrasena sin cifrar, solo para el ejemplo.
    @Bean
    UserDetailsService usuarios() {
        return new InMemoryUserDetailsManager(
                User.withUsername("a").password("{noop}a").roles("USUARIO").build(),
                User.withUsername("b").password("{noop}b").roles("EMPLEADO").build());
    }
}
