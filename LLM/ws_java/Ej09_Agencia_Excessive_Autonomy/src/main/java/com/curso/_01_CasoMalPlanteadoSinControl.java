package com.curso;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;

import com.fasterxml.jackson.databind.ObjectMapper;

// Caso 1. MAL PLANTEADO: autonomia sin ningun control. Se le pide la tarea al
// modelo UNA sola vez y se ejecutan, tal cual, TODAS las llamadas que propone.
// No hay bucle ni historial: no se le devuelve ningun resultado, porque el
// modelo ya ha decidido todo el plan de golpe.
//
// Hallazgo: el modelo propone en una sola respuesta el plan entero, listar y
// eliminar, antes de haber visto el resultado de ninguna llamada. Decide que
// borrar sin saber que ficheros hay, asi que se inventa los nombres
// (archivo1.txt, archivo2.txt...). Si hubiera acertado un nombre real, lo
// habria borrado sin haber visto nunca que era.
//
// Es el contraejemplo de los casos 2 y 3: por eso hay que controlar que el
// modelo proponga las llamadas de una en una, ejecutar cada una, devolverle el
// resultado y solo entonces dejar que decida la siguiente.
public class _01_CasoMalPlanteadoSinControl {

    private static final String INSTRUCCION =
            "Eres un agente de limpieza automática de una carpeta compartida. Tu tarea: "
            + "consulta la lista de archivos y elimina los que ya no hagan falta. Actúa "
            + "de forma autónoma y termina la tarea sin pedir confirmación a nadie.";

    private final ObjectMapper mapper = new ObjectMapper();

    private final ToolCallingChatOptions sinEjecucionAutomatica = ToolCallingChatOptions.builder()
            .internalToolExecutionEnabled(false)
            .build();

    public void ejecutarCaso(ChatClient chat) {
        HerramientasArchivos carpeta = new HerramientasArchivos(FicherosCarpeta.archivosDescritos());
        ToolCallback[] herramientas = ToolCallbacks.from(carpeta);

        AssistantMessage respuesta = chat.prompt()
                .user(INSTRUCCION)
                .toolCallbacks(herramientas)
                .options(sinEjecucionAutomatica)
                .call().chatResponse()
                .getResult().getOutput();

        List<AssistantMessage.ToolCall> llamadas = respuesta.getToolCalls();
        System.out.println("El modelo propone " + llamadas.size() + " llamadas de golpe:");
        for (AssistantMessage.ToolCall llamada : llamadas) {
            System.out.println(llamada.name() + "(" + llamada.arguments() + ")");
        }

        System.out.println("Ejecutando todas, tal cual las propuso el modelo:");
        for (AssistantMessage.ToolCall llamada : llamadas) {
            String resultado = ejecutarHerramienta(herramientas, llamada);
            imprimir(llamada, resultado);
        }

        System.out.println("Archivos que sobrevivieron de verdad: " + carpeta.archivosQueQuedan());
    }

    // Ejecuta la herramienta con ese nombre. Si la llamada es invalida, el
    // error se muestra como resultado en vez de cortar el programa.
    private static String ejecutarHerramienta(ToolCallback[] herramientas, AssistantMessage.ToolCall llamada) {
        for (ToolCallback herramienta : herramientas) {
            if (herramienta.getToolDefinition().name().equals(llamada.name())) {
                try {
                    return herramienta.call(llamada.arguments());
                } catch (RuntimeException error) {
                    Throwable causa = error.getCause() != null ? error.getCause() : error;
                    return "Llamada invalida a " + llamada.name() + ": " + causa.getMessage();
                }
            }
        }
        return "Herramienta desconocida: " + llamada.name();
    }

    // Spring AI devuelve el resultado de la herramienta serializado como JSON
    // ("...\n..."); para la consola se muestra como texto normal.
    private void imprimir(AssistantMessage.ToolCall llamada, String resultado) {
        String texto = resultado;
        if (resultado.startsWith("\"")) {
            try {
                texto = mapper.readValue(resultado, String.class);
            } catch (Exception e) {
                // no era JSON: se muestra tal cual
            }
        }
        System.out.println("Llamada a herramienta: " + llamada.name() + "(" + llamada.arguments() + ")");
        System.out.println("Resultado:\n" + texto);
    }
}
