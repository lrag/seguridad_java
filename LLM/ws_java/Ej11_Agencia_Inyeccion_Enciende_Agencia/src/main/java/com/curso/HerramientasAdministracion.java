package com.curso;

import org.springframework.ai.tool.annotation.Tool;

// La herramienta irreversible que la inyeccion del ticket intenta accionar.
public class HerramientasAdministracion {

    @Tool(name = "eliminar_cuenta_usuario", description = "Elimina permanentemente la cuenta de un usuario.")
    public String eliminarCuentaUsuario(String usuarioId) {
        return "Cuenta " + usuarioId + " eliminada.";
    }
}
