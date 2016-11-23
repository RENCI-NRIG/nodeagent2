package orca.nodeagent2.agent.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

/**
 * From http://massimilianosciacco.com/implementing-hmac-authentication-rest-api-spring-security
 * @author ibaldin
 *
 */
public class RestHMACAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {
	
	RestHMACAuthenticationEntryPoint() {
		setRealmName("NA2 Realm");
	}
	
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.addHeader("WWW-Authenticate", "Basic realm=\"" + getRealmName() + "\"");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter writer = response.getWriter();
        writer.println("HTTP Status " + HttpServletResponse.SC_UNAUTHORIZED + " - " + authException.getMessage());
    }
}
