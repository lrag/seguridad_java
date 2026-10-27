package com.curso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Agencia excesiva por permisos (relacionado con LLM03 4.3 y Ej08), resuelta
// con Spring Security: el agente no tiene permisos propios, actua en nombre
// del usuario de la sesion. Las herramientas que invoca el modelo
// (HerramientasPedidos) no contienen logica de negocio: llaman a
// ServicioPedidos, cuyos metodos llevan @PreAuthorize. Como Spring AI ejecuta
// las herramientas en el mismo hilo de la peticion web, la comprobacion se
// hace con el usuario autenticado, no con el "agente".
//
// Usuarios (SeguridadConfig):
// - "a" / "a", rol USUARIO: solo lectura (listar y consultar pedidos).
// - "b" / "b", rol EMPLEADO: todos los permisos (tambien eliminar pedidos).
//
// Resultado esperado: el mismo ticket con una instruccion inyectada que pide
// eliminar un pedido no consigue nada si lo envia un usuario -el servicio
// rechaza el borrado aunque el modelo lo haya pedido-, pero si lo envia un
// empleado el pedido se elimina. La inyeccion sigue funcionando sobre el
// modelo en los dos casos; lo que cambia es lo que el usuario tiene permitido
// hacer. Aqui se acepta porque nos fiamos de los empleados.
//
// Resultado observado (temperature 0, qwen2.5:7b) con el ticket inyectado: el
// modelo invoca eliminar_pedido(555) y consultar_pedido(201) con los dos
// usuarios. Con "a" el borrado queda DENEGADO y el pedido sigue en la tabla;
// con "b" se ejecuta y desaparece. Ademas, la respuesta a "a" llego a decir
// que el pedido 555 "ha sido eliminado correctamente" y, dos frases despues,
// que se denego el acceso: lo que dice el modelo no es fiable, lo que cuenta
// es lo que ha hecho el servicio (la traza y la tabla de pedidos).
//
// Segunda variante (ControladorTicketsAgente, boton "Enviar como agente IA"):
// el ticket se procesa con la identidad propia del agente (IdentidadAgenteIA,
// rol AGENTE_IA, solo lectura), sea quien sea el usuario que lo envia. Asi el
// resultado no depende de quien envie la peticion: ni siquiera el ticket de un
// empleado consigue que el agente elimine nada.
//
// En el navegador: http://localhost:8080 (redirige al formulario de login por
// defecto de Spring Security).
//
// El modelo (qwen2.5:7b), la temperature (0) y el puerto (8080) estan en
// application.properties.
@SpringBootApplication
public class Aplicacion {

    public static void main(String[] args) {
        SpringApplication.run(Aplicacion.class, args);
    }
}
