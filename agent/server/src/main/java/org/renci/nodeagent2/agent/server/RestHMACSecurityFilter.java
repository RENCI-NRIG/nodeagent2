package orca.nodeagent2.agent.server;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.GenericFilterBean;

/**
 * http://massimilianosciacco.com/implementing-hmac-authentication-rest-api-spring-security
 * @author ibaldin
 *
 */
public class RestHMACSecurityFilter extends GenericFilterBean {

	// Enable Multi-Read for GET methods
    private static final Set<String> METHOD_HAS_CONTENT = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER) {
        private static final long serialVersionUID = 1L; 
        { add("GET"); }
    };
    
    private AuthenticationManager authenticationManager;
    private AuthenticationEntryPoint authenticationEntryPoint;
    private Md5PasswordEncoder md5 = new Md5PasswordEncoder();

    
    public RestHMACSecurityFilter(AuthenticationManager authenticationManager) {
        this(authenticationManager, new RestHMACAuthenticationEntryPoint());
        ((RestHMACAuthenticationEntryPoint)this.authenticationEntryPoint).setRealmName("NA2 realm2");
    }

    public RestHMACSecurityFilter(AuthenticationManager authenticationManager, AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationManager = authenticationManager;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.md5 = new Md5PasswordEncoder();
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        // use wrapper to read multiple times the content
        RestHMACAuthenticationRequestWrapper request = new RestHMACAuthenticationRequestWrapper((HttpServletRequest) req);
        HttpServletResponse response = (HttpServletResponse) resp;
        
        // Get authorization header
        String credentials = request.getHeader("Authorization");

        // If there's not credentials return...
        if (credentials == null) {
            chain.doFilter(request, response);
            return;
        }

        // Authorization header is in the form <public_access_key>:<signature>
        String auth[] = credentials.split(":");
        
        // get md5 content and content-type if the request is POST or PUT method
        boolean hasContent = METHOD_HAS_CONTENT.contains(request.getMethod());
        String contentMd5 = hasContent ? md5.encodePassword(request.getPayload(), null) : "";
        String contentType = hasContent ? request.getContentType() : "";
        
        // get timestamp
        String timestamp = request.getHeader("Date");

        // calculate content to sign
        StringBuilder toSign = new StringBuilder();
        toSign.append(request.getMethod()).append("\n")
              .append(contentMd5).append("\n")
              .append(contentType).append("\n")
              .append(timestamp).append("\n")
              .append(request.getRequestURI());
        
        // a rest credential is composed by request data to sign and the signature
        RestHMACCredentials restCredential = new RestHMACCredentials(toSign.toString(), auth[1]);

        // calculate UTC time from timestamp (usually Date header is GMT but still...)
        Date date = null;
        try {
        	String[] patterns = new String[1];
        	patterns[0] = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern();
        	System.out.println("PARSING DATE " + timestamp + " using pattern " + patterns[0]);
        	date = DateUtils.parseDate(timestamp, patterns);
        } catch (ParseException  ex) {
            ex.printStackTrace();
       	}
        
        // Create an authentication token
        Authentication authentication = new RestHMACToken(auth[0], restCredential, date);

        try {
            // Request the authentication manager to authenticate the token (throws exception)
            Authentication successfulAuthentication = authenticationManager.authenticate(authentication);
            
            // Pass the successful token to the SecurityHolder where it can be
            // retrieved by this thread at any stage.
            SecurityContextHolder.getContext().setAuthentication(successfulAuthentication);
            // Continue with the Filters
            chain.doFilter(request, response);
        } catch (AuthenticationException authenticationException) {
            // If it fails clear this threads context and kick off the
            // authentication entry point process.
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, authenticationException);
        }
    }

}
