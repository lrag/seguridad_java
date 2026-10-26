package com.curso;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;

// El RAG: recupera de la base vectorial el documento mas parecido a la
// pregunta y se lo pasa al modelo como unico contexto. El modelo no sabe si
// ese documento es verdad; solo que el prompt le pide responder con el.
public class RAG {

    private final ChatClient chat;
    private final BBDDVectorial bbdd;

    public RAG(ChatClient chat, BBDDVectorial bbdd) {
        this.chat = chat;
        this.bbdd = bbdd;
    }

    public String responder(String pregunta) {
        Document recuperado = bbdd.recuperar(pregunta);
        System.out.println("Contexto recuperado: " + recuperado.getText());
        return chat.prompt()
                .user("Responde a la pregunta del usuario utilizando ÚNICAMENTE el contexto adjunto.\n\n"
                        + "Contexto:\n" + recuperado.getText() + "\n\n"
                        + "Pregunta: " + pregunta)
                .call().content();
    }
}
