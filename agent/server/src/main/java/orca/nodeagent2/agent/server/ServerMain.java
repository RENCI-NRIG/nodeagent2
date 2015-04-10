package orca.nodeagent2.agent.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import orca.nodeagent2.agent.config.Config;
import orca.nodeagent2.agent.core.PluginsRegistry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

@SpringBootApplication
public class ServerMain {
	private static final String DEFAULT_LOG4J="orca/nodeagent2/agent/server/log4j-default.properties";

	// SPRING gets its configuration from externally specified (via SPRING_CONFIG_LOCATION env. variable)
	// it's a yaml file and in it there is a 'logging.config' variable that specified the log4j config file that is used by
	// NA2 as well.
	private static class NA2Initializer implements ApplicationContextInitializer<GenericApplicationContext> {
		String[] argv;

		public NA2Initializer(String[] a) {
			argv = a;
		}

		public void initialize(GenericApplicationContext ac) {

			// init logging
			String loggingConfig = ac.getEnvironment().getProperty("logging.config");
			boolean defaultConfig = true;

			if (loggingConfig != null) {
				File lpf = new File(loggingConfig);
				if (lpf.exists()) { 
					try {
						PropertyConfigurator.configure(new FileInputStream(lpf));
						defaultConfig = false;
					} catch(FileNotFoundException fnfe) {
						;
					}
				}
				
				if (defaultConfig) 
					PropertyConfigurator.configure(ServerMain.class.getClassLoader().getResourceAsStream(DEFAULT_LOG4J));

				Log l = LogFactory.getLog("serverMain");
				l.info("NA2 using " + (defaultConfig ? "built-in log4j configuration" : "user-specified configuration in " + loggingConfig));

				try {
					l.info("Initializing config parser");
					Config.initialize(argv[0]);
					
					l.info("Initializing plugins");
					PluginsRegistry.getInstance().initialize();
					
				} catch (Exception e) {
					l.error("Unable to initialize NA2: " + e);
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
	}

	public static void main(String[] argv) {
		// start up spring
		SpringApplication spring = new SpringApplication(ServerMain.class);

		NA2Initializer na2 = new NA2Initializer(argv);

		spring.addInitializers(na2);

		spring.run(argv);

	}
}
