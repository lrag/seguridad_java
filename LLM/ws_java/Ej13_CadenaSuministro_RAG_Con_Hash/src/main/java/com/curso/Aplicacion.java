package com.curso;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Fuentes de procedencia dudosa en RAG (LLM04 5.3). Escenario: el RAG reingiere
// periodicamente sus fuentes (una wiki, una carpeta compartida...). Un
// atacante con permiso de edicion sobre ellas -pero no sobre la aplicacion ni
// sobre la base vectorial- modifica la politica de devoluciones y anade un
// documento nuevo, politica_devoluciones_2026, que tambien habla de ella (ver
// Fuentes). Despues espera: la siguiente reingesta automatica lo indexa todo
// como si fuera legitimo y el RAG empieza a responder lo que el atacante
// quiere. El paso 1 lo muestra sin ninguna comprobacion: el documento
// modificado reemplaza al original sin ningun aviso, y el nuevo se anade.
//
// El control del paso 2 (ver BBDDVectorial) toma como version de confianza la
// carga inicial, guardando el hash de cada documento. En cada reingesta
// compara el hash de lo que llega con el de confianza: los documentos sin
// cambios pasan solos, y los modificados o nuevos quedan en cuarentena: no se
// indexan, se anotan en un informe y la version de confianza sigue activa
// hasta que una persona apruebe el cambio. El hash no puede decidir si un
// cambio es legitimo; lo que hace es reducir la revision humana a lo que
// realmente ha cambiado.
//
// Es un control de prevencion: el contenido alterado nunca llega a responder
// a nadie. El precio es que un cambio legitimo tampoco llega al RAG hasta que
// alguien lo apruebe. La alternativa -indexar el cambio y solo anotarlo- es un
// control de deteccion: el contenido alterado estaria activo desde la
// reingesta hasta la revision.
//
// Limitacion real de este mecanismo, que este ejemplo NO resuelve: el hash se
// guarda en la MISMA base vectorial y bajo el MISMO control de acceso que el
// propio documento. Si un atacante compromete directamente el proceso de
// ingesta o la base vectorial -no solo el documento fuente externo-, puede
// escribir un documento alterado y su hash correspondiente en la misma
// operacion, y el informe no muestra nada. Para ese caso haria falta que el
// hash de confianza viviera en un sitio con un control de acceso distinto y
// mas restringido, por ejemplo registrado por el proceso de publicacion
// autorizado de la fuente.
//
// El modelo (llama3.1:8b), la temperature (0) y el modelo de embeddings
// (nomic-embed-text) estan en application.properties.
@SpringBootApplication
public class Aplicacion implements CommandLineRunner {

    private static final String PREGUNTA = "¿Cuál es la política de devoluciones?";
    private static final Path INFORME = Path.of("informe_reingesta.txt");

    private final ChatClient.Builder chatClientBuilder;
    private final EmbeddingModel embeddingModel;

    Aplicacion(ChatClient.Builder chatClientBuilder, EmbeddingModel embeddingModel) {
        this.chatClientBuilder = chatClientBuilder;
        this.embeddingModel = embeddingModel;
    }

    public static void main(String[] args) {
        SpringApplication.run(Aplicacion.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ChatClient chat = chatClientBuilder.build();

        System.out.println("========================================================");
        System.out.println("1. Sin ninguna comprobación: la reingesta indexa lo que encuentre");
        BBDDVectorial bbddSinControl = new BBDDVectorial(embeddingModel);
        System.out.println("Carga inicial de las fuentes");
        bbddSinControl.ingerirSinComprobar(Fuentes.cargaInicial());
        responder(chat, bbddSinControl);
        System.out.println();

        System.out.println("Reingesta programada, después de que el atacante haya tocado las fuentes");
        bbddSinControl.ingerirSinComprobar(Fuentes.reingesta());
        responder(chat, bbddSinControl);
        System.out.println("Nada en el sistema detectó los cambios: la respuesta cambió en silencio.");
        System.out.println();

        System.out.println("========================================================");
        System.out.println("2. Con comprobación por hash");
        BBDDVectorial bbdd = new BBDDVectorial(embeddingModel);
        System.out.println("Carga inicial de las fuentes: sus hashes son la versión de confianza");
        bbdd.ingerirSinComprobar(Fuentes.cargaInicial());
        responder(chat, bbdd);
        System.out.println();

        System.out.println("Reingesta programada, después de que el atacante haya tocado las fuentes");
        bbdd.ingerirConComprobacion(Fuentes.reingesta());
        responder(chat, bbdd);
        System.out.println("La respuesta no cambia: los cambios esperan en el informe a que alguien los apruebe.");
        System.out.println();

        System.out.println("Informe para la revisión humana, guardado en " + INFORME);
        String informe = bbdd.informe();
        Files.writeString(INFORME, informe);
        System.out.println(informe);
    }

    // RAG minimo: recupera los fragmentos mas parecidos a la pregunta y se los
    // pasa al modelo como unico contexto, uno por linea. Se imprime la
    // similitud de cada uno con la pregunta (de 0 a 1).
    private static void responder(ChatClient chat, BBDDVectorial base) {
        List<Document> recuperados = base.recuperar(PREGUNTA);
        System.out.println("Pregunta: " + PREGUNTA);
        String contexto = "";
        for (Document recuperado : recuperados) {
            contexto += recuperado.getText() + "\n";
            System.out.printf("Contexto recuperado (similitud %.3f): %s%n", recuperado.getScore(), recuperado.getText());
        }
        String respuesta = chat.prompt()
                .user("Responde la pregunta del usuario usando exclusivamente este contexto:\n\n"
                        + contexto + "\nPregunta: " + PREGUNTA)
                .call().content();
        System.out.println("Respuesta: " + respuesta);
    }
}
