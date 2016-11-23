package orca.nodeagent2.agent.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


public class SimpleSecurityFilter implements Filter {

	public void destroy() {
		System.out.println("Destroying filter");
	}

	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		System.out.println("Call filter with " + arg0 + " " + arg1 + " " + arg2); 

	}

	public void init(FilterConfig arg0) throws ServletException {
		System.out.println("INIT FILTER with config " + arg0);

	}

}
