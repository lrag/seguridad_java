package com.curso.cfg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled=true, prePostEnabled=true)
public class ConfiguracionSpringSecurity extends WebSecurityConfigurerAdapter {

	@Bean
	public PasswordEncoder passwordEncoder(){
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder;
	}
	
	@Autowired
	public void configureGlobalSecurity(AuthenticationManagerBuilder auth, PasswordEncoder pe) throws Exception {
		auth.userDetailsService(userDetailsService()).passwordEncoder(pe);
	}
	
	@Bean
	public UserDetailsService userDetailsService(){
		return new UsuarioDetailsService();
	}	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		//Necesitamos el CSRF Token
		//http
		//	.csrf().disable();
		
		http
			.csrf()
			.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
			
		http
			.authorizeRequests()
			.antMatchers("/css/*").permitAll()
			.antMatchers("/js/*").permitAll()
			.antMatchers("/*.html").permitAll()
			.antMatchers(HttpMethod.POST, "/servicios/login").permitAll()						
			.antMatchers(HttpMethod.GET, "/servicios/peliculas").authenticated()
			.antMatchers(HttpMethod.POST, "/servicios/peliculas").hasAnyRole("AGENTE_ESPECIAL", "DIRECTOR")
			.antMatchers(HttpMethod.PUT, "/servicios/peliculas/*").hasAnyRole("AGENTE_ESPECIAL", "DIRECTOR")
			.antMatchers(HttpMethod.DELETE, "/servicios/peliculas/*").hasRole("DIRECTOR")
						
			//.antMatchers("/**").authenticated().and() //Esta l�nea es equivalente a la siguiente
			.anyRequest().authenticated().and()
				.addFilter(new SessionAuthenticationFilter(authenticationManager()))
				.addFilter(new SessionAuthorizationFilter(authenticationManager()));	
	}
		
	
	
	
}











