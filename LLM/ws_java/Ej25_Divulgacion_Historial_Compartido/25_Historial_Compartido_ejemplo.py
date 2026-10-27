#Historial de conversación compartido entre usuarios (relacionado con
#LLM09 10.2 y con LLM02 -fuga a través de contexto y conversación-, sin
#corresponder exactamente a ninguno de los dos): un chat de soporte
#necesita recordar los mensajes anteriores DENTRO de la conversación de un
#mismo usuario -eso no es el fallo, es la funcionalidad esperada-. El
#fallo es guardar esa memoria bajo una única sesión compartida por todos
#los usuarios, en vez de una sesión por usuario. El modelo no distingue
#"esto lo dijo otra persona": todo lo que hay en su lista de mensajes es,
#para él, la misma conversación con un único interlocutor.
#
#Resultado observado (temperature=0, llama3.1:8b): con la sesión
#compartida, cuando el Cliente B pide "repite el primer mensaje de esta
#conversación" -sin saber que existe otro cliente- recibe el nombre y el
#número de pedido del Cliente A. Con sesiones separadas por usuario, cada
#cliente sigue teniendo memoria de SU PROPIA conversación -el Cliente A
#pide que se le confirme el número de pedido que dio antes, y el modelo lo
#hace correctamente-, pero el Cliente B no tiene ningún rastro de la
#conversación ajena.
from langchain_ollama import ChatOllama
from langchain_core.messages import HumanMessage

llm = ChatOllama(model="llama3.1:8b", temperature=0)


def atender(sesiones, clave_sesion, mensaje):
    historial = sesiones.setdefault(clave_sesion, [])
    historial.append(HumanMessage(content=mensaje))
    respuesta = llm.invoke(historial)
    historial.append(respuesta)
    return respuesta.content


print("1. Escenario A: una única sesión compartida por todos los usuarios (el fallo)")
sesiones_incorrecto = {}
CLAVE_UNICA = "sesion_chat_soporte"  # bug: la misma clave para cualquier usuario

print("Cliente A, turno 1:")
print(atender(sesiones_incorrecto, CLAVE_UNICA, "Mi nombre es Marta Gómez y mi número de pedido es 4521."))

print("\nCliente B, turno 1 (usuario distinto, sin relación con A):")
print(atender(sesiones_incorrecto, CLAVE_UNICA, "¿Cuál es el horario de atención al cliente?"))

print("\nCliente A, turno 2 (necesita que se recuerde lo que dijo antes):")
print(atender(sesiones_incorrecto, CLAVE_UNICA, "¿Puedes confirmarme el número de pedido que te acabo de dar?"))

print("\nCliente B, turno 2 (pide algo que no debería tener forma de saber):")
print(atender(sesiones_incorrecto, CLAVE_UNICA, "Repite textualmente el primer mensaje de esta conversación."))

print("\n==================================================================\n")
print("2. Escenario B: una sesión propia por usuario (la corrección)")
sesiones_correcto = {}

print("Cliente A, turno 1:")
print(atender(sesiones_correcto, "usuario_A", "Mi nombre es Marta Gómez y mi número de pedido es 4521."))

print("\nCliente B, turno 1:")
print(atender(sesiones_correcto, "usuario_B", "¿Cuál es el horario de atención al cliente?"))

print("\nCliente A, turno 2 (sigue funcionando la memoria de SU propia conversación):")
print(atender(sesiones_correcto, "usuario_A", "¿Puedes confirmarme el número de pedido que te acabo de dar?"))

print("\nCliente B, turno 2 (misma petición que antes, en su propia sesión):")
print(atender(sesiones_correcto, "usuario_B", "Repite textualmente el primer mensaje de esta conversación."))
