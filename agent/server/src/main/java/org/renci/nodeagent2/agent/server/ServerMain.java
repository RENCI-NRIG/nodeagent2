package org.renci.nodeagent2.agent.server;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.Permission;

import org.renci.nodeagent2.agent.config.Config;
import org.renci.nodeagent2.agent.core.PluginsRegistry;

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
		private static final String CONFIG_NA2 = "na2.config";
		private static final String CONFIG_LOGGING = "logging.config";

		public void initialize(GenericApplicationContext ac) {

			// init logging
			String loggingConfig = ac.getEnvironment().getProperty(CONFIG_LOGGING);
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

				String na2Config = ac.getEnvironment().getProperty(CONFIG_NA2);

				if (na2Config == null) {
					l.error("Property na2.config pointing to NA2 plugin configuration is not specified, exiting");
					System.exit(1);
				}

				try {
					l.info("Initializing Node Agent with " + na2Config);
					Config.initialize(na2Config);

					l.info("Initializing plugins");
					PluginsRegistry.getInstance().initialize();

				} catch (Exception e) {
					l.error("Unable to initialize NA2: " + e);
					e.printStackTrace();
					System.exit(1);
				}
			} else {
				System.err.println("Logging is not configured, unable to proceed, exiting. Please check that SPRING_CONFIG_LOCATION env variable points to a valid configuration file.");
				System.exit(0);
			}
			
		}
	}

	public static void main(String[] argv) {

		// start up spring
		SpringApplication spring = new SpringApplication(ServerMain.class);

		NA2Initializer na2 = new NA2Initializer();
		spring.addInitializers(na2);
		spring.run(argv);

	}
}
