package com.deepO.Release_2.utility.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import com.deepO.Release_2.service.LoginAttemptService;

import lombok.AllArgsConstructor;


@AllArgsConstructor
@Component
public class AuthenticationFailtureListener {
	@Autowired
	private LoginAttemptService loginAttemptService;

	@EventListener
	public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) 
	{
		Object principal = event.getAuthentication().getPrincipal();
		if(principal instanceof String) {
			String username = (String) event.getAuthentication().getPrincipal();
			loginAttemptService.addUserToLoginAttemptCache(username);
		}
	}
	
	
	
}
