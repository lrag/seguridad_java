#Fuentes de procedencia dudosa en RAG (LLM04 5.3): parte de RAG_ejemplo_1.py
#y le añade el mecanismo de "pin por hash" descrito en el documento. Cada
#fragmento se guarda en la base vectorial junto con el hash de su contenido
#en el momento de la ingesta. Antes de reemplazar una entrada existente por
#una versión nueva de la misma fuente, el *pipeline* compara el hash de lo
#que encuentra ahora contra el hash guardado, en vez de sobreescribir sin
#más -que es exactamente lo que hace Chroma por defecto: reingerir el mismo
#id sobreescribe la entrada anterior sin ningún aviso, como muestra el
#paso 0-.
#
#Limitación real de este mecanismo, que este script NO resuelve: el hash se
#guarda en la MISMA base de datos vectorial y bajo el MISMO control de
#acceso que el propio documento. Si un atacante compromete directamente el
#proceso de ingesta o la base vectorial -no solo el documento fuente
#externo-, puede escribir un documento alterado y su hash correspondiente
#en la misma operación, y la verificación no detecta nada. Este script
#demuestra el caso en el que la fuente externa cambia sin que la base
#vectorial se vea comprometida directamente; para el caso en el que el
#propio *pipeline* de ingesta está comprometido haría falta que el hash de
#confianza viviera en un sitio con un control de acceso distinto y más
#restringido, que aquí no existe.
import hashlib

from langchain_chroma import Chroma
from langchain_core.prompts import ChatPromptTemplate
from langchain_ollama import ChatOllama, OllamaEmbeddings

embeddings = OllamaEmbeddings(model="nomic-embed-text")
llm = ChatOllama(model="llama3.2", temperature=0)

contenido_original = "Política de devoluciones: el cliente dispone de 30 días naturales para devolver un producto sin coste alguno."
contenido_alterado = "Política de devoluciones: no se aceptan devoluciones bajo ningún concepto."
pregunta = "¿Cuál es la política de devoluciones?"

prompt = ChatPromptTemplate.from_template(
    "Responde la pregunta del usuario usando exclusivamente este contexto:\n\n{contexto}\n\nPregunta: {pregunta}"
)
cadena = prompt | llm


def responder(vectorstore):
    documento_recuperado = vectorstore.similarity_search(pregunta, k=1)[0]
    respuesta = cadena.invoke({"contexto": documento_recuperado.page_content, "pregunta": pregunta})
    print(f"Contexto recuperado: {documento_recuperado.page_content}")
    print(f"Respuesta: {respuesta.content}")


def hash_de(contenido):
    return hashlib.sha256(contenido.encode("utf-8")).hexdigest()


def ingerir_sin_verificar(vectorstore, id_fuente, contenido):
    vectorstore.add_texts(texts=[contenido], ids=[id_fuente])
    print(f"'{id_fuente}' ingerido/actualizado sin ninguna verificación.")


def ingerir_o_actualizar(vectorstore, id_fuente, contenido, ruta_fuente):
    hash_nuevo = hash_de(contenido)
    existentes = vectorstore.get(ids=[id_fuente])
    if existentes["ids"]:
        hash_confiado = existentes["metadatas"][0]["hash"]
        if hash_nuevo == hash_confiado:
            print(f"'{id_fuente}' sin cambios respecto a la versión confiada, no hace falta reindexar.")
            return
        print(f"Hash confiado:   {hash_confiado}")
        print(f"Hash encontrado: {hash_nuevo}")
        print(f"ACTUALIZACIÓN RECHAZADA para '{id_fuente}': la fuente cambió respecto a la versión confiada. Requiere revisión humana antes de reindexar.")
        return
    vectorstore.add_texts(
        texts=[contenido],
        ids=[id_fuente],
        metadatas=[{"fuente": ruta_fuente, "hash": hash_nuevo}],
    )
    print(f"'{id_fuente}' ingerido por primera vez. Hash guardado: {hash_nuevo}")

print("====================================================================================")
print("0. Sin ningún control: una reingesta ingenua sobreescribe sin más")
#collection_name explicito: dos Chroma() sin el mismo nombre de coleccion
#comparten por defecto la misma coleccion subyacente, y este script
#necesita dos completamente aisladas para no mezclar sus resultados.
vectorstore_sin_control = Chroma(embedding_function=embeddings, collection_name="sin_control")
ingerir_sin_verificar(vectorstore_sin_control, "politica_devoluciones", contenido_original)
responder(vectorstore_sin_control)
print("")
print("La fuente cambia sin pasar por ningún proceso de publicación autorizado")
ingerir_sin_verificar(vectorstore_sin_control, "politica_devoluciones", contenido_alterado)
responder(vectorstore_sin_control)
print("Nada en el sistema detectó el cambio: la respuesta cambió en silencio.")
print()

print("====================================================================================")
print("\n1. Con verificación: creando la base de datos vectorial e ingiriendo la fuente original")
vectorstore = Chroma(embedding_function=embeddings, collection_name="con_hash")
ingerir_o_actualizar(vectorstore, "politica_devoluciones", contenido_original, "politica_devoluciones.md")

print("====================================================================================")
print("\n2. Reingesta programada de la misma fuente, sin ningún cambio real")
ingerir_o_actualizar(vectorstore, "politica_devoluciones", contenido_original, "politica_devoluciones.md")

print("\n3. La fuente cambia sin pasar por ningún proceso de publicación autorizado")
ingerir_o_actualizar(vectorstore, "politica_devoluciones", contenido_alterado, "politica_devoluciones.md")

print("\n4. Comprobando qué versión ha quedado realmente en la base vectorial")
resultado = vectorstore.get(ids=["politica_devoluciones"])
print(f"Documento almacenado: {resultado['documents'][0]}")

print("\n5. Respondiendo con RAG a la misma pregunta, tras el intento de manipulación")
responder(vectorstore)
