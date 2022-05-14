package com.deepO.Release_2.service.impl;

import static com.deepO.Release_2.utility.constant.DepositExceptionConstant.NO_DEPOSIT_FOUND_BY_DEPOSIT_NAME;
import static com.deepO.Release_2.utility.constant.FileConstant.*;
import static com.deepO.Release_2.utility.constant.UserExceptionConstant.*;
import static com.deepO.Release_2.utility.enumeration.Role.ROLE_USER;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.MediaType.IMAGE_GIF_VALUE;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.deepO.Release_2.models.Deposit;
import com.deepO.Release_2.models.User;
import com.deepO.Release_2.repository.UserRepository;
import com.deepO.Release_2.service.EmailService;
import com.deepO.Release_2.service.LoginAttemptService;
import com.deepO.Release_2.service.inter.DepositService;
import com.deepO.Release_2.service.inter.UserService;
import com.deepO.Release_2.utility.domain.UserPrincipal;
import com.deepO.Release_2.utility.enumeration.Role;
import com.deepO.Release_2.utility.exception.domain.deposit.DepositNameNotFoundException;
import com.deepO.Release_2.utility.exception.domain.users.EmailExistException;
import com.deepO.Release_2.utility.exception.domain.users.EmailNotFoundException;
import com.deepO.Release_2.utility.exception.domain.users.NotAnImageFileException;
import com.deepO.Release_2.utility.exception.domain.users.UserNotFoundException;
import com.deepO.Release_2.utility.exception.domain.users.UsernameExistException;

@Service

@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {
	private Logger LOGGER = LoggerFactory.getLogger(getClass());
	// ---------------------------------------------
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private DepositService depositService;
	// ---------------------------------------------
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private LoginAttemptService loginAttemptService;
	@Autowired
	private EmailService emailService;


	// ---------------------------------------------
//    @Autowired
//    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//    }
	// ---------------------------------------------
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findUserByUsername(username);
		if (user == null) {
			LOGGER.error(NO_USER_FOUND_BY_USERNAME + username);
			throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
		} else {
			validateLoginAttenmpt(user);
			user.setLastLoginDateDisplay(user.getLastLoginDate());
			user.setLastLoginDate(new Date());
			userRepository.save(user);
			UserPrincipal userPrincipal = new UserPrincipal(user);
			LOGGER.info(FOUND_USER_BY_USERNAME + username);
			return userPrincipal;
		}
	}

	
	@Override
	public User addDepositIntoUser(String username, String depositName) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
	
	
	// ---------------------------------------------

	@Override
	public User register(String firstName, String lastName, String username, String email)
			throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
		validateNewUsernameAndEmail(EMPTY, username, email);
		User user = new User();

		String password = generatePassword();
		String encodedPassword = encodePassword(password);

		user.setUserId(generateUserId());
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUsername(username);
		user.setEmail(email);
		user.setJoinDate(new Date());
		user.setPassword(encodedPassword);
		user.setActive(true);
		user.setNotLocked(true);
		user.setRole(ROLE_USER.name());
		user.setAuthorities(ROLE_USER.getAuthorities());
		user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
		userRepository.save(user);
		LOGGER.info("New user password: " + password);
		emailService.sendNewPasswordEmail(firstName, password, email);
		return user;
	}

	// ---------------------------------------------
	@Override
	public User addNewUser(String firstName, String lastName, String userName, String email, String role,
			boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException,
			UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
		validateNewUsernameAndEmail(EMPTY, userName, email);
		User user = new User();

		String password = generatePassword();
		String encodedPassword = encodePassword(password);

		user.setUserId(generateUserId());
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUsername(userName);
		user.setEmail(email);
		user.setJoinDate(new Date());
		user.setPassword(encodedPassword);
		user.setActive(isActive);
		user.setNotLocked(isNonLocked);
		user.setRole(getRoleEnumName(role).name());
		user.setAuthorities(getRoleEnumName(role).getAuthorities());
		user.setProfileImageUrl(getTemporaryProfileImageUrl(userName));
		LOGGER.info("New added user password: " + password);
		userRepository.save(user);
		saveProfileImage(user, profileImage);

		return user;
	}

	// ---------------------------------------------
	@Override
	public User updateUser(String currentUserName, String newFirstName, String newLastName, String newUserName,
			String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage)
			throws UserNotFoundException, UsernameExistException, EmailExistException, IOException,
			NotAnImageFileException {

		User currentUser = validateNewUsernameAndEmail(currentUserName, newUserName, newEmail);

		currentUser.setFirstName(newFirstName);
		currentUser.setLastName(newLastName);
		currentUser.setUsername(newUserName);
		currentUser.setEmail(newEmail);
		currentUser.setActive(isActive);
		currentUser.setNotLocked(true);
		currentUser.setRole(getRoleEnumName(role).name());
		currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());

		userRepository.save(currentUser);
		saveProfileImage(currentUser, profileImage);

		return currentUser;
	}

	// ---------------------------------------------
	@Override
	public void deleteUser(String username) throws IOException {
		User user = userRepository.findUserByUsername(username);
		Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
		FileUtils.deleteDirectory(new File(userFolder.toString()));
		userRepository.deleteById(user.getId());
	}

	// ---------------------------------------------
	@Override
	public void resetPassword(String email) throws EmailNotFoundException, MessagingException {
		User user = userRepository.findUserByEmail(email);
		if (user == null) {
			throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
		}
		String password = generatePassword();
		String encodedPassword = encodePassword(password);
		user.setPassword(encodedPassword);
		userRepository.save(user);
		emailService.sendNewPasswordEmail(user.getFirstName(), password, user.getEmail());

	}

	// ---------------------------------------------
	@Override
	public User UpdateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException,
			UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
		User user = validateNewUsernameAndEmail(username, null, null);
		saveProfileImage(user, profileImage);
		return user;
	}

	// ---------------------------------------------
	@Override
	public List<User> getUsers() {
		return userRepository.findAll();
	}

	// ---------------------------------------------
	@Override
	public User findUserByUsername(String username) {
		return userRepository.findUserByUsername(username);
	}

	// ---------------------------------------------
	@Override
	public User findUserByEmail(String email) {
		return userRepository.findUserByEmail(email);
	}

	// ==============================================================================================

	private String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

	// ---------------------------------------------
	private String generatePassword() {
		return RandomStringUtils.randomAlphanumeric(10);
	}

	// ---------------------------------------------
	private String generateUserId() {
		return RandomStringUtils.randomNumeric(10);
	}

	// ---------------------------------------------
	private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail)
			throws UserNotFoundException, UsernameExistException, EmailExistException {

		User userByNewUsername = findUserByUsername(newUsername);
		User userByNewEmail = findUserByEmail(newEmail);

		if (StringUtils.isNotBlank(currentUsername)) {
			User currentUser = findUserByUsername(currentUsername);
			if (currentUser == null) {
				throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
			}
			if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
				throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
			}
			if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
				throw new EmailExistException(EMAIL_ALREADY_EXISTS);
			}
			return currentUser;
		} else {
			if (userByNewUsername != null) {
				throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
			}
			if (userByNewEmail != null) {
				throw new EmailExistException(EMAIL_ALREADY_EXISTS);
			}
			return null;
		}
	}
	
	
	private Deposit ifExistDeposit(String deositName ) throws DepositNameNotFoundException 
	{

		if (StringUtils.isNotEmpty(deositName)) {
			Deposit CurrentDeposit = depositService.findDepositByName(deositName);
			if (CurrentDeposit == null) {
				throw new DepositNameNotFoundException(NO_DEPOSIT_FOUND_BY_DEPOSIT_NAME);
			}
			return CurrentDeposit;
		}
		return null;
	
		
	}

	// ---------------------------------------------
	private void validateLoginAttenmpt(User user) {
		if (user.isNotLocked()) {
			if (loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
				user.setNotLocked(false);
			} else {
				user.setNotLocked(true);
			}
		} else {
			loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
		}

	}

	// ---------------------------------------------
	private void saveProfileImage(User user, MultipartFile profileImage) throws IOException, NotAnImageFileException {
		if (profileImage != null) {
			if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE)
					.contains(profileImage.getContentType())) {
				throw new NotAnImageFileException(profileImage.getOriginalFilename() + NOT_AN_IMAGE_FILE);
			}
			Path UserFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
			if (!Files.exists(UserFolder)) {
				Files.createDirectories(UserFolder);
				LOGGER.info(DIRECTORY_CREATED + UserFolder);
			}
			Files.deleteIfExists(Paths.get(user.getUsername() + DOT + JPG_EXTENSION));
			Files.copy(profileImage.getInputStream(), UserFolder.resolve(user.getUsername() + DOT + JPG_EXTENSION),
					REPLACE_EXISTING);
			user.setProfileImageUrl(setProfileImageUrl(user.getUsername()));
			userRepository.save(user);
			LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
		}
	}

	// ---------------------------------------------
	private String getTemporaryProfileImageUrl(String userNmae) {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + userNmae)
				.toUriString();
	}

	// ---------------------------------------------
	private String setProfileImageUrl(String username) {

		return ServletUriComponentsBuilder.fromCurrentContextPath()
				.path(USER_IMAGE_PATH + username + FORWARD_SLASH + username + DOT + JPG_EXTENSION).toUriString();
	}

	// ---------------------------------------------
	private Role getRoleEnumName(String role) {

		return Role.valueOf(role.toUpperCase());
	}


}
