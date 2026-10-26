package com.curso;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.tool.annotation.Tool;

// Herramientas del agente de limpieza sobre una carpeta simulada: un mapa
// nombre de archivo -> descripcion. Cada escenario crea su propia instancia,
// asi que empieza siempre con la carpeta completa.
public class HerramientasArchivos {

    private final Map<String, String> archivos;

    public HerramientasArchivos(Map<String, String> archivosIniciales) {
        this.archivos = new LinkedHashMap<>(archivosIniciales);
    }

    @Tool(name = "listar_archivos",
            description = "Lista los archivos de la carpeta junto con una breve descripcion de cada uno.")
    public String listarArchivos() {
        return archivos.entrySet().stream()
                .map(archivo -> archivo.getKey() + ": " + archivo.getValue())
                .collect(Collectors.joining("\n"));
    }

    @Tool(name = "eliminar_archivo",
            description = "Elimina permanentemente un archivo de la carpeta por su nombre exacto.")
    public String eliminarArchivo(String nombre) {
        // Spring AI pasa null si el modelo no envia "nombre" (por ejemplo, si
        // inventa un argumento "archivos" con una lista): se trata como llamada
        // invalida, igual que haria la validacion de argumentos en Python.
        if (nombre == null) {
            throw new IllegalArgumentException("falta el argumento obligatorio \"nombre\"");
        }
        if (archivos.remove(nombre) != null) {
            return "Archivo " + nombre + " eliminado.";
        }
        return "Archivo " + nombre + " no encontrado.";
    }

    public Iterable<String> archivosQueQuedan() {
        return archivos.keySet();
    }
}
