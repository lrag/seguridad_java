package servlets;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import entidades.Cliente;


/**
 * https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#Java
 */
@WebServlet("/JAXBServlet")
public class JAXBServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
			
		System.setProperty("javax.xml.accessExternalSchema", "file, http");
		System.setProperty("javax.xml.accessExternalDTD", "file, http");		
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance( Cliente.class );

	        XMLInputFactory xif = XMLInputFactory.newFactory();
	        //xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
	        //xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
	        XMLStreamReader xsr = xif.createXMLStreamReader(request.getInputStream());
	
	        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
	        Cliente cliente = (Cliente) unmarshaller.unmarshal(xsr);
	        
	        
	        response.getWriter().print(cliente);
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
        
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}







