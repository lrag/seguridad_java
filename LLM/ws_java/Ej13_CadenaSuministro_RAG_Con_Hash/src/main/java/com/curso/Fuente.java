package com.curso;

// Un documento de las fuentes del RAG: su id en la base vectorial, la ruta de
// la que se lee y su contenido en el momento de la ingesta.
public record Fuente(String id, String ruta, String contenido) {
}
