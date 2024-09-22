package _02_Cifrado_simetrico;

import java.security.Security;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;


/**
 * Encriptación simetrica con padding
 * Se encripta por bloques y automáticamente se completa el último bloque del original
 */
public class _02_SimpleSymmetricPaddingExample{  
	
    public static void main(String[] args) throws Exception{
    	
        byte[] input = new byte[] { 
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, /*0x07,*/
                0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
                0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17 };
                
        byte[] keyBytes = new byte[] { 
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
                0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
                0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17 };
        
        //Creamos la clave a partir del array de bytes
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

        //Obtenemos el cifrador
        //Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
    	Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");        

        System.out.println("input : " + Utils.toHex(input));
        
        //////////////////
        // ENCRIPTACIÓN //
        //////////////////
        
        //Indicamos al cifrador que va a encriptar
        //Entregamos la clave al cifrador
        cipher.init(Cipher.ENCRYPT_MODE, key);
        
        byte[] textoCifrado = new byte[cipher.getOutputSize(input.length)];

        //Los ceros son offsets
        int ctLength = cipher.update(input, 0, input.length, textoCifrado, 0);
        
        ctLength += cipher.doFinal(textoCifrado, ctLength);
        
        System.out.println("cipher: " + Utils.toHex(textoCifrado) + " bytes: " + ctLength);
        
        /////////////////////
        // DESENCRIPTACIÓN //
        /////////////////////

        cipher.init(Cipher.DECRYPT_MODE, key);
        
        byte[] textoDescifrado = new byte[cipher.getOutputSize(ctLength)];

        int ptLength = cipher.update(textoCifrado, 0, ctLength, textoDescifrado, 0);
        
        ptLength += cipher.doFinal(textoDescifrado, ptLength);
        
        System.out.println("plain : " + Utils.toHex(textoDescifrado) + " bytes: " + ptLength);
    }
}
