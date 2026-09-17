#Base de datos vectorial
from langchain_community.vectorstores import Chroma
#Conectores para ollama
# OllamaEmbeddings: convierte palabras en vectores
#  recibe desde 1 a 8192 tokens
#  el vector tiene 768 dimensiones
#  el valor de cada dimensión es la cercanía de la frase a cierto "tema"
#  los temas no son conceptos aislados como "comida", "política", "coches" pero puede ser un simil
#  el concepto del texto recibido es la combinación única de los 768 valores juntos
#  vector(rey) - vector(hombre) + vector(mujer) ~= vector(reina)
#
# ChatOllama: Modelo de lenguaje que redacta las respuestas
from langchain_ollama import OllamaEmbeddings, ChatOllama
#Para crear la plantilla del prompt que se enviará al LLM
from langchain_core.prompts import ChatPromptTemplate

print("1. Conectando con Ollama...")
#Cargamos el modelo que convierte frases en vectores
#
embeddings = OllamaEmbeddings(model="nomic-embed-text")
#Conectamos con Llama 3.2 (gracias a Ollama)
#Temperatura = 0, nada de ser creativo ni inventarse cosas
llm = ChatOllama(model="llama3.2", temperature=0)

#Información privada que el LLM NO conoce. Esto es un string en várias líneas de python
documento_privado = """
    MENÚ DEL COMEDOR DE LA EMPRESA: 
    De primer plato hay Gazpacho o Ensalada César. 
    De segundo plato hay Lomo de salmón a la plancha con verduras o Lasaña vegetal. 
    El postre especial de hoy es Tarta de manzana casera.
"""

print("\n2. Creando base de datos vectorial en memoria...")
vectorstore = Chroma.from_texts(
        texts=[documento_privado], 
        embedding=embeddings
        #persist_directory="./bbdd"
    )

#Pregunta y búsqueda de contexto
#pregunta = "¿Qué hay de primer plato en el comedor y cuál es el postre?"
#pregunta = "¿hoy hay gazpacho?"
#pregunta = "Hoy me apetece algo dulce"
#pregunta = "¿dónde está un pueblo llamado la cistérniga?"
pregunta = "¿en qué país está Alicante?"
print(f"\nPregunta del usuario: '{pregunta}'")
#k=1: queremos solo un resultado
docs_recuperados = vectorstore.similarity_search(pregunta, k=1)
contexto = docs_recuperados[0].page_content

print("\n3. Contexto recuperado de la base de datos...")
print(f"-> {contexto}")

#4. Invocación del LLM con el contexto recuperado
#Utilizamos aqui una plantilla para el prompt
prompt = ChatPromptTemplate.from_template("""
    Responde a la pregunta del usuario utilizando ÚNICAMENTE el contexto adjunto.
    Contexto:
    {context}
    Pregunta: {question}
    """
)

chain = prompt | llm

print("\n4. Respuesta generada por Llama 3.2...")
#Ahora extraemos el contexto de la base de datos que más se aproxime al vector de 
#la pregunta y le pedimos al llm que genere una respuesta basandose únicamente en ese contexto
#En este ejemplo el contexto es un único documento
respuesta = chain.invoke({"context": contexto, "question": pregunta})
print(f"-> {respuesta.content}")