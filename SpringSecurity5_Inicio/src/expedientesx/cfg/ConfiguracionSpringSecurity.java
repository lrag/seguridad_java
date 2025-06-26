package expedientesx.cfg;

import java.util.Collection;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
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
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled=true, prePostEnabled=true)
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
        .authorizeHttpRequests( auth -> auth
            .requestMatchers(AntPathRequestMatcher.antMatcher("/paginas/*")).permitAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/css/*")).permitAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/imagenes/*")).permitAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/**")).authenticated()      
            //.requestMatchers(HttpMethod.POST, "/clasificar").hasAnyRole("DIRECTOR")
            //.requestMatchers(HttpMethod.POST, "/desclasificar").hasAnyRole("AGENTE_ESPECIAL", "DIRECTOR")
        );

	    http.formLogin(form -> form
	    	.loginPage("/paginas/nuestro-login.jsp")
	    	//.usernameParameter("login")
	    	//.passwordParameter("pw")
	    	.failureUrl("/paginas/nuestro-login.jsp?login_error"));
	
	    http.logout(logout -> logout
	    		.logoutSuccessUrl("/paginas/desconectado.jsp"));
	    
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
    	);    
	    
    	http.exceptionHandling(handling -> handling
    	    	.accessDeniedPage("/paginas/acceso-denegado.jsp")
    		);		
    	
    	
	
	    http.csrf().disable();
	
	    return http.build();	
    }
          
    
    
}



class UsuarioCustom implements UserDetails {
	
	private String username;
	private String password;
	private Collection<GrantedAuthority> authorities;
	
	private String preferencias;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getPreferencias() {
		return preferencias;
	}

	public void setPreferencias(String preferencias) {
		this.preferencias = preferencias;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAuthorities(Collection<GrantedAuthority> authorities) {
		this.authorities = authorities;
	}
	
	
	
}

class CustomUserDetailsService implements UserDetailsService {
	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
		
		//Buscar el usuario en mongoDB
		
		UsuarioCustom uc = new UsuarioCustom();
		
		return uc;
		
	}
	
}


class CustomPasswordEncoder implements PasswordEncoder {

	@Override
	public String encode(CharSequence rawPassword) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		// TODO Auto-generated method stub
		return false;
	}
	
}



