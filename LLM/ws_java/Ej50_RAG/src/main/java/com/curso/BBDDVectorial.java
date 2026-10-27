package com.curso;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

// La base vectorial del RAG. SimpleVectorStore es una base vectorial en
// memoria, el equivalente del Chroma en memoria del script Python. Los
// documentos se indexan al arrancar la aplicacion (hace falta Ollama en
// marcha para calcular sus embeddings).
@Component
public class BBDDVectorial {

    private static final List<String> DOCUMENTOS = List.of(
            "DOCUMENTO 1 (Menú): De primer plato hay Gazpacho o Ensalada César. De segundo plato hay Lomo de "
                    + "salmón a la plancha con verduras o Lasaña vegetal. El postre especial de hoy es Tarta de "
                    + "manzana casera.",
            "DOCUMENTO 2 (Horarios): El horario del comedor es de 13:00 a 15:30. Los viernes cierra a las 15:00.",
            "DOCUMENTO 3 (Normativa): No está permitido reservar mesa con antelación. Las bandejas se dejan en "
                    + "el carro gris.",
            "DOCUMENTO 4 (Localización): El comedor está en La Cistérniga, Valladolid.");

    private final VectorStore vectorStore;

    public BBDDVectorial(EmbeddingModel embeddingModel) {
        this.vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        ingerir(DOCUMENTOS);
    }

    // Crea un vector (embedding) por cada documento y lo guarda.
    private void ingerir(List<String> documentos) {
        List<Document> documentosVectoriales = new ArrayList<>();
        for (String documento : documentos) {
            documentosVectoriales.add(new Document(documento));
        }
        vectorStore.add(documentosVectoriales);
        System.out.println(documentos.size() + " documentos indexados.");
    }

    // El documento mas parecido a la pregunta (k=1, como en el script).
    public Document recuperar(String pregunta) {
        return vectorStore.similaritySearch(
                SearchRequest.builder().query(pregunta).topK(1).build()).get(0);
    }
}
