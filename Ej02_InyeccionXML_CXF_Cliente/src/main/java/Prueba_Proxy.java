import javax.xml.rpc.ServiceException;

import com.curso.modelo.servicio.ServicioUsuarios;
import com.curso.modelo.servicio.ServicioUsuariosService;
import com.curso.modelo.servicio.ServicioUsuariosServiceLocator;
import com.curso.modelo.servicio.UsuarioDTO;

public class Prueba_Proxy {

	//Main que llama al servicio mediante los objetos proxies creados por
	//apache CXF
	public static void main(String[] args) throws Exception {
		
		//http://localhost:8080/Ej02_InyeccionXML_CXF/services/ServicioUsuarios?wsdl

		//Servicio
        ServicioUsuariosServiceLocator ss = new ServicioUsuariosServiceLocator();
        
        try {
        	ServicioUsuarios su = ss.getServicioUsuariosPort();  
        	UsuarioDTO usuario = new UsuarioDTO(1,"Lo","PW","Admin");
        	su.modificarUsuario(usuario);
		} catch (ServiceException e1) {
			e1.printStackTrace();
		} 
		
		
	}
	
}
