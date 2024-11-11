package expedientesx.cfg;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

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
	
	public UserDetailsService userDetailsService(){
        Properties usuarios = new Properties();
		usuarios.put("Fernando","$2a$10$SMPYtil7Hs2.cV7nrMjrM.dRAkuoLdYM8NdVrF.GeHfs/MrzcQ/zi,ROLE_AGENTE,enabled");
		usuarios.put("Mulder"  ,"$2a$10$M2JRRHUHTfv4uMR4NWmCLebk1r9DyWSwCMZmuq4LKbImOkfhGFAIa,ROLE_AGENTE_ESPECIAL,enabled");
		usuarios.put("Scully"  ,"$2a$10$cbF5xp0grCOGcI6jZvPhA.asgmILATW1hNbM2MEqGJEFnRhhQd3ba,ROLE_AGENTE_ESPECIAL,enabled");
		usuarios.put("Skinner" ,"$2a$10$ZFtPIULMcxPe3r/5VunbVujMD7Lw8hkqAmJlxmK5Y1TK3L1bf8ULG,ROLE_DIRECTOR,enabled");
        
		return new InMemoryUserDetailsManager(usuarios);
	}


	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http
		.formLogin()
			.loginPage("/paginas/nuestro-login.jsp")
			.failureUrl("/paginas/nuestro-login.jsp?login_error");

		http
		.logout()
			.logoutSuccessUrl("/paginas/desconectado.jsp")
			.deleteCookies("JSESSIONID");

		http
			.sessionManagement()
			.invalidSessionUrl("/paginas/sesion-expirada.jsp")
			.maximumSessions(1)
			.maxSessionsPreventsLogin(true);
		
		http
		.authorizeRequests()
			.antMatchers("/paginas/*").permitAll()
			.antMatchers("/css/*").permitAll()
			.antMatchers("/imagenes/*").permitAll()
			.antMatchers("/**").access("isAuthenticated()");

		http
		.rememberMe() 
			.rememberMeParameter("remember-me-param")
			.rememberMeCookieName("my-remember-me")
			.tokenValiditySeconds(86400);
		
		http
			.requiresChannel()
			.anyRequest()
			.requiresSecure()
		.and()
			.portMapper().http(8080).mapsTo(8443);		
		
		http
			.csrf().disable();
		
	}	
	
}





