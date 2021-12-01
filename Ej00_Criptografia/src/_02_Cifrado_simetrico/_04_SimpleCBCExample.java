package _02_Cifrado_simetrico;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Cipher Block Chaining (CBC)
 * 
 * ivBytes: primer bloque con el que se hará el xor del primer bloque encriptado
 *          si no se usa el primer bloque se desencriptará como basura
 * ver la imagen de Tux: https://en.wikipedia.org/wiki/Block_cipher_mode_of_operation
 */
public class _04_SimpleCBCExample{  
	
    public static void main(String[]args) throws Exception{
    	Security.addProvider(new BouncyCastleProvider());
        byte[] input = new byte[] { 
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 
                0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 };
        byte[] keyBytes = new byte[] { 
                0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef };
        //Vector de Inicialización (IV)
        byte[] ivBytes = new byte[] { 
                0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00 };
    	
        Security.addProvider(new BouncyCastleProvider()); 
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS7Padding", "BC");        

        System.out.println("input : " + Utils.toHex(input));
        
        //////////////////
        // ENCRIPTACIÓN //
        //////////////////

        //Si no añadimos el IvParameter vemos que el primer bloque se estropea
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        
        byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
        
        int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
        
        ctLength += cipher.doFinal(cipherText, ctLength);
        
        System.out.println("cipher: " + Utils.toHex(cipherText, ctLength) + " bytes: " + ctLength);
        
        /////////////////////
        // DESENCRIPTACIÓN //
        /////////////////////
        
        //ivSpec no necesita ser secreto
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        
        byte[] plainText = new byte[cipher.getOutputSize(ctLength)];

        int ptLength = cipher.update(cipherText, 0, ctLength, plainText, 0);
        
        ptLength += cipher.doFinal(plainText, ptLength);
        
        System.out.println("plain : " + Utils.toHex(plainText, ptLength) + " bytes: " + ptLength);
        
    }
}
