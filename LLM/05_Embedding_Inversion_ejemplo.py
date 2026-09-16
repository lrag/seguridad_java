#Demo de "inversion de embeddings" por similitud, sin descifrar el vector letra
#a letra: un atacante que solo tiene el embedding de un documento secreto (por
#ejemplo, robado de una base vectorial) prueba una lista de frases candidatas y
#se queda con la mas parecida por similitud coseno. No es una inversion real
#(eso requeriria un modelo decodificador tipo vec2text), pero demuestra que el
#vector conserva suficiente informacion semantica como para identificar el
#contenido original sin haberlo visto nunca en texto plano.
#
#Pasos de un ataque real (a diferencia del diccionario de 6 frases de este demo):
#1. Exfiltrar los vectores objetivo de una base vectorial mal asegurada (backup
#   filtrado, API sin autenticacion, instancia mal configurada, insider).
#2. Identificar que modelo de embeddings los generó (se suele deducir por el
#   numero de dimensiones o por metadatos filtrados junto a los vectores),
#   porque comparar solo funciona si las candidatas se embeben con ese mismo
#   modelo.
#3. Construir un corpus de candidatas mucho mayor que el de este script: registros
#   publicos filtrados de organizaciones similares, o plantillas generadas por
#   fuerza bruta combinando campos probables (nombres, cifras, ciudades).
#4. Calcular el embedding de cada candidata con el modelo identificado en el
#   paso 2, para que los vectores sean comparables entre si.
#5. Comparar cada vector robado contra todas las candidatas. A esa escala ya no
#   vale un bucle como el de este script: se usa un indice de busqueda por
#   vecinos mas cercanos (FAISS, HNSW) para no comparar uno a uno.
#6. Quedarse con las candidatas mas similares como mejor reconstruccion
#   aproximada de cada vector robado.
#7. Opcional, para acercarse mas al texto exacto: tomar las candidatas mejor
#   puntuadas, generar variaciones pequenas de ellas, volver a embeberlas y
#   repetir la comparacion, en vez de conformarse con la primera pasada.
#8. La busqueda por diccionario no escala a texto libre arbitrario: por eso la
#   investigacion real (vec2text) entrena un modelo que va directamente del
#   vector al texto aproximado, sin necesitar un diccionario de candidatas.
import math

from langchain_ollama import OllamaEmbeddings

embeddings = OllamaEmbeddings(model="nomic-embed-text")


def similitud_coseno(a, b):
    producto_punto = sum(x * y for x, y in zip(a, b))
    norma_a = math.sqrt(sum(x * x for x in a))
    norma_b = math.sqrt(sum(y * y for y in b))
    return producto_punto / (norma_a * norma_b)


print("1. Documento secreto (el atacante sólo tiene el embedding, nunca el texto)")
secreto = "El empleado Marcos Diaz tiene un salario anual de 54000 euros y vive en Valladolid."
embedding_secreto = embeddings.embed_query(secreto)
print(f"Vector de {len(embedding_secreto)} dimensiones, texto NO disponible para el atacante")

print("\n2. Diccionario de frases candidatas del atacante")
candidatos = [
    "El empleado Marcos Diaz tiene un salario anual de 54000 euros y vive en Valladolid.",
    "Marcos Diaz cobra 54000 euros al ano y reside en Valladolid.",
    "El empleado Marcos Diaz gana un salario de 30000 euros y vive en Madrid.",
    "La empleada Laura Perez tiene un salario anual de 54000 euros y vive en Sevilla.",
    "El comedor de la empresa sirve gazpacho los martes.",
    "La capital de Francia es Paris.",
]
for candidato in candidatos:
    print(f"- {candidato}")

print("\n3. Comparando el embedding secreto contra cada candidata")
resultados = []
for candidato in candidatos:
    embedding_candidato = embeddings.embed_query(candidato)
    similitud = similitud_coseno(embedding_secreto, embedding_candidato)
    resultados.append((similitud, candidato))

resultados.sort(reverse=True)
for similitud, candidato in resultados:
    print(f"{similitud:.4f}  {candidato}")

print("\n4. Mejor reconstruccion aproximada del atacante, sin haber leido el original")
mejor_similitud, mejor_candidato = resultados[0]
print(f"{mejor_candidato} (similitud {mejor_similitud:.4f})")
