package expedientesx.cfg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class ConfiguracionSpringSecurity extends WebSecurityConfigurerAdapter {
	
	@Autowired
	public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("Fernando").password("1234").roles("AGENTE");
		auth.inMemoryAuthentication().withUser("Mulder").password("fox").roles("AGENTE_ESPECIAL");
		auth.inMemoryAuthentication().withUser("Scully").password("dana").roles("AGENTE_ESPECIAL");
		auth.inMemoryAuthentication().withUser("Skinner").password("walter").roles("DIRECTOR");
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