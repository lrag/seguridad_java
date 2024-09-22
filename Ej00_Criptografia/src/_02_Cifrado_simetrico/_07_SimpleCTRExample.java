package _02_Cifrado_simetrico;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Basic symmetric encryption example with CTR using AES
 * CTR toma la entrada, la cifra y hace un XOR con el mensaje original
 * No necesita bloques, y el mensaje puede tener cualquier longitud
 */
public class _07_SimpleCTRExample
{   
    public static void main(
        String[]    args)
        throws Exception
    {
    	String texto = "No por mucho madrugar amanece más temprano. "
			    + "No por mucho madrugar amanece más temprano. "
			    + "No por mucho madrugar amanece más temprano. "
			    + "No por mucho madrugar amanece más temprano. "
			    + "No por mucho madrugar amanece más temprano. "
			    + "No por mucho madrugar amanece más temprano. "
			    + "No por mucho madrugar amanece más temprano. "
			    + "No por mucho madrugar amanece más temprano. "
			    + "No por mucho madrugar amanece más temprano. "
			    + "No por mucho madrugar amanece más temprano. "
			    + "No por mucho madrugar amanece más temprano. "
			    + "No por mucho madrugar amanece más temprano. "
			    + "No por mucho madrugar amanece más temprano. "
			    + "No por mucho madrugar amanece más temprano. "
			    + "No por mucho madrugar amanece más temprano. "
			    + "No por mucho madrugar amanece más temprano. "
			    + "No por mucho madrugar amanece más temprano. "
			    + "No por mucho madrugar amanece más temprano. "
			    + "No por mucho madrugar amanece más temprano. "
			    + "No por mucho madrugar amanece más temprano. "
			    + "No por mucho madrugar amanece más temprano. ";
    	byte[] input = texto.getBytes();
    	
        byte[] keyBytes = new byte[] { 
                            0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef,
                            0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef };
        byte[] ivBytes = new byte[] { 
                            0x00, 0x01, 0x02, 0x03, 0x00, 0x00, 0x00, 0x01,
        					0x00, 0x01, 0x02, 0x03, 0x00, 0x00, 0x00, 0x01 };
        
        Security.addProvider(new BouncyCastleProvider()); 
        SecretKeySpec   key = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        //NoPadding no precisa cifrado por bloques
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding", "BC");
        

        System.out.println("input : " + Utils.toHex(input));
        
        //////////////////
        // ENCRIPTACIÓN //
        //////////////////
        
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        
        byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
        
        int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
        
        ctLength += cipher.doFinal(cipherText, ctLength);
        
        System.out.println("cipher: " + Utils.toHex(cipherText, ctLength) + " bytes: " + ctLength);
        
        /////////////////////
        // DESENCRIPTACIÓN //
        /////////////////////
        
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        
        byte[] plainText = new byte[cipher.getOutputSize(ctLength)];

        int ptLength = cipher.update(cipherText, 0, ctLength, plainText, 0);
        
        ptLength += cipher.doFinal(plainText, ptLength);
        
        System.out.println("plain : " + Utils.toHex(plainText, ptLength) + " bytes: " + ptLength);
        System.out.println("texto : " + new String(plainText));

    }
}
