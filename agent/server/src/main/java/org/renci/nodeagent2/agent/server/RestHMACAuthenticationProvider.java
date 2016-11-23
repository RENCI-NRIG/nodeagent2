package orca.nodeagent2.agent.server;

import java.security.GeneralSecurityException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;


/**
 * From http://massimilianosciacco.com/implementing-hmac-authentication-rest-api-spring-security
 * @author ibaldin
 *
 */
public class RestHMACAuthenticationProvider implements AuthenticationProvider {

    
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    	System.out.println("AUTHENTICATE REQUEST");
        RestHMACToken restToken = (RestHMACToken) authentication;

        // hashed blob
        RestHMACCredentials credentials = restToken.getCredentials();

        // get secret access key from api key
        String secret = "abra";
        
        // calculate the hmac of content with secret key
        String hmac = calculateHMAC(secret, credentials.getRequestData());
        // check if signatures match
        if (!credentials.getSignature().equals(hmac)) {
            throw new BadCredentialsException("Invalid username or password.");
        }

        // this constructor create a new fully authenticated token, with the "authenticated" flag set to true
        // we use null as to indicates that the user has no authorities. you can change it if you need to set some roles.
        restToken = new RestHMACToken("user", credentials, restToken.getTimestamp(), null);
        
        return restToken;
    }

    public boolean supports(Class<?> authentication) {
        return RestHMACToken.class.equals(authentication);
    }

    private String calculateHMAC(String secret, String data) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            String result = new String(Base64.encodeBase64(rawHmac));
            return result;
        } catch (GeneralSecurityException e) {
            throw new IllegalArgumentException();
        }
    }

}
