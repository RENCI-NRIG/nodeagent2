package org.renci.nodeagent2.agent.server;

import java.util.Collection;
import java.util.Date;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class RestHMACToken extends UsernamePasswordAuthenticationToken {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Date timestamp;

	// this constructor creates a non-authenticated token (see super-class)
	public RestHMACToken(String principal, RestHMACCredentials credentials, Date timestamp) {
		super(principal, credentials);
		this.timestamp = timestamp;
	}

	// this constructor creates an authenticated token (see super-class)
	public RestHMACToken(String principal, RestHMACCredentials credentials, Date timestamp, Collection authorities) {
		super(principal, credentials, authorities);
		this.timestamp = timestamp;
	}

	@Override
	public String getPrincipal() {
		return (String) super.getPrincipal();
	}

	@Override
	public RestHMACCredentials getCredentials() {
		return (RestHMACCredentials) super.getCredentials();
	}

	public Date getTimestamp() {
		return timestamp;
	}

}
