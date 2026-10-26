package com.curso;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Entradas disenadas para maximizar el coste de computo (LLM06 7.3): el coste
// de procesar una entrada depende del NUMERO de tokens, no de si esos tokens
// aportan informacion real. Este ejemplo compara el tiempo de procesar dos
// entradas con un numero de tokens similar: una repetitiva y sin contenido
// real, y otra con frases informativas. Un atacante no necesita escribir nada
// con sentido para que su peticion sea cara.
//
// Nota metodologica importante: Ollama cachea el procesamiento del principio
// comun entre un prompt y los anteriores. Si se reutiliza literalmente el
// mismo texto en cada ejecucion, su procesamiento sale casi gratis (en una
// primera version del script Python, 0.04s frente a 4.7s) y parece que el
// contenido informativo es mas barato, cuando es un artefacto de la cache.
// Por eso los dos textos llevan numeros aleatorios desde el PRINCIPIO: si el
// numero estuviera al final, la cache seguiria aprovechando todo lo anterior.
// La cache es a la vez una mitigacion de coste (abarata las peticiones
// repetidas) y una trampa para quien mide tiempos sin tenerla en cuenta.
//
// Resultado observado en el script Python equivalente (llama3.1:8b): unos
// 4.7-4.9s para los dos textos, con ~2034 y ~2050 tokens de entrada.
//
// El modelo (llama3.1:8b) y la temperature (0) estan en application.properties;
// num_predict=10 deja la salida en lo minimo para medir solo la entrada.
@SpringBootApplication
public class Aplicacion implements CommandLineRunner {

    private static final String PREGUNTA = "Responde solo con OK, no hace falta que digas nada más.";

    private static final List<String> HECHOS = List.of(
            "El sistema solar está formado por el sol y los cuerpos que orbitan a su alrededor.",
            "Mercurio es el planeta más cercano al sol y el más pequeño del sistema solar.",
            "Venus tiene la atmósfera más densa de todos los planetas rocosos.",
            "La Tierra es el único planeta conocido que alberga vida.",
            "Marte tiene el volcán más grande conocido, llamado Monte Olimpo.",
            "Júpiter es el planeta más grande del sistema solar.",
            "Saturno es conocido por sus prominentes anillos de hielo y roca.",
            "Urano gira prácticamente de lado respecto al resto de planetas.",
            "Neptuno es el planeta más alejado del sol.",
            "Plutón fue reclasificado como planeta enano en 2006.");

    private static final OllamaChatOptions OPCIONES = OllamaChatOptions.builder().numPredict(10).build();

    private final ChatClient.Builder chatClientBuilder;
    private final Random aleatorio = new Random();

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
        System.out.println("1. Entrada degenerada (la misma frase sin sentido repetida cientos de veces)");
        medir(chat, "Degenerada", textoDegenerado());
        System.out.println();

        System.out.println("========================================================");
        System.out.println("2. Entrada con contenido real, de longitud comparable");
        medir(chat, "Informativa", textoInformativo());
    }

    // "lorem ipsum" 400 veces, con un numero aleatorio al principio para que
    // no se aproveche ninguna cache.
    private String textoDegenerado() {
        return "(consulta " + aleatorio.nextInt(1_000_000_000) + ")\n" + "lorem ipsum dolor sit amet ".repeat(400);
    }

    // Las diez frases 30 veces. En cada repeticion se barajan y cada frase
    // lleva un numero aleatorio delante, asi que la secuencia de tokens es
    // nueva de principio a fin.
    private String textoInformativo() {
        List<String> frases = new ArrayList<>();
        for (int repeticion = 0; repeticion < 30; repeticion++) {
            List<String> bloque = new ArrayList<>(HECHOS);
            Collections.shuffle(bloque, aleatorio);
            for (String hecho : bloque) {
                frases.add(aleatorio.nextInt(1_000_000_000) + ": " + hecho);
            }
        }
        return String.join(" ", frases);
    }

    private static void medir(ChatClient chat, String nombre, String texto) {
        ChatResponse respuesta = chat.prompt()
                .user(texto + "\n\n" + PREGUNTA)
                .options(OPCIONES)
                .call().chatResponse();
        ChatResponseMetadata metadatos = respuesta.getMetadata();
        Integer tokensEntrada = metadatos.get("prompt-eval-count");
        Duration tiempoEntrada = metadatos.get("prompt-eval-duration");
        System.out.printf("%s: %d tokens de entrada, procesados en %.2fs%n",
                nombre, tokensEntrada, tiempoEntrada.toNanos() / 1e9);
    }
}
