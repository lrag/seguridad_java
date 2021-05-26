package com.curso.controlador;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathVariableResolver;

//Esta clase el objetivo es tener una relación de 'huecos' con valores
//seria como emular un prepared statement, solo que en vez de '?'
//tenemos 'huecos' o 'claves' establecidas
//Esta clase la tenemos que hacer nosotros pero hecha una, hechas todas
@SuppressWarnings("static-method")
public class SimpleVariableResolver implements XPathVariableResolver {
	//Tiene un mapa para dar de alta todos los huecos que tiene en la expresión
	//El mapa tiene el QName del hueco(clave), y el valor del hueco (Valor)
	//Qnames son generalmente usados para referenciar elementos o atributos
	//dentro de documentos xml
	private final Map<QName, Object> vars = new HashMap<QName, Object>();

	public void addVariable(QName name, Object value) {
		vars.put(name, value);
	}

	//Este sería el unico metodo a implementar, debemos de devolverle un objeto
	//segun un QName pasado
	@Override
	public Object resolveVariable(QName variableName) {
		return vars.get(variableName);
	}

}