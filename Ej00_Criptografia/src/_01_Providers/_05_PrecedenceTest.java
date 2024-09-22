package _01_Providers;

import java.security.Security;

import javax.crypto.Cipher;

/**
 * Basic demonstration of precedence in action.
 */
public class _05_PrecedenceTest
{
    public static void main(
        String[]    args)
        throws Exception
    {
    	Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    	
        Cipher cipher = Cipher.getInstance("Blowfish/ECB/NoPadding");
        
        System.out.println(cipher.getProvider());
        
        cipher = Cipher.getInstance("Blowfish/ECB/NoPadding", "BC");
        
        System.out.println(cipher.getProvider());
    }
}
