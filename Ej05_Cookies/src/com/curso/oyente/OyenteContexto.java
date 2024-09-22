package com.curso.oyente;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.SessionCookieConfig;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

//Este listener se recibir� un evento cuando se inicie el contexto de la aplicaci�n en el servidor
@WebListener
public class OyenteContexto implements ServletContextListener {

    public OyenteContexto() {
    }

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		//Por defecto tomcat 7 utiliza httpOnly=true para el jsessionid
		//Esto quiere decir que no se puede leer la jsessionid desde 
		//javascript
		//Se puede ver en el server tomcat
		//servers/tomcat v7.0/context.xmls
		//<Context useHttpOnly="false">
		
		System.out.println("====================================================");
		System.out.println("Contexto inicializado.");
		
		//La cookie solo se enviar� a esta url pattern
		SessionCookieConfig scf = sce.getServletContext().getSessionCookieConfig();
		System.out.println("PATH ANTES  :"+scf.getPath());
		scf.setPath("/Ej05_Cookies/seguro");
		System.out.println("PATH DESPUES:"+scf.getPath());

		//Más opciones:		
		scf.setHttpOnly(true); //No podr� ser accedida desde JS
							   //diga lo que diga la conf del servidor
		
		//Esto add una exigencia extra, solo se enviara la
		//cookie al navegador si es sobre https
		//scf.setSecure(true);
		
		System.out.println("Is http only:"+scf.isHttpOnly());
		System.out.println("Is secure   :"+scf.isSecure());
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

}
