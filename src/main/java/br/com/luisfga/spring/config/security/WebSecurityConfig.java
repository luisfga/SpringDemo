package br.com.luisfga.spring.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class.getSimpleName());

	@Autowired
	CustomizeAuthenticationSuccessHandler customizeAuthenticationSuccessHandler;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Bean
	public UserDetailsService jpaUserDetailsService() {
		return new CustomUserDetailsService();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		logger.debug("configure(AuthenticationManagerBuilder)");
		UserDetailsService userDetailsService = jpaUserDetailsService();

		auth.userDetailsService(userDetailsService)
			.passwordEncoder(passwordEncoder);
	}

	public AuthenticationProvider authProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(jpaUserDetailsService());
		provider.setPasswordEncoder(passwordEncoder);
		return provider;
	}

	public CustomAuthenticationFilter authenticationFilter() throws Exception {
		CustomAuthenticationFilter filter = new CustomAuthenticationFilter();
		filter.setAuthenticationManager(authenticationManagerBean());
		filter.setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
			@Override
			public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {

				if (e instanceof CustomAuthenticationFilter.AuthenticationCompositeException) {
					logger.debug("AuthenticationCompositeException!");

					CustomAuthenticationFilter.AuthenticationCompositeException ace = (CustomAuthenticationFilter.AuthenticationCompositeException) e;

					String responseParameters = null;
					if (ace.emailLoginException != null) {
						responseParameters = setResponseParameter(responseParameters, "emailLoginError=true");
					}
					if (ace.passwordLoginException != null) {
						responseParameters = setResponseParameter(responseParameters, "passwordLoginError=true");
					}
					response.sendRedirect("/login"+responseParameters);
				} else if (e instanceof BadCredentialsException) {
					logger.debug("BadCredentialsException!");
					response.sendRedirect("/login?error=true");
				}

			}
		});
		return filter;
	}

	private String setResponseParameter(String responseParameters, String parameter){
		if(responseParameters == null) {
			responseParameters="?"+parameter;
			logger.debug("Setting emailError: "+responseParameters);
		} else {
			responseParameters+="&"+parameter;
			logger.debug("Setting passError: "+responseParameters);
		}
		return responseParameters;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)

			.authorizeRequests()

				//actions abertas pra todos
				.antMatchers("/", "/index", "/requestLocale", "/registerInput", "/register", "/login")
				.permitAll()

				.anyRequest()
				.authenticated()

				.and().csrf().disable()

				.formLogin()
					.loginPage("/login")

				.and().logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/").and().exceptionHandling();
	}

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
        }

}
