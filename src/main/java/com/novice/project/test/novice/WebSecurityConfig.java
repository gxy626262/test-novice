package com.novice.project.test.novice;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
				.authorizeRequests()
				.antMatchers("/decision/**", "/govern/**", "/employee/*").hasAnyRole("EMPLOYEE", "ADMIN")
				.antMatchers("/employee/login").permitAll()
				.antMatchers("/admin/**").hasRole("ADMIN")
				.antMatchers("/oauth/**").permitAll()
				.anyRequest().permitAll()
				.and().anonymous()
				.and().formLogin()
				.and().httpBasic();
	}
}
