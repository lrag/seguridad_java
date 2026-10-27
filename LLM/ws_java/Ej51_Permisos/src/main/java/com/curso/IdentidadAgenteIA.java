package com.curso;

import java.util.function.Supplier;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

// La identidad propia del agente de IA: el usuario "agente-ia", con el rol
// AGENTE_IA (solo lectura en ServicioPedidos).
//
// No esta en el UserDetailsService a proposito: ahi estan los usuarios que
// pueden iniciar sesion, y la IA no se autentica nunca. Si estuviera, quien
// adivinase su contrasena podria entrar en la aplicacion como la IA. Aqui la
// identidad la asigna el codigo del servidor, al vuelo.
//
// ejecutar() sustituye el SecurityContext de la peticion por uno con el
// agente, ejecuta la accion y restaura el original en el finally. Como Spring
// AI ejecuta las herramientas en el mismo hilo, los @PreAuthorize ven al
// agente, no al usuario que ha enviado la peticion.
@Component
public class IdentidadAgenteIA {

    public static final String NOMBRE = "agente-ia";

    private final Authentication autenticacion = UsernamePasswordAuthenticationToken.authenticated(
            NOMBRE, null, AuthorityUtils.createAuthorityList("ROLE_AGENTE_IA"));

    public <T> T ejecutar(Supplier<T> accion) {
        SecurityContext original = SecurityContextHolder.getContext();
        SecurityContext contextoAgente = SecurityContextHolder.createEmptyContext();
        contextoAgente.setAuthentication(autenticacion);
        SecurityContextHolder.setContext(contextoAgente);
        try {
            return accion.get();
        } finally {
            SecurityContextHolder.setContext(original);
        }
    }
}
