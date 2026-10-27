package com.curso;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

// El RAG: recupera de la base vectorial el documento mas parecido a la
// pregunta y se lo pasa al modelo como unico contexto. Lo usan los dos
// controladores, la pagina web y la API REST.
@Service
public class RAG {

    // Lo que devuelve una consulta: la pregunta, el documento recuperado y la
    // respuesta del modelo. La API REST lo devuelve tal cual, como JSON.
    public record Resultado(String pregunta, String contexto, String respuesta) {
    }

    private final ChatClient chat;
    private final BBDDVectorial bbdd;

    public RAG(ChatClient.Builder chatClientBuilder, BBDDVectorial bbdd) {
        this.chat = chatClientBuilder.build();
        this.bbdd = bbdd;
    }

    public Resultado responder(String pregunta) {
        Document recuperado = bbdd.recuperar(pregunta);
        System.out.println("Pregunta: " + pregunta);
        System.out.println("Contexto recuperado: " + recuperado.getText());
        String respuesta = chat.prompt()
                .user("Responde a la pregunta del usuario utilizando ÚNICAMENTE el contexto adjunto.\n\n"
                        + "Contexto:\n" + recuperado.getText() + "\n\n"
                        + "Pregunta: " + pregunta)
                .call().content();
        return new Resultado(pregunta, recuperado.getText(), respuesta);
    }
}
