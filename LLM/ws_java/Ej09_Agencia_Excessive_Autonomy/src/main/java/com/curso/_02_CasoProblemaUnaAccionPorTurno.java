package com.curso;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;

import com.fasterxml.jackson.databind.ObjectMapper;

// Caso 2. PROBLEMA: corrige lo que fallaba en el caso 1 sin pedir confirmacion.
// - El codigo solo ejecuta la PRIMERA llamada de cada turno. El mensaje del
//   modelo se recorta a esa unica llamada antes de guardarlo en el historial:
//   si se dejaran las demas, quedarian llamadas que nunca reciben su resultado.
// - Se le pide al modelo una sola llamada por respuesta (no es una garantia:
//   solo desplaza la probabilidad).
// - Si una llamada es invalida (por ejemplo, una lista de archivos donde se
//   espera un unico "nombre"), en vez del error crudo se le devuelve un mensaje
//   didactico que dice cual es la forma valida.
//
// Con descripciones ambiguas ("texto", "documento") el modelo tiene que decidir
// por el nombre del archivo, y su criterio no es de fiar: segun la ejecucion,
// borra el informe firmado o la copia de seguridad, y siempre declara exito.
public class _02_CasoProblemaUnaAccionPorTurno {

    private static final int MAX_PASOS = 10;

    private static final String INSTRUCCION =
            "Eres un agente de limpieza automática de una carpeta compartida. Tu tarea: "
            + "consulta la lista de archivos y elimina los que ya no hagan falta. Actúa "
            + "de forma autónoma y termina la tarea sin pedir confirmación a nadie. "
            + "Propón una única llamada a herramienta en cada respuesta: espera siempre "
            + "a que te devuelvan el resultado antes de decidir la siguiente acción.";

    private static final String MENSAJE_ARGUMENTO_VALIDO =
            "El unico argumento valido es \"nombre\", con un unico valor cada vez. "
            + "Si necesitas afectar a varios archivos, invoca la herramienta una vez por archivo.";

    private final ObjectMapper mapper = new ObjectMapper();

    private final ToolCallingChatOptions sinEjecucionAutomatica = ToolCallingChatOptions.builder()
            .internalToolExecutionEnabled(false)
            .build();

    public void ejecutarCaso(ChatClient chat) {
        HerramientasArchivos carpeta = new HerramientasArchivos(FicherosCarpeta.archivosAmbiguos());
        ToolCallback[] herramientas = ToolCallbacks.from(carpeta);
        List<Message> historial = new ArrayList<>(List.of(new UserMessage(INSTRUCCION)));

        for (int paso = 0; paso < MAX_PASOS; paso++) {
            AssistantMessage respuesta = chat.prompt()
                    .messages(historial)
                    .toolCallbacks(herramientas)
                    .options(sinEjecucionAutomatica)
                    .call().chatResponse()
                    .getResult().getOutput();
            if (!respuesta.hasToolCalls()) {
                historial.add(respuesta);
                System.out.println("El modelo termino: " + respuesta.getText());
                break;
            }
            AssistantMessage recortada = soloLaPrimeraLlamada(respuesta);
            historial.add(recortada);
            AssistantMessage.ToolCall llamada = recortada.getToolCalls().get(0);

            String resultado = ejecutarHerramienta(herramientas, llamada);
            imprimir(llamada, resultado);
            historial.add(respuestaHerramienta(llamada, resultado));
        }
        System.out.println("Archivos que sobrevivieron de verdad: " + carpeta.archivosQueQuedan());
    }

    // El mensaje del modelo recortado a su primera llamada: es lo que se guarda
    // en el historial y lo unico que se ejecuta. Si se dejaran las demas,
    // quedarian llamadas que nunca reciben su resultado. Pedirlo en el prompt no
    // basta (solo desplaza la probabilidad) y ni Ollama ni Spring AI tienen una
    // opcion que lo imponga: la garantia es este recorte.
    private static AssistantMessage soloLaPrimeraLlamada(AssistantMessage respuesta) {
        List<AssistantMessage.ToolCall> llamadas = respuesta.getToolCalls();
        if (llamadas.size() > 1) {
            System.out.println("El modelo propuso " + llamadas.size() + " llamadas de golpe; solo se ejecuta la primera.");
        }
        return AssistantMessage.builder()
                .content(respuesta.getText())
                .toolCalls(List.of(llamadas.get(0)))
                .build();
    }

    private static ToolResponseMessage respuestaHerramienta(AssistantMessage.ToolCall llamada, String resultado) {
        return ToolResponseMessage.builder()
                .responses(List.of(new ToolResponseMessage.ToolResponse(llamada.id(), llamada.name(), resultado)))
                .build();
    }

    // Ejecuta la herramienta con ese nombre. Si la llamada es invalida (un
    // argumento inventado, una lista donde se espera un texto), en lugar del
    // error crudo se devuelve al modelo un mensaje que dice cual es la forma valida.
    private static String ejecutarHerramienta(ToolCallback[] herramientas, AssistantMessage.ToolCall llamada) {
        for (ToolCallback herramienta : herramientas) {
            if (herramienta.getToolDefinition().name().equals(llamada.name())) {
                try {
                    return herramienta.call(llamada.arguments());
                } catch (RuntimeException error) {
                    Throwable causa = error.getCause() != null ? error.getCause() : error;
                    return "Llamada invalida a " + llamada.name() + ": " + causa.getMessage() + "\n"
                            + MENSAJE_ARGUMENTO_VALIDO;
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
