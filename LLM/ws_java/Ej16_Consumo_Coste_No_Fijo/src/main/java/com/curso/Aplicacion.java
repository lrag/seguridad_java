package com.curso;

import java.time.Duration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Consumo descontrolado (LLM06 7.1): el coste de una peticion a un LLM no es
// fijo, crece con los tokens de entrada y con los de salida. En vez de asumir
// esa relacion, este ejemplo la mide de verdad: Ollama devuelve en cada
// respuesta metadatos reales de rendimiento -tokens procesados, tokens
// generados, tiempo de cada fase-, y Spring AI los deja en los metadatos de la
// ChatResponse.
//
// Dos experimentos:
// 1. Crece la entrada (un parrafo repetido 1, 20 y 100 veces, con la misma
//    pregunta) y el limite de salida se mantiene en 30 tokens.
// 2. Crece el limite de salida (num_predict 20, 200 y 800) con la misma
//    entrada corta.
// Ninguna de las dos cifras es fija: ambas escalan con lo que decide quien
// escribe el prompt, no con nada que el servidor controle por su cuenta si no
// impone un limite.
//
// Resultado observado en el script Python equivalente (llama3.1:8b, GTX 1070):
// al crecer la entrada de 42 a 422 y a 2022 tokens, el tiempo de procesarla
// sube de 0.23s a 0.89s y a 3.76s; al crecer num_predict de 20 a 200 y a 800,
// el tiempo de generar la salida sube de 0.65s a 6.83s y a 16.35s.
//
// num_predict es un techo, no una cantidad garantizada: con 800 el modelo
// puede terminar antes por su cuenta (en una ejecucion genero 330 tokens).
// Para forzar mucha salida, un atacante pide algo que obligue a escribir
// mucho ("una lista de 500 elementos"), no "una breve historia".
//
// MITIGACIONES, de mas a menos directas respecto a lo que mide este ejemplo:
//
// 1. Limitar la entrada: mejor RECHAZAR que truncar en silencio (truncando se
//    puede cortar justo la pregunta). El limite se cuenta en tokens, no en
//    caracteres. En un RAG tambien cuenta el contexto recuperado: limitar el
//    topK y el tamano de los fragmentos.
// 2. Limitar la salida desde el servidor: aqui num_predict lo fija quien hace
//    la peticion; en una aplicacion real lo fija el backend, con un maximo que
//    el cliente no puede subir. En Ollama, num_ctx acota ademas el contexto
//    total.
// 3. Rate limiting por usuario o clave de API: peticiones por minuto y, sobre
//    todo, tokens por minuto (una peticion de 2000 tokens no cuesta lo mismo
//    que una de 42).
// 4. Cuotas y presupuestos: tope de tokens o de dinero por usuario y dia, y
//    alertas de gasto. Es lo que frena la "denegacion de cartera" cuando cada
//    peticion por separado cumple los limites.
// 5. Timeouts y concurrencia: cortar las peticiones que tardan demasiado y
//    limitar cuantas se atienden a la vez por usuario, con una cola para el
//    resto.
// 6. Limitar los bucles de agentes: maximo de iteraciones o de llamadas a
//    herramientas por peticion (ver Ej09).
// 7. Monitorizar: registrar los tokens de cada peticion -los mismos metadatos
//    que imprime este ejemplo- y alertar cuando un usuario se sale de su
//    patron habitual.
//
// El modelo (llama3.1:8b) y la temperature (0) estan en application.properties;
// el limite de salida (num_predict) se fija en cada llamada.
@SpringBootApplication
public class Aplicacion implements CommandLineRunner {

    private static final String PARRAFO = "El comedor de la empresa abre de nueve a cinco, de lunes a viernes. ";
    private static final String PREGUNTA = "Resume en una frase el horario del comedor.";
    private static final String HISTORIA = "Cuenta una breve historia sobre un viaje en tren.";

    private final ChatClient.Builder chatClientBuilder;

    Aplicacion(ChatClient.Builder chatClientBuilder) {
        this.chatClientBuilder = chatClientBuilder;
    }

    public static void main(String[] args) {
        SpringApplication.run(Aplicacion.class, args);
    }

    @Override
    public void run(String... args) {
        ChatClient chat = chatClientBuilder.build();

        System.out.println("========================================================");
        System.out.println("1. Variando el tamaño de la entrada, con la misma pregunta y el mismo límite de salida");
        System.out.println();

        System.out.println("Párrafo repetido 1 vez:");
        medir(chat, PARRAFO.repeat(1) + "\n\n" + PREGUNTA, 30);
        System.out.println();

        System.out.println("Párrafo repetido 20 veces:");
        medir(chat, PARRAFO.repeat(20) + "\n\n" + PREGUNTA, 30);
        System.out.println();

        System.out.println("Párrafo repetido 100 veces:");
        medir(chat, PARRAFO.repeat(100) + "\n\n" + PREGUNTA, 30);
        System.out.println();

        System.out.println("========================================================");
        System.out.println("2. Variando el límite de salida solicitado, con la misma entrada corta");
        System.out.println();

        System.out.println("num_predict=20:");
        medir(chat, HISTORIA, 20);
        System.out.println();

        System.out.println("num_predict=200:");
        medir(chat, HISTORIA, 200);
        System.out.println();

        System.out.println("num_predict=800:");
        medir(chat, HISTORIA, 800);
    }

    // Una llamada con un limite de tokens de salida (num_predict) y las cifras
    // reales que devuelve Ollama: tokens y tiempo de procesar la entrada, y
    // tokens y tiempo de generar la salida.
    private static void medir(ChatClient chat, String prompt, int numPredict) {
        ChatResponse respuesta = chat.prompt()
                .user(prompt)
                .options(OllamaChatOptions.builder().numPredict(numPredict).build())
                .call().chatResponse();
        ChatResponseMetadata metadatos = respuesta.getMetadata();
        Integer tokensEntrada = metadatos.get("prompt-eval-count");
        Integer tokensSalida = metadatos.get("eval-count");
        Duration tiempoEntrada = metadatos.get("prompt-eval-duration");
        Duration tiempoSalida = metadatos.get("eval-duration");
        System.out.printf("Tokens de entrada: %d (procesados en %.2fs)%n", tokensEntrada, segundos(tiempoEntrada));
        System.out.printf("Tokens de salida: %d (generados en %.2fs)%n", tokensSalida, segundos(tiempoSalida));
        System.out.println("Salida: " + respuesta.getResult().getOutput().getText());
    }

    private static double segundos(Duration duracion) {
        return duracion.toNanos() / 1e9;
    }
}
