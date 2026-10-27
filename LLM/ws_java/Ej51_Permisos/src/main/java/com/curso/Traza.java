package com.curso;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

// Las herramientas que ha invocado el modelo durante la peticion web actual,
// para mostrarlas en la pagina. @RequestScope: una traza nueva por peticion,
// asi no se mezclan las de dos usuarios. Cada llamada guarda tambien en nombre
// de quien se ejecuto: el usuario del SecurityContext en ese momento.
@Component
@RequestScope
public class Traza {

    public record Llamada(String herramienta, String resultado, boolean denegada, String enNombreDe) {
    }

    private final List<Llamada> llamadas = new ArrayList<>();

    public void anotar(String herramienta, String resultado, boolean denegada) {
        String enNombreDe = SecurityContextHolder.getContext().getAuthentication().getName();
        llamadas.add(new Llamada(herramienta, resultado, denegada, enNombreDe));
    }

    public List<Llamada> getLlamadas() {
        return llamadas;
    }
}
