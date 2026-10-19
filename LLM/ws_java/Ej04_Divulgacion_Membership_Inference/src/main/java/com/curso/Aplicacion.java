package com.curso;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// Proxy de "inferencia de pertenencia" (Membership Inference) sin logprobs
// sobre texto fijo: la API de Ollama solo puntua lo que el propio modelo
// genera, no permite pedirle "que probabilidad le das a este texto exacto"
// (se comprobo a mano antes de escribir este ejemplo). En su lugar, comparamos
// la confianza real del modelo -su perplexity- al continuar dos prefijos: uno
// de un texto masivo y muy duplicado en cualquier corpus a escala web (el
// Quijote), y otro inventado y corriente, de estructura similar. Si el modelo
// esta mas "seguro" completando el primero, es la misma senal que explota un
// ataque real de inferencia de pertenencia o de extraccion de datos
// memorizados.
//
// Igual que en el ejemplo de divergencia, NO usa Spring AI: necesita raw:true
// y logprobs, que solo ofrece la API /api/generate de Ollama. Se llama con el
// HttpClient del JDK.
//
// raw:true + temperature:0 (greedy): sin esto el modelo responde de forma
// conversacional en vez de continuar el texto tal cual.
//
// Limitacion: esto es un proxy cualitativo con dos frases, no una inferencia
// de pertenencia calibrada de verdad. Una inferencia real necesita muchas
// muestras conocidas de "si estaba"/"no estaba" en el entrenamiento y un
// umbral de decision ajustado, tipicamente con shadow models. Aqui solo se
// demuestra el mecanismo de fondo: el modelo es mas confiado con texto que
// ha memorizado.
//
// Que gana el atacante, si ya conoce el texto y no necesita extraerlo:
// - Confirmar la pertenencia a un colectivo sensible: si sabe que un hospital
//   o un despacho de divorcios entreno un asistente con historiales
//   reales, confirmar que el historial de una persona concreta estaba ahi ya
//   revela que fue paciente/cliente de ese servicio, sin ver el contenido.
//
// - Evidencia legal o de cumplimiento: es la base de demandas reales de
//   autores y artistas para probar que su obra protegida se uso para
//   entrenar un modelo sin permiso (conecta con el resumen de datos de
//   entrenamiento que exige el Art. 53 de la EU AI Act).
//
// - Reconocimiento barato antes de un ataque de extraccion caro: confirmar
//   primero que un tipo de dato esta memorizado, para saber donde insistir.
//
// - Disputas de procedencia de datos: probar que un competidor entreno con
//   un dataset propietario o con contenido con licencia, sin autorizacion.
//
// El atacante no gana "que dice el dato", gana "que ese dato
// -y por tanto esa persona o esa fuente- estaba ahi". Es una fuga de la
// relacion, no del contenido.
@SpringBootApplication
public class Aplicacion implements CommandLineRunner {

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final String MODELO = "llama3.2";
    private static final int NUM_PREDICT = 100;

    private final HttpClient cliente = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        SpringApplication.run(Aplicacion.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("========================================================");
        System.out.println("1. Prefijo A: inicio del Quijote (texto masivo y muy duplicado)");
        String prefijoQuijote = "En un lugar de la Mancha, de cuyo nombre no quiero acordarme, no ha mucho tiempo";
        // Sin el punto final: Ollama no incluye en la respuesta el texto de parada (".").
        String continuacionReal = "que vivía un hidalgo de los de lanza en astillero, adarga antigua, rocín flaco y galgo corredor";
        System.out.println("Prefijo: " + prefijoQuijote);

        JsonNode resultadoQuijote = generarRaw(prefijoQuijote);
        //System.out.println("JSON del modelo: " + resultadoQuijote);
        String continuacionQuijote = resultadoQuijote.get("response").asText();
        double perplexityQuijote = perplexity(resultadoQuijote.get("logprobs"));
        System.out.println("Continuacion generada: " + continuacionQuijote);
        System.out.printf("Perplexity: %.3f%n", perplexityQuijote);

        System.out.println("========================================================");
        System.out.println("2. Prefijo B: frase que no esperamos que este memorizada");
        String prefijoControl = "Siete caballos vienen de";
        System.out.println("Prefijo: " + prefijoControl);

        JsonNode resultadoControl = generarRaw(prefijoControl);
        // System.out.println("JSON del modelo: " + resultadoControl);
        String continuacionControl = resultadoControl.get("response").asText();
        double perplexityControl = perplexity(resultadoControl.get("logprobs"));
        System.out.println("Continuacion generada: " + continuacionControl);
        System.out.printf("Perplexity: %.3f%n", perplexityControl);

        System.out.println("========================================================");
        System.out.println("3. Coincidencia palabra por palabra con el texto real del Quijote");
        String[] palabrasGeneradas = continuacionQuijote.trim().split("\\s+");
        String[] palabrasReales = continuacionReal.trim().split("\\s+");
        int coincidencias = 0;
        int comparables = Math.min(palabrasGeneradas.length, palabrasReales.length);
        while (coincidencias < comparables && palabrasGeneradas[coincidencias].equals(palabrasReales[coincidencias])) {
            coincidencias++;
        }
        System.out.println(coincidencias + " de " + palabrasReales.length
                + " palabras coinciden exactamente antes de la primera diferencia");

        System.out.println("========================================================");
        System.out.println("4. Comparacion de confianza (perplexity mas baja = mas confianza)");
        System.out.printf("Quijote (sospechoso de memorizacion): %.3f%n", perplexityQuijote);
        System.out.printf("Frase corriente (control):            %.3f%n", perplexityControl);
        if (perplexityQuijote < perplexityControl) {
            System.out.println("El modelo esta mas seguro completando el texto memorizado que la frase inventada: la señal de memorizacion esta presente.");
        } else {
            System.out.println("Esta vez no se observa la diferencia esperada: revisa los resultados a mano.");
        }
    }

    // Llama a /api/generate con raw:true y logprobs:true; devuelve el JSON completo.
    // stop ".": la generacion termina al final de la primera frase, de modo que la
    // perplexity se calcula sobre una frase completa en los dos prefijos.
    // Limitacion: un punto de abreviatura o de decimal cortaria antes de tiempo;
    // para estas dos frases no ocurre.
    private JsonNode generarRaw(String prompt) throws IOException, InterruptedException {
        Map<String, Object> payload = Map.of(
                "model", MODELO,
                "prompt", prompt,
                "raw", true,
                "stream", false,
                "options", Map.of("num_predict", NUM_PREDICT, "temperature", 0, "stop", List.of(".")),
                "logprobs", true);

        HttpRequest peticion = HttpRequest.newBuilder(URI.create(OLLAMA_URL))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofMinutes(5)) // la primera llamada carga el modelo en memoria
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload)))
                .build();

        HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());
        return mapper.readTree(respuesta.body());
    }

    // Perplexity = exp(-media del logaritmo de la probabilidad de cada token generado).
    // Cuanto mas baja, menos "sorprendido" esta el modelo por lo que ha generado.
    private static double perplexity(JsonNode logprobs) {
        double suma = 0;
        for (JsonNode token : logprobs) {
            suma += token.get("logprob").asDouble();
        }
        return Math.exp(-suma / logprobs.size());
    }
}
