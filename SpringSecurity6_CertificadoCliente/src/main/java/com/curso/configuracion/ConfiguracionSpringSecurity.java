package com.curso.configuracion;

import javax.security.auth.x500.X500Principal;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.x509.SubjectX500PrincipalExtractor;
import org.springframework.security.web.authentication.preauth.x509.X509PrincipalExtractor;

@Configuration
public class ConfiguracionSpringSecurity {

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		//Se buscará en el certificado el CN (Common Name) para utilizarlo como USERNAME
        SubjectX500PrincipalExtractor principalExtractor = new SubjectX500PrincipalExtractor();
        //Si el correo electrónico fuera el username:
        //principalExtractor.setExtractPrincipalNameFromEmail(true);
        
        /*
        CN/Common Name			El nombre completo del usuario o del equipo					
        EMAILADDRESS			La dirección de correo electrónico institucional o personal
        UID/User ID				Un identificador único (a veces el DNI, RFC, o código de empleado.
        OU/Organizational Unit	El departamento, área o división de la empresa.
        O,Organization			El nombre oficial de la empresa o corporación.
        L,Locality				La ciudad o municipio.
        ST,State/Province		El estado, provincia o región."
        C,Country				El código de dos letras del país
        */     		
        X509PrincipalExtractor customExtractor = clientCert -> {
        	//Los datos de identidad certificado
            X500Principal principal = clientCert.getSubjectX500Principal();
            //dn ya es un string con el formato marcado por RFC2253
            //EMAILADDRESS=juanGomez@georgeforeman.com,CN=Juan Gómez,OU=Departamento,O=Empresa,C=ES
            String dn = principal.getName(X500Principal.RFC2253);
            //Buscamos lo que sea
            if (dn.contains("EMAILADDRESS=")) {
                return dn.split("EMAILADDRESS=")[1].split(",")[0];
            }
            //Lo que devolvamos será el userName
            return dn; // O retornar un fallback
        };        
        
        http.authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            );
        http.x509(x509 -> x509
                .x509PrincipalExtractor(principalExtractor)
                .userDetailsService(userDetailsService())
            );
        
		return http.build();
		
		/*
		http.authorizeHttpRequests(auth -> auth
	            .anyRequest().authenticated()
	        );
	    
	    http.x509(x509 -> x509
	            // Esta Regex extrae el CN limpiamente de los certificados de OpenSSL
	            .subjectPrincipalRegex("CN=(.*?)(?:,|$)")
	            .userDetailsService(userDetailsService())
	        );
	    
	    return http.build();		
		*/
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

/*
class CustomUserDetailsService implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
*/




