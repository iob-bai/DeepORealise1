package com.deepO.Release_2.utility.constant;

public class SecurityConstant {
	public static final long EXPIRATION_TIME = 432_000_000;//5 days in milliseconds
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String JWT_TOKEN_HEADER = "Jwt-Token";
	public static final String TOKEN_CANNOT_BE_VERIFIED ="TOKEN CANNOT BE VERIFIED";
	public static final String DEEP_O_LLC="DEEP O ,LLC";
	public static final String DEEP_O_ADMINISTRATION ="USER MANAGEMENT";
	public static final String AUTHORITIES="AUTHORITIES";
	public static final String FORBIDEN_MESSAGE ="you need to log in to access this page";
	public static final String ACCESS_DENIED_MESSAGE="You don't have permission to access";
	public static final String OPTIONS_HTTP_METHOD = "Options";
	public static final String[] PUBLIC_URLS = {"/user/login","/user/register","/user/resetpassword/**","/user/image/**","/deposit/image/**","/item/image/**" };
	//public static final String[] PUBLIC_URLS = {"**"}; 
	//public static final String[] PUBLIC_URLS = {"/user/login","/user/register"};
	
	
	
}
