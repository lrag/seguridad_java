package expedientesx.cfg;

import java.util.Collection;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = { "expedientesx.util" })
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled=true, prePostEnabled=true)
public class ConfiguracionSpringSecurity {
	
	@Bean
	DataSource dataSource() {
		return new EmbeddedDatabaseBuilder()
			.setType(EmbeddedDatabaseType.H2)
			.addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
			.build();
	}

	@Bean
	PasswordEncoder passwordEncoder(){
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder;
	}	

	@Bean
	UserDetailsService jdbcUserDetailsService(DataSource dataSource) {
		String usersByUsernameQuery = "select username, password, enabled from users where username = ?";
		String authsByUserQuery = "select username, authority from authorities where username = ?";
		JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
		userDetailsManager.setUsersByUsernameQuery(usersByUsernameQuery);
		userDetailsManager.setAuthoritiesByUsernameQuery(authsByUserQuery);
		  
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
	
	
	/*	
	GET
	POST
	PUT
	PATCH
	DELETE
	------------
	HEAD
	OPTIONS
	
	/recursos
	 	rec1
	 	rec2
	 	rec3
	 	/movidas
	 		movida1
	 		movida2
	 		movida3
	 
	GET /recursos/movidas/movida2
	GET /recursos/movidas
	
	/recursos/*
	/recursos/**
	*/
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {    	
		
		http
	    .authorizeHttpRequests( auth -> auth
	        .requestMatchers(AntPathRequestMatcher.antMatcher("/paginas/*")).permitAll()
	        .requestMatchers(AntPathRequestMatcher.antMatcher("/css/*")).permitAll()
	        .requestMatchers(AntPathRequestMatcher.antMatcher("/imagenes/*")).permitAll()
	        .requestMatchers(AntPathRequestMatcher.antMatcher("/**")).authenticated()   
			//.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/clasificar")).hasRole("DIRECTOR")   
			//.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/mostrar/*")).hasAnyRole("AGENTE_ESPECIAL", "DIRECTOR")   
			
	    );

		http.formLogin(form -> form
			.loginPage("/paginas/nuestro-login.jsp")
			//.usernameParameter("login")
			//.passwordParameter("pw")
			.failureUrl("/paginas/nuestro-login.jsp?login_error"));
		
		http.rememberMe(rememberMe -> rememberMe
				.key("estoEsUnSecreto")
				.tokenValiditySeconds(86400)
				.rememberMeCookieName("my-remember-me")
				.rememberMeParameter("remember-me-param")); //remember-me			
	

		http.requiresChannel(channel -> channel
				.anyRequest()
				.requiresSecure()
			);

		//Activo por defecto
		http.headers(headers -> headers
			.httpStrictTransportSecurity(hsts -> hsts
				.includeSubDomains(true)
				.preload(true)
				.maxAgeInSeconds(31536000)
			)
			.contentSecurityPolicy(csp -> csp
				    .policyDirectives("default-src 'self'")
			)	
		);	

		http.logout(logout -> logout
				.logoutSuccessUrl("/paginas/desconectado.jsp")
				.logoutUrl("/logout")
				.deleteCookies("JSESSIONID")
			);

		http.sessionManagement(management -> management
		        .invalidSessionUrl("/paginas/sesion-expirada.jsp")
		        .maximumSessions(1)
		        .maxSessionsPreventsLogin(false) //false por defecto
		    );			
	
		//http.csrf().disable();
		
		http.exceptionHandling(handling -> handling
		    	.accessDeniedPage("/paginas/acceso-denegado.jsp")
			);			
		
	    return http.build();
	}
    
}

class CustomUserDetailsService implements UserDetailsService {
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//CONSULTA A UNA BB:DD: MongoDB
		CustomUserDetails usuario = new CustomUserDetails();
		return usuario;
	}
}

class CustomUserDetails implements UserDetails {

	//Campos "oficiales"
	private Collection<GrantedAuthority> authorities;
	private String password;
	private String userName;
	private boolean enabled;
	//resto de campos "oficiales"
	
	//Lo nuestro
	private String preferencias;
	
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return null;
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}
	
}

class CustomPasswordEncoder implements PasswordEncoder {

	@Override
	public String encode(CharSequence rawPassword) {
		return null;
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return false;
	}
	
}



