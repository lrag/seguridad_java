package _02_Cifrado_simetrico;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;


/**
 * Basic symmetric encryption example
 */
public class _01_SimpleSymmetricExample{ 
	
    public static void main(String[] args) throws Exception
    {
    	//Mensaje a cifrar
        byte[] input = new byte[] { 
                0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, /*0x77,*/
                (byte)0x88, (byte)0x99, (byte)0xaa, (byte)0xbb,
                (byte)0xcc, (byte)0xdd, (byte)0xee, (byte)0xff };
        //Bytes de la clave
        byte[] keyBytes = new byte[] { 
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
                0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
                0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17 };
        
        //Creamos la clave a partir del array de bytes
        //DES: 1977. clave de 56 bits. Puede romperse ya con facilidad
        //AES: 2000. clave de 128, 192 o 256 bits
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

        //Obtenemos el cifrador
        //Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
    	Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding", "BC");           

        System.out.println("input text : " + Utils.toHex(input));
        
        //////////////////
        // ENCRIPTACIÓN //
        //////////////////
        
        //Indicamos al cifrador que va a encriptar
        //Entregamos la clave al cifrador
        cipher.init(Cipher.ENCRYPT_MODE, key);
        
        byte[] cipherText = new byte[input.length];

        int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
        
        ctLength += cipher.doFinal(cipherText, ctLength);
        
        System.out.println("cipher text: " + Utils.toHex(cipherText) + " bytes: " + ctLength);
        
        /////////////////////
        // DESENCRIPTACIÓN //
        /////////////////////
        
        byte[] plainText = new byte[ctLength];
        
        cipher.init(Cipher.DECRYPT_MODE, key);

        int ptLength = cipher.update(cipherText, 0, ctLength, plainText, 0);
        
        ptLength += cipher.doFinal(plainText, ptLength);
        
        System.out.println("plain text : " + Utils.toHex(plainText) + " bytes: " + ptLength);
    }
}
