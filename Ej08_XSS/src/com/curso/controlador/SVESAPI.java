package com.curso.controlador;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncodingException;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

@WebServlet("/SVESAPI")
public class SVESAPI extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SVESAPI() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
			//MAL
			/*
			String textoHtml    = request.getParameter("textoHtml");
			String atributo     = request.getParameter("atributo");
			String css          = request.getParameter("css");
			String parametroUrl = request.getParameter("parametroUrl");
			String url          = request.getParameter("url");
			String javascript   = request.getParameter("javascript");
			String html         = request.getParameter("html");
			*/

			//Aqui empiezan los encoder, descomentar para aplicar
			//Estos encoders nos van a asegurar que no nos van a inyectar codigo malicioso
			//siempre y cuando usemos el encoder apropiado para el destion de los parametros
			//por ejemplo en encoderForHTML nos garantiza que vamos a poder poner
			//ese el codigo devuelto en un html sin que suframos una inyeccion html
			
			System.out.println("ESAPI");
			//Los encoders sirven apra sustituir caracteres en su equivalente
			//Este permite caracteres, letras, etc. Pero si se encuentran
			//<h1> por ejemplo, tienen que codificarlo a &lt; h1 &gt;
			String textoHtml  = 
				ESAPI.encoder().
					encodeForHTML(request.getParameter("textoHtml"));
			//Este seria m�s intesivo, aqui por ejemplo ni espacios en blanco permitiria
			String atributo   = 
				ESAPI.encoder().
					encodeForHTMLAttribute(request.getParameter("atributo"));
			String css        = 
				ESAPI.encoder().
					encodeForCSS(request.getParameter("css"));

			String parametroUrl = "";
			try {
				parametroUrl = ESAPI.encoder().encodeForURL(request.getParameter("parametroUrl"));
			} catch (EncodingException e) {
				e.printStackTrace();
			}
			
			//El primer paremetro para el contexto de validaci�n que queremos, luego la url
			//El tercer parametro el tipo que queremos validar, el cuarto la longitud maxima
			//el ultimo si permitimos nulos
			//tambien podriamos hacerla como el encodeForURL de arriba
			String url = request.getParameter("url");
			
			try {
				url = ESAPI.validator().getValidInput("URLContext", url, "HTTPURL", 255, false);
				//NO ES SUFICIENTE!
				url = ESAPI.encoder().encodeForHTMLAttribute(url);
			} catch (ValidationException e) {
				e.printStackTrace();
			} catch (IntrusionException e) {
				e.printStackTrace();
			} 
						
			//Encode for HTML: elimina el JS pero respeta el HTML
			//Permite añadir una lista blanca de etiquetas HTML permitidas
			String html = request.getParameter("html");
			PolicyFactory sanitizer = Sanitizers.FORMATTING.and(Sanitizers.BLOCKS);
			html = sanitizer.sanitize(html);
			
			//El encoder para javascript es el m�s estricto de todos,sustituye
			//practicamente todo
			//si queremos poner algo en codigo javascript, solo podemos permitir
			//poner valor de variables, es decir, texto
			String javascript = ESAPI.encoder().encodeForJavaScript(request.getParameter("javascript"));
			
			//Fin encoders			
				
			System.out.println("------------------------------");
			System.out.println("------------------------------");
			System.out.println("textoHtml:\n" + textoHtml);
			System.out.println("html:\n" + html);
			System.out.println("atributo:\n" + atributo);
			System.out.println("css:\n" + css);
			System.out.println("parametroUrl:\n" + parametroUrl);
			System.out.println("url:\n" + url);
			System.out.println("javascript:\n" + javascript);
						
			HttpSession sesion = request.getSession(true);
			sesion.setAttribute("textoHtml",textoHtml);
			sesion.setAttribute("atributo",atributo);
			sesion.setAttribute("css",css);
			sesion.setAttribute("parametroUrl",parametroUrl);
			sesion.setAttribute("url",url);
			sesion.setAttribute("javascript",javascript);
			sesion.setAttribute("html",html);
		
			response.sendRedirect("resumenESAPI.jsp");
	
	
	}

}
