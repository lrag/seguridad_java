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
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.ssl.SSLContexts;

public class AplicacionCliente {

	public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, KeyManagementException, UnrecoverableKeyException, ParseException {
		
		KeyStore ks = KeyStore.getInstance("PKCS12");
		ks.load(new FileInputStream("cliente3.p12"), "changeme".toCharArray());
		
		SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(ks, "changeme".toCharArray())
                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                .build();

		SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
		
		Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslConnectionFactory)
                .register("http", new PlainConnectionSocketFactory())
                .build();	
		
		HttpClientConnectionManager connManager = PoolingHttpClientConnectionManagerBuilder.create().setSSLSocketFactory(sslConnectionFactory).build();
				
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create().setConnectionManager(connManager);
		
		CloseableHttpClient httpClient = httpClientBuilder.build();
		HttpGet httpGet = new HttpGet("https://localhost:8443/peliculas");
		CloseableHttpResponse response = httpClient.execute(httpGet);
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
