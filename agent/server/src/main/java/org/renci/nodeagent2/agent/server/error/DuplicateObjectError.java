package org.renci.nodeagent2.agent.server.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.PRECONDITION_FAILED, reason="Duplicate object detected") 
public class DuplicateObjectError extends RuntimeException {
	public DuplicateObjectError(String s) {
		super(s);
	}
}
