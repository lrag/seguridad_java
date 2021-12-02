package expedientesx.cfg;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class ConfiguracionSpringSecurity extends WebSecurityConfigurerAdapter {

	@Autowired
	public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService());
	}	
	
	public UserDetailsService userDetailsService(){
        Properties usuarios = new Properties();
		usuarios.put("Fernando","1234,ROLE_AGENTE,enabled");
		usuarios.put("Mulder"  ,"fox,ROLE_AGENTE_ESPECIAL,enabled");
		usuarios.put("Scully"  ,"dana,ROLE_AGENTE_ESPECIAL,enabled");
		usuarios.put("Skinner" ,"walter,ROLE_DIRECTOR,enabled");
        
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
			.logoutSuccessUrl("/paginas/desconectado.jsp");

		http
		.authorizeRequests()
			.antMatchers("/paginas/*").permitAll()
			.antMatchers("/css/*").permitAll()
			.antMatchers("/imagenes/*").permitAll()
			.antMatchers("/**").access("hasRole('AGENTE_ESPECIAL')");

		http
			.csrf().disable();
	}	
	

}


