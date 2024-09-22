package _02_Cifrado_simetrico;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;


/**
 * Basic symmetric encryption example with padding and ECB 
 * ECB cifra por bloques de manera determinista y en el resultado pueden aparecer bloques repetidos
 * Similar a un 'libro de c�digos'
 * ver la imagen de Tux: https://en.wikipedia.org/wiki/Block_cipher_mode_of_operation
 */
public class _03_SimpleECBExample{   
	
    public static void main(String[] args) throws Exception
    {
        byte[] input = new byte[] { 
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 
                0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 };
        byte[] keyBytes = new byte[] { 
                0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef };
                
    	Security.addProvider(new BouncyCastleProvider());
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS7Padding", "BC");
        
        //////////////////
        // ENCRIPTACI�N //
        //////////////////
        long inicio = System.currentTimeMillis();
        
        cipher.init(Cipher.ENCRYPT_MODE, key);
        
        byte[] cipherText = new byte[cipher.getOutputSize(input.length)];

        int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
        
        ctLength += cipher.doFinal(cipherText, ctLength);
        
        System.out.println("cipher: " + Utils.toHex(cipherText, ctLength) + " bytes: " + ctLength);
        
        /////////////////////
        // DESENCRIPTACI�N //
        /////////////////////
        
        cipher.init(Cipher.DECRYPT_MODE, key);
        
        byte[] plainText = new byte[cipher.getOutputSize(ctLength)];

        int ptLength = cipher.update(cipherText, 0, ctLength, plainText, 0);
        
        ptLength += cipher.doFinal(plainText, ptLength);
        
        //Quitamos los ceros del final
        System.out.println("plain : " + Utils.toHex(plainText, ptLength) + " bytes: " + ptLength);

        System.out.println("Y 3260266c2cf202e2 aparece dos veces...");
        
    }
}
