package _02_Cifrado_simetrico;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * CBC con un IV basado en un nonce (number used once). 
 * En este caso un hipotético 'número de mensaje'
 */
public class _06_NonceIvCBCExample{
	
    public static void main(String[] args) throws Exception{
    	
        byte[] input = new byte[] { 
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 
                0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 };
        byte[] keyBytes = new byte[] { 
                0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef };
        byte[] msgNumber = new byte[] {
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02 };
        
        //Si es DES el IV de 8 bytes
        //Si es AES de 16.
        IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
        
        Security.addProvider(new BouncyCastleProvider()); 
        SecretKeySpec   key = new SecretKeySpec(keyBytes, "DES");
        
        Cipher          cipher = Cipher.getInstance("DES/CBC/PKCS7Padding", "BC");
        

        System.out.println("input : " + Utils.toHex(input));
        
        //////////////////
        // ENCRIPTACIÓN //
        //////////////////
        
        // Generar un IV
        
        cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
        
        IvParameterSpec encryptionIv = new IvParameterSpec(cipher.doFinal(msgNumber), 0, 8);
        
        //Encriptar        
        cipher.init(Cipher.ENCRYPT_MODE, key, encryptionIv);
        
        byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
        
        int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
        
        ctLength += cipher.doFinal(cipherText, ctLength);
        
        System.out.println("cipher: " + Utils.toHex(cipherText, ctLength) + " bytes: " + ctLength);
        
        /////////////////////
        // DESENCRIPTACIÓN //
        /////////////////////
        
        //Generar IV        
        cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
        
        //El número de mensaje lo tiene el receptor y no es secreto.
        //Cada vez es distinto
        IvParameterSpec decryptionIv = new IvParameterSpec(cipher.doFinal(msgNumber), 0, 8);
        
        //Desencriptar        
        cipher.init(Cipher.DECRYPT_MODE, key, decryptionIv);
        
        byte[] plainText = new byte[cipher.getOutputSize(ctLength)];

        int ptLength = cipher.update(cipherText, 0, ctLength, plainText, 0);
        
        ptLength += cipher.doFinal(plainText, ptLength);
        
        System.out.println("plain : " + Utils.toHex(plainText, ptLength) + " bytes: " + ptLength);
    }
}
