from langchain_community.vectorstores import Chroma
from langchain_ollama import OllamaEmbeddings, ChatOllama
from langchain_core.prompts import ChatPromptTemplate

print("=== 1. Conectando con Ollama ===")
embeddings = OllamaEmbeddings(model="nomic-embed-text")
llm = ChatOllama(model="llama3.2", temperature=0)


print("\n=== 2. Creando base de datos vectorial en memoria ===")
# Lista con varios documentos
documentos = [
    "DOCUMENTO 1 (Menú): De primer plato hay Gazpacho o Ensalada César. De segundo plato hay Lomo de salmón a la plancha con verduras o Lasaña vegetal. El postre especial de hoy es Tarta de manzana casera.",
    "DOCUMENTO 2 (Horarios): El horario del comedor es de 13:00 a 15:30. Los viernes cierra a las 15:00.",
    "DOCUMENTO 3 (Normativa): No está permitido reservar mesa con antelación. Las bandejas se dejan en el carro gris.",
    "DOCUMENTO 4 (Localización): El comedor está en La Cistérniga, Valladolid."
]

# ChromaDB creará un vector para cada uno de los elementos de la lista
vectorstore = Chroma.from_texts(texts=documentos, embedding=embeddings)

# 3. Pregunta y búsqueda de contexto
pregunta = "¿Qué hay de primer plato en el comedor y cuál es el postre?"
# pregunta = "Puedo reservar mesa con antelación"
# pregunta = "Hoy me apetece algo dulce"
# pregunta = "¿dónde está un pueblo llamado la cisterniga?"
# pregunta = "Quiero comer a las doce"
# pregunta = "Quiero comer a las 14:00 "
# pregunta = "Vivo en Alicante. ¿Qué ruta debo tomar hasta el comedor?"
print(f"\nPregunta del usuario: '{pregunta}'")

docs_recuperados = vectorstore.similarity_search(pregunta, k=1)
contexto = docs_recuperados[0].page_content

print("\n=== 3. Contexto recuperado de la base de datos ===")
print(f"-> {contexto}")

# 4. Invocación del LLM con el contexto recuperado
prompt = ChatPromptTemplate.from_template(
    "Responde a la pregunta del usuario utilizando ÚNICAMENTE el contexto adjunto.\n\n"
    "Contexto:\n{context}\n\n"
    "Pregunta: {question}"
)

chain = prompt | llm

print("\n=== 4. Respuesta generada por Llama 3.2 ===")
respuesta = chain.invoke({"context": contexto, "question": pregunta})
print(f"-> {respuesta.content}")