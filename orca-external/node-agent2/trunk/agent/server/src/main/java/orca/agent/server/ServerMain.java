package orca.agent.server;

import orca.agent.config.Config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerMain {
	
	public static void main(String[] argv) {
		Log l = LogFactory.getLog("serverMain");
		try {
			Config.initialize(argv[0]);
		} catch (Exception e) {
			l.error(e);
		}
		SpringApplication.run(ServerMain.class, argv);
	}
}
