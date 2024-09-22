package com.curso.oyente;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

//Declaramos un evento con dos interfaces
//para que nos avise de cuando se ha creado una session
//para que nos avise de cuando se ha generado un atributo a la session
@WebListener
public class OyenteSesion implements HttpSessionListener, HttpSessionAttributeListener {

    public OyenteSesion() {
    }

    public void attributeRemoved(HttpSessionBindingEvent arg0)  { 
    }

    //cuando establecemos un atributo nuevo en la session
    public void attributeAdded(HttpSessionBindingEvent ev)  { 
         if(ev.getName().equals("usuario")){
        	 System.out.println("Atributo añadido:"+ev.getSession().getId()+":"+ev.getName()+":"+ev.getValue());
         }
    }

    //Cuando metemos un atributo con una clave que ya existe
    public void attributeReplaced(HttpSessionBindingEvent ev)  { 
    	if(ev.getName().equals("usuario")){
    		System.out.println("Atributo reemplazado:"+ev.getSession().getId()+":");
    		System.out.println("Antiguo:"+ev.getValue());
    		System.out.println("Nuevo  :"+ev.getSession().getAttribute(ev.getName()));
    	}
    }

    public void sessionCreated(HttpSessionEvent arg0)  { 
         System.out.println("Se ha creado una sesion:"+arg0.getSession().getId());
    }

    public void sessionDestroyed(HttpSessionEvent arg0)  { 
        System.out.println("Se ha destruido una sesion:"+arg0.getSession().getId());
    }
	
}
