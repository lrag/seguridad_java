package _02_Cifrado_simetrico;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Symmetric encryption example with padding and CBC using AES
 * with the initialization vector inline.
 */
public class _05_InlineIvCBCExample{   

	public static void main(String[] args) throws Exception{
		
        byte[] input = new byte[] { 
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 
                0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 };
        byte[] keyBytes = new byte[] { 
                0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
        		0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef };
        byte[] ivBytes = new byte[] { 
                0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00,
        		0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00 };
        
        Security.addProvider(new BouncyCastleProvider()); 
        SecretKeySpec   key = new SecretKeySpec(keyBytes, "AES");
        //Usamos un vector de inicializacion con ceros patateros
        //Así no hay que enviárselo a nadie (aunque no se trate de una información secreta)
        //Pero es predecible...
        IvParameterSpec ivSpec = new IvParameterSpec(new byte[16]);
        Cipher          cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        

        System.out.println("input : " + Utils.toHex(input));
        
        //////////////////
        // ENCRIPTACIÓN //
        //////////////////
        
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        
        byte[] cipherText = new byte[cipher.getOutputSize(ivBytes.length + input.length)];
        
        //Como primer bloque del texto cifrado añadimos el vector IV
        int ctLength = cipher.update(ivBytes, 0, ivBytes.length, cipherText, 0);
        //Añadimos a continuación el texto a cifrar
        ctLength += cipher.update(input, 0, input.length, cipherText, ctLength);
        
        ctLength += cipher.doFinal(cipherText, ctLength);
        
        System.out.println("cipher: " + Utils.toHex(cipherText, ctLength) + " bytes: " + ctLength);
        
        /////////////////////
        // DESENCRIPTACIÓN //
        /////////////////////
        
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        
        byte[] buf = new byte[cipher.getOutputSize(ctLength)];
        
        int bufLength = cipher.update(cipherText, 0, ctLength, buf, 0);
        
        bufLength += cipher.doFinal(buf, bufLength);
        
        //Eliminamos del comienzo del texto el VI         
        //byte[] plainText = new byte[bufLength];
        //System.arraycopy(buf, 0, plainText, 0, plainText.length); //Vemos que el primer bloque es exactamente el IV

        byte[] plainText = new byte[bufLength - ivBytes.length];
        System.arraycopy(buf, ivBytes.length, plainText, 0, plainText.length);
        
        System.out.println("plain : " + Utils.toHex(plainText, plainText.length) + " bytes: " + plainText.length);
    }
}










