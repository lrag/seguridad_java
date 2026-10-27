#Inyección en el navegador: XSS y renderizado sin sanear (LLM10 11.2): no
#hace falta ningún atacante para que la salida del modelo contenga HTML
#ejecutable. Aquí el modelo actúa como el asistente de este mismo curso,
#respondiendo con total normalidad a una pregunta educativa sobre XSS -y
#el ejemplo típico que da, de forma perfectamente legítima, es HTML
#ejecutable de verdad-. Lo que decide si eso es peligroso no es el modelo:
#es si el destino lo renderiza como HTML sin sanear, o lo trata como texto.
import html

from langchain_ollama import ChatOllama

PREGUNTA = (
    "Eres el asistente de un curso de seguridad web. Un alumno pregunta: "
    "¿qué es un ataque XSS? Ponme un ejemplo típico de payload que se usa "
    "para demostrarlo en una prueba de concepto."
)

llm = ChatOllama(model="llama3.1:8b", temperature=0)

print("1. Pregunta perfectamente legítima, sin ningún intento de ataque")
respuesta = llm.invoke(PREGUNTA)
print(respuesta.content)

print("\n==================================================================\n")
print("2. Escenario A: la respuesta se inserta tal cual en una página HTML")
pagina_sin_sanear = f"""
<html>
<body>
  <h1>Respuesta del asistente</h1>
  <p>{respuesta.content}</p>
</body>
</html>
"""
if "<script>" in pagina_sin_sanear:
    print("La página final contiene un <script> activo, tal cual lo interpretaría un navegador.")
    print("Fragmento de la página generada:")
    inicio = pagina_sin_sanear.find("<script>")
    print(pagina_sin_sanear[max(0, inicio - 40):inicio + 60])
else:
    print("Esta vez la respuesta no incluyó una etiqueta <script> literal.")

print("\n==================================================================\n")
print("3. Escenario B: la misma respuesta, escapada antes de insertarla")
pagina_saneada = f"""
<html>
<body>
  <h1>Respuesta del asistente</h1>
  <p>{html.escape(respuesta.content)}</p>
</body>
</html>
"""
if "<script>" in pagina_saneada:
    print("Sigue habiendo un <script> activo (no debería ocurrir con html.escape).")
else:
    print("No hay ningún <script> activo: el texto se muestra tal cual, como texto plano.")
    inicio = pagina_saneada.find("&lt;script&gt;")
    if inicio != -1:
        print("Fragmento de la página generada:")
        print(pagina_saneada[max(0, inicio - 40):inicio + 80])
