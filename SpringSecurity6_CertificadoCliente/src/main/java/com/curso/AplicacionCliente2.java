package com.curso;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

//Mismo ejemplo que AplicacionCliente, pero sin la libreria de Apache (httpclient5):
//el SSLContext con nuestro certificado de cliente se sigue construyendo con la API de Java,
//lo unico que cambia es que la peticion la hace el HttpClient del JDK envuelto en el RestClient de Spring
public class AplicacionCliente2 {

	public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
			FileNotFoundException, IOException, KeyManagementException, UnrecoverableKeyException {

		//He aqui nuestro almacen de certificados en el que está nuestra clave privada y certificado digital
		KeyStore ks = KeyStore.getInstance("PKCS12");
		ks.load(new FileInputStream("cliente2.p12"), "changeme".toCharArray());

		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(ks, "changeme".toCharArray());

		//Equivalente al TrustAllStrategy que usaba Apache: no valida la cadena de confianza del certificado del servidor
		X509TrustManager confiaEnTodos = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		};

		//Añadimos nuestro certificado (KeyManager) y el TrustManager que se fia de todos al contexto
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(kmf.getKeyManagers(), new X509TrustManager[] { confiaEnTodos }, null);

		HttpClient httpClient = HttpClient.newBuilder()
				.sslContext(sslContext)
				.build();

		RestClient restClient = RestClient.builder()
				.requestFactory(new JdkClientHttpRequestFactory(httpClient))
				.baseUrl("https://localhost:8443")
				.build();

		String body = restClient.get()
				.uri("/peliculas")
				.retrieve()
				.body(String.class);

		System.out.println("----------------------------------------");
		System.out.println(body);

	}

}
