package orca.nodeagent2.agent.server.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR, reason="Server encountered an error, inspect the logs") 
public class InternalError extends RuntimeException {
	public InternalError(String s) {
		super(s);
	}
}
