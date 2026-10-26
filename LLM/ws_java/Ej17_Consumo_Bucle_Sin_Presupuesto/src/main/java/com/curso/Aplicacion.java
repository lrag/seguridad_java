package com.curso;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Consumo descontrolado en bucles de agente (LLM06 7.2): a un agente se le
// pide "sigue hasta que estes satisfecho", sin ningun criterio objetivo de
// parada. Sin presupuesto, es el propio modelo quien decide cuando terminar
// -y "cuando el modelo decida" puede tardar mucho mas, y costar mucho mas, que
// con un presupuesto explicito de pasos-. Este ejemplo mide el coste real
// acumulado (tokens, segundos) de las dos versiones, con los mismos metadatos
// de Ollama que Ej16.
//
// El escenario "sin presupuesto" lleva en realidad un limite de seguridad de 8
// pasos, solo para que la demo termine.
//
// El coste crece mas que linealmente: en cada paso se reenvia toda la
// conversacion anterior, asi que los tokens de entrada aumentan paso a paso
// (el paso 8 vuelve a pagar los 7 anteriores). Por eso se imprimen tambien los
// tokens de entrada de cada paso.
//
// Resultado observado en el script Python equivalente (llama3.1:8b): el
// modelo nunca responde "FIN"; la salida crece turno a turno hasta chocar con
// el limite de 150 tokens. Sin presupuesto: 3543 tokens y 26.24s; con
// presupuesto de 2 pasos: 284 tokens y 3.22s.
//
// El modelo (llama3.1:8b) y la temperature (0) estan en application.properties;
// el limite de salida por paso (num_predict=150) se fija en cada llamada.
@SpringBootApplication
public class Aplicacion implements CommandLineRunner {

    private static final String TEXTO = "Los perros son mamíferos carnívoros domesticados hace miles de años.";

    private static final String INSTRUCCION = "Refina este resumen un poco cada vez, mejorándolo: '" + TEXTO + "' "
            + "Cuando de verdad no se te ocurra ninguna mejora más, responde únicamente con la palabra FIN.";

    private static final OllamaChatOptions OPCIONES = OllamaChatOptions.builder().numPredict(150).build();

    // Lo que ha costado un bucle completo.
    record Coste(int pasos, int tokens, double segundos) {
    }

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
        System.out.println("1. Escenario A: sin presupuesto explícito, hasta un límite de seguridad de 8 pasos");
        Coste costeA = ejecutarBucle(chat, 8);
        System.out.printf("Total: %d pasos, %d tokens, %.2fs%n", costeA.pasos(), costeA.tokens(), costeA.segundos());
        System.out.println();

        System.out.println("========================================================");
        System.out.println("2. Escenario B: con un presupuesto deliberado de 2 pasos");
        Coste costeB = ejecutarBucle(chat, 2);
        System.out.printf("Total: %d pasos, %d tokens, %.2fs%n", costeB.pasos(), costeB.tokens(), costeB.segundos());
        System.out.println();

        System.out.println("========================================================");
        System.out.printf("Diferencia: %d tokens y %.2fs de más sin presupuesto, sin haber avanzado hacia una "
                + "respuesta mejor.%n", costeA.tokens() - costeB.tokens(), costeA.segundos() - costeB.segundos());
    }

    // El bucle del agente: cada paso envia TODA la conversacion hasta ese
    // momento y para cuando el modelo responde FIN o se agota maxPasos.
    private static Coste ejecutarBucle(ChatClient chat, int maxPasos) {
        List<Message> mensajes = new ArrayList<>();
        mensajes.add(new UserMessage(INSTRUCCION));
        int tokensTotales = 0;
        double segundosTotales = 0;
        int paso = 0;
        while (paso < maxPasos) {
            paso++;
            ChatResponse respuesta = chat
            		.prompt()
            		.messages(mensajes)
            		.options(OPCIONES)
            		.call()
            		.chatResponse();
            ChatResponseMetadata metadatos = respuesta.getMetadata();
            Integer tokensEntrada = metadatos.get("prompt-eval-count");
            Integer tokensSalida = metadatos.get("eval-count");
            Duration tiempoEntrada = metadatos.get("prompt-eval-duration");
            Duration tiempoSalida = metadatos.get("eval-duration");
            tokensTotales += tokensEntrada + tokensSalida;
            segundosTotales += (tiempoEntrada.toNanos() + tiempoSalida.toNanos()) / 1e9;

            String texto = respuesta.getResult().getOutput().getText();
            System.out.println("Paso " + paso + ": " + tokensEntrada + " tokens de entrada, "
                    + tokensSalida + " tokens generados");
            System.out.println("Salida: " + texto.strip());
            if (esFin(texto)) {
                System.out.println("El modelo decidió terminar por su cuenta.");
                break;
            }
            mensajes.add(new AssistantMessage(texto));
            mensajes.add(new UserMessage("Sigue refinándolo un poco más."));
        }
        return new Coste(paso, tokensTotales, segundosTotales);
    }

    // La respuesta es FIN si es exactamente esa palabra (con o sin punto). No
    // basta con que la contenga: "FINAL" o "DEFINIR" cortarian el bucle.
    private static boolean esFin(String texto) {
        return texto.strip().replaceAll("[.!]+$", "").equalsIgnoreCase("FIN");
    }
}
