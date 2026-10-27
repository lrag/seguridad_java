package com.curso;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// Desinformacion (LLM07 8.1): el modelo no distingue internamente entre "lo
// que se con certeza" y "lo que me parece plausible". Este ejemplo lo prueba
// con una pregunta que en principio no deberia tener ningun misterio: quien
// vive en la pina debajo del mar, la letra de la sintonia de Bob Esponja. La
// respuesta correcta es, sencillamente, "Bob Esponja".
//
// La misma pregunta se repite seis veces con temperature 0.7: con 0 la
// respuesta seria siempre la misma y no se veria la variabilidad.
//
// Resultado observado en el script Python equivalente (llama3.1:8b): en una
// tanda de seis intentos el modelo no acerto ninguno -"Pinocho", "Pez",
// "Crustaceo" (tres veces), "Triton"-, pero en tandas posteriores si acierta
// "Bob Esponja" alguna vez, aunque pocas. No hace falta un hecho oscuro o
// tecnico para que el modelo confabule, y tampoco falla siempre: la misma
// pregunta puede acertar en una ejecucion y fallar en la mayoria de las
// demas, siempre con el mismo tono seguro, sin que nada distinga por
// adelantado cual va a ser el caso.
//
// El resultado depende mucho del modelo: uno mas capaz (qwen2.5:7b) acertaria
// probablemente casi siempre. Aqui interesa uno que falle.
//
// El modelo (llama3.1:8b) y la temperature (0.7) estan en application.properties.
@SpringBootApplication
public class Aplicacion implements CommandLineRunner {

    private static final String PREGUNTA = "¿Quién vive en la piña debajo del mar? Responde solo con el nombre.";

    private final ChatClient.Builder chatClientBuilder;

    // Para la llamada directa a la API de Ollama: los mismos valores que usa
    // Spring AI, leidos de application.properties.
    @Value("${spring.ai.ollama.base-url}")
    private String urlOllama;
    @Value("${spring.ai.ollama.chat.options.model}")
    private String modelo;
    @Value("${spring.ai.ollama.chat.options.temperature}")
    private double temperatura;

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    Aplicacion(ChatClient.Builder chatClientBuilder) {
        this.chatClientBuilder = chatClientBuilder;
    }

    public static void main(String[] args) {
        SpringApplication.run(Aplicacion.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ChatClient chat = chatClientBuilder.build();

        
        System.out.println("========================================================");
        System.out.println("1. La misma pregunta hasta que acierte, con un máximo de 250 intentos (la respuesta correcta es Bob Esponja)");
        System.out.println("Pregunta: " + PREGUNTA);
        for (int intento = 1; intento <= 250; intento++) {
            String respuesta = chat.prompt().user(PREGUNTA).call().content();
            System.out.println("Intento " + intento + ": " + respuesta.strip());
            if(respuesta.strip().toLowerCase().contains("spongebob")) {
            	break;
            }
        }
        System.out.println();
        
        System.out.println("========================================================");
        System.out.println("2. La misma pregunta con la perplejidad de cada respuesta (llamada directa a la API de Ollama)");
        preguntarConPerplejidad(250);
    }

    // Hace la pregunta "intentos" veces contra /api/chat de Ollama pidiendo
    // logprobs: el logaritmo de la probabilidad que el modelo asigno a cada
    // token que genero. Spring AI 1.1.8 no las expone, por eso se llama a la
    // API directamente con el HttpClient del JDK.
    //
    // Perplejidad = exp(-media de los logprobs). Vale 1 si el modelo estaba
    // completamente seguro de cada token y crece cuanto mas dudaba. Es la senal
    // interna de incertidumbre que NO se refleja en el texto: el modelo dice
    // "Crustaceo" con el mismo tono que "Spongebob", pero la probabilidad que
    // le asigna puede ser muy distinta.
    //
    // Con respuestas tan cortas (uno a cuatro tokens) la perplejidad es
    // ruidosa, y baja probabilidad no siempre significa error: si la
    // probabilidad se reparte entre "Spongebob" y "Bob Esponja", las dos
    // aciertan y ninguna sale muy probable.
    private void preguntarConPerplejidad(int intentos) throws Exception {
        String cuerpo = mapper.writeValueAsString(Map.of(
                "model", modelo,
                "stream", false,
                "logprobs", true,
                "options", Map.of("temperature", temperatura),
                "messages", List.of(Map.of("role", "user", "content", PREGUNTA))));
        HttpRequest peticion = HttpRequest.newBuilder(URI.create(urlOllama + "/api/chat"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(cuerpo, StandardCharsets.UTF_8))
                .build();

        for (int intento = 1; intento <= intentos; intento++) {
            JsonNode json = mapper.readTree(http.send(peticion, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).body());
            String respuesta = json.path("message").path("content").asText().strip();
            double sumaLogprobs = 0;
            StringBuilder detalle = new StringBuilder();
            for (JsonNode token : json.path("logprobs")) {
                double logprob = token.path("logprob").asDouble();
                sumaLogprobs += logprob;
                detalle.append(String.format(" [%s %.0f%%]", token.path("token").asText(), Math.exp(logprob) * 100));
            }
            double perplejidad = Math.exp(-sumaLogprobs / json.path("logprobs").size());
            System.out.printf("Intento %d: %s (perplejidad %.2f)%n", intento, respuesta, perplejidad);
            System.out.println("Probabilidad de cada token:" + detalle);
            if (respuesta.toLowerCase().contains("spongebob")) {
                break;
            }
        }
    }
}
