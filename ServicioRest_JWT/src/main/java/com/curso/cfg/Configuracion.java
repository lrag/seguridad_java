package com.curso.cfg;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan(basePackages= { "com.curso.modelo.negocio", 
							   "com.curso.modelo.persistencia" })
public class Configuracion {
	
}
