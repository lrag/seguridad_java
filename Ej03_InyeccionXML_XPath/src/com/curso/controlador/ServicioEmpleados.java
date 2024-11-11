package com.curso.controlador;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.owasp.esapi.ESAPI;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.curso.modelo.entidad.Empleado;

// Consultas XPath
/*
Expression	Description
nodename	Selects all nodes with the name "nodename", hijos directos desde donde estemos
/			Selects from the root node
//			Selects nodes in the document from the current node that match the selection no matter where they are
.			Selects the current node
..			Selects the parent of the current node
@			Selects attributes of a node

Path Expression	Result
bookstore		Selects all nodes with the name "bookstore", hijos directos desde donde estemos
/bookstore		Selects the root element bookstore
				Note: If the path starts with a slash ( / ) 
				it always represents an absolute path to an element!

bookstore/book	Selects all book elements that are children of bookstore
//book			Selects all book elements no matter where they are in the document
bookstore//book	Selects all book elements that are descendant of the bookstore 
				element, no matter where they are under the bookstore element
//@lang			Selects all attributes that are named lang


Path Expression						Result
/bookstore/book[1]					Selects the first book element that is the child of the bookstore element.
									Note: In IE 5,6,7,8,9 first node is[0], but according to W3C, it is [1]. To solve this 
									problem in IE, set the SelectionLanguage to XPath:
									In JavaScript: xml.setProperty("SelectionLanguage","XPath");
/bookstore/book[last()]				Selects the last book element that is the child of the bookstore element
/bookstore/book[last()-1]			Selects the last but one book element that is the child of the bookstore element
/bookstore/book[position() < 3]		Selects the first two book elements that are children of the bookstore element
//title[@lang]						Selects all the title elements that have an attribute named lang
//title[@lang='en']					Selects all the title elements that have an attribute named lang with a value of 'en'
/bookstore/book[price>35.00]		Selects all the book elements of the bookstore element that have a price element with 
 									a value greater than 35.00
/bookstore/book[price>35.00]/title	Selects all the title elements of the book elements of the bookstore element that have 
 									a price element with a value greater than 35.00
			
Wildcard	Description
*			Matches any element node
@*			Matches any attribute node
node()		Matches any node of any kind

Path Expression	Result
/bookstore/*	Selects all the child nodes of the bookstore element
//*				Selects all elements in the document
//title[@*]		Selects all title elements which have any attribute

Path Expression	Result
//book/title | //book/price		Selects all the title AND price elements of all book elements
//title | //price				Selects all the title AND price elements in the document
/bookstore/book/title | //price	Selects all the title elements of the book element of the bookstore element AND all the price elements in the document

*/


@WebServlet("/ServicioEmpleados")
public class ServicioEmpleados extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String DATASOURCE_XML = 
			"<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
			"<empleados>"+
			  "<empleado id='111' nombre='Groucho' apellidos='Marx'    sueldo='70000'>aaa</empleado>"+
			  "<empleado id='222' nombre='Bosco'   apellidos='Baracus' sueldo='50000'>bbb</empleado>"+
			  "<empleado id='333' nombre='Pato'    apellidos='Lucas'   sueldo='30000'>ccc</empleado>"+
			"</empleados>";

	@SuppressWarnings("rawtypes")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("Get");
		
		try {

			//Recogemos el par�metro
			String idEmpleado = request.getParameter("idEmpleado");
			if (idEmpleado == null) {
				idEmpleado = "";
			}
			
			//Leemos el XML
			//Aqui falta evitar ataques por XXE (más adelante)
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse( new InputSource(new StringReader(DATASOURCE_XML)));
			
			//Con nuestro m�todo podemos evitar la inyeccion...
			//descomentandolo para validar la entrada del usuario
				
			/*
			if (!checkValueForXpathInjection(idEmpleado)) {
				System.out.println("Inyección!!!");
				//devolvemos un 400, problema del cliente
				response.sendError(HttpServletResponse.SC_BAD_REQUEST,"INYECCION!!!!");
				// Que hacemos si detectamos una inyecci�n?
				// ->No seguir y mandar al usuario a login.
				//   Y escribir en un log lo que ha pasado
				//ya que es posible que suframos m�s ataques y conviene 
				//estar prevenidos
				return;
			}
			*/
			
			//O con ESAPI
			/*
			if(!validarEntrada(idEmpleado)){
				System.out.println("Inyeccion!");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST,"INYECCION!!!!");
				return;
			}
			*/
			
			///////////////////////////////
			//Creamos el objeto que puede lanzar consultas
			///////////////////////////////
	        XPathFactory xpathfactory = XPathFactory.newInstance();
	        XPath xpath = xpathfactory.newXPath();
	        //Creamos la expresion xpath para buscar en nuestro xml
	        String xPathExpression = "/empleados/empleado[@id='" + idEmpleado + "']";
	        	  
	        ///////////////////////////////////////
			//Consultas precompiladas con par�metros
	        //Equivalente a prepare statement en xpath
	        //no nos podrian inyectar xpath
	        ///////////////////////////////////////

			//Esta clase la tenemos que programar nosotros
	        SimpleVariableResolver variableResolver = new SimpleVariableResolver();
	        
	        //decimos que va a haber un hueco con nombre id, seria equivalente a las
	        //'?' en preparedStatements
			variableResolver.addVariable(new QName("id"), idEmpleado);
	        
			//Establecemos el resolver a nuestro objeto xpath
	        xpath.setXPathVariableResolver(variableResolver);
	        
	        
	        //Ahora la expresi�n xml cambiara, siendo '$id' la clave
	        //metida como 'QName("id")'
			//String xPathExpression = "/empleados/empleado[@id=$id]";
	        //Fin consultas con par�metros
	        
	        //compilamos la expresion
	        XPathExpression expr = xpath.compile(xPathExpression);
	        //pedimos que nos devuelva todos los nodos que coincidan con la expresi�n
			NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
	         	                
			//Siempre recorremos los nodos que coincidan con la 
	        //expresion xpath y creamos una lista con los empleados
	        List<Empleado> empleados = new ArrayList<>();
	        for (int i = 0; i < nodes.getLength(); i++) {
	            NamedNodeMap atr = nodes.item(i).getAttributes();
	            
	            Empleado e = new Empleado();
	            e.setId(Integer.valueOf(atr.getNamedItem("id").getNodeValue()));
	            e.setNombre(atr.getNamedItem("nombre").getNodeValue());
	            e.setApellidos(atr.getNamedItem("apellidos").getNodeValue());
	            e.setSueldo(Integer.valueOf(atr.getNamedItem("sueldo").getNodeValue()));

	            empleados.add(e);
	        }
	        
	        request.setAttribute("empleados", empleados);
	        request.getRequestDispatcher("listadoEmpleados.jsp").forward(request, response);
	        
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			e.printStackTrace();
		}
	}

	//Devolvemos si una cadena tiene inyeccion xpath, es decir, si es valida o no
	public boolean checkValueForXpathInjection(String value) throws Exception {
		boolean isValid = true;
		//Si value no es vacio ni nulo, comprobamos si nos estan inyectando
		if ((value != null) && !"".equals(value)) {
			//si la cadena tiene algunos de estos elementos
			//no ser� valida (Caracteres susceptibles a inyeccion Xpath)
			String xpathCharList = "()='[]:,*/ ";
			
			//recorremos la cadena que nos ha metido el usuario
			for (char c : value.toCharArray()) {
				//indexOf devuleve la posicion del caracter que estamos buscando
				//y en caso de que no exista devuelve '-1', por lo tanto
				//si devuelve una posicion distinta a '-1' es que existe en 
				//la cadena xpathCharList por lo que sera un caracter NO permitido
				//y habremos detectado una posible inyeccion XPATH
				if (xpathCharList.indexOf(c) != -1) {
					isValid = false;
					break;
				}
			}
		}
		return isValid;
	}
	
	public boolean validarEntrada(String valor){
		//Este metodo le paso el valor y me lo devuelve saneado
		String valorESAPI = ESAPI.encoder().encodeForXPath(valor);
		
		System.out.println("===========================================");
		System.out.println(valor);
		System.out.println(valorESAPI);
		//Si son iguales la cadena de entrada con la parseada por ESAPI
		//es que es una entrada Valida
		boolean esValida = valor.equals(valorESAPI);
		return esValida;	
	}
	
}






