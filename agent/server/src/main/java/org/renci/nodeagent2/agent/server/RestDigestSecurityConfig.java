package org.renci.nodeagent2.agent.server;

import org.renci.nodeagent2.agent.config.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class RestDigestSecurityConfig extends WebSecurityConfigurerAdapter
{
	private static final String DIGEST_REALM = "NA2 Digest Authentication Realm";
	private static final String DIGEST_KEY = "Dd54ZXeGa5";

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception
	{
		auth.inMemoryAuthentication().withUser("admin").password(Config.getInstance().getPassword()).roles("USER");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception
	{
		http
		.csrf().disable() // disable CSRF
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // make stateless
		.and()
		.authorizeRequests().antMatchers("/**").authenticated() // require authentication everywhere
		.and()
		.exceptionHandling().authenticationEntryPoint(digestEntryPoint()); // point to digest entry point on failure (generates 401)

		http.addFilterAfter(digestAuthenticationFilter(digestEntryPoint()), BasicAuthenticationFilter.class);
	}

	/**
	 * The only kind of override that is allowed here
	 */
	@Override
	@Bean
	public UserDetailsService userDetailsServiceBean() throws Exception
	{
		return super.userDetailsServiceBean();
	}

	public DigestAuthenticationFilter digestAuthenticationFilter(DigestAuthenticationEntryPoint digestAuthenticationEntryPoint) throws Exception
	{
		DigestAuthenticationFilter digestAuthenticationFilter = new DigestAuthenticationFilter();
		digestAuthenticationFilter.setAuthenticationEntryPoint(digestEntryPoint());
		digestAuthenticationFilter.setUserDetailsService(userDetailsServiceBean());
		return digestAuthenticationFilter;
	}

	@Bean
	public DigestAuthenticationEntryPoint digestEntryPoint()
	{
		DigestAuthenticationEntryPoint digestAuthenticationEntryPoint = new DigestAuthenticationEntryPoint();
		digestAuthenticationEntryPoint.setKey(DIGEST_KEY);
		digestAuthenticationEntryPoint.setRealmName(DIGEST_REALM);
		return digestAuthenticationEntryPoint;
	}
}
