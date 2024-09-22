package _04_Cifrado_asimetrico;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class _05_Cliente {

	public static void main(String[] args) throws Exception, NoSuchProviderException {

		Security.addProvider(new BouncyCastleProvider());
		
		try (Socket sk = new Socket("localhost", 2000)) {

			Scanner sc = new Scanner(sk.getInputStream());
			PrintWriter out = new PrintWriter(sk.getOutputStream(), true);

			out.println("client hello");
			String respuesta = sc.nextLine();
			System.out.println("Respuesta recibida:"+respuesta);

			//Recibimos la clave publica
			String clavePublicaB64 = sc.nextLine();
			Decoder decoderB64 = Base64.getDecoder();
			byte[] keyBytes = decoderB64.decode(clavePublicaB64);
			System.out.println("Clave pública recibida:"+clavePublicaB64);
			X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
			KeyFactory kf = KeyFactory.getInstance("RSA", "BC");
			Key clavePublica = kf.generatePublic(spec);

			// Crear la clave simetrica
			SecureRandom random = Utils.createFixedRandom();
			Key sKey = Utils.createKeyForAES(256, random);
			IvParameterSpec sIvSpec = Utils.createCtrIvForAES(0, random);

			// Encriptar la clave simétrica con la clave pública del servidor
			Cipher xCipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA1AndMGF1Padding", "BC");
			xCipher.init(Cipher.ENCRYPT_MODE, clavePublica, random);
			byte[] claveSimetricaBytes = xCipher.doFinal(packKeyAndIv(sKey, sIvSpec));

			//Enviar la clave simétrica cifrada al servidor
			Encoder encoderB64 = Base64.getEncoder();
			String claveSimetricaB64 = encoderB64.encodeToString(claveSimetricaBytes);
			System.out.println("Enviando clave simétrica cifrada:"+claveSimetricaB64);
			out.println(claveSimetricaB64);
			
			//Leemos el mensaje encriptado con la clave simétrica
			String mensajeEncriptadoB64 = sc.nextLine();
			byte[] mensajeEncriptadoBytes = decoderB64.decode(mensajeEncriptadoB64);
			
			//Desencriptamos el mensaje
	        Cipher sCipher = Cipher.getInstance("AES/CTR/NoPadding", "BC");	
	        sCipher.init(Cipher.DECRYPT_MODE, sKey, sIvSpec);
			byte[] mensajeDesencriptado = sCipher.doFinal(mensajeEncriptadoBytes);
			
			System.out.println(new String(mensajeDesencriptado));
			
			sk.close();
			
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static byte[] packKeyAndIv(Key key, IvParameterSpec ivSpec) throws IOException {

		ByteArrayOutputStream bOut = new ByteArrayOutputStream();

		bOut.write(ivSpec.getIV());
		bOut.write(key.getEncoded());
		return bOut.toByteArray();
	}
	
}
