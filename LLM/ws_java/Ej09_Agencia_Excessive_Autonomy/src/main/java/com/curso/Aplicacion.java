package com.curso;

import java.util.Scanner;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Autonomia excesiva (LLM03 4.4): un agente de limpieza de una carpeta
// compartida, con una herramienta de solo lectura (listar_archivos) y otra
// irreversible (eliminar_archivo), al que se le pide actuar "de forma
// autonoma, sin pedir confirmacion". Reune en un solo proyecto la progresion
// de los tres scripts Python 09_Excessive_Autonomy_ejemplo_*, con una clase
// por caso:
//
// 1. _01_CasoMalPlanteadoSinControl: una sola llamada al modelo, sin bucle ni
//    historial; se ejecuta todo lo que propone. Decide que borrar sin haber
//    visto la lista real: el contraejemplo de los casos 2 y 3.
// 2. _02_CasoProblemaUnaAccionPorTurno: una accion por turno y errores didacticos.
//    Con descripciones ambiguas, el criterio del modelo no es de fiar.
// 3. _03_CasoSolucionConfirmacionHumana: el codigo exige confirmacion humana antes
//    de cada eliminar_archivo, y falla cerrado ante llamadas mal formadas.
//
// En todos los casos la ejecucion automatica de herramientas de Spring AI esta
// desactivada. En los casos 2 y 3 el historial de la conversacion se gestiona
// a mano (UserMessage, AssistantMessage con sus tool calls y ToolResponseMessage
// con cada resultado), igual que el bucle del script Python.
//
// El modelo (llama3.1:8b), la temperature (0) y num_predict (300) estan en
// application.properties.
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
        ChatClient chat = chatClientBuilder.build();
        Scanner teclado = new Scanner(System.in);

        /*
        System.out.println("========================================================");
        System.out.println("1. MAL PLANTEADO: el modelo propone todo el plan de golpe y se ejecuta sin control");
        new _01_CasoMalPlanteadoSinControl().ejecutarCaso(chat);
        */

        /*
        System.out.println("========================================================");
        System.out.println("2. PROBLEMA: una accion por turno, descripciones ambiguas");
        new _02_CasoProblemaUnaAccionPorTurno().ejecutarCaso(chat);
        */

        System.out.println("========================================================");
        System.out.println("3. SOLUCION: confirmacion humana antes de cada eliminacion");
        new _03_CasoSolucionConfirmacionHumana().ejecutarCaso(chat, teclado);
    }
}
