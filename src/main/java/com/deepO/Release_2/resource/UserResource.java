package com.deepO.Release_2.resource;

import com.deepO.Release_2.models.User;
import com.deepO.Release_2.service.inter.UserService;
import com.deepO.Release_2.utility.domain.HttpResponse;
import com.deepO.Release_2.utility.domain.UserPrincipal;
import com.deepO.Release_2.utility.exception.ExceptionHandling;
import com.deepO.Release_2.utility.exception.domain.users.EmailExistException;
import com.deepO.Release_2.utility.exception.domain.users.EmailNotFoundException;
import com.deepO.Release_2.utility.exception.domain.users.NotAnImageFileException;
import com.deepO.Release_2.utility.exception.domain.users.UserNotFoundException;
import com.deepO.Release_2.utility.exception.domain.users.UsernameExistException;
import com.deepO.Release_2.utility.jwt.JwtTokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static com.deepO.Release_2.utility.constant.FileConstant.*;
import static com.deepO.Release_2.utility.constant.SecurityConstant.*;
import static org.springframework.http.HttpStatus.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.mail.MessagingException;

@RestController
@RequestMapping(path = { "/", "/user" })
//@CrossOrigin("http://localhost:4200")
public class UserResource extends ExceptionHandling {
	
	public static final String SENT_EMAIL ="an Email with a new password was sent to :";
	public static final String USER_DELETED_SUCESSFULLY ="The user was been Deleted sucessfully";
	
	
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserService userService;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;

//	@Autowired
//	public UserResource(AuthenticationManager authenticationManager, UserService userService,
//			JwtTokenProvider jwtTokenProvider) {
//		this.authenticationManager = authenticationManager;
//		this.userService = userService;
//		this.jwtTokenProvider = jwtTokenProvider;
//	}

	@PostMapping("/login")
	public ResponseEntity<User> login(@RequestBody User user) {
		authenticate(user.getUsername(), user.getPassword());
		User loginUser = userService.findUserByUsername(user.getUsername());
		UserPrincipal userPrincipal = new UserPrincipal(loginUser);
		HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
		return new ResponseEntity<>(loginUser, jwtHeader, OK);
	}
	//--------------------------------------------------
	@PostMapping("/register")
	public ResponseEntity<User> register(@RequestBody User user)
			throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException
	{
		User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(),user.getEmail());
		return new ResponseEntity<>(newUser, OK);
	}
	//--------------------------------------------------
	@PostMapping("/add")
	public ResponseEntity<User> addNewUser(@RequestParam("firstName") String firstName,
											@RequestParam("lastName") String lastName,
											@RequestParam("userName") String userName,
											@RequestParam("email") String email,
											@RequestParam("role") String role,
											@RequestParam("isActive") String isActive,
											@RequestParam("isNonLocked") String isNonLocked,
											@RequestParam(value = "profileImage",required = false/*optional*/) MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException
	{
		User newUser = userService.addNewUser(firstName, lastName, userName, email, role, Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImage);
		return new ResponseEntity<User>(newUser,OK);
	}
	//--------------------------------------------------
	@PutMapping("/update")
	public ResponseEntity<User> updateNewUser(@RequestParam("currentUsername") String currentUsername,
											@RequestParam("firstName") String firstName,
											@RequestParam("lastName") String lastName,
											@RequestParam("userName") String userName,
											@RequestParam("email") String email,
											@RequestParam("role") String role,
											@RequestParam("isActive") String isActive,
											@RequestParam("isNonLocked") String isNonLocked,
											@RequestParam(value = "profileImage",required = false/*optional*/) MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException
	{
		User updateUser = userService.updateUser(currentUsername,firstName, lastName, userName, email, role, Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImage);
		return new ResponseEntity<User>(updateUser,OK);
	}
	//--------------------------------------------------
	@GetMapping("/find/{username}")
	public ResponseEntity<User> getUser(@PathVariable("username")String username)
	{
		User user = userService.findUserByUsername(username);
		return new ResponseEntity<User>(user,OK);
	}
	//--------------------------------------------------
	@GetMapping("/list")
	public ResponseEntity<List<User>> getAllUser()
	{
		List<User> users = userService.getUsers();
		return new ResponseEntity<>(users,OK);
	}
	//--------------------------------------------------
	@GetMapping("/resetPassword/{email}")
	public ResponseEntity<HttpResponse> getAllUser(@PathVariable("email") String email) throws EmailNotFoundException, MessagingException
	{
		userService.resetPassword(email);
		return  response(OK,SENT_EMAIL+email);
	}
	//--------------------------------------------------
	@DeleteMapping("/delete/{username}")
	@PreAuthorize("hasAnyAuthority('user:delete')")
	public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username) throws IOException
	{
		userService.deleteUser(username);
		return response(OK,USER_DELETED_SUCESSFULLY);
	}
	//--------------------------------------------------
	@PutMapping("/updateProfileImage")
	public ResponseEntity<User> updateProfileImage(@RequestParam("username") String username,@RequestParam("profileImage") MultipartFile profileImage)
			throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException
	{
		User user = userService.UpdateProfileImage(username, profileImage);
		return new ResponseEntity<User>(user,OK);
	}
	//--------------------------------------------------
	@GetMapping(path = "/image/{username}/{fileName}",produces = IMAGE_JPEG_VALUE)
	public byte[] getProfileImage(@PathVariable("username") String username,@PathVariable("fileName") String fileName) throws IOException 
	{
		return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName));
	}
	//--------------------------------------------------
	@GetMapping(path = "/image/profile/{username}",produces = IMAGE_JPEG_VALUE)
	public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException 
	{
		URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
		ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream();
		try(InputStream inputStream = url.openStream())
		{
			int bytesRead;
			byte[] chunk = new byte[1024];
			while ((bytesRead = inputStream.read(chunk)) > 0)
			{
				byteArrayOutputStream.write(chunk,0,bytesRead);
			}
		}
		return byteArrayOutputStream.toByteArray();
	}
	
	//======================================================================================	
	private HttpHeaders getJwtHeader(UserPrincipal user) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(user));
		return headers;
	}

	private void authenticate(String username, String password) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	}
	private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
		
		return  new ResponseEntity<>(new HttpResponse(httpStatus.value(),httpStatus,httpStatus.getReasonPhrase(),message),OK );
	}
}
