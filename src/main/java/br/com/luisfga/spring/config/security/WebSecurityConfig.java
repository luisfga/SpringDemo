package br.com.luisfga.spring.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Bean
	public UserDetailsService jpaUserDetailsService() {
		return new CustomUserDetailsService();
	}

	private CustomAuthFilter customAuthenticationFilter() throws Exception {
		//creating filter, setting provider with detailsService and passEncoder
		CustomAuthFilter filter = new CustomAuthFilter(new CustomAuthProvider(jpaUserDetailsService(), passwordEncoder));
		filter.setAuthenticationFailureHandler(new CustomAuthFilterFailureHandler());
		return filter;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.addFilterBefore(customAuthenticationFilter(), CustomAuthFilter.class)

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
