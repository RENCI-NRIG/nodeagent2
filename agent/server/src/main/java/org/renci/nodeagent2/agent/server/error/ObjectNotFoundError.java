package orca.nodeagent2.agent.server.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Object not found") 
public class ObjectNotFoundError extends RuntimeException {
	public ObjectNotFoundError(String s) {
		super(s);
	}
}
