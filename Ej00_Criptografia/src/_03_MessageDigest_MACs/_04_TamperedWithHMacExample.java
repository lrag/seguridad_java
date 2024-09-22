package _03_MessageDigest_MACs;

import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Tampered message with HMac, encryption AES in CTR mode
 */
public class _04_TamperedWithHMacExample{
	
    public static void main(String[] args) throws Exception{
    	
    	Security.addProvider(new BouncyCastleProvider());
    	
        SecureRandom	random = new SecureRandom();
        IvParameterSpec ivSpec = Utils.createCtrIvForAES(1, random);
        //Clave aleatoria para el MENSAJE
        Key             key = Utils.createKeyForAES(256, random);
        Cipher          cipher = Cipher.getInstance("AES/CTR/NoPadding", "BC");
        String          input = "Transfer 0000100 to AC 1234-5678";
        Mac             hMac = Mac.getInstance("HMacSHA1", "BC");
        //Es recomendable utilizar otra clave para el Mac
        Key             hMacKey = new SecretKeySpec(key.getEncoded(), "HMacSHA1");
        
        System.out.println("input : " + input);
        
        // encryption step
        
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        
        byte[] cipherText = new byte[cipher.getOutputSize(input.length() + hMac.getMacLength())];

        int ctLength = cipher.update(Utils.toByteArray(input), 0, input.length(), cipherText, 0);
        
        hMac.init(hMacKey);
        hMac.update(Utils.toByteArray(input));
        
        ctLength += cipher.doFinal(hMac.doFinal(), 0, hMac.getMacLength(), cipherText, ctLength);
        
        // tampering step
        
        cipherText[9] ^= '0' ^ '9';
        
        // replace digest
        
        // ? 
        
        // decryption step

        Mac hMac2 = Mac.getInstance("HMacSHA1", "BC");
        Key hMacKey2 = new SecretKeySpec(key.getEncoded(), "HMacSHA1");
                
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        
        byte[] plainText = cipher.doFinal(cipherText, 0, ctLength);
        int    messageLength = plainText.length - hMac.getMacLength();
        
        hMac2.init(hMacKey2);
        hMac2.update(plainText, 0, messageLength);
        
        byte[] messageHash = new byte[hMac2.getMacLength()];
        System.arraycopy(plainText, messageLength, messageHash, 0, messageHash.length);
        
        System.out.println("plain : " + Utils.toString(plainText, messageLength) + " verified: " + MessageDigest.isEqual(hMac.doFinal(), messageHash));
    }
}
