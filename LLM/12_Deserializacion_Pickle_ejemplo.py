#Modelos de origen no verificado (LLM04 5.2): un fichero de pesos en formato
#pickle clasico de PyTorch (.pt/.bin) no es un contenedor de datos inerte.
#Pickle reconstruye objetos de Python arbitrarios, y esa reconstruccion
#puede incluir la llamada a cualquier funcion. "Cargar un modelo" con este
#formato es, en la practica, ejecutar lo que el fichero decida ejecutar,
#antes incluso de que nadie llegue a usar el modelo para nada. El payload
#de este ejemplo es deliberadamente inofensivo -solo imprime un mensaje-,
#pero podria ser cualquier cosa con los mismos permisos que el proceso que
#carga el fichero.
#
#Resultado observado (torch 2.5.1): "weights_only=False" sigue siendo el
#valor por defecto de torch.load en esta version -el propio torch avisa por
#FutureWarning de que cambiara a "True" en una version futura-, asi que dar
#por hecho que una version reciente de PyTorch ya es segura por defecto es,
#hoy por hoy, incorrecto: hay que pedir la proteccion explicitamente.
#weights_only=True si bloquea la carga de este objeto malicioso.
import os

import torch
from safetensors.torch import load_file, save_file


class CargaMaliciosa:
    def __reduce__(self):
        #__reduce__ se invoca al DESERIALIZAR el objeto, no al usarlo despues.
        #Devuelve (funcion, argumentos): pickle llama a funcion(*argumentos)
        #en el momento mismo de la carga.
        return (
            print,
            ("[PAYLOAD] Esto se ha ejecutado solo por cargar el fichero, antes de usar ningun 'modelo'.",),
        )


print("1. Guardando un objeto malicioso con torch.save, como si fuera un checkpoint")
torch.save(CargaMaliciosa(), "modelo_falso.pt")
print("Escrito modelo_falso.pt: parece un checkpoint de PyTorch cualquiera")

print("\n2. Cargando con torch.load por defecto")
resultado = torch.load("modelo_falso.pt")
print(f"Valor devuelto: {resultado}")

print("\n3. Cargando con torch.load(weights_only=False), la opción insegura explícita")
resultado = torch.load("modelo_falso.pt", weights_only=False)
print(f"Valor devuelto: {resultado}")

print("\n4. Cargando con torch.load(weights_only=True), la protección real")
try:
    resultado = torch.load("modelo_falso.pt", weights_only=True)
    print(f"Valor devuelto: {resultado}")
except Exception as error:
    print(f"Carga bloqueada: {type(error).__name__}")

print("\n5. Contraste: guardando y cargando tensores reales en formato safetensors")
pesos_reales = {"capa1.peso": torch.rand(2, 2)}
save_file(pesos_reales, "modelo_real.safetensors")
pesos_cargados = load_file("modelo_real.safetensors")
print(f"Contenido cargado: {pesos_cargados}")
print("safetensors solo sabe describir tensores: no existe ninguna forma de que")
print("este fichero ejecute nada al cargarse, sea cual sea su contenido.")

del pesos_cargados
os.remove("modelo_falso.pt")
try:
    os.remove("modelo_real.safetensors")
except PermissionError:
    pass  # Windows mantiene el fichero bloqueado por el mapeo en memoria de safetensors
