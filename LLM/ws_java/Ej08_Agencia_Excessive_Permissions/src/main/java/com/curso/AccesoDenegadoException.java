package com.curso;

// Se lanza cuando una herramienta recibe un usuario distinto del de la sesion
// autenticada: el modelo ha intentado operar sobre datos ajenos.
public class AccesoDenegadoException extends RuntimeException {

    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
