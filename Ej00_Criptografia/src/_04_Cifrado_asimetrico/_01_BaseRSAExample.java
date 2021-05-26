package _04_Cifrado_asimetrico;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Basic RSA example.
 */
public class _01_BaseRSAExample{
	
    public static void main(String[] args) throws Exception{
        
        Security.addProvider(new BouncyCastleProvider());
        
        //No se pueden encriptar mas bits que la longitud de la clave
    	byte[] input  = new byte[] { 
    		(byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07, (byte)0x08,
    		(byte)0x09, (byte)0x0a, (byte)0x0b, (byte)0x0c, (byte)0x0d, (byte)0x0e, (byte)0x0f, (byte)0x10
    	};
        Cipher cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
        KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
        
        //Crear las claves
        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(
                new BigInteger("d46f473a2d746537de2056ae3092c451", 16),
                new BigInteger("11", 16)); 
        RSAPrivateKeySpec privKeySpec = new RSAPrivateKeySpec(
                new BigInteger("d46f473a2d746537de2056ae3092c451", 16),  
                new BigInteger("57791d5430d593164082036ad8b29fb1", 16));
        
        RSAPublicKey pubKey = (RSAPublicKey)keyFactory.generatePublic(pubKeySpec);
        RSAPrivateKey privKey = (RSAPrivateKey)keyFactory.generatePrivate(privKeySpec);

        System.out.println("input : " + Utils.toHex(input));

        //Encriptación
        //inicio = System.currentTimeMillis();
        
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);

        byte[] cipherText = cipher.doFinal(input);

        System.out.println("cipher: " + Utils.toHex(cipherText));
        
        //Desencriptación

        cipher.init(Cipher.DECRYPT_MODE, privKey);

        byte[] plainText = cipher.doFinal(cipherText);
        
        System.out.println("plain : " + Utils.toHex(plainText));
        
    }
}




















