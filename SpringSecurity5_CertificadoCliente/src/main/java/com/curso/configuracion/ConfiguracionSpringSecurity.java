package com.curso.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ConfiguracionSpringSecurity {

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
        http.authorizeRequests()
	        .anyRequest()
	        .authenticated()
	        .and()
	        .x509()
	        .subjectPrincipalRegex("CN=(.*?)(?:,|$)")
	        .userDetailsService(userDetailsService());        
        
		return http.build();
	}

	@Bean
	UserDetailsService userDetailsService() {
		return new UserDetailsService() {
			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				System.out.println("USERNAME: " + username);
				if (username.equals("Harry Callahan")) {
					return new User(username, "", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
				} else if (username.equals("Bud Spencer")) {
					return new User(username, "", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
				} else if (username.equals("Harpo")) {
					return new User(username, "", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_ADMIN"));
				}
				throw new UsernameNotFoundException("User not found!");
			}
		};
	}

}

class CustomUserDetailsService implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}
	
}




