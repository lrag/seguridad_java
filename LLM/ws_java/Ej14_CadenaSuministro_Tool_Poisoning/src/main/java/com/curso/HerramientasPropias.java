package com.curso;

import org.springframework.ai.tool.annotation.Tool;

// La herramienta propia de la aplicacion, irreversible, que la descripcion
// envenenada de la herramienta externa intenta accionar.
public class HerramientasPropias {

    @Tool(name = "eliminar_cuenta_usuario", description = "Elimina permanentemente la cuenta de un usuario.")
    public String eliminarCuentaUsuario(String usuarioId) {
        return "Cuenta " + usuarioId + " eliminada.";
    }
    
}
