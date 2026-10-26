package com.curso;

import java.io.Serializable;
import java.util.Arrays;

// El "dato real" que de verdad queremos guardar y cargar: el contraste limpio
// del ejemplo (equivale a los tensores en safetensors del script Python). No
// ejecuta nada al deserializarse; solo lleva datos.
//
// Es un record a proposito: Java deserializa los records llamando a su
// constructor canonico, no reconstruyendo los campos a escondidas como con una
// clase normal, y un record no puede definir readObject. Usar records para los
// datos que viajan serializados elimina una de las vias de ataque, aunque no
// sustituye al filtro: el riesgo esta en las OTRAS clases que el flujo pueda
// colar.
public record DatoReal(String nombreCapa, double[] pesos) implements Serializable {

    @Override
    public String toString() {
        return nombreCapa + " = " + Arrays.toString(pesos);
    }
}
