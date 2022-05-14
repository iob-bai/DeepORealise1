package com.deepO.Release_2.utility.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.deepO.Release_2.utility.jwt.filter.JwtAccessDeniedHandler;
import com.deepO.Release_2.utility.jwt.filter.JwtAuthenticationEntryPoint;
import com.deepO.Release_2.utility.jwt.filter.JwtAuthorizationFilter;

import static com.deepO.Release_2.utility.constant.SecurityConstant.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	@Autowired
	private JwtAuthorizationFilter jwtAuthorizationFilter;
	@Autowired
	private JwtAccessDeniedHandler jwtAccessDeniedHandler;
	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

//    @Autowired
//    public SecurityConfiguration(JwtAuthorizationFilter jwtAuthorizationFilter,
//                                 JwtAccessDeniedHandler jwtAccessDeniedHandler,
//                                 JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
//                                 @Qualifier("userDetailsService")UserDetailsService userDetailsService,
//                                 BCryptPasswordEncoder bCryptPasswordEncoder) {
//        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
//        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
//        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
//        this.userDetailsService = userDetailsService;
//        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().cors().and()
                .sessionManagement().sessionCreationPolicy(STATELESS)
                .and().authorizeRequests().antMatchers(PUBLIC_URLS).permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedHandler(jwtAccessDeniedHandler)
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
    }

	@Bean
	@Override 
	public AuthenticationManager authenticationManagerBean() throws Exception 
	{
		return super.authenticationManagerBean();
	}
}
