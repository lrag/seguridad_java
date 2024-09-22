package _03_MessageDigest_MACs;

import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Tampered message, plain encryption, AES in CTR mode
 */
public class _01_TamperedExample{
	
    public static void main(String[] args) throws Exception{
    	
        Security.addProvider(new BouncyCastleProvider());
    	
        SecureRandom	random = new SecureRandom();
        IvParameterSpec ivSpec = Utils.createCtrIvForAES(1, random);
        Key             key = Utils.createKeyForAES(256, random);
        Cipher          cipher = Cipher.getInstance("AES/CTR/NoPadding", "BC");
        String          input = "Transfer 0000100 to AC 1234-5678";

        System.out.println("input : " + input);
        
        //ENCRIPTACIÓN
        
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        
        byte[] cipherText = cipher.doFinal(Utils.toByteArray(input));

        
        //MANIPULACIÓN DEL MENSAJE
        //Digamos que saben hace un xor para encriptar en modo stream, pues hacemos otro
        System.out.println(Utils.toString(cipherText));
        cipherText[9]  ^= '0' ^ 'h';
        cipherText[10] ^= '0' ^ 'o';
        cipherText[11] ^= '0' ^ 'l';
        cipherText[12] ^= '0' ^ 'a';
        System.out.println(Utils.toString(cipherText));
        
        //DESENCRIPTACIÓN
        
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        
        byte[] plainText = cipher.doFinal(cipherText);
        
        System.out.println("plain : " + Utils.toString(plainText));
    }
}
