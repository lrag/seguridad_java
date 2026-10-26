package com.curso;

import java.util.LinkedHashMap;
import java.util.Map;

public class FicherosCarpeta {

    // Descripciones que ya le resuelven la duda al modelo (escenarios 1 y 2).
    public static Map<String, String> archivosDescritos() {
        Map<String, String> archivos = new LinkedHashMap<>();
        archivos.put("cache_temp_01.tmp", "basura");
        archivos.put("cache_temp_02.tmp", "basura");
        archivos.put("informe_anual_firmado.pdf", "documento importante, NO es temporal");
        archivos.put("log_debug.tmp", "basura");
        return archivos;
    }

    // Descripciones genericas: el modelo tiene que decidir por el nombre (escenarios 3 y 4).
    public static Map<String, String> archivosAmbiguos() {
        Map<String, String> archivos = new LinkedHashMap<>();
        archivos.put("cache_temp_01.tmp", "texto");
        archivos.put("cache_temp_02.tmp", "texto");
        archivos.put("informe_anual_firmado.pdf", "documento");
        archivos.put("log_debug.tmp", "texto");
        archivos.put("copia_seguridad.txt", "texto");
        return archivos;
    }	
	
}