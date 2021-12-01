package _02_Cifrado_simetrico;

import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.Security;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Example of using PBE with a PBEParameterSpec
 */
public class _09_PBEExample {

	public static void main(String[] args) throws Exception {
		byte[] input = new byte[] { 
				0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 
				0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d,	0x0e, 0x0f, 
				0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 };
		byte[] ivBytes = new byte[] { 
				(byte) 0xb0, 0x7b, (byte) 0xf5, 0x22, (byte) 0xc8, (byte) 0xd6, 0x08, (byte) 0xb8,
				(byte) 0xb0, 0x7b, (byte) 0xf5, 0x22, (byte) 0xc8, (byte) 0xd6, 0x08, (byte) 0xb8 };
		
		
		
		
		//Creando la clave
		Security.addProvider(new BouncyCastleProvider()); 
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		//La semilla es pública
		byte[] salt = new byte[] { 
				0x7d, 0x60, 0x43, 0x5f, 0x02, (byte) 0xe9, (byte) 0xe0, (byte) 0xae };		
		//Iteration count es público
		//Su cometido es aumentar el tiempo de creación de la clave
		int iterationCount = 65536;
		KeySpec spec = new PBEKeySpec("password".toCharArray(), salt, iterationCount, 256);
		SecretKey tmp = factory.generateSecret(spec);
		SecretKey key = new SecretKeySpec(tmp.getEncoded(), "AES");		
		
        //////////////////
        // ENCRIPTACIÓN //
        //////////////////
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        
        //Para desencriptar hemos de conocer la contraseña, la semilla 7 el iteration count
		KeySpec specDec = new PBEKeySpec("password".toCharArray(), salt, iterationCount, 256);
		SecretKey tmpDec = factory.generateSecret(spec);
		SecretKey keyDec = new SecretKeySpec(tmp.getEncoded(), "AES");	        
        
        cipher.init(Cipher.ENCRYPT_MODE, keyDec, ivSpec);        
        
        byte[] cipherText = new byte[cipher.getOutputSize(input.length)];        
        int ctLength = cipher.update(input, 0, input.length, cipherText, 0);        
        ctLength += cipher.doFinal(cipherText, ctLength);
        
        
        System.out.println("clave : " + Utils.toHex(key.getEncoded()));		
        System.out.println("cipher: " + Utils.toHex(cipherText, ctLength) + " bytes: " + ctLength);		
		
        /////////////////////
        // DESENCRIPTACIÓN //
        /////////////////////
        
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);        
        byte[] plainText = new byte[cipher.getOutputSize(ctLength)];
        int ptLength = cipher.update(cipherText, 0, ctLength, plainText, 0);        
        ptLength += cipher.doFinal(plainText, ptLength);
        
        System.out.println("plain : " + Utils.toHex(plainText, ptLength) + " bytes: " + ptLength);
   
        
        
	}
}
