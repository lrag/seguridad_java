package com.curso.xss;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncodingException;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

//Clase encargada de sanear los atributos de la clase pelicula
//Es singleton
//Las anotaciones se suelen programar con refection que es la parte
//de java encargada de hacer la metaprogramación
public class XSSEncoder {

	private static XSSEncoder instancia = new XSSEncoder();
	
	public static XSSEncoder getInstancia(){
		return instancia;
	}
	
	///////////////////////////////////////////////////////
	
	private XSSEncoder(){		
	}
	
	public void encode(Object obj) throws XSSException{
		
		System.out.println("================================");
		
		//Sacamos la clase a la que pertenece
		Class<? extends Object> tipo = obj.getClass();
		
		System.out.println(tipo);
		
		//Sacamos los atributos de la clase
		Field[] atributos = tipo.getDeclaredFields();
		
		System.out.println(atributos.length);
		
		//Nos recorremos los atributos para buscar anotaciones
		for(Field a: atributos){
			
			String nombre = a.getName();
		
			System.out.println("Atributo:"+nombre);
			
			//Si no tiene ninguna de nuestras anotaciones
			//pasamos al sigiente atributo para procesarlo
			if(!isAnotado(a)){
				continue;
			}
			
			try {
				//Obtenemos el valor del atributo llamando al metodo
				//set del objeto
				String valor = getValor(tipo, nombre, obj);
				
				//En función de la anotación que tenga
				//emplearemos un encoder un otro
				//En esta por ejemplo si tiene el EncodeHTMLText
				//usamos el metodo encodeForHTML
				if(a.isAnnotationPresent(EncodeHTMLText.class)){
					valor = ESAPI.encoder().encodeForHTML(valor);
				}
				if(a.isAnnotationPresent(EncodeHTML.class)){
					PolicyFactory sanitizer = Sanitizers.FORMATTING.and(Sanitizers.BLOCKS);
					valor = sanitizer.sanitize(valor);
				}
				if(a.isAnnotationPresent(EncodeHTMLAttribute.class)){
					valor = ESAPI.encoder().encodeForHTMLAttribute(valor);
				}
				if(a.isAnnotationPresent(EncodeCSS.class)){
					valor = ESAPI.encoder().encodeForCSS(valor);
				}
				if(a.isAnnotationPresent(EncodeJavascript.class)){
					valor = ESAPI.encoder().encodeForJavaScript(valor);
				}
				if(a.isAnnotationPresent(EncodeURL.class)){
					try {
						valor = ESAPI.encoder().encodeForURL(valor);
					} catch (EncodingException e) {
						
					}
				}
				
				//Al final, cambiamos el valor del atributo del objeto
				//llamando a su metodo set
				setValor(tipo, nombre, valor, obj);
				
			} catch (NoSuchMethodException e) {
				throw new XSSException(nombre+" no es una propiedad", e);
			} catch (SecurityException e) {
				//
			} catch (IllegalAccessException e) {
				throw new XSSException(nombre+" no es una propiedad pública", e);				
			} catch (IllegalArgumentException e) {
				throw new XSSException(nombre+" no es una propiedad de tipo String", e);
			} catch (InvocationTargetException e) {
				//
			}			
		}	
	}
	
	//Esta clase se encarga de saber si un atributo tiene anotaciones
	//y si las tiene, que sean las anotaciones especificas que hemos 
	//declarado
	private boolean isAnotado(Field a){
		
		//preguntamos por todas las anotaciones que tiene el atributo
		Annotation[] anotaciones = a.getAnnotations();
		if(anotaciones.length == 0){
			//en caso de que no tenga, el atributo no esta anotado
			return false;
		}
		for(Annotation anotacion: anotaciones){
			if(anotacion instanceof EncodeHTML     ||
			   anotacion instanceof EncodeHTMLText ||
			   anotacion instanceof EncodeHTMLAttribute || 
			   anotacion instanceof EncodeCSS || 
			   anotacion instanceof EncodeJavascript ){
				//si tiene alguna de nuestras anotaciones, entonces
				//lo podemos procesar
				return true;
			}			
		}
		return false;		
		
	}

	//Estos dos metodos obtienen el valor y ponen el valor de un atributo
	//para ello buscaran su get y su set en la clase, por lo tanto deben de
	//tenerlos en la clase(JavaBeans)
	//le pasamos la clase, el nombre del atributo y el objeto de la clase
	private String getValor(Class<?extends Object> tipo, String nombre, Object obj) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		nombre = "get"+nombre.substring(0,1).toUpperCase()+nombre.substring(1);
		Method getter = tipo.getMethod(nombre, new Class[]{});
		return (String) getter.invoke(obj, new Object[]{});
	}
	
	//le pasamos la clase, el nombre del atributo, el nuevo valor y el objeto de la clase
	private void setValor(Class<?extends Object> tipo, String nombre, String valor, Object obj) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		nombre = "set"+nombre.substring(0,1).toUpperCase()+nombre.substring(1);
		Method setter = tipo.getMethod(nombre, String.class);
		setter.invoke(obj, valor);
	}

}
