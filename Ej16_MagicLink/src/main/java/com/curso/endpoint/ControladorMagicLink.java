package com.curso.endpoint;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.curso.modelo.negocio.ServicioMagicLink;



@Controller
public class ControladorMagicLink {

	private final ServicioMagicLink servicioMagicLink;

	public ControladorMagicLink(ServicioMagicLink servicioMagicLink) {
		this.servicioMagicLink = servicioMagicLink;
	}

	@PostMapping("/magic-link/solicitar")
	public String solicitar(@RequestParam("correo") String correo) {
		servicioMagicLink.solicitarEnlace(correo);
		return "redirect:/revisa-tu-correo.html";
	}

	//El enlace del correo llega aqui. Si el token es valido, redirige a la aplicacion
	//cliente con un segundo token de vida muy corta en la URL (nunca con el JWT directamente)
	@GetMapping("/magic-link/verificar")
	public String verificar(@RequestParam("token") String token) {
		String codigoTemporal = servicioMagicLink.verificarEnlace(token);
		if (codigoTemporal == null) {
			return "redirect:/enlace-invalido.html";
		}
		return "redirect:/cliente/index.html?codigo=" + codigoTemporal;
	}

	//Lo llama el JS de la aplicacion cliente, con el codigo temporal recibido en la URL,
	//para canjearlo por el JWT de verdad
	@PostMapping("/magic-link/token")
	public ResponseEntity<?> token(@RequestParam("codigo") String codigo) {
		String jwt = servicioMagicLink.obtenerJwt(codigo);
		if (jwt == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Codigo invalido o caducado");
		}
		return ResponseEntity.ok(Map.of("token", jwt));
	}

}
