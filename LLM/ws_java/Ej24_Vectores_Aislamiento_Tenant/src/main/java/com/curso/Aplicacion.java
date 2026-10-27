package com.curso;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Falta de aislamiento entre tenants (LLM09 10.2): dos empresas comparten la
// misma coleccion vectorial -por simplicidad de implementacion-. Sus
// documentos, sobre el mismo tema de negocio, quedan muy cerca en el espacio
// de embeddings, asi que una consulta sin filtro por tenant devuelve contenido
// de las dos, sin que haga falta ningun ataque dirigido: basta con preguntar
// por el tema.
//
// Mitigaciones:
// - El filtro por tenant lo pone el codigo, con el tenant de la sesion
//   autenticada; nunca un parametro que el usuario pueda manipular ni algo que
//   decida el modelo. En BBDDVectorial.buscar el tenant es obligatorio, para
//   que no se pueda olvidar.
// - Un filtro que se olvida una sola vez basta para la fuga. Cuando el
//   aislamiento importa de verdad, mejor colecciones o namespaces separados
//   por tenant, o seguridad a nivel de fila en la propia base de datos, que
//   confiar en que cada consulta lleve su filtro.
//
// Con solo dos documentos y k=2, sin filtro salen siempre los dos. En una base
// real con miles de documentos la fuga ocurre igual, pero solo cuando los
// documentos de otra empresa quedan mas cerca de la pregunta que los propios.
//
// El modelo (llama3.1:8b), la temperature (0) y el modelo de embeddings
// (nomic-embed-text) estan en application.properties.
@SpringBootApplication
public class Aplicacion implements CommandLineRunner {

    private static final String PREGUNTA = "¿Cuál es la política de reembolsos automáticos para clientes VIP?";

    // El tenant del agente que consulta: en una aplicacion real, el de su sesion autenticada.
    private static final String TENANT_DE_LA_SESION = "Tech3000";

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
    public void run(String... args) {
        ChatClient chat = chatClientBuilder.build();

        System.out.println("========================================================");
        System.out.println("1. Ingesta: dos empresas distintas, en la misma colección compartida");
        BBDDVectorial bbdd = new BBDDVectorial(embeddingModel);
        bbdd.ingerir(
        		"Tech3000", 
        		"Política interna de Tech3000: los reembolsos a clientes VIP se aprueban "+
                "automáticamente hasta 5000 euros sin revisión adicional."
        	);
        bbdd.ingerir(
        		"artica_retail", 
        		"Política interna de Ártica Retail: los reembolsos a clientes VIP se "+ 
        		"aprueban automáticamente hasta 800 euros sin revisión adicional."
        	);
        System.out.println("2 documentos indexados: uno de Tech3000 y uno de Ártica Retail.");
        System.out.println();

        System.out.println("========================================================");
        System.out.println("2. Escenario A: un agente de Tech3000 consulta sin filtrar por tenant");
        System.out.println("Pregunta: " + PREGUNTA);
        responder(chat, bbdd.buscarSinFiltro(PREGUNTA, 2));
        System.out.println();

        System.out.println("========================================================");
        System.out.println("3. Escenario B: la misma consulta, con el filtro de tenant aplicado");
        System.out.println("Pregunta: " + PREGUNTA);
        responder(chat, bbdd.buscar(PREGUNTA, 2, TENANT_DE_LA_SESION));
    }

    // Muestra de que empresa es cada documento recuperado y responde con todos
    // ellos como contexto.
    private static void responder(ChatClient chat, List<Document> recuperados) {
        String contexto = "";
        for (Document documento : recuperados) {
            System.out.println("Recuperado (tenant real: " + documento.getMetadata().get("tenant_id") + "): "
                    + documento.getText());
            contexto += documento.getText() + "\n";
        }
        String respuesta = chat.prompt()
                .user("Responde la pregunta usando exclusivamente este contexto:\n\n" + contexto
                        + "\nPregunta: " + PREGUNTA)
                .call().content();
        System.out.println("Respuesta al agente de NovaTech: " + respuesta.strip());
    }
}
