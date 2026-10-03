package com.curso.configuracion;

import java.security.cert.X509Certificate;

import javax.security.auth.x500.X500Principal;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.x509.SubjectX500PrincipalExtractor;
import org.springframework.security.web.authentication.preauth.x509.X509PrincipalExtractor;

import com.curso.modelo.entidad.Usuario;

@Configuration
public class ConfiguracionSpringSecurity {

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
    	http.sessionManagement(sess -> sess
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS) 
			);
	
		http.csrf(csrf -> csrf.disable());		
		
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
            //EMAILADDRESS=juan.gomez@georgeforeman.com,CN=Juan Gómez,OU=Departamento,O=Empresa,C=ES
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
		return new CustomUserDetailsService();
	}

}

//Esta clase estaría en su propio fichero
class CustomX509PrincipalExtractor implements X509PrincipalExtractor {

	@Override
	public Object extractPrincipal(X509Certificate clientCert) {
    	//Los datos de identidad certificado
        X500Principal principal = clientCert.getSubjectX500Principal();
        //dn ya es un string con el formato marcado por RFC2253
        //EMAILADDRESS=juan.gomez@georgeforeman.com,CN=Juan Gómez,OU=Departamento,O=Empresa,C=ES
        String dn = principal.getName(X500Principal.RFC2253);
        //Buscamos lo que sea
        if (dn.contains("EMAILADDRESS=")) {
            return dn.split("EMAILADDRESS=")[1].split(",")[0];
        }
        //Lo que devolvamos será el userName
        return dn; // O retornar un fallback
	}
	
}

//Esta clase estaría en su propio fichero
class CustomUserDetailsService implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		System.out.println("USERNAME: " + username);
		
		switch(username) {
			case "Harry Callahan" : 
				return new Usuario(username, "[PROTECTED]", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"), "APLICACIÓN 1", "adminApp1@b.c", "");
			case "Bud Spencer"    : 
				return new Usuario(username, "[PROTECTED]", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"), "APLICACIÓN 2", "adminApp2@b.c", "");
			case "Harpo"		  : 
				return new Usuario(username, "[PROTECTED]", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_ADMIN"), "APLICACIÓN 3", "adminApp3@b.c", "");
			default : throw new UsernameNotFoundException("User not found!");
		}
		
		/*
		if (username.equals("Harry Callahan")) {
			return new Usuario(username, "[PROTECTED]", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"), "APLICACIÓN 1", "adminApp1@b.c", "");
		} else if (username.equals("Bud Spencer")) {
			return new Usuario(username, "[PROTECTED]", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"), "APLICACIÓN 2", "adminApp2@b.c", "");
		} else if (username.equals("Harpo")) {
			//El certificado de Harpo no está firmado, nunca se ejecutará este if
			return new Usuario(username, "[PROTECTED]", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_ADMIN"), "APLICACIÓN 3", "adminApp3@b.c", "");
		}
		throw new UsernameNotFoundException("User not found!");
		*/
	}
	
}




