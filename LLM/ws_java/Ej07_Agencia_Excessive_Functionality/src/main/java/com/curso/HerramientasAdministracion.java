package com.curso;

import org.springframework.ai.tool.annotation.Tool;

// Herramientas que la tarea de soporte NO necesita: son la funcionalidad
// excesiva. Llegan al catalogo del agente por comodidad (una plantilla
// generica, una funcion de administracion olvidada), no porque haga falta.
public class HerramientasAdministracion {

    @Tool(name = "eliminar_cuenta_usuario", description = "Elimina permanentemente la cuenta de un usuario.")
    public String eliminarCuentaUsuario(String usuarioId) {
        return "Cuenta " + usuarioId + " eliminada.";
    }

    @Tool(name = "resetear_password_cualquier_usuario", description = "Resetea la contrasena de cualquier usuario del sistema.")
    public String resetearPasswordCualquierUsuario(String usuarioId) {
        return "Password de " + usuarioId + " reseteado.";
    }
}
