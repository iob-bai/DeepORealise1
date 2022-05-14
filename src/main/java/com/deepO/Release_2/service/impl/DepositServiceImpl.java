package com.deepO.Release_2.service.impl;

import static com.deepO.Release_2.utility.constant.DepositExceptionConstant.DEPOSIT_ADRESS_ALREADY_EXISTS;
import static com.deepO.Release_2.utility.constant.DepositExceptionConstant.DEPOSIT_NAME_ALREADY_EXISTS;
import static com.deepO.Release_2.utility.constant.DepositExceptionConstant.NO_DEPOSIT_FOUND_BY_DEPOSIT_NAME;
import static com.deepO.Release_2.utility.constant.FileConstant.*;
import static com.deepO.Release_2.utility.constant.UserExceptionConstant.NO_USER_FOUND_BY_USERNAME;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.deepO.Release_2.models.Deposit;
import com.deepO.Release_2.models.Item;
import com.deepO.Release_2.models.User;
import com.deepO.Release_2.repository.DepositRepository;
import com.deepO.Release_2.service.inter.DepositService;
import com.deepO.Release_2.service.inter.ItemService;
import com.deepO.Release_2.service.inter.UserService;
import com.deepO.Release_2.utility.exception.domain.deposit.DepositAdressAlreadyExistsException;
import com.deepO.Release_2.utility.exception.domain.deposit.DepositNameAlreadyExistsException;
import com.deepO.Release_2.utility.exception.domain.deposit.DepositNameNotFoundException;
import com.deepO.Release_2.utility.exception.domain.users.NotAnImageFileException;
import com.deepO.Release_2.utility.exception.domain.users.UserNotFoundException;

@Service
@Transactional
public class DepositServiceImpl implements DepositService {
	private Logger LOGGER = LoggerFactory.getLogger(getClass());
	@Autowired
	private DepositRepository depositRepository;
	@Autowired
	private UserService userService;
	
	
	
	
	//----------------------------------------------------------------
	@Override
	public Deposit addDepositToUser(String DepositName, String userName) throws UserNotFoundException, DepositAdressAlreadyExistsException, DepositNameAlreadyExistsException, DepositNameNotFoundException {
		User user =ifExistUser(userName);
		Deposit deposit = ifExistDeposit(DepositName, null, null);
		
		deposit.setDepositUsername(userName);
		
		deposit.setDepositUser(user);
		depositRepository.save(deposit);
		return deposit;
	}

	
	// ---------------------------------------------
	@Override
	public Deposit changeDepositToRent(boolean isvalidated, Deposit deposit,User user) {
		
		if(isvalidated) {
			 deposit.setRent(true);
			 deposit.setDepositUser(user);
			 deposit.setDepositUsername(user.getUsername());
			 depositRepository.save(deposit);
			}
		return deposit;
	}

	

	// ---------------------------------------------
	@Override
	public Deposit addNewDeposit(String DepositName, String DepositAdress, String description, String category,
			double price, boolean isRent, boolean islocked, MultipartFile profileImage)
			throws DepositAdressAlreadyExistsException, DepositNameAlreadyExistsException, DepositNameNotFoundException,
			IOException, NotAnImageFileException {
		ifExistDeposit(EMPTY, DepositName, DepositAdress);
		Deposit deposit = new Deposit();
		deposit.setDepotId(generateUserId());
		deposit.setDepositName(DepositName);
		deposit.setDepositAdress(DepositAdress);
		deposit.setDepositDescription(description);
		deposit.setDepositCategory(category);
		deposit.setDepositPrice(price);
		deposit.setRent(isRent);
		deposit.setIslocked(islocked);
		deposit.setDepositCreation(new Date());
		deposit.setDepotImageUrl(getTemporaryDepositImageUrl(DepositName));
		
	
	

		depositRepository.save(deposit);
		saveProfileImage(deposit, profileImage);

		return deposit;
	}

	// ---------------------------------------------
	@Override
	public Deposit updateDeposit(String CurrentDepositName, String DepositName, String DepositAdress,
			String description, String category, double price, boolean isRent, boolean islocked,
			MultipartFile profileImage) throws DepositAdressAlreadyExistsException, DepositNameAlreadyExistsException,
			DepositNameNotFoundException, IOException, NotAnImageFileException {
		Deposit CurrentDeposit = ifExistDeposit(CurrentDepositName, DepositName, DepositAdress);
		CurrentDeposit.setDepotId(generateUserId());
		CurrentDeposit.setDepositName(DepositName);
		CurrentDeposit.setDepositAdress(DepositAdress);
		CurrentDeposit.setDepositDescription(description);
		CurrentDeposit.setDepositCategory(category);
		CurrentDeposit.setDepositPrice(price);
		CurrentDeposit.setRent(isRent);
		CurrentDeposit.setIslocked(islocked);
		

		depositRepository.save(CurrentDeposit);
		saveProfileImage(CurrentDeposit, profileImage);

		return CurrentDeposit;
	}

	// ---------------------------------------------
	@Override
	public void deleteDeposit(String depositName) throws IOException {
		Deposit deposit = depositRepository.findDepositByDepositName(depositName);
		Path userFolder = Paths.get(DEPOSIT_FOLDER + deposit.getDepositName()).toAbsolutePath().normalize();
		FileUtils.deleteDirectory(new File(userFolder.toString()));
		depositRepository.deleteById(deposit.getId());
	}

	// ---------------------------------------------
	@Override
	public List<Deposit> getAllDeposit() {

		return depositRepository.findAll();
	}

	// ---------------------------------------------
	@Override
	public Deposit findDepositByName(String depositName) {

		return depositRepository.findDepositByDepositName(depositName);
	}

	// ---------------------------------------------
	@Override
	public Deposit findDepositByAdress(String depositAdress) {

		return depositRepository.findDepositByDepositAdress(depositAdress);
	}

	// ---------------------------------------------
	@Override
	public Deposit UpdateDepositImage(String username, MultipartFile profileImage)
			throws DepositAdressAlreadyExistsException, DepositNameAlreadyExistsException, DepositNameNotFoundException,
			IOException, NotAnImageFileException {
		Deposit deposit = ifExistDeposit(username, null, null);
		saveProfileImage(deposit, profileImage);
		return deposit;
	}

	// ==============================================================================
	// ---------------------------------------------
	private User ifExistUser(String username) throws UserNotFoundException 
	{
		
		if (StringUtils.isNotEmpty(username)) {
			User CurrentUser = userService.findUserByUsername(username);
			if (CurrentUser == null) {
				throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
			}
			return CurrentUser;
		}
		return null;
	
		
	}
	// ---------------------------------------------
	private Deposit ifExistDeposit(String CurrentDepositName, String newDepositName, String newAdress)
			throws DepositAdressAlreadyExistsException, DepositNameAlreadyExistsException,
			DepositNameNotFoundException {

		if (StringUtils.isNotEmpty(CurrentDepositName)) {
			Deposit oldDeposit = findDepositByName(CurrentDepositName);
			if (oldDeposit == null) {
				throw new DepositNameNotFoundException(NO_DEPOSIT_FOUND_BY_DEPOSIT_NAME);
			}
			return oldDeposit;
		}
		Deposit NewDeposit = findDepositByName(newDepositName);
		Deposit newDepositByAdress = findDepositByAdress(newAdress);
		if (NewDeposit != null) {
			throw new DepositNameAlreadyExistsException(DEPOSIT_NAME_ALREADY_EXISTS);
		}
		if (newDepositByAdress != null) {
			throw new DepositAdressAlreadyExistsException(DEPOSIT_ADRESS_ALREADY_EXISTS);
		}
		return null;

	}

	// ---------------------------------------------
	private String generateUserId() {
		return RandomStringUtils.randomNumeric(10);
	}

	// ---------------------------------------------
	private String getTemporaryDepositImageUrl(String depositName) {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_DEPOSIT_IMAGE_PATH + depositName).toUriString();
	}

	// ---------------------------------------------
	private String setProfileImageUrl(String depositName) {

		return ServletUriComponentsBuilder.fromCurrentContextPath()
				.path(DEPOSIT_IMAGE_PATH + depositName + FORWARD_SLASH + depositName + DOT + JPG_EXTENSION)
				.toUriString();
	}

	// ---------------------------------------------
	private void saveProfileImage(Deposit deposit, MultipartFile profileImage)
			throws IOException, NotAnImageFileException {
		if (profileImage != null) {
			if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE)
					.contains(profileImage.getContentType())) {
				throw new NotAnImageFileException(profileImage.getOriginalFilename() + NOT_AN_IMAGE_FILE);
			}
			Path depositFolder = Paths.get(DEPOSIT_FOLDER + deposit.getDepositName()).toAbsolutePath().normalize();
			if (!Files.exists(depositFolder)) {
				Files.createDirectories(depositFolder);
				LOGGER.info(DIRECTORY_CREATED + depositFolder);
			}
			Files.deleteIfExists(Paths.get(deposit.getDepositName() + DOT + JPG_EXTENSION));
			Files.copy(profileImage.getInputStream(),
					depositFolder.resolve(deposit.getDepositName() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
			deposit.setDepotImageUrl(setProfileImageUrl(deposit.getDepositName()));
			depositRepository.save(deposit);
			LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
		}
	}



	




	

}
