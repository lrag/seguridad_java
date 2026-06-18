package com.curso;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.TlsSocketStrategy;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.ssl.SSLContexts;

public class AplicacionCliente {

	public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, KeyManagementException, UnrecoverableKeyException, ParseException {
		
		//He aqui nuestro almacen decertificados en el que está nuestra clave privada y certificado digital
		KeyStore ks = KeyStore.getInstance("PKCS12");
		ks.load(new FileInputStream("cliente2.p12"), "changeme".toCharArray());
		
		//Añadimos nuestro certificado al contexto
		SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(ks, "changeme".toCharArray())
                .loadTrustMaterial(null, TrustAllStrategy.INSTANCE)
                .build();

		TlsSocketStrategy tlsSocketStrategy = ClientTlsStrategyBuilder.create()
		        .setSslContext(sslContext)
		        .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
		        .buildClassic();

		HttpClientConnectionManager connManager = PoolingHttpClientConnectionManagerBuilder.create()
		        .setTlsSocketStrategy(tlsSocketStrategy) // <--- Corregido el nombre del método
		        .build();

		CloseableHttpClient httpClient = HttpClientBuilder.create()
		        .setConnectionManager(connManager)
		        .build();		
		
		HttpGet httpGet = new HttpGet("https://localhost:8443/peliculas");
		CloseableHttpResponse response = (CloseableHttpResponse) httpClient.executeOpen(null, httpGet, null);
        HttpEntity entity = response.getEntity();
        System.out.println("----------------------------------------");
        System.out.println(response.getCode());
        if (entity != null) {
            System.out.println("Response content length: " + entity.getContentLength());
            System.out.printf(EntityUtils.toString(entity));
        }
        EntityUtils.consume(entity);		
		
	}
	
}
