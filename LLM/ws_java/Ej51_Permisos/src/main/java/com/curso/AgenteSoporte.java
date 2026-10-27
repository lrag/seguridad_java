package com.curso;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

// El agente: recibe el ticket y deja que el modelo use las herramientas. La
// ejecucion automatica de herramientas de Spring AI esta activada (la opcion
// por defecto): el modelo propone una llamada, Spring AI la ejecuta y le
// devuelve el resultado, hasta que el modelo da su respuesta final. Todo
// ocurre en el hilo de la peticion web, asi que las herramientas se ejecutan
// con el usuario de la sesion.
@Service
public class AgenteSoporte {

    private final ChatClient chat;

    public AgenteSoporte(ChatClient.Builder chatClientBuilder, HerramientasPedidos herramientas) {
        this.chat = chatClientBuilder
                .defaultSystem("Eres el agente de soporte al cliente de una tienda online. Gestiona el "
                        + "ticket que te envían usando las herramientas disponibles y responde al final "
                        + "con un resumen de lo que has hecho.")
                .defaultTools(herramientas)
                .build();
    }

    public String atender(String ticket) {
        return chat.prompt().user(ticket).call().content();
    }
}
