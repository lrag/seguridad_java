package expedientesx.controlador;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

//Spring boot encontrará este oyente porque en la clase marcada
//con @SpringBootApplication tenemos puesta también la anotación '@ServletComponentScan'
@WebListener

//Tambien podemos declarar componentes web en un poroyecto hecho con string boot si los anotamos
//con estereotipos de Spring
//@Component
public class OyenteSesion implements HttpSessionListener, HttpSessionAttributeListener {

    public OyenteSesion() {
    }

    public void sessionCreated(HttpSessionEvent arg0)  { 
    	System.out.println("SESIÓN CREADA");
    }

    public void sessionDestroyed(HttpSessionEvent ev)  { 
    	System.out.println("SESIÓN INVALIDADA");
    }

	@Override
	public void attributeAdded(HttpSessionBindingEvent event) {
		System.out.println("Atributo añadido a la sesión ("+ event.getSession().getId() +"): "+ event.getName());
	}

	@Override
	public void attributeRemoved(HttpSessionBindingEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attributeReplaced(HttpSessionBindingEvent event) {
		System.out.println("Atributo reemplazado a la sesión ("+ event.getSession().getId() +"): "+ event.getName());
		// TODO Auto-generated method stub
		
	}
	
}
