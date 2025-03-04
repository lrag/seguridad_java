package com.curso.filtro;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.regex.Pattern;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;



//Esta clase extiende de HttpServletRequestWrapper que implementa
//HttpServletRequest, 
public class XSSRequestWrapper extends HttpServletRequestWrapper {

    private byte[] body;	
	
	//El objeto que creamos usamos el de la request para coger
	//por defecto sus valores
    public XSSRequestWrapper(HttpServletRequest servletRequest) {
        super(servletRequest);

        //Leemos el body y lo guardamos en un array de bytes
        try {
            
        	//No deberíamos leer el body si no es un jotasón 
        	body = IOUtils.toByteArray(super.getInputStream());
            
            String contentType = servletRequest.getHeader("content-type");
            if(contentType.contains("application/json")) {
            	System.out.println("=========================================");
            	System.out.println("Un JSON!");
            	String json = new String(body);
            	System.out.println("STR 1:"+json);
            	String jsonSinXSS = stripXSS(json);
            	System.out.println("STR 2:"+jsonSinXSS);
            	body = jsonSinXSS.getBytes();
            }            
            
        }catch (Exception e){
        	System.out.println(e.getMessage());
        }        
    }

    //Gracias a la herencia podemos sobrescribir los metodos de la
    //clase request original, para así poder pasar los encoders
    //a los parametros

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new ServletInputStreamImpl(new ByteArrayInputStream(body));
    }

    @Override
    public BufferedReader getReader() throws IOException {
        String enc = getCharacterEncoding();
        if (enc == null) enc = "UTF-8";
        return new BufferedReader(new InputStreamReader(getInputStream(), enc));
    }
    
    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);

        if (values == null) {
            return null;
        }

        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = stripXSS(values[i]);
        }

        return encodedValues;
    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);

        System.out.println(parameter+":"+value+":"+stripXSS(value));
        //Por supuesto podriamos seguir poniendo aqui en su lugar ESAPI
        //en vez de esta funcion que limpia javascript que hemos probado
        return stripXSS(value);
    }
    
    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return stripXSS(value);
    }

    //
    // 
    //
    private String stripXSS(String value) {
        if (value != null) {
            //value = ESAPI.encoder().canonicalize(value);
            
            //Este ejemplo sería otra alternativa al ESAPI para evitar el 
            //XSS, pasa todos los barridos necesarios para evitar la inyecccion
            
            // Avoid null characters
            value = value.replaceAll("\0", "");

            // Avoid anything between script tags
            Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid anything in a src='...' type of expression
            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Remove any lonesome </script> tag
            scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Remove any lonesome <script ...> tag
            scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid eval(...) expressions
            scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid expression(...) expressions
            scriptPattern = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid javascript:... expressions
            scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid vbscript:... expressions
            scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid onload= expressions
            scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
        }
        return value;
    }
}