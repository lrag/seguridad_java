package com.curso.controlador;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//Ejemplo de Server-Side Request Forgery (SSRF), A10:2021 del OWASP Top 10,
//la unica categoria totalmente nueva de esa revision
//
//El usuario pega la URL de una imagen (por ejemplo para su avatar) y es
//el SERVIDOR, no el navegador del usuario, quien descarga esa imagen.
//El problema: si no limitamos a donde puede apuntar esa URL, el servidor
//se convierte en un proxy que el atacante puede usar para alcanzar
//recursos que jamas deberian ser visibles desde fuera (paneles internos,
//bases de datos, o el tipico endpoint de metadata de la nube en
//169.254.169.254, sin autenticacion, porque se asume que solo la propia
//maquina puede llegar hasta ahi)
//
//Prueba primero con una URL normal de una imagen, y luego con
//http://127.0.0.1:8080/Ej15_SSRF/interno/secreto (ajusta el puerto si
//hace falta) para ver lo que el servidor es capaz de traerte
@WebServlet("/SVAvatar")
public class SVAvatar extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String url = request.getParameter("url");
		if (url == null || url.isEmpty()) {
			response.sendRedirect("formularioAvatar.html");
			return;
		}

		//Aqui es donde deberiamos validar la URL antes de seguirla.
		//Descomentar UNA de las tres alternativas de abajo (estan
		//ordenadas de peor a mejor)

		//ALTERNATIVA 1: lista negra de texto, facil de saltarse
		//(127.1, notacion decimal/octal de la IP, [::1], o una simple
		//redireccion HTTP que apunte a donde queramos)
		/*
		if (url.contains("localhost") || url.contains("127.0.0.1")) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL no permitida");
			return;
		}
		*/

		//ALTERNATIVA 2: lista blanca de dominios externos permitidos.
		//Mejor que la negra, pero ojo: solo mira el texto del host, no
		//la IP a la que resuelve. Si el atacante controla un dominio
		//propio que resuelve a una IP interna (DNS rebinding), esto
		//tampoco basta
		//
		//El apandador:
		//Registra su propio dominio, golfosapandadores.com.
		//Configura su DNS para que golfosapandadores.com resuelva a 127.0.0.1 (o a la IP interna que quiera).
		//Ya está hecho el lío
		/*
		try {
			String host = new URL(url).getHost();
			if (!host.equals("miscdn.ejemplo.com") && !host.equals("imagenes.ejemplo.com")) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Dominio no permitido");
				return;
			}
		} catch (java.net.MalformedURLException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL invalida");
			return;
		}
		*/

		//ALTERNATIVA 3 (la buena): resolvemos el host a su IP real y
		//rechazamos direcciones loopback/privadas/link-local, que es
		//donde viven los recursos internos (incluida la IP de metadata
		//de la nube, 169.254.169.254, que cae dentro de link-local).
		//Ademas restringimos el esquema y, ojo, esto no basta si luego
		//seguimos redirecciones sin revalidar cada salto (ver mas abajo)
		/*
		try {
			URL u = new URL(url);
			if (!"http".equals(u.getProtocol()) && !"https".equals(u.getProtocol())) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Esquema no permitido");
				return;
			}
			java.net.InetAddress direccion = java.net.InetAddress.getByName(u.getHost());
			if (direccion.isLoopbackAddress() || direccion.isSiteLocalAddress()
					|| direccion.isLinkLocalAddress() || direccion.isAnyLocalAddress()) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Destino no permitido");
				return;
			}
		} catch (java.net.UnknownHostException | java.net.MalformedURLException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL invalida");
			return;
		}
		*/
		
		//LA SOLUCIÓN REAL:
		//
		//No haceptar nunca jamás urls del cliente

		HttpURLConnection cx = null;
		try {
			URL u = new URL(url);
			cx = (HttpURLConnection) u.openConnection();
			//Seguimos redirecciones sin revalidar el destino: otro fallo
			//tipico, incluso con la alternativa 3 activa habria que
			//revalidar en cada salto, no solo en la URL de partida
			cx.setInstanceFollowRedirects(true);
			cx.setConnectTimeout(3000);
			cx.setReadTimeout(3000);

			response.setContentType(cx.getContentType() != null ? cx.getContentType() : "application/octet-stream");

			try (InputStream in = cx.getInputStream(); OutputStream out = response.getOutputStream()) {
				byte[] buffer = new byte[4096];
				int leidos;
				while ((leidos = in.read(buffer)) != -1) {
					out.write(buffer, 0, leidos);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "No se pudo descargar la URL");
		} finally {
			if (cx != null) {
				cx.disconnect();
			}
		}
	}

}
