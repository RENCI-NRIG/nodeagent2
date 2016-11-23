package org.renci.nodeagent2.agent.server;

/**
 * From http://massimilianosciacco.com/implementing-hmac-authentication-rest-api-spring-security
 * @author ibaldin
 *
 */
public class RestHMACCredentials {
	private String requestData;
	private String signature;

	public RestHMACCredentials(String requestData, String signature) {
		this.requestData = requestData;
		this.signature = signature;
	}

	public String getRequestData() {
		return requestData;
	}

	public String getSignature() {
		return signature;
	}
}
