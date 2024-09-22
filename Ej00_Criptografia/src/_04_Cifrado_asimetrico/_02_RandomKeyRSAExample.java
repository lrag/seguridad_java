package _04_Cifrado_asimetrico;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * RSA example with random key generation.
 */
public class _02_RandomKeyRSAExample{
	
    public static void main(String[] args) throws Exception{
    	
        Security.addProvider(new BouncyCastleProvider());
    	
        byte[] input = new byte[] { 
        		(byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07, (byte)0x08,
        		(byte)0x09, (byte)0x0a, (byte)0x0b, (byte)0x0c, (byte)0x0d, (byte)0x0e, (byte)0x0f, (byte)0x10,
        		(byte)0x11, (byte)0x12, (byte)0x13, (byte)0x14, (byte)0x15, (byte)0x16, (byte)0x17, (byte)0x18,
        		(byte)0x19, (byte)0x1a, (byte)0x1b, (byte)0x1c, (byte)0x1d, (byte)0x1e, (byte)0x1f, (byte)0x20
        };
        Cipher cipher = Cipher.getInstance("RSA/NONE/NoPadding", "BC");
        SecureRandom random = Utils.createFixedRandom();
        
        // create the keys
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
        
        generator.initialize(256, random);

        KeyPair pair = generator.generateKeyPair();
        Key pubKey = pair.getPublic();
        Key privKey = pair.getPrivate();
        
        System.out.println("input : " + Utils.toHex(input));
        
        // encryption step
        
        cipher.init(Cipher.ENCRYPT_MODE, pubKey, random);

        byte[] cipherText = cipher.doFinal(input);

        System.out.println("cipher: " + Utils.toHex(cipherText));
        
        // decryption step

        cipher.init(Cipher.DECRYPT_MODE, privKey);

        byte[] plainText = cipher.doFinal(cipherText);
        
        System.out.println("plain : " + Utils.toHex(plainText));
    }
}
