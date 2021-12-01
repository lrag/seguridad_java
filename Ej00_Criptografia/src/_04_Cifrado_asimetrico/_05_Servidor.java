package _04_Cifrado_asimetrico;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class _05_Servidor {

	private Key clavePublica;
	private Key clavePrivada;
	private SecureRandom random;	
	
	public static void main(String[] args) throws Exception {

		Security.addProvider(new BouncyCastleProvider());
		
		_05_Servidor servidor = new _05_Servidor();
		servidor.inicializarRSA();
		servidor.abrirPuerto();
	}	
	
	private void inicializarRSA() throws Exception {

		random = Utils.createFixedRandom();
        
        // create the RSA Key
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
        generator.initialize(1024, random);
        KeyPair pair = generator.generateKeyPair();
        clavePublica = pair.getPublic();
        clavePrivada = pair.getPrivate();
	}
	
	private void abrirPuerto() {
		try {
			ServerSocket svSk = new ServerSocket(2000);
			
			while(true) {
				System.out.println("Esperando conexiones...");
				Socket sk = svSk.accept();
				System.out.println("Conexión recibida");
				procesarSolicitud(sk);
			}		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void procesarSolicitud(Socket sk) throws Exception {
		
		//Esperamos 'cliente hello'
		Scanner sc = new Scanner(sk.getInputStream());
		String peticion = sc.nextLine();
		System.out.println("Recibido:"+peticion);
		if(!peticion.equals("client hello")) {
			crearError(sk,"Se esperaba 'cliente hello");
			return;
		}

		//Enviamos 'server hello'
        PrintWriter out = new PrintWriter(sk.getOutputStream(), true);
        System.out.println("Enviando server hello");
        out.println("server hello");

        //Enviamos la clave pública
        Encoder encoderB64 = Base64.getEncoder();
        String clavePublicaB64 = encoderB64.encodeToString(clavePublica.getEncoded());
        System.out.println("Enviando clave pública:"+clavePublicaB64);
        out.println(clavePublicaB64);

        //Recibimos la clave simétrica encriptada con nuestra clave pública
        String claveSimetricaB64 = sc.nextLine();
        System.out.println("Clave simétrica recibida (encriptada):"+claveSimetricaB64);
		Decoder decoderB64 = Base64.getDecoder();
		byte[] claveSimetricaEncriptada = decoderB64.decode(claveSimetricaB64);
        
        //Desencriptamos la clave simétrica utilizando nuestra clave privada
        Cipher xCipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA1AndMGF1Padding", "BC");
        xCipher.init(Cipher.DECRYPT_MODE, clavePrivada);
        
        Object[] keyIv = unpackKeyAndIV(xCipher.doFinal(claveSimetricaEncriptada));

        //Encriptamos el mensaje con la clave simétrica recibida
        Cipher sCipher = Cipher.getInstance("AES/CTR/NoPadding", "BC");	
        sCipher.init(Cipher.ENCRYPT_MODE, (Key)keyIv[0], (IvParameterSpec)keyIv[1]);
        byte[] mensajeEncriptadoBytes = sCipher.doFinal("Siete caballos vienen de Bonanza".getBytes());        
        
        //Enviamos el mensaje encriptado
        String mensajeEncriptadoB64 = encoderB64.encodeToString(mensajeEncriptadoBytes);
        System.out.println("Enviando el mensaje cifrado:"+mensajeEncriptadoB64);
        out.println(mensajeEncriptadoB64);
        
        //FIN
        System.out.println("FIN");
        
	}

	private void crearError(Socket sk, String mensaje) throws IOException {

		PrintWriter out = new PrintWriter(sk.getOutputStream());
		out.println("ERROR:"+mensaje);
		out.flush();
		sk.close();
		
	}
	
    private static Object[] unpackKeyAndIV(byte[] data){
        //byte[]    keyD = new byte[16];
        //byte[]    iv   = new byte[data.length - 16];
        
        return new Object[] {
             new SecretKeySpec(data, 16, data.length - 16, "AES"),
             new IvParameterSpec(data, 0, 16)
        };
    }    
	
	
}
