package com.curso;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.web.util.HtmlUtils;

// Inyeccion en el navegador: XSS y renderizado sin sanear (LLM10 11.2): no
// hace falta ningun atacante para que la salida del modelo contenga HTML
// ejecutable. Aqui el modelo actua como el asistente de un curso de seguridad
// web, respondiendo con total normalidad a una pregunta educativa sobre XSS
// -y el ejemplo tipico que da, de forma perfectamente legitima, es HTML
// ejecutable de verdad-. Lo que decide si eso es peligroso no es el modelo: es
// si el destino lo renderiza como HTML sin sanear, o lo trata como texto.
//
// Mitigacion: tratar la salida del modelo como cualquier dato que no es de
// confianza y escaparla segun el contexto donde se inserta (aqui, contenido
// HTML, con HtmlUtils.htmlEscape de Spring, el equivalente de html.escape de
// Python). Si se quiere formato enriquecido (markdown), pasarlo por un
// sanitizador que deje solo las etiquetas permitidas.
//
// El programa guarda las dos paginas en ficheros HTML (en el directorio desde
// el que se ejecuta) y las abre en el navegador por defecto: la sin sanear
// ejecuta el payload (salta el alert), la saneada muestra el mismo texto sin
// ejecutar nada. Ademas comprueba si contienen una etiqueta <script> activa.
// Solo busca <script> literal; una respuesta con otro vector (<img
// onerror=...>) seria igual de peligrosa sin sanear y aqui no se detectaria.
//
// El modelo (llama3.1:8b) y la temperature (0) estan en application.properties.
@SpringBootApplication
public class Aplicacion implements CommandLineRunner {

    private static final String PREGUNTA = "Eres el asistente de un curso de seguridad web. Un alumno pregunta: "
            + "¿qué es un ataque XSS? Ponme un ejemplo típico de payload que se usa "
            + "para demostrarlo en una prueba de concepto.";

    private final ChatClient.Builder chatClientBuilder;

    Aplicacion(ChatClient.Builder chatClientBuilder) {
        this.chatClientBuilder = chatClientBuilder;
    }

    public static void main(String[] args) {
        // Spring Boot arranca por defecto en modo "headless" (sin entorno
        // grafico), y entonces Desktop no puede abrir el navegador.
        new SpringApplicationBuilder(Aplicacion.class).headless(false).run(args);
    }

    @Override
    public void run(String... args) throws IOException {
        ChatClient chat = chatClientBuilder.build();

        System.out.println("========================================================");
        System.out.println("1. Pregunta perfectamente legítima, sin ningún intento de ataque");
        String respuesta = chat.prompt().user(PREGUNTA).call().content();
        System.out.println(respuesta);
        System.out.println();

        System.out.println("========================================================");
        System.out.println("2. Escenario A: la respuesta se inserta tal cual en una página HTML");
        String paginaSinSanear = pagina(respuesta);
        if (paginaSinSanear.contains("<script>")) {
            System.out.println("La página final contiene un <script> activo, tal cual lo interpretaría un navegador.");
            System.out.println("Fragmento de la página generada:");
            System.out.println(fragmento(paginaSinSanear, "<script>", 60));
        } else {
            System.out.println("Esta vez la respuesta no incluyó una etiqueta <script> literal.");
        }
        abrirEnNavegador(paginaSinSanear, "pagina_sin_sanear.html");
        System.out.println();

        System.out.println("========================================================");
        System.out.println("3. Escenario B: la misma respuesta, escapada antes de insertarla");
        String paginaSaneada = pagina(HtmlUtils.htmlEscape(respuesta));
        if (paginaSaneada.contains("<script>")) {
            System.out.println("Sigue habiendo un <script> activo (no debería ocurrir con htmlEscape).");
        } else {
            System.out.println("No hay ningún <script> activo: el texto se muestra tal cual, como texto plano.");
            if (paginaSaneada.contains("&lt;script&gt;")) {
                System.out.println("Fragmento de la página generada:");
                System.out.println(fragmento(paginaSaneada, "&lt;script&gt;", 80));
            }
        }
        abrirEnNavegador(paginaSaneada, "pagina_saneada.html");
    }

    // Guarda la pagina en un fichero y la abre en el navegador por defecto,
    // como la veria el alumno.
    private static void abrirEnNavegador(String pagina, String nombreFichero) throws IOException {
        Path fichero = Path.of(nombreFichero).toAbsolutePath();
        Files.writeString(fichero, pagina, StandardCharsets.UTF_8);
        System.out.println("Página guardada en " + fichero);
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(fichero.toUri());
            System.out.println("Abierta en el navegador.");
        } else {
            System.out.println("No se puede abrir el navegador desde aquí: abre el fichero a mano.");
        }
    }

    // La pagina que veria el alumno, con el contenido insertado en un <p>.
    private static String pagina(String contenido) {
        return """
                <html>
                <head><meta charset="utf-8"></head>
                <body>
                  <h1>Respuesta del asistente</h1>
                  <p>%s</p>
                </body>
                </html>
                """.formatted(contenido);
    }

    // Unos caracteres antes de la etiqueta y unos cuantos despues.
    private static String fragmento(String pagina, String etiqueta, int despues) {
        int inicio = pagina.indexOf(etiqueta);
        return pagina.substring(Math.max(0, inicio - 40), Math.min(pagina.length(), inicio + despues));
    }
}
