package _01_Providers;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Basic class to confirm the Bouncy Castle provider is 
 * installed.
 */
public class _03_SimpleProviderTest
{
    public static void main(String[] args)
    {
    	Security.addProvider(new BouncyCastleProvider());
        String providerName = "BC";
                
        if (Security.getProvider(providerName) == null){
            System.out.println(providerName + " provider not installed");
        } else {
            System.out.println(providerName + " is installed.");
        }
    }
}
