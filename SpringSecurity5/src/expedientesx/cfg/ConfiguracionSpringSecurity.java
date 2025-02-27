package expedientesx.cfg;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
	public PasswordEncoder passwordEncoder(){
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder;
	}	

	@Bean
	public UserDetailsService jdbcUserDetailsService(DataSource dataSource) {
		//String usersByUsernameQuery = "select username, password, enabled from users where username = ?";
		//String authsByUserQuery = "select username, authority from authorities where username = ?";
		JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
		//userDetailsManager.setUsersByUsernameQuery(usersByUsernameQuery);
		//userDetailsManager.setAuthoritiesByUsernameQuery(authsByUserQuery);
		  
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
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {    	
		
		http
	    .authorizeHttpRequests((authz) -> authz
	        .requestMatchers(AntPathRequestMatcher.antMatcher("/paginas/*")).permitAll()
	        .requestMatchers(AntPathRequestMatcher.antMatcher("/css/*")).permitAll()
	        .requestMatchers(AntPathRequestMatcher.antMatcher("/imagenes/*")).permitAll()
	        .requestMatchers(AntPathRequestMatcher.antMatcher("/**")).authenticated()   
			.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/clasificar")).permitAll()
	    );

		http.formLogin(form -> form
			.loginPage("/paginas/nuestro-login.jsp")
			//.usernameParameter("login")
			//.passwordParameter("pw")
			.failureUrl("/paginas/nuestro-login.jsp?login_error"));

        http.exceptionHandling(handling -> handling
            	.accessDeniedPage("/paginas/acceso-denegado.jsp")
    		);		
		
		
		
		http.csrf().disable();
	
		return http.build();
	}
}
