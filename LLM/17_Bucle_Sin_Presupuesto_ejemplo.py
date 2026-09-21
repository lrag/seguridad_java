#Consumo descontrolado en bucles de agente (LLM06 7.2): a un agente se le
#pide "sigue hasta que estés satisfecho", sin ningún criterio objetivo de
#parada. Sin presupuesto, es el propio modelo quien decide cuándo terminar
#-y "cuando el modelo decida" puede tardar mucho más, y costar mucho más,
#que con un presupuesto explícito de pasos-. Este script mide el coste real
#acumulado (tokens, segundos) de las dos versiones, con los mismos
#metadatos de rendimiento reales que ya se usaron en 16.
#
#Resultado observado (temperature=0, llama3.1:8b): pedirle que "refine un
#resumen hasta quedar completamente satisfecho" nunca converge por sí
#solo. En ocho turnos seguidos el texto generado crece turno a turno (43,
#46, 59, 77, 87, 108, 138, hasta agotar el limite de 150), sin acercarse
#nunca a responder "FIN". El escenario sin presupuesto acabo consumiendo
#3543 tokens y 26.24s frente a los 284 tokens y 3.22s del escenario con
#presupuesto de 2 pasos -3259 tokens y 23s de mas sin haber llegado un
#solo paso mas cerca de una respuesta terminada-.
from langchain_ollama import ChatOllama
from langchain_core.messages import HumanMessage

TEXTO = "Los perros son mamíferos carnívoros domesticados hace miles de años."

INSTRUCCION = (
    f"Refina este resumen un poco cada vez, mejorándolo: '{TEXTO}' "
    "Cuando de verdad no se te ocurra ninguna mejora más, responde "
    "únicamente con la palabra FIN."
)


def ejecutar_bucle(max_pasos):
    llm = ChatOllama(model="llama3.1:8b", temperature=0, num_predict=150)
    mensajes = [HumanMessage(content=INSTRUCCION)]
    tokens_totales = 0
    tiempo_total = 0.0
    for paso in range(max_pasos):
        respuesta = llm.invoke(mensajes)
        meta = respuesta.response_metadata
        tokens_totales += meta["prompt_eval_count"] + meta["eval_count"]
        tiempo_total += (meta["prompt_eval_duration"] + meta["eval_duration"]) / 1e9
        print(f"Paso {paso + 1}: {meta['eval_count']} tokens generados")
        print(f"Salida: {respuesta.content}")
        if "FIN" in respuesta.content.upper():
            print("El modelo decidió terminar por su cuenta.")
            break
        mensajes.append(respuesta)
        mensajes.append(HumanMessage(content="Sigue refinándolo un poco más."))
    return paso + 1, tokens_totales, tiempo_total


print("1. Escenario A: sin presupuesto explícito, hasta un límite de seguridad de 8 pasos")
pasos_a, tokens_a, tiempo_a = ejecutar_bucle(max_pasos=8)
print(f"Total: {pasos_a} pasos, {tokens_a} tokens, {tiempo_a:.2f}s")

print("\n==================================================================\n")
print("2. Escenario B: con un presupuesto deliberado de 2 pasos")
pasos_b, tokens_b, tiempo_b = ejecutar_bucle(max_pasos=2)
print(f"Total: {pasos_b} pasos, {tokens_b} tokens, {tiempo_b:.2f}s")

print(f"\nDiferencia: {tokens_a - tokens_b} tokens y {tiempo_a - tiempo_b:.2f}s de más sin presupuesto, sin haber avanzado hacia una respuesta mejor.")
