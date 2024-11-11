package expedientesx.controlador;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class BORRAR implements ServletContextListener {

    public BORRAR() {
    }

    public void contextDestroyed(ServletContextEvent sce)  { 
    }

    public void contextInitialized(ServletContextEvent sce)  { 
    	System.out.println("====================================");
    	System.out.println("= CONTEXTO DE LA APLICACIÓN CREADO =");
    	System.out.println("====================================");
    }
	
}
