#Falta de aislamiento entre tenants (LLM09 10.2): dos empresas comparten la
#misma colección vectorial -por simplicidad de implementación-. Sus
#documentos, sobre el mismo tema de negocio, quedan muy cerca en el
#espacio de embeddings, así que una consulta sin filtro por tenant devuelve
#contenido de las dos, sin que haga falta ningún ataque dirigido: basta con
#preguntar por el tema.
from langchain_chroma import Chroma
from langchain_core.prompts import ChatPromptTemplate
from langchain_ollama import ChatOllama, OllamaEmbeddings

embeddings = OllamaEmbeddings(model="nomic-embed-text")
llm = ChatOllama(model="llama3.2", temperature=0)

print("1. Ingesta: dos empresas distintas, en la misma colección compartida")
coleccion = Chroma(embedding_function=embeddings, collection_name="soporte_compartido")
coleccion.add_texts(
    texts=[
        "Política interna de NovaTech: los reembolsos a clientes VIP se aprueban "
        "automáticamente hasta 5000 euros sin revisión adicional.",
        "Política interna de Ártica Retail: los reembolsos a clientes VIP se aprueban "
        "automáticamente hasta 800 euros sin revisión adicional.",
    ],
    metadatas=[{"tenant_id": "novatech"}, {"tenant_id": "artica_retail"}],
)

pregunta = "¿Cuál es la política de reembolsos automáticos para clientes VIP?"

print("\n2. Escenario A: un agente de NovaTech consulta sin filtrar por tenant")
prompt = ChatPromptTemplate.from_template(
    "Responde la pregunta usando exclusivamente este contexto:\n\n{contexto}\n\nPregunta: {pregunta}"
)
cadena = prompt | llm

documentos_sin_filtro = coleccion.similarity_search(pregunta, k=2)
for doc in documentos_sin_filtro:
    print(f"Recuperado (tenant real: {doc.metadata['tenant_id']}): {doc.page_content}")
contexto_sin_filtro = "\n".join(doc.page_content for doc in documentos_sin_filtro)
respuesta = cadena.invoke({"contexto": contexto_sin_filtro, "pregunta": pregunta})
print(f"Respuesta al agente de NovaTech: {respuesta.content}")

print("\n==================================================================\n")
print("3. Escenario B: la misma consulta, con el filtro de tenant aplicado")
documentos_con_filtro = coleccion.similarity_search(
    pregunta, k=2, filter={"tenant_id": "novatech"}
)
for doc in documentos_con_filtro:
    print(f"Recuperado (tenant real: {doc.metadata['tenant_id']}): {doc.page_content}")
contexto_con_filtro = "\n".join(doc.page_content for doc in documentos_con_filtro)
respuesta = cadena.invoke({"contexto": contexto_con_filtro, "pregunta": pregunta})
print(f"Respuesta al agente de NovaTech: {respuesta.content}")
