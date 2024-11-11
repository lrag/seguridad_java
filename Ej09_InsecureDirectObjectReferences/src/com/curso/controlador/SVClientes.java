package com.curso.controlador;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.owasp.esapi.AccessReferenceMap;
import org.owasp.esapi.errors.AccessControlException;
import org.owasp.esapi.reference.IntegerAccessReferenceMap;
import org.owasp.esapi.reference.RandomAccessReferenceMap;

import com.curso.modelo.entidad.Cliente;
import com.curso.modelo.negocio.GestorClientes;

@WebServlet("/SVClientes")
public class SVClientes extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private GestorClientes gestorClientes = new GestorClientes();
	
    public SVClientes() {
        super();
    }
    
    //GET usado para lo sigiente
    //Cancelar (verListado)
    //Nuevo, Vaciar (verFormulario)
    //Seleccionar.
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession sesion = request.getSession(true);
		String accion = request.getParameter("accion");
		
		String siguienteVista = "listadoClientes.jsp";
		//Si pulsamos el boton nuevo
		if("verFormulario".equals(accion)){			
			siguienteVista = "formularioClientes.jsp";
		} else if("seleccionar".equals(accion)){	
			//Cuando seleccionamos un cliente de la tabla debemmos de buscarlo en la base de datos
			//y cargarlo en la tabla del formulario
			//Sin AccessRefernceMap			
			//Cliente clienteSel = gestorClientes.buscar(Integer.parseInt(request.getParameter("idCliente")));

			//Con AccessRerenceMap
			
			IntegerAccessReferenceMap armap = (IntegerAccessReferenceMap) sesion.getAttribute("armap");
			Integer idCliente = 0;
			//tenemos que buscar un cliente por id pero lo que nos llega es una clave indirecta
			//preguntamos al mapa por la clave verdadera pasandole la clave falsa
			try {
				idCliente = (Integer) armap.getDirectReference(request.getParameter("idCliente"));
			} catch (AccessControlException e) {
				e.printStackTrace();
			}
			
			
			//ahora que tenemos la clave verdadera podemos pasarsela al negocio
			Cliente clienteSel = gestorClientes.buscar(idCliente);
			//rellenamos el cliente seleccionado con la clave indirecta para pasarsela a la pantalla
			clienteSel.setId(Integer.valueOf(armap.getIndirectReference(idCliente)));
			
					
			request.setAttribute("clienteSel", clienteSel);			
			siguienteVista = "formularioClientes.jsp";
		} 	
		
		//Si lo que tenemos que mostrar es la lista de clientes, habra que acceder a la base de 
		//datos para buscarlos
		if(siguienteVista.equals("listadoClientes.jsp")){
			List<Cliente> clientes = gestorClientes.listar(0,20);
			//la primera vez que mostramos la lista es cuando creamos RandomAccessRefereceMap
			//El objetivo de esta clase es tener mapeadas las claves verdaderas con las claves
			//indirectas, la clave seria la indirecta y el valor la verdadera
			//Con AccessReferenceMap
			//AccessReferenceMap armap = new RandomAccessReferenceMap();
			
			
			IntegerAccessReferenceMap armap = new IntegerAccessReferenceMap();
			for(Cliente c:clientes){
				//add al mapa la clave verdadera, el generar� automaticamente una clave
				//indirecta
				Integer claveIndirecta = Integer.valueOf(armap.addDirectReference(c.getId()));
				System.out.println("CI:"+claveIndirecta);
				c.setId(claveIndirecta);
			}
			//Guardamos este mapa en la session del usuario
			sesion.setAttribute("armap", armap);	
			
			request.setAttribute("listadoClientes", clientes);
			
		} else if(siguienteVista.equals("formularioClientes.jsp")){
			//
		}		
		
		request.getRequestDispatcher(siguienteVista).
			forward(request,response);		
	}
	
	//POST usado para lo siguiente
	//Insertar
	//Modificar
	//Borrar
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		//Recoger los par�metros y hacer una primera conversion
		//de String al tipo adecuado		
		//Sin AccessReferenceMap
		/*
		int id = 0;
		try {
			//El id del cliente a insertar, modificar o borrar nos vendra informado en idCliente
			id = Integer.parseInt(request.getParameter("idCliente"));
		} catch (NumberFormatException e) {
			//e.printStackTrace();
		}
		*/
		
    	//Fin
		
		//Con AccessReferenceMap
		
		HttpSession sesion = request.getSession(true);
		IntegerAccessReferenceMap armap = (IntegerAccessReferenceMap) sesion.getAttribute("armap");
		Integer id = 0;
		//en este caso no nos va a llegar la clave verdadera del cliente, sino la indirecta
		try {
			System.out.println(request.getParameter("idCliente"));
			//le pedimos al mapa la clave verdadera pasandole la clave indirecta
			id = (Integer) armap.getDirectReference(request.getParameter("idCliente"));
			//id tiene ahora la clave verdadera
		} catch (AccessControlException e) {
			e.printStackTrace();
		} 
		
		
   	    
				
		//Habría que impedir inyecciones (XSS, HTML, etc)
		String nombre = request.getParameter("nombre");
		String direccion = request.getParameter("direccion");
		String telefono = request.getParameter("telefono");
		
		//Construir objetos del modelo con los parametros recibidos
		Cliente c = new Cliente(id, nombre, direccion, telefono);
		
		//Validar los objetos
		//...
		
		//Averiguar que nos est�n pidiendo
		String accion = request.getParameter("accion");
		//Llamar al m�todo de negocio adecuado segun la accion
		switch(accion) {
		case "insertar" : 
				gestorClientes.insertar(c);
				break;
			case "modificar" :
				gestorClientes.modificar(c);
				break;
			case "borrar" :
				gestorClientes.borrar(c);
				break;			
		}

		//Despues de una petici�n post se hace un redirect.
		//Si se est� usando MVC no se puede hacer un redirect a una vista
		//Se hace un redirect al controlador que muestre la vista que nos 
		//interesa
		//GET SVClientes
		response.sendRedirect("SVClientes");
	
	}

}





