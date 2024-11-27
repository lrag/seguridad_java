package expedientesx.cfg;

import javax.sql.DataSource;

import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled=true, prePostEnabled=true)
public class ConfiguracionSpringSecurity {

	//Esta bean NO es el oyente
	//Es un objeto cuya tarea es registrar un oyente en el contenedor de servlets
	@Bean
	ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
		return new ServletListenerRegistrationBean<HttpSessionEventPublisher>(new HttpSessionEventPublisher());
	}	
	
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
	
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {    	
        
    	http
	        .authorizeHttpRequests((authz) -> authz
	            .antMatchers("/paginas/*").permitAll()
	            .antMatchers("/css/*").permitAll()
	            .antMatchers("/imagenes/*").permitAll()
	            .antMatchers("/**").authenticated()	        		
	        );
        
	    http.formLogin(form -> form
	    		.loginPage("/paginas/nuestro-login.jsp")
	    		//.usernameParameter("login")
	    		//.passwordParameter("pw")
	    		.failureUrl("/paginas/nuestro-login.jsp?login_error")
	    	);
	    
	    http.logout(logout -> logout
	    		.logoutSuccessUrl("/paginas/desconectado.jsp")
	    		.deleteCookies("JSESSIONID")
	    	);

        http.sessionManagement(management -> management
                .invalidSessionUrl("/paginas/sesion-expirada.jsp")
                .maximumSessions(1)
                .maxSessionsPreventsLogin(true) //false por defecto
            );	 
	    
        //Activo por defecto
        http.sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
        		.sessionFixation().migrateSession() //por defecto
        					    //.none()
        		                //.newSession()
        	);        
        
	    http.rememberMe(rememberMe -> rememberMe
	    		.key("estoEsUnSecreto")
	    		.tokenValiditySeconds(86400)
	    		.rememberMeCookieName("my-remember-me")
	    		.rememberMeParameter("remember-me-param")); //remember-me
        
        http.requiresChannel(channel -> channel
            .anyRequest().requiresSecure()
        );
        
        http.headers(headers -> headers
            .httpStrictTransportSecurity(hsts -> hsts
                .includeSubDomains(true)
                .preload(true)
                .maxAgeInSeconds(31536000)
            )
        );
        
        http.exceptionHandling(handling -> handling
            .accessDeniedPage("/paginas/acceso-denegado.jsp"));	    
	    
	    //http.csrf().disable();
		
		
	    
        return http.build();
    }
    

}