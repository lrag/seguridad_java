package servlets;

import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import entidades.Cliente;


/**
 * https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#Java
 */
@WebServlet("/JAXPServlet")
public class JAXPServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
        Scanner s = new Scanner(request.getInputStream());
        StringBuilder sb = new StringBuilder();
        while(s.hasNextLine()) {
        	sb.append(s.nextLine());
        }
        String xml = sb.toString();
        //Podemos examinar aqui el xml en bruto buscando entidades '&entidad;'
        System.out.println("XML:");
        System.out.println(xml);		
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			String FEATURE;
			// This is the PRIMARY defense. If DTDs (doctypes) are disallowed, almost all 
		    // XML entity attacks are prevented
		    // Xerces 2 only - http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl
		    //FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
		    //dbf.setFeature(FEATURE, true);

		    /*
		    // If you can't completely disable DTDs, then at least do the following:
		    // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-general-entities
		    // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-general-entities
		    // JDK7+ - http://xml.org/sax/features/external-general-entities
		    FEATURE = "http://xml.org/sax/features/external-general-entities";
		    dbf.setFeature(FEATURE, false);

		    // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-parameter-entities
		    // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities
		    // JDK7+ - http://xml.org/sax/features/external-parameter-entities
		    FEATURE = "http://xml.org/sax/features/external-parameter-entities";
		    dbf.setFeature(FEATURE, false);

		    // Disable external DTDs as well
		    FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
		    dbf.setFeature(FEATURE, false);

		    // and these as well, per Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks"
		    dbf.setXIncludeAware(false);
		    dbf.setExpandEntityReferences(false);
			*/

			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(xml)));
			
			// ra�z
			Element element = doc.getDocumentElement();
			NodeList nodes = element.getChildNodes();
			Cliente cliente = new Cliente();
			for (int a = 0; a < nodes.getLength(); a++) {
				Node nodo = nodes.item(a);
				switch(nodo.getNodeName()){
					case "nombre"    : cliente.setNombre(nodo.getTextContent());
									   break;
					case "direccion" : cliente.setDireccion(nodo.getTextContent());
									   break;
					case "telefono"  : cliente.setTelefono(nodo.getTextContent());
								       break;
				}
				//System.out.println("" + nodes.item(i).getTextContent());
			}
		
			//Simulamos que el cliente se inserta y recibe un ID
			cliente.setId((int) Math.round(Math.random()*10000));
			
			response.getWriter().append("Cliente insertado: "+cliente);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
