package com.curso;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

import com.curso.modelo.entidad.Usuario;
import com.curso.modelo.persistencia.UsuarioDao;

/*

Guarda un hash del token en BD, no el token en claro — mismo principio que las contraseñas: si se filtra la tabla, que no sirva para suplantar directamente.

Un solo uso de verdad: marca el token como consumido en la misma operación en la que lo validas (o con una transacción/lock), no "lo valido y luego lo marco" — si no, dos peticiones casi simultáneas podrían colarse las dos.

;uchos clientes de correo (Outlook Safe Links, escáneres antiphishing corporativos) "pre-visitan" los enlaces del email 
automáticamente antes de que el usuario haga clic, para comprobar que no son maliciosos — eso consume tu token de un solo 
uso sin que el usuario lo haya pulsado nunca, y el login "falla" aparentemente sin motivo. Mitigación habitual: el enlace 
no autentica directamente por GET, lleva a una página intermedia con un botón ("Confirmar acceso") que dispara el POST real.

No revelar si el email existe: el endpoint de "mándame un enlace" debe responder siempre igual ("si esa cuenta existe, 
te hemos enviado un correo"), tanto si el email está registrado como si no — 

Rate-limit ese mismo endpoint, para que no se pueda usar para bombardear de correos a un email ajeno.

Cuidado con redirecciones abiertas si el enlace acepta un parámetro ?redirect= tras validar el token — 
es el típico open-redirect.

https://aplicacion.com/magic-link/verificar?token=XXXX&redirect=/cliente/index.html
Un atacante puede fabricar este enlace:
https://aplicacion.com/magic-link/verificar?token=XXXX&redirect=https://sitio-malicioso.com/login-falso

*/


@SpringBootApplication
public class Aplicacion implements CommandLineRunner {

	private final UsuarioDao usuarioDao;

	public Aplicacion(UsuarioDao usuarioDao) {
		this.usuarioDao = usuarioDao;
	}

	public static void main(String[] args) {
		SpringApplication.run(Aplicacion.class, args);
	}

	@Override
	@Transactional
	public void run(String... args) {
		if (usuarioDao.contar() == 0) {
			usuarioDao.guardar(new Usuario("Bud Spencer", "a", "a", "USER", "a@a.a"));
			usuarioDao.guardar(new Usuario("Harry Callahan", "b", "b", "USER", "b@b.b"));
			usuarioDao.guardar(new Usuario("Antunez", "c", "c", "USER", "c@c.c"));
		}
	}

}
