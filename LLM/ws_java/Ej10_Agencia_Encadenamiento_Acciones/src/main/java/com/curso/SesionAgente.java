package com.curso;

// Estado compartido por las herramientas durante una tarea del agente. Lo
// escribe HerramientaPreciosCompra al consultar el dato interno y lo lee
// HerramientaEmail antes de enviar. Cada tarea crea su propia SesionAgente,
// asi que la bandera empieza siempre en false.
public class SesionAgente {

    private boolean datoInternoConsultado = false;

    public void marcarDatoInternoConsultado() {
        datoInternoConsultado = true;
    }

    public boolean datoInternoConsultado() {
        return datoInternoConsultado;
    }
}
