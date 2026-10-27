package com.curso;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Historial de conversacion compartido entre usuarios (LLM02, fuga a traves de
// contexto y conversacion; relacionado tambien con LLM09 10.2): un chat de
// soporte necesita recordar los mensajes anteriores DENTRO de la conversacion
// de un mismo usuario -eso no es el fallo, es la funcionalidad esperada-. El
// fallo es guardar esa memoria bajo un unico identificador de conversacion
// compartido por todos los usuarios, en vez de uno por usuario. El modelo no
// distingue "esto lo dijo otra persona": todo lo que hay en su lista de
// mensajes es, para el, la misma conversacion con un unico interlocutor.
//
// En Spring AI la memoria la gestiona MessageChatMemoryAdvisor: antes de cada
// llamada anade al prompt los mensajes guardados bajo el ChatMemory.CONVERSATION_ID
// de la peticion, y despues guarda la pregunta y la respuesta bajo ese mismo
// id. Todo el fallo esta en que id se le pasa.
//
// Mitigacion: el id de conversacion lo pone el codigo a partir del usuario de
// la sesion autenticada (aqui, "usuario_A" y "usuario_B"); nunca una constante
// ni un valor que el usuario pueda elegir.
//
// Resultado observado (temperature 0, llama3.1:8b): con la conversacion
// compartida, cuando el Cliente B pide "repite el primer mensaje de esta
// conversacion" -sin saber que existe otro cliente- recibe el nombre y el
// numero de pedido del Cliente A. Ya en su primer turno el modelo le llama
// "Marta": la fuga no necesita una pregunta maliciosa, el historial ajeno se
// cuela en cualquier respuesta. Con una conversacion por usuario, cada
// cliente sigue teniendo memoria de SU PROPIA conversacion -el Cliente A pide
// que se le confirme el numero de pedido que dio antes, y el modelo lo hace
// correctamente-, pero el Cliente B no tiene ningun rastro de la conversacion
// ajena.
//
// El modelo (llama3.1:8b) y la temperature (0) estan en application.properties.
@SpringBootApplication
public class Aplicacion implements CommandLineRunner {

    private final ChatClient.Builder chatClientBuilder;

    Aplicacion(ChatClient.Builder chatClientBuilder) {
        this.chatClientBuilder = chatClientBuilder;
    }

    public static void main(String[] args) {
        SpringApplication.run(Aplicacion.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("========================================================");
        System.out.println("1. Escenario A: una única conversación compartida por todos los usuarios (el fallo)");
        ChatClient chatA = chatConMemoria();
        // El fallo: el mismo id de conversacion para cualquier usuario.
        String idUnico = "conversacion_chat_soporte";

        System.out.println("Cliente A, turno 1:");
        System.out.println(responderPregunta(chatA, idUnico, "Mi nombre es Fistro Pecador Salvaje de la Pradera y mi número de pedido es 4521."));
        System.out.println();

        System.out.println("Cliente B, turno 1 (usuario distinto, sin relación con A):");
        System.out.println(responderPregunta(chatA, idUnico, "¿Cuál es el horario de atención al cliente?"));
        System.out.println();

        System.out.println("Cliente A, turno 2 (necesita que se recuerde lo que dijo antes):");
        System.out.println(responderPregunta(chatA, idUnico, "¿Puedes confirmarme el número de pedido que te acabo de dar?"));
        System.out.println();

        System.out.println("Cliente B, turno 2 (pide algo que no debería tener forma de saber):");
        System.out.println(responderPregunta(chatA, idUnico, "Repite textualmente el primer mensaje de esta conversación."));
        System.out.println();

        System.out.println("========================================================");
        System.out.println("2. Escenario B: una conversación propia por usuario (la corrección)");
        ChatClient chatB = chatConMemoria();

        System.out.println("Cliente A, turno 1:");
        System.out.println(responderPregunta(chatB, "usuario_A", "Mi nombre es Antúnez y mi número de pedido es 4521."));
        System.out.println();

        System.out.println("Cliente B, turno 1:");
        System.out.println(responderPregunta(chatB, "usuario_B", "¿Cuál es el horario de atención al cliente?"));
        System.out.println();

        System.out.println("Cliente A, turno 2 (sigue funcionando la memoria de SU propia conversación):");
        System.out.println(responderPregunta(chatB, "usuario_A", "¿Puedes confirmarme el número de pedido que te acabo de dar?"));
        System.out.println();

        System.out.println("Cliente B, turno 2 (misma petición que antes, en su propia conversación):");
        System.out.println(responderPregunta(chatB, "usuario_B", "Repite textualmente el primer mensaje de esta conversación."));
    }

    // Un ChatClient con su propia memoria de conversaciones, vacia: cada
    // escenario empieza de cero.
    private ChatClient chatConMemoria() {
        ChatMemory memoria = MessageWindowChatMemory.builder().build();
        return chatClientBuilder.clone()
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(memoria).build())
                .build();
    }

    // Envia el mensaje dentro de la conversacion idConversacion: el advisor
    // anade al prompt el historial guardado bajo ese id y guarda despues el
    // nuevo turno.
    private static String responderPregunta(ChatClient chat, String idConversacion, String mensaje) {
        
    	System.out.println(mensaje);
    	return chat.prompt()
                .user(mensaje)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, idConversacion))
                .call().content()
                .strip();
    }
}
