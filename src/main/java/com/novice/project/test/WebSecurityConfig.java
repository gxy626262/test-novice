package com.novice.project.test;

import com.novice.framework.usersystem.domain.Role;
import com.novice.framework.usersystem.security.CustomResponseAuthenticationFailureHandler;
import com.novice.framework.usersystem.security.CustomResponseAuthenticationSuccessHandler;
import com.novice.framework.usersystem.security.UserLogoutSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	private static final String REMEMBER_ME_KEY = "novice";

	@Resource(name = "us.UserDetailsService")
	private UserDetailsService userDetailsService;
	@Resource(name = "us.PasswordEncoder")
	private PasswordEncoder passwordEncoder;

	@Bean
	public AuthenticationManager authenticationManager() {
		List<AuthenticationProvider> providers = Arrays.asList(daoAuthenticationProvider(), rememberMeAuthenticationProvider());
		return new ProviderManager(providers);
	}

	@Bean(name = "us.rememberMeServices")
	public RememberMeServices rememberMeServices() {
		return new TokenBasedRememberMeServices(REMEMBER_ME_KEY, this.userDetailsService);
	}

	@Bean(name = "us.authenticationSuccessHandler")
	public AuthenticationSuccessHandler authenticationSuccessHandler() {
		CustomResponseAuthenticationSuccessHandler successHandler = new CustomResponseAuthenticationSuccessHandler();
		successHandler.setAlwaysUseDefaultTargetUrl(true);
		successHandler.setDefaultTargetUrl("/login.html?$=success");
		return successHandler;
	}

	@Bean(name = "us.authenticationFailureHandler")
	public AuthenticationFailureHandler authenticationFailureHandler() {
		CustomResponseAuthenticationFailureHandler failureHandler = new CustomResponseAuthenticationFailureHandler();
		failureHandler.setDefaultFailureUrl("/login.html?$=deny");
		return failureHandler;
	}

	@Bean
	public LogoutSuccessHandler logoutSuccessHandler() {
		return new UserLogoutSuccessHandler();
	}

	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}

	@Bean
	public SessionManagementFilter sessionManagementFilter() {
		return new SessionManagementFilter(new HttpSessionSecurityContextRepository(), this.sessionAuthenticationStrategy());
	}

	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	@Bean
	public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		ConcurrentSessionControlAuthenticationStrategy concurrentSessionControlAuthenticationStrategy = new ConcurrentSessionControlAuthenticationStrategy(this.sessionRegistry());
		// 设置账号允许的最大登录Session数,设置为-1则不做限制.
		concurrentSessionControlAuthenticationStrategy.setMaximumSessions(-1);
		RegisterSessionAuthenticationStrategy registerSessionAuthenticationStrategy = new RegisterSessionAuthenticationStrategy(this.sessionRegistry());
		return new CompositeSessionAuthenticationStrategy(Arrays.asList(concurrentSessionControlAuthenticationStrategy, registerSessionAuthenticationStrategy));
	}

	@Bean
	public ConcurrentSessionFilter concurrentSessionFilter() {
		return new ConcurrentSessionFilter(sessionRegistry());
	}

	@Bean
	public UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter() {
		var usernamePasswordAuthenticationFilter = new UsernamePasswordAuthenticationFilter();
		usernamePasswordAuthenticationFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login", "POST"));
		usernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler(this.authenticationSuccessHandler());
		usernamePasswordAuthenticationFilter.setAuthenticationFailureHandler(this.authenticationFailureHandler());
		usernamePasswordAuthenticationFilter.setAuthenticationManager(this.authenticationManager());
		usernamePasswordAuthenticationFilter.setSessionAuthenticationStrategy(this.sessionAuthenticationStrategy());
		usernamePasswordAuthenticationFilter.setRememberMeServices(this.rememberMeServices());
		return usernamePasswordAuthenticationFilter;
	}

	@Bean(name = "us.daoAuthenticationProvider")
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setHideUserNotFoundExceptions(false);
		daoAuthenticationProvider.setUserDetailsService(this.userDetailsService);
		daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder);
		return daoAuthenticationProvider;
	}

	@Bean(name = "us.rememberMeAuthenticationProvider")
	public RememberMeAuthenticationProvider rememberMeAuthenticationProvider() {
		return new RememberMeAuthenticationProvider(REMEMBER_ME_KEY);
	}

	@Bean
	public AuthenticationEntryPoint api401StatusEntryPoint() {
		return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(this.userDetailsService).passwordEncoder(this.passwordEncoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.addFilter(this.concurrentSessionFilter());
		http.addFilter(this.sessionManagementFilter());
		http.addFilterAt(this.usernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
		http.csrf().disable()
				.authorizeRequests()
				.antMatchers("/js/**").permitAll()
				.antMatchers("/images/**").permitAll()
				.antMatchers("/favicon.ico").permitAll()
				.antMatchers("/css/**").permitAll()
				.antMatchers("/login.html").permitAll()
				.antMatchers("/login").permitAll()
				.antMatchers("/logout").permitAll()
				.antMatchers("/novice/file/**").permitAll()
				.antMatchers("/novice/i18n/**").permitAll()
				.antMatchers("/novice/user/checkLogin").permitAll()
				.antMatchers("/novice/menu/**").authenticated()
				.antMatchers("/novice/metaLoader/**").authenticated()
				.antMatchers("/novice/**").authenticated()
				.anyRequest().hasRole(Role.ADMIN_ROLE_ID)
				.and()
				.rememberMe().key(REMEMBER_ME_KEY).rememberMeServices(this.rememberMeServices())
				.and().anonymous()
				.and().formLogin()
				.loginPage("/login.html").permitAll()
				.and()
				.logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
				.invalidateHttpSession(true)
				.logoutSuccessHandler(this.logoutSuccessHandler())
				.deleteCookies("JSESSIONID")
				.permitAll()
				.and()
				.anonymous()
				.and()
				.exceptionHandling()
				.accessDeniedPage("/errors/403.html")
				.defaultAuthenticationEntryPointFor(api401StatusEntryPoint(), new AntPathRequestMatcher("/**"));
	}
}
