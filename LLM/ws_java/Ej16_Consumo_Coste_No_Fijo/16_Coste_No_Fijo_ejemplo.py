#Consumo descontrolado (LLM06 7.1): el coste de una petición a un LLM no es
#fijo, crece con los tokens de entrada y con los de salida. En vez de
#asumir esa relación, este script la mide de verdad: Ollama devuelve
#metadatos reales de rendimiento en cada respuesta -tokens procesados,
#tokens generados, tiempo de cada fase-, contra el mismo modelo que usa el
#resto de scripts de este curso.
#
#Resultado observado (llama3.1:8b, GTX 1070): al crecer la entrada de 42 a
#422 y a 2022 tokens (misma pregunta, mismo num_predict=30), el tiempo de
#procesar la entrada sube de 0.23s a 0.89s y a 3.76s -crece con el tamaño
#del contexto, mientras la salida se mantiene igual-. Al crecer
#num_predict de 20 a 200 y a 800 (misma entrada corta), el tiempo de
#generar la salida sube de 0.65s a 6.83s y a 16.35s. Ninguna de las dos
#cifras es fija: ambas escalan con lo que decide quien escribe el prompt,
#no con nada que el servidor controle por su cuenta si no impone un límite.
from langchain_ollama import ChatOllama

PARRAFO = "El comedor de la empresa abre de nueve a cinco, de lunes a viernes. "
PREGUNTA = "Resume en una frase el horario del comedor."


def medir(prompt, num_predict):
    llm = ChatOllama(model="llama3.1:8b", temperature=0, num_predict=num_predict)
    respuesta = llm.invoke(prompt)
    meta = respuesta.response_metadata
    tokens_entrada = meta["prompt_eval_count"]
    tokens_salida = meta["eval_count"]
    tiempo_entrada = meta["prompt_eval_duration"] / 1e9
    tiempo_salida = meta["eval_duration"] / 1e9
    print(f"Tokens de entrada: {tokens_entrada} (procesados en {tiempo_entrada:.2f}s)")
    print(f"Tokens de salida: {tokens_salida} (generados en {tiempo_salida:.2f}s)")
    print(f"Salida: {respuesta.content}")


print("1. Variando el tamaño de la entrada, con la misma pregunta y el mismo límite de salida")
for repeticiones in [1, 20, 100]:
    contexto = PARRAFO * repeticiones
    print(f"\nPárrafo repetido {repeticiones} veces:")
    medir(f"{contexto}\n\n{PREGUNTA}", num_predict=30)

print("\n==================================================================\n")
print("2. Variando el límite de salida solicitado, con la misma entrada corta")
for limite in [20, 200, 800]:
    print(f"\nnum_predict={limite}:")
    medir("Cuenta una breve historia sobre un viaje en tren.", num_predict=limite)
