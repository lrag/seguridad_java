package com.curso.controlador;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

//@WebListener
public class OyenteContexto implements ServletContextListener, ServletContextAttributeListener {

    public OyenteContexto() {
    }

    public void attributeAdded(ServletContextAttributeEvent e)  { 
    	System.out.println("Atributo añadido al SvCtx:"+e.getValue());
    }

    public void attributeRemoved(ServletContextAttributeEvent arg0)  { 
    }

    public void attributeReplaced(ServletContextAttributeEvent arg0)  { 
    }

    public void contextInitialized(ServletContextEvent svCtxE)  { 
    	System.out.println("Aplicacion arrancada");
    	//ApplicationContext appCtx = new...
    	//svCtxE.getServletContext().setAttribute("spring", appCtx);
    }
	
    public void contextDestroyed(ServletContextEvent arg0)  { 
    }
}
