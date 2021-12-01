package com.curso.modelo.servicio;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.curso.modelo.servicio.dto.UsuarioDTO;


//http://localhost:8080/Ej02_InyeccionXML_CXF/services

//Esta sería nuestra clase de servicio, que solo tiene un metodo publicado
//y muy sencillo
//a partir de esta clase se generará nuestro ficheor wsdl
//El fichero wsdl es una guia para construir los distintos 
//mensajes SOAP entre el cliente y el servidor
@WebService(targetNamespace = "http://servicio.modelo.curso.com/", 
            portName = "ServicioUsuariosPort", 
            serviceName = "ServicioUsuariosService")
public class ServicioUsuarios {

	//El objetivo de este metodo es modificar algun rol del usuario
	@WebMethod
	public void modificarUsuario(UsuarioDTO usuario){		
		System.out.println("Modificando el usuario:"+usuario);		
	}
		
}
