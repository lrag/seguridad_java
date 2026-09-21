#Entradas diseñadas para maximizar el coste de cómputo (LLM06 7.3): el
#coste de procesar una entrada depende del NÚMERO de tokens, no de si esos
#tokens aportan información real. Este script compara el tiempo de
#procesar dos entradas con un número de tokens similar: una repetitiva y
#sin contenido real, y otra con texto variado e informativo.
#
#Nota metodológica importante: la primera versión de este script reutilizaba
#literalmente el mismo texto informativo en cada ejecución, y el tiempo de
#procesarlo salía casi cero (0.04s) frente a los ~4.7s del texto
#degenerado. Parecía un hallazgo real -"el contenido informativo es mucho
#más barato"-, pero era un artefacto: Ollama cachea el procesamiento de
#prompts ya vistos, y ese texto se había repetido muchas veces en la misma
#sesión. Con contenido informativo genuinamente nuevo cada vez (las frases
#se barajan y se numeran en cada ejecución), el tiempo de procesarlo es
#prácticamente idéntico al del texto degenerado (en las pruebas finales,
#en torno a 4.7-4.9s para ambos, con un número de tokens de entrada
#comparable: ~2034 frente a ~2050), confirmando lo que la sección predice:
#el coste depende del número de tokens, no del contenido.
import random

from langchain_ollama import ChatOllama

PREGUNTA = "Responde solo con OK, no hace falta que digas nada más."

HECHOS = [
    "El sistema solar está formado por el sol y los cuerpos que orbitan a su alrededor.",
    "Mercurio es el planeta más cercano al sol y el más pequeño del sistema solar.",
    "Venus tiene la atmósfera más densa de todos los planetas rocosos.",
    "La Tierra es el único planeta conocido que alberga vida.",
    "Marte tiene el volcán más grande conocido, llamado Monte Olimpo.",
    "Júpiter es el planeta más grande del sistema solar.",
    "Saturno es conocido por sus prominentes anillos de hielo y roca.",
    "Urano gira prácticamente de lado respecto al resto de planetas.",
    "Neptuno es el planeta más alejado del sol.",
    "Plutón fue reclasificado como planeta enano en 2006.",
]


def texto_informativo_fresco():
    #Cada llamada baraja el orden y antepone un numero distinto a cada
    #frase, para que la secuencia de tokens sea siempre nueva y no pueda
    #beneficiarse de ningun cache de un prompt ya procesado antes.
    frases = [f"{random.randint(0, 10**9)}: {frase}" for frase in HECHOS]
    bloque = list(frases)
    random.shuffle(bloque)
    return " ".join(bloque * 30)


def texto_degenerado_fresco():
    nonce = random.randint(0, 10**9)
    return f"(consulta {nonce})\n" + "lorem ipsum dolor sit amet " * 400


def medir(nombre, texto):
    llm = ChatOllama(model="llama3.1:8b", temperature=0, num_predict=10)
    respuesta = llm.invoke(f"{texto}\n\n{PREGUNTA}")
    meta = respuesta.response_metadata
    tokens_entrada = meta["prompt_eval_count"]
    tiempo_entrada = meta["prompt_eval_duration"] / 1e9
    print(f"{nombre}: {tokens_entrada} tokens de entrada, procesados en {tiempo_entrada:.2f}s")


print("1. Entrada degenerada (la misma frase sin sentido repetida cientos de veces)")
medir("Degenerada", texto_degenerado_fresco())

print("\n2. Entrada con contenido real y variado, de longitud comparable")
medir("Informativa", texto_informativo_fresco())
