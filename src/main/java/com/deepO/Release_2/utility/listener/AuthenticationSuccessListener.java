package com.deepO.Release_2.utility.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import com.deepO.Release_2.models.User;
import com.deepO.Release_2.service.LoginAttemptService;
import com.deepO.Release_2.utility.domain.UserPrincipal;

@Component
public class AuthenticationSuccessListener {
	@Autowired
	private LoginAttemptService loginAttemptService;

	@EventListener
	public void onAuthenticationSuccess(AuthenticationSuccessEvent event) 
	{
		Object principal = event.getAuthentication().getPrincipal();
		if(principal instanceof UserPrincipal) {
			UserPrincipal user = (UserPrincipal) event.getAuthentication().getPrincipal();
			loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
		}
	} 
}
