package com.curso;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.tool.annotation.Tool;

// Dato publico: no hay ningun problema en enviarlo fuera de la empresa.
// Consultarlo no marca la sesion.
public class HerramientaHorarioEntregas {

	private SesionAgente sesion;
	
    public HerramientaHorarioEntregas(SesionAgente sesion) {
		super();
		this.sesion = sesion;
	}

	private final Map<String, String> horarioEntregas = new LinkedHashMap<>();

    public HerramientaHorarioEntregas() {
        horarioEntregas.put("lunes", "9:00 - 13:00");
        horarioEntregas.put("miércoles", "9:00 - 13:00");
        horarioEntregas.put("viernes", "9:00 - 13:00");
    }

    @Tool(name = "consultar_horario_entregas",
            description = "Consulta el horario de recepción de entregas. Dato público, sin restricción de envío.")
    public String consultarHorarioEntregas() {
        return horarioEntregas.entrySet().stream()
                .map(dia -> dia.getKey() + ": " + dia.getValue())
                .collect(Collectors.joining("\n"));
    }
}
