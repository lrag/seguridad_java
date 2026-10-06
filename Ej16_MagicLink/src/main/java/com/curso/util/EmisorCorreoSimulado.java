package com.curso.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

//Simula un emisor de correo: en vez de mandar nada de verdad, escribe un fichero de texto
//en la raiz del proyecto, para que el alumno pueda copiar el enlace y pegarlo en el navegador.
//El dia que haya que enviar correos de verdad, esta es la clase que se sustituye por una
//que use JavaMailSender (o lo que sea) -el resto de la aplicacion no se entera del cambio-.
@Component
public class EmisorCorreoSimulado {

	private static final Logger log = LoggerFactory.getLogger(EmisorCorreoSimulado.class);

	public void enviar(String destinatario, String asunto, String cuerpo) {
		String contenido = """
				Para: %s
				Asunto: %s

				%s
				""".formatted(destinatario, asunto, cuerpo);

		try {
			Path fichero = Path.of("correo.txt").toAbsolutePath();
			Files.writeString(fichero, contenido);
			log.info("Correo simulado escrito en: {}", fichero);
		} catch (IOException ex) {
			throw new RuntimeException("No se ha podido escribir el fichero del correo simulado", ex);
		}
	}

}
