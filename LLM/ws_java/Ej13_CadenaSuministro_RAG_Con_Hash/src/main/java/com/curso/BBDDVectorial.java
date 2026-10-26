package com.curso;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;

// La base vectorial del RAG, con las dos formas de ingerir las fuentes: sin
// comprobacion (lo que hace la base por defecto) y con comprobacion por hash.
//
// SimpleVectorStore es una base vectorial en memoria, el equivalente del
// Chroma en memoria del script Python. Cada instancia de BBDDVectorial crea la
// suya, asi que dos instancias no mezclan sus documentos.
public class BBDDVectorial {

    // Similitud minima (de 0 a 1) para que un fragmento entre en el contexto.
    private static final double UMBRAL_SIMILITUD = 0.6;

    private final VectorStore vectorStore;

    // Entradas del informe de revision: una por cada documento en cuarentena.
    private final List<String> pendientes = new ArrayList<>();

    public BBDDVectorial(EmbeddingModel embeddingModel) {
        this.vectorStore = SimpleVectorStore.builder(embeddingModel).build();
    }

    // Indexa todo lo que recibe. Un documento con un id que ya existe
    // reemplaza la entrada anterior sin ningun aviso. Guarda el hash de cada
    // documento: si esta es la carga inicial, esos hashes son la version de
    // confianza contra la que se compararan las reingestas con comprobacion.
    public void ingerirSinComprobar(List<Fuente> fuentes) {
        for (Fuente fuente : fuentes) {
            guardar(fuente, hashDe(fuente.contenido()));
            System.out.println("'" + fuente.id() + "' ingerido sin comprobación.");
        }
    }

    // Compara el hash de cada documento con el de la version de confianza:
    // - Sin cambios: no hace nada.
    // - Modificado: NO se reindexa; la version de confianza sigue activa.
    // - Nuevo (un id que no estaba): NO se indexa; no hay version de confianza
    //   con la que compararlo.
    // Los modificados y los nuevos quedan en cuarentena, anotados en el informe
    // como pendientes de aprobacion. Es un control de prevencion; el precio es
    // que un cambio legitimo tampoco llega al RAG hasta que alguien lo apruebe.
    public void ingerirConComprobacion(List<Fuente> fuentes) {
        for (Fuente fuente : fuentes) {
            String hashNuevo = hashDe(fuente.contenido());
            Document existente = buscarPorId(fuente.id());
            if (existente == null) {
                pendientes.add("NUEVO '" + fuente.id() + "' (" + fuente.ruta() + ")\n"
                        + "Hash propuesto:      " + hashNuevo + "\n"
                        + "Contenido propuesto: " + fuente.contenido());
                System.out.println("'" + fuente.id() + "' es nuevo: NO se indexa, queda en el informe pendiente de aprobación.");
                continue;
            }
            String hashConfiado = (String) existente.getMetadata().get("hash");
            if (hashNuevo.equals(hashConfiado)) {
                System.out.println("'" + fuente.id() + "' sin cambios respecto a la versión de confianza, no hace falta reindexar.");
                continue;
            }
            pendientes.add("MODIFICADO '" + fuente.id() + "' (" + fuente.ruta() + ")\n"
                    + "Hash actual:         " + hashConfiado + "\n"
                    + "Contenido actual:    " + existente.getText() + "\n"
                    + "Hash propuesto:      " + hashNuevo + "\n"
                    + "Contenido propuesto: " + fuente.contenido());
            System.out.println("'" + fuente.id() + "' ha cambiado: NO se reindexa, queda en el informe pendiente de aprobación.");
        }
    }

    // El informe para la revision humana: los documentos en cuarentena.
    public String informe() {
        StringBuilder informe = new StringBuilder();
        informe.append("Informe de reingesta (").append(LocalDateTime.now().withNano(0)).append(")\n");
        informe.append("Documentos pendientes de aprobación: ").append(pendientes.size()).append("\n");
        for (String pendiente : pendientes) {
            informe.append("\n").append(pendiente).append("\n");
        }
        return informe.toString();
    }

    // Hasta 3 fragmentos parecidos a la pregunta, de mas a menos parecido: la
    // parte de recuperacion del RAG.
    //
    // topK por si solo no filtra por relevancia: devuelve los k mas parecidos
    // aunque no tengan nada que ver con la pregunta (sin umbral, el alta de
    // clientes, con similitud ~0,47, entraria en el contexto de una pregunta
    // sobre devoluciones). El umbral de similitud descarta lo que no llegue a
    // ese minimo. Su valor no es universal: depende del modelo de embeddings
    // y de los textos, y hay que ajustarlo a mano mirando las similitudes
    // reales. Demasiado alto deja fuera fragmentos utiles; demasiado bajo
    // deja entrar ruido.
    public List<Document> recuperar(String pregunta) {
        return vectorStore.similaritySearch(
        		SearchRequest.builder()
        		.query(pregunta)
        		.topK(3)
        		.similarityThreshold(UMBRAL_SIMILITUD)
        		.build());
    }

    // La interfaz VectorStore no tiene un "get por id" como el de Chroma: se
    // busca filtrando por el id guardado en los metadatos. El texto de la
    // consulta da igual, el filtro deja como mucho un documento.
    private Document buscarPorId(String idFuente) {
        List<Document> encontrados = vectorStore.similaritySearch(SearchRequest.builder()
                .query(idFuente)
                .topK(1)
                .similarityThresholdAll()
                .filterExpression("id_fuente == '" + idFuente + "'")
                .build());
        return encontrados.isEmpty() ? null : encontrados.get(0);
    }

    private void guardar(Fuente fuente, String hash) {
        vectorStore.add(List.of(Document.builder()
                .id(fuente.id())
                .text(fuente.contenido())
                .metadata(Map.of("id_fuente", fuente.id(), "fuente", fuente.ruta(), "hash", hash))
                .build()));
    }

    private static String hashDe(String contenido) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(contenido.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
