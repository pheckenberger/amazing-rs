package com.amazing.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Organization security configuration.
 * 
 * @author hp
 */
@Configuration
public class OrganizationSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private OrganizationProperties organizationProperties;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.inMemoryAuthentication().withUser(organizationProperties.getApiReaderUser())
				.password(organizationProperties.getApiReaderPassword()).roles("READER");
		auth.inMemoryAuthentication().withUser(organizationProperties.getApiWriterUser())
				.password(organizationProperties.getApiWriterPassword()).roles("WRITER");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.cors();
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.httpBasic();

		http.authorizeRequests().mvcMatchers(HttpMethod.GET, "/v1/*").hasAnyRole("READER", "WRITER");
		http.authorizeRequests().mvcMatchers(HttpMethod.POST, "/v1/*").hasRole("WRITER");
		http.authorizeRequests().mvcMatchers(HttpMethod.PATCH, "/v1/*").hasRole("WRITER");
		http.authorizeRequests().mvcMatchers(HttpMethod.DELETE, "/v1/*").hasRole("WRITER");
	}
}
