package com.curso;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

//Servidor de los indeseables
public class ServidorAtacante {

    private static final int PUERTO = 1880;

    public static void main(String[] args) throws IOException {
        System.out.println("1. Levantando servidor de los indeseables");
        HttpServer servidor = HttpServer.create(new InetSocketAddress("localhost", PUERTO), 0);

        servidor.createContext("/", intercambio -> {
            System.out.println("[SERVIDOR ATACANTE] Peticion recibida -> " + intercambio.getRequestURI());
            intercambio.sendResponseHeaders(200, -1);
            intercambio.close();
        });

        servidor.start();
        System.out.println("Escuchando en http://localhost:" + PUERTO + "...\n");
    }
}
