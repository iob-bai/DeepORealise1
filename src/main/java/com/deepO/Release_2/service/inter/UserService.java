package com.deepO.Release_2.service.inter;



import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.web.multipart.MultipartFile;

import com.deepO.Release_2.models.User;
import com.deepO.Release_2.utility.exception.domain.users.EmailExistException;
import com.deepO.Release_2.utility.exception.domain.users.EmailNotFoundException;
import com.deepO.Release_2.utility.exception.domain.users.NotAnImageFileException;
import com.deepO.Release_2.utility.exception.domain.users.UserNotFoundException;
import com.deepO.Release_2.utility.exception.domain.users.UsernameExistException;

public interface UserService {

    User register(String firstName, String lastName, String username, String email) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException;
    List<User> getUsers();
    User findUserByUsername(String username);
    User findUserByEmail(String email);
    
    User addNewUser(String firstName,String lastName,String userName,String email,String role, boolean isNonLocked,boolean isActive,MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException ;
    User updateUser(String currentuserName,String newFirstName,String newLastName,String newUserName,String newEmail,String role, boolean isNonLocked,boolean isActive,MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException ;
    
    void deleteUser(String username)throws IOException;
    void resetPassword(String email) throws EmailNotFoundException, MessagingException;
    User UpdateProfileImage(String username,MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException;

    User addDepositIntoUser(String username ,String depositName) ;
	

}
