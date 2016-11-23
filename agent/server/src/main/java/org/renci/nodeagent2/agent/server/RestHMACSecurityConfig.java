package org.renci.nodeagent2.agent.server;


import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Disabled and not tested HMAC security config 04/14/2015/ib
 * @author ibaldin
 *
 */

//@Configuration
//@EnableGlobalMethodSecurity(securedEnabled = true)
public class RestHMACSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(restAuthenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http
		.csrf().disable() // disable CSRF
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // make stateless
		.and()
		.authorizeRequests().antMatchers("/**").authenticated() // require authentication everywhere
		.and()
		.exceptionHandling().authenticationEntryPoint(restHMACAuthEntryPoint()); // point to HMAC entry point on failure (generates 401)

		http.addFilterBefore(new RestHMACSecurityFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);
	}

	//@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(restAuthenticationProvider());
	}

	//@Bean
	public AuthenticationProvider restAuthenticationProvider() {
		RestHMACAuthenticationProvider provider = new RestHMACAuthenticationProvider();
		return provider;
	}
	
	//@Bean
	public RestHMACAuthenticationEntryPoint restHMACAuthEntryPoint()
	{
		RestHMACAuthenticationEntryPoint authenticationEntryPoint = new RestHMACAuthenticationEntryPoint();
		return authenticationEntryPoint;
	}
	
}
