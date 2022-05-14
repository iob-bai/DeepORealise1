package com.deepO.Release_2.service;

import static java.util.concurrent.TimeUnit.*;

import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Service
public class LoginAttemptService {
	private static final int MAXIMUM_NUMBER_OF_ATTEMPT = 5;
	private static final int ATTEMPT_INCREMENT = 1;
	private LoadingCache<String, Integer> loadingAttemptCache;

	public LoginAttemptService() {
		super();
		//build the cache
		loadingAttemptCache = CacheBuilder.newBuilder().expireAfterWrite(15, MINUTES).maximumSize(100)
				.build(new CacheLoader<String, Integer>() {
					public Integer load(String key) {
						return 0;
					}
				});
	}

	// find && remove the user from the cache
	public void evictUserFromLoginAttemptCache(String username) {
		loadingAttemptCache.invalidate(username);
	}
	//increament && adding the user in the cache
	public void addUserToLoginAttemptCache(String username)  {
		int attempts = 0;

		try {
			attempts = ATTEMPT_INCREMENT + loadingAttemptCache.get(username);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loadingAttemptCache.put(username, attempts);

	}
	//verify if the cache is exceeded the max tempts
	public boolean hasExceededMaxAttempts(String username)  {
		try {
			return loadingAttemptCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPT;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
