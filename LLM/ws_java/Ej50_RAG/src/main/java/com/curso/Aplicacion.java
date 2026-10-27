package com.curso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// RAG basico (port de RAG_ejemplo_2.py), sin relacion directa con el OWASP
// Top 10: la base vectorial en memoria con los cuatro documentos del comedor
// (BBDDVectorial), el RAG que recupera el documento mas parecido a la
// pregunta y responde con el (RAG), y dos formas de probarlo:
// - En el navegador: http://localhost:8080 (ControladorWeb).
// - Desde Postman: POST http://localhost:8080/api/consulta con el cuerpo JSON
//   {"pregunta": "..."} (ControladorRest).
//
// El modelo (llama3.2, como en el script), la temperature (0), el modelo de
// embeddings (nomic-embed-text) y el puerto (8080) estan en
// application.properties.
@SpringBootApplication
public class Aplicacion {

    public static void main(String[] args) {
        SpringApplication.run(Aplicacion.class, args);
    }
}
